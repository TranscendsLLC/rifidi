package com.espertech.esper.event.xml;

import com.espertech.esper.client.ConfigurationException;
import com.espertech.esper.util.ResourceLoader;
import com.sun.org.apache.xerces.internal.dom.DOMXSImplementationSourceImpl;
import com.sun.org.apache.xerces.internal.impl.dv.xs.XSSimpleTypeDecl;
import com.sun.org.apache.xerces.internal.xs.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.bootstrap.DOMImplementationRegistry;

import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Helper class for mapping a XSD schema model to an internal representation.
 */
public class XSDSchemaMapper
{
    private static final Log log = LogFactory.getLog(XSDSchemaMapper.class);

    /**
     * Loading and mapping of the schema to the internal representation.
     * @param schemaResource schema to load and map.
     * @param maxRecusiveDepth depth of maximal recursive element
     * @return model
     */
    public static SchemaModel loadAndMap(String schemaResource, int maxRecusiveDepth)
    {
        // Load schema
        XSModel model;
        try
        {
            model = readSchemaInternal(schemaResource);
        }
        catch (ConfigurationException ex)
        {
            throw ex;
        }
        catch (Exception ex)
        {
            throw new ConfigurationException("Failed to read schema '" + schemaResource + "' : " + ex.getMessage(), ex);
        }

        // Map schema to internal representation
        return map(model, maxRecusiveDepth);
    }

    private static XSModel readSchemaInternal(String schemaResource) throws IllegalAccessException, InstantiationException, ClassNotFoundException,
            ConfigurationException, URISyntaxException
    {
        URL url = ResourceLoader.resolveClassPathOrURLResource("schema", schemaResource);
        String uri = url.toURI().toString();

        // Uses Xerxes internal classes
        DOMImplementationRegistry registry = DOMImplementationRegistry.newInstance();
        registry.addSource(new DOMXSImplementationSourceImpl());
        XSImplementation impl =(XSImplementation) registry.getDOMImplementation("XS-Loader");
        XSLoader schemaLoader = impl.createXSLoader(null);
        XSModel xsModel = schemaLoader.loadURI(uri);

        if (xsModel == null)
        {
            throw new ConfigurationException("Failed to read schema via URL '" + schemaResource + '\'');
        }

        return xsModel;
    }

    private static SchemaModel map(XSModel xsModel, int maxRecusiveDepth)
    {
        // get namespaces
        StringList namespaces = xsModel.getNamespaces();
        List<String> namesspaceList = new ArrayList<String>();
        for (int i = 0; i < namespaces.getLength(); i++)
        {
            namesspaceList.add(namespaces.item(i));
        }

        // get top-level complex elements
        XSNamedMap elements = xsModel.getComponents(XSConstants.ELEMENT_DECLARATION);
        List<SchemaElementComplex> components = new ArrayList<SchemaElementComplex>();

        for (int i = 0; i < elements.getLength(); i++)
        {
            XSObject object = elements.item(i);
            if (!(object instanceof XSElementDeclaration))
            {
                continue;
            }

            XSElementDeclaration decl = (XSElementDeclaration) elements.item(i);
            if (decl.getTypeDefinition().getTypeCategory() != XSTypeDefinition.COMPLEX_TYPE)
            {
                continue;
            }            

            XSComplexTypeDefinition complexActualElement = (XSComplexTypeDefinition) decl.getTypeDefinition();
            String name = object.getName();
            String namespace = object.getNamespace();
            Stack<NamespaceNamePair> nameNamespaceStack = new Stack<NamespaceNamePair>();
            NamespaceNamePair nameNamespace = new NamespaceNamePair(namespace, name);
            nameNamespaceStack.add(nameNamespace);

            if (log.isDebugEnabled())
            {
                log.debug("Processing component " + namespace + " " + name);
            }

            SchemaElementComplex complexElement = process(name, namespace, complexActualElement, false, nameNamespaceStack, maxRecusiveDepth);

            if (log.isDebugEnabled())
            {
                log.debug("Adding component " + namespace + " " + name);
            }
            components.add(complexElement);
        }

        return new SchemaModel(components, namesspaceList);
    }

    private static SchemaElementComplex process(String complexElementName, String complexElementNamespace, XSComplexTypeDefinition complexActualElement, boolean isArray, Stack<NamespaceNamePair> nameNamespaceStack, int maxRecursiveDepth)
    {
        if (log.isDebugEnabled())
        {
            log.debug("Processing complex " + complexElementNamespace + " " + complexElementName + " stack " + nameNamespaceStack);
        }

        List<SchemaItemAttribute> attributes = new ArrayList<SchemaItemAttribute>();
        List<SchemaElementSimple> simpleElements = new ArrayList<SchemaElementSimple>();
        List<SchemaElementComplex> complexElements = new ArrayList<SchemaElementComplex>();

        Short optionalSimplyType = null;
        String optionalSimplyTypeName = null;
        if (complexActualElement.getSimpleType() != null) {
            XSSimpleTypeDecl simpleType = (XSSimpleTypeDecl) complexActualElement.getSimpleType();
            optionalSimplyType = simpleType.getPrimitiveKind();
            optionalSimplyTypeName = simpleType.getName();
        }

        SchemaElementComplex complexElement = new SchemaElementComplex(complexElementName, complexElementNamespace, attributes, complexElements, simpleElements, isArray, optionalSimplyType, optionalSimplyTypeName);

        // add attributes
        XSObjectList attrs = complexActualElement.getAttributeUses();
        for(int i = 0; i < attrs.getLength(); i++)
        {
            XSAttributeUse attr = (XSAttributeUse)attrs.item(i);
            String namespace = attr.getAttrDeclaration().getNamespace();
            String name = attr.getAttrDeclaration().getName();
            XSSimpleTypeDecl simpleType = (XSSimpleTypeDecl) attr.getAttrDeclaration().getTypeDefinition();
            attributes.add(new SchemaItemAttribute(namespace, name, simpleType.getPrimitiveKind(), simpleType.getName()));
        }

        if ((complexActualElement.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_ELEMENT) ||
            (complexActualElement.getContentType() == XSComplexTypeDefinition.CONTENTTYPE_MIXED))
        {
            // has children
            XSParticle particle = complexActualElement.getParticle();
            if (particle.getTerm() instanceof XSModelGroup )
            {
                XSModelGroup group = (XSModelGroup)particle.getTerm();
                XSObjectList particles = group.getParticles();
                for (int i = 0; i < particles.getLength(); i++)
                {
                    XSParticle childParticle = (XSParticle)particles.item(i);

                    if (childParticle.getTerm() instanceof XSElementDeclaration)
                    {
                        XSElementDeclaration decl = (XSElementDeclaration) childParticle.getTerm();
                        boolean isArrayFlag = isArray(childParticle);

                        if (decl.getTypeDefinition().getTypeCategory() == XSTypeDefinition.SIMPLE_TYPE) {
                            XSSimpleTypeDecl simpleType = (XSSimpleTypeDecl) decl.getTypeDefinition();
                            simpleElements.add(new SchemaElementSimple(decl.getName(), decl.getNamespace(), simpleType.getPrimitiveKind(), simpleType.getName(), isArrayFlag));
                        }

                        if (decl.getTypeDefinition().getTypeCategory() == XSTypeDefinition.COMPLEX_TYPE)
                        {
                            String name = decl.getName();
                            String namespace = decl.getNamespace();
                            NamespaceNamePair nameNamespace = new NamespaceNamePair(namespace, name);
                            nameNamespaceStack.add(nameNamespace);

                            // if the stack contains
                            if (maxRecursiveDepth != Integer.MAX_VALUE)
                            {
                                int containsCount = 0;
                                for (NamespaceNamePair pair : nameNamespaceStack)
                                {
                                    if (nameNamespace.equals(pair))
                                    {
                                        containsCount++;
                                    }
                                }

                                if (containsCount >= maxRecursiveDepth)
                                {
                                    continue;
                                }
                            }

                            complexActualElement = (XSComplexTypeDefinition) decl.getTypeDefinition();
                            SchemaElementComplex innerComplex = process(name, namespace, complexActualElement, isArrayFlag, nameNamespaceStack, maxRecursiveDepth);

                            nameNamespaceStack.pop();

                            if (log.isDebugEnabled())
                            {
                                log.debug("Adding complex " + complexElement);
                            }
                            complexElements.add(innerComplex);
                        }
                    }
                }
            }
        }

        return complexElement;
    }

    private static boolean isArray(XSParticle particle)
    {
        return particle.getMaxOccursUnbounded() || (particle.getMaxOccurs() > 1); 
    }
}
