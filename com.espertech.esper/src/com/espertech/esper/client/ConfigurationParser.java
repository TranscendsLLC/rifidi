/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.client;

import com.espertech.esper.util.DOMElementIterator;
import com.espertech.esper.util.JavaClassHelper;
import com.espertech.esper.event.EventAdapterException;
import com.espertech.esper.client.soda.StreamSelector;
import com.espertech.esper.type.StringPatternSet;
import com.espertech.esper.type.StringPatternSetRegex;
import com.espertech.esper.type.StringPatternSetLike;
import com.espertech.esper.collection.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathConstants;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.*;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * Parser for configuration XML.
 */
class ConfigurationParser {

    /**
     * Use the configuration specified in the given input stream.
     * @param configuration is the configuration object to populate
     * @param stream	   Inputstream to be read from
     * @param resourceName The name to use in warning/error messages
     * @throws EPException is thrown when the configuration could not be parsed
     */
    protected static void doConfigure(Configuration configuration, InputStream stream, String resourceName) throws EPException
    {
        Document document = getDocument(stream, resourceName);
        doConfigure(configuration, document);
    }

    protected static Document getDocument(InputStream stream, String resourceName) throws EPException
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;

        Document document = null;

        try
        {
            builder = factory.newDocumentBuilder();
            document = builder.parse(stream);
        }
        catch (ParserConfigurationException ex)
        {
            throw new EPException("Could not get a DOM parser configuration: " + resourceName, ex);
        }
        catch (SAXException ex)
        {
            throw new EPException("Could not parse configuration: " + resourceName, ex);
        }
        catch (IOException ex)
        {
            throw new EPException("Could not read configuration: " + resourceName, ex);
        }
        finally {
            try {
                stream.close();
            }
            catch (IOException ioe) {
                ConfigurationParser.log.warn( "could not close input stream for: " + resourceName, ioe );
            }
        }

        return document;
    }

    /**
     * Parse the W3C DOM document.
     * @param configuration is the configuration object to populate
     * @param doc to parse
     * @throws EPException to indicate parse errors
     */
    protected static void doConfigure(Configuration configuration, Document doc) throws EPException
    {
        Element root = doc.getDocumentElement();

        DOMElementIterator eventTypeNodeIterator = new DOMElementIterator(root.getChildNodes());
        while (eventTypeNodeIterator.hasNext())
        {
            Element element = eventTypeNodeIterator.next();
            String nodeName = element.getNodeName();
            if (nodeName.equals("event-type-auto-name"))
            {
                handleEventTypeAutoNames(configuration, element);
            }
            else if (nodeName.equals("event-type"))
            {
                handleEventTypes(configuration, element);
            }
            else if(nodeName.equals("auto-import"))
            {
            	handleAutoImports(configuration, element);
            }
            else if(nodeName.equals("method-reference"))
            {
            	handleMethodReference(configuration, element);
            }
            else if (nodeName.equals("database-reference"))
            {
                handleDatabaseRefs(configuration, element);
            }
            else if (nodeName.equals("plugin-view"))
            {
                handlePlugInView(configuration, element);
            }
            else if (nodeName.equals("plugin-aggregation-function"))
            {
                handlePlugInAggregation(configuration, element);
            }
            else if (nodeName.equals("plugin-pattern-guard"))
            {
                handlePlugInPatternGuard(configuration, element);
            }
            else if (nodeName.equals("plugin-pattern-observer"))
            {
                handlePlugInPatternObserver(configuration, element);
            }
            else if (nodeName.equals("variable"))
            {
                handleVariable(configuration, element);
            }
            else if (nodeName.equals("plugin-loader"))
            {
                handlePluginLoaders(configuration, element);
            }
            else if (nodeName.equals("engine-settings"))
            {
                handleEngineSettings(configuration, element);
            }
            else if (nodeName.equals("plugin-event-representation"))
            {
                handlePlugInEventRepresentation(configuration, element);
            }
            else if (nodeName.equals("plugin-event-type"))
            {
                handlePlugInEventType(configuration, element);
            }
            else if (nodeName.equals("plugin-event-type-name-resolution"))
            {
                handlePlugIneventTypeNameResolution(configuration, element);
            }
            else if (nodeName.equals("revision-event-type"))
            {
                handleRevisionEventType(configuration, element);
            }
            else if (nodeName.equals("variant-stream"))
            {
                handleVariantStream(configuration, element);
            }
        }
    }

    private static void handleEventTypeAutoNames(Configuration configuration, Element element)
    {
        String name = element.getAttributes().getNamedItem("package-name").getTextContent();
        configuration.addEventTypeAutoName(name);
    }

    private static void handleEventTypes(Configuration configuration, Element element)
    {
        String name = getRequiredAttribute(element, "name");
        Node classNode = element.getAttributes().getNamedItem("class");

        String optionalClassName = null;
        if (classNode != null)
        {
            optionalClassName = classNode.getTextContent();
            configuration.addEventType(name, optionalClassName);
        }

        handleEventTypeDef(name, optionalClassName, configuration, element);
    }

    private static void handleEventTypeDef(String name, String optionalClassName, Configuration configuration, Node parentNode)
    {
        DOMElementIterator eventTypeNodeIterator = new DOMElementIterator(parentNode.getChildNodes());
        while (eventTypeNodeIterator.hasNext())
        {
            Element eventTypeElement = eventTypeNodeIterator.next();
            String nodeName = eventTypeElement.getNodeName();
            if (nodeName.equals("xml-dom"))
            {
                handleXMLDOM(name, configuration, eventTypeElement);
            }
            else if(nodeName.equals("java-util-map"))
            {
            	handleMap(name, configuration, eventTypeElement);
            }
            else if (nodeName.equals("legacy-type"))
            {
                handleLegacy(name, optionalClassName, configuration, eventTypeElement);
            }
        }
    }

    private static void handleMap(String name, Configuration configuration, Element eventTypeElement)
    {
        Node superTypesList = eventTypeElement.getAttributes().getNamedItem("supertype-names");
        if (superTypesList != null)
        {
            String value = superTypesList.getTextContent();
            String[] names = value.split(",");
            for (String superTypeName : names)
            {
                configuration.addMapSuperType(name, superTypeName.trim());
            }
        }

        Properties propertyTypeNames = new Properties();
		NodeList propertyList = eventTypeElement.getElementsByTagName("map-property");
		for (int i = 0; i < propertyList.getLength(); i++)
	    {
	        String nameProperty = propertyList.item(i).getAttributes().getNamedItem("name").getTextContent();
	        String clazz = propertyList.item(i).getAttributes().getNamedItem("class").getTextContent();
	        propertyTypeNames.put(nameProperty, clazz);
	    }
    	configuration.addEventType(name, propertyTypeNames);
    }

    private static void handleXMLDOM(String name, Configuration configuration, Element xmldomElement)
    {
        String rootElementName = xmldomElement.getAttributes().getNamedItem("root-element-name").getTextContent();
        String rootElementNamespace = getOptionalAttribute(xmldomElement, "root-element-namespace");
        String schemaResource = getOptionalAttribute(xmldomElement, "schema-resource");
        String defaultNamespace = getOptionalAttribute(xmldomElement, "default-namespace");
        String resolvePropertiesAbsoluteStr = getOptionalAttribute(xmldomElement, "xpath-resolve-properties-absolute");
        String propertyExprXPathStr = getOptionalAttribute(xmldomElement, "xpath-property-expr");
        String eventSenderChecksRootStr = getOptionalAttribute(xmldomElement, "event-sender-validates-root");
        String xpathFunctionResolverClass = getOptionalAttribute(xmldomElement, "xpath-function-resolver");
        String xpathVariableResolverClass = getOptionalAttribute(xmldomElement, "xpath-variable-resolver");
        String autoFragmentStr = getOptionalAttribute(xmldomElement, "auto-fragment");

        ConfigurationEventTypeXMLDOM xmlDOMEventTypeDesc = new ConfigurationEventTypeXMLDOM();
        xmlDOMEventTypeDesc.setRootElementName(rootElementName);
        xmlDOMEventTypeDesc.setSchemaResource(schemaResource);
        xmlDOMEventTypeDesc.setRootElementNamespace(rootElementNamespace);
        xmlDOMEventTypeDesc.setDefaultNamespace(defaultNamespace);
        xmlDOMEventTypeDesc.setXPathFunctionResolver(xpathFunctionResolverClass);
        xmlDOMEventTypeDesc.setXPathVariableResolver(xpathVariableResolverClass);
        if (resolvePropertiesAbsoluteStr != null)
        {
            xmlDOMEventTypeDesc.setXPathResolvePropertiesAbsolute(Boolean.parseBoolean(resolvePropertiesAbsoluteStr));
        }
        if (propertyExprXPathStr != null)
        {
            xmlDOMEventTypeDesc.setXPathPropertyExpr(Boolean.parseBoolean(propertyExprXPathStr));
        }
        if (eventSenderChecksRootStr != null)
        {
            xmlDOMEventTypeDesc.setEventSenderValidatesRoot(Boolean.parseBoolean(eventSenderChecksRootStr));
        }
        if (autoFragmentStr != null)
        {
            xmlDOMEventTypeDesc.setAutoFragment(Boolean.parseBoolean(autoFragmentStr));            
        }
        configuration.addEventType(name, xmlDOMEventTypeDesc);

        DOMElementIterator propertyNodeIterator = new DOMElementIterator(xmldomElement.getChildNodes());
        while (propertyNodeIterator.hasNext())
        {
            Element propertyElement = propertyNodeIterator.next();
            if (propertyElement.getNodeName().equals("namespace-prefix"))
            {
                String prefix = propertyElement.getAttributes().getNamedItem("prefix").getTextContent();
                String namespace = propertyElement.getAttributes().getNamedItem("namespace").getTextContent();
                xmlDOMEventTypeDesc.addNamespacePrefix(prefix, namespace);
            }
            if (propertyElement.getNodeName().equals("xpath-property"))
            {
                String propertyName = propertyElement.getAttributes().getNamedItem("property-name").getTextContent();
                String xPath = propertyElement.getAttributes().getNamedItem("xpath").getTextContent();

                String propertyType = propertyElement.getAttributes().getNamedItem("type").getTextContent();
                QName xpathConstantType;
                if (propertyType.toUpperCase().equals("NUMBER"))
                {
                    xpathConstantType = XPathConstants.NUMBER;
                }
                else if (propertyType.toUpperCase().equals("STRING"))
                {
                    xpathConstantType = XPathConstants.STRING;
                }
                else if (propertyType.toUpperCase().equals("BOOLEAN"))
                {
                    xpathConstantType = XPathConstants.BOOLEAN;
                }
                else if (propertyType.toUpperCase().equals("NODE"))
                {
                    xpathConstantType = XPathConstants.NODE;
                }
                else if (propertyType.toUpperCase().equals("NODESET"))
                {
                    xpathConstantType = XPathConstants.NODESET;
                }
                else
                {
                    throw new IllegalArgumentException("Invalid xpath property type for property '" +
                        propertyName + "' and type '" + propertyType + '\'');
                }

                String castToClass = null;
                if (propertyElement.getAttributes().getNamedItem("cast") != null)
                {
                    castToClass = propertyElement.getAttributes().getNamedItem("cast").getTextContent();
                }

                String optionaleventTypeName = null;
                if (propertyElement.getAttributes().getNamedItem("event-type-name") != null)
                {
                    optionaleventTypeName = propertyElement.getAttributes().getNamedItem("event-type-name").getTextContent();
                }

                if (optionaleventTypeName != null)
                {
                    xmlDOMEventTypeDesc.addXPathPropertyFragment(propertyName, xPath, xpathConstantType, optionaleventTypeName);
                }
                else
                {
                    xmlDOMEventTypeDesc.addXPathProperty(propertyName, xPath, xpathConstantType, castToClass);
                }
            }
        }
    }

    private static void handleLegacy(String name, String className, Configuration configuration, Element xmldomElement)
    {
        // Class name is required for legacy classes
        if (className == null)
        {
            throw new ConfigurationException("Required class name not supplied for legacy type definition");
        }

        String accessorStyle = xmldomElement.getAttributes().getNamedItem("accessor-style").getTextContent();
        String codeGeneration = xmldomElement.getAttributes().getNamedItem("code-generation").getTextContent();
        String propertyResolution = xmldomElement.getAttributes().getNamedItem("property-resolution-style").getTextContent();

        ConfigurationEventTypeLegacy legacyDesc = new ConfigurationEventTypeLegacy();
        if (accessorStyle != null)
        {
            legacyDesc.setAccessorStyle(ConfigurationEventTypeLegacy.AccessorStyle.valueOf(accessorStyle.toUpperCase()));
        }
        if (codeGeneration != null)
        {
            legacyDesc.setCodeGeneration(ConfigurationEventTypeLegacy.CodeGeneration.valueOf(codeGeneration.toUpperCase()));
        }
        if (propertyResolution != null)
        {
            legacyDesc.setPropertyResolutionStyle(Configuration.PropertyResolutionStyle.valueOf(propertyResolution.toUpperCase()));
        }
        configuration.addEventType(name, className, legacyDesc);

        DOMElementIterator propertyNodeIterator = new DOMElementIterator(xmldomElement.getChildNodes());
        while (propertyNodeIterator.hasNext())
        {
            Element propertyElement = propertyNodeIterator.next();
            if (propertyElement.getNodeName().equals("method-property"))
            {
                String nameProperty = propertyElement.getAttributes().getNamedItem("name").getTextContent();
                String method = propertyElement.getAttributes().getNamedItem("accessor-method").getTextContent();
                legacyDesc.addMethodProperty(nameProperty, method);
            }
            else if (propertyElement.getNodeName().equals("field-property"))
            {
                String nameProperty = propertyElement.getAttributes().getNamedItem("name").getTextContent();
                String field = propertyElement.getAttributes().getNamedItem("accessor-field").getTextContent();
                legacyDesc.addFieldProperty(nameProperty, field);
            }
            else
            {
                throw new ConfigurationException("Invalid node " + propertyElement.getNodeName()
                        + " encountered while parsing legacy type definition");
            }
        }
    }

    private static void handleAutoImports(Configuration configuration, Element element)
    {
        String name = element.getAttributes().getNamedItem("import-name").getTextContent();
        configuration.addImport(name);
    }

    private static void handleDatabaseRefs(Configuration configuration, Element element)
    {
        String name = element.getAttributes().getNamedItem("name").getTextContent();
        ConfigurationDBRef configDBRef = new ConfigurationDBRef();
        configuration.addDatabaseReference(name, configDBRef);

        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("datasource-connection"))
            {
                String lookup = subElement.getAttributes().getNamedItem("context-lookup-name").getTextContent();
                Properties properties = handleProperties(subElement, "env-property");
                configDBRef.setDataSourceConnection(lookup, properties);
            }
            if (subElement.getNodeName().equals("datasourcefactory-connection"))
            {
                String className = subElement.getAttributes().getNamedItem("class-name").getTextContent();
                Properties properties = handleProperties(subElement, "env-property");
                configDBRef.setDataSourceFactory(properties, className);
            }
            else if (subElement.getNodeName().equals("drivermanager-connection"))
            {
                String className = subElement.getAttributes().getNamedItem("class-name").getTextContent();
                String url = subElement.getAttributes().getNamedItem("url").getTextContent();
                String userName = subElement.getAttributes().getNamedItem("user").getTextContent();
                String password = subElement.getAttributes().getNamedItem("password").getTextContent();
                Properties properties = handleProperties(subElement, "connection-arg");
                configDBRef.setDriverManagerConnection(className, url, userName, password, properties);
            }
            else if (subElement.getNodeName().equals("connection-lifecycle"))
            {
                String value = subElement.getAttributes().getNamedItem("value").getTextContent();
                configDBRef.setConnectionLifecycleEnum(ConfigurationDBRef.ConnectionLifecycleEnum.valueOf(value.toUpperCase()));
            }
            else if (subElement.getNodeName().equals("connection-settings"))
            {
                if (subElement.getAttributes().getNamedItem("auto-commit") != null)
                {
                    String autoCommit = subElement.getAttributes().getNamedItem("auto-commit").getTextContent();
                    configDBRef.setConnectionAutoCommit(Boolean.parseBoolean(autoCommit));
                }
                if (subElement.getAttributes().getNamedItem("transaction-isolation") != null)
                {
                    String transactionIsolation = subElement.getAttributes().getNamedItem("transaction-isolation").getTextContent();
                    configDBRef.setConnectionTransactionIsolation(Integer.parseInt(transactionIsolation));
                }
                if (subElement.getAttributes().getNamedItem("catalog") != null)
                {
                    String catalog = subElement.getAttributes().getNamedItem("catalog").getTextContent();
                    configDBRef.setConnectionCatalog(catalog);
                }
                if (subElement.getAttributes().getNamedItem("read-only") != null)
                {
                    String readOnly = subElement.getAttributes().getNamedItem("read-only").getTextContent();
                    configDBRef.setConnectionReadOnly(Boolean.parseBoolean(readOnly));
                }
            }
            else if (subElement.getNodeName().equals("column-change-case"))
            {
                String value = subElement.getAttributes().getNamedItem("value").getTextContent();
                ConfigurationDBRef.ColumnChangeCaseEnum parsed = ConfigurationDBRef.ColumnChangeCaseEnum.valueOf(value.toUpperCase());
                configDBRef.setColumnChangeCase(parsed);
            }
            else if (subElement.getNodeName().equals("metadata-origin"))
            {
                String value = subElement.getAttributes().getNamedItem("value").getTextContent();
                ConfigurationDBRef.MetadataOriginEnum parsed = ConfigurationDBRef.MetadataOriginEnum.valueOf(value.toUpperCase());
                configDBRef.setMetadataOrigin(parsed);
            }
            else if (subElement.getNodeName().equals("sql-types-mapping"))
            {
                String sqlType = subElement.getAttributes().getNamedItem("sql-type").getTextContent();
                String javaType = subElement.getAttributes().getNamedItem("java-type").getTextContent();
                Integer sqlTypeInt;
                try
                {
                    sqlTypeInt = Integer.parseInt(sqlType);
                }
                catch (NumberFormatException ex)
                {
                    throw new ConfigurationException("Error converting sql type '" + sqlType + "' to integer java.sql.Types constant");
                }
                configDBRef.addSqlTypesBinding(sqlTypeInt, javaType);
            }
            else if (subElement.getNodeName().equals("expiry-time-cache"))
            {
                String maxAge = subElement.getAttributes().getNamedItem("max-age-seconds").getTextContent();
                String purgeInterval = subElement.getAttributes().getNamedItem("purge-interval-seconds").getTextContent();
                ConfigurationCacheReferenceType refTypeEnum = ConfigurationCacheReferenceType.getDefault();
                if (subElement.getAttributes().getNamedItem("ref-type") != null)
                {
                    String refType = subElement.getAttributes().getNamedItem("ref-type").getTextContent();
                    refTypeEnum = ConfigurationCacheReferenceType.valueOf(refType.toUpperCase());
                }
                configDBRef.setExpiryTimeCache(Double.parseDouble(maxAge), Double.parseDouble(purgeInterval), refTypeEnum);
            }
            else if (subElement.getNodeName().equals("lru-cache"))
            {
                String size = subElement.getAttributes().getNamedItem("size").getTextContent();
                configDBRef.setLRUCache(Integer.parseInt(size));
            }
        }
    }

    private static void handleMethodReference(Configuration configuration, Element element)
    {
        String className = element.getAttributes().getNamedItem("class-name").getTextContent();
        ConfigurationMethodRef configMethodRef = new ConfigurationMethodRef();
        configuration.addMethodRef(className, configMethodRef);

        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("expiry-time-cache"))
            {
                String maxAge = subElement.getAttributes().getNamedItem("max-age-seconds").getTextContent();
                String purgeInterval = subElement.getAttributes().getNamedItem("purge-interval-seconds").getTextContent();
                ConfigurationCacheReferenceType refTypeEnum = ConfigurationCacheReferenceType.getDefault();
                if (subElement.getAttributes().getNamedItem("ref-type") != null)
                {
                    String refType = subElement.getAttributes().getNamedItem("ref-type").getTextContent();
                    refTypeEnum = ConfigurationCacheReferenceType.valueOf(refType.toUpperCase());
                }
                configMethodRef.setExpiryTimeCache(Double.parseDouble(maxAge), Double.parseDouble(purgeInterval), refTypeEnum);
            }
            else if (subElement.getNodeName().equals("lru-cache"))
            {
                String size = subElement.getAttributes().getNamedItem("size").getTextContent();
                configMethodRef.setLRUCache(Integer.parseInt(size));
            }
        }
    }

    private static void handlePlugInView(Configuration configuration, Element element)
    {
        String namespace = element.getAttributes().getNamedItem("namespace").getTextContent();
        String name = element.getAttributes().getNamedItem("name").getTextContent();
        String factoryClassName = element.getAttributes().getNamedItem("factory-class").getTextContent();
        configuration.addPlugInView(namespace, name, factoryClassName);
    }

    private static void handlePlugInAggregation(Configuration configuration, Element element)
    {
        String name = element.getAttributes().getNamedItem("name").getTextContent();
        String functionClassName = element.getAttributes().getNamedItem("function-class").getTextContent();
        configuration.addPlugInAggregationFunction(name, functionClassName);
    }

    private static void handlePlugInPatternGuard(Configuration configuration, Element element)
    {
        String namespace = element.getAttributes().getNamedItem("namespace").getTextContent();
        String name = element.getAttributes().getNamedItem("name").getTextContent();
        String factoryClassName = element.getAttributes().getNamedItem("factory-class").getTextContent();
        configuration.addPlugInPatternGuard(namespace, name, factoryClassName);
    }

    private static void handlePlugInPatternObserver(Configuration configuration, Element element)
    {
        String namespace = element.getAttributes().getNamedItem("namespace").getTextContent();
        String name = element.getAttributes().getNamedItem("name").getTextContent();
        String factoryClassName = element.getAttributes().getNamedItem("factory-class").getTextContent();
        configuration.addPlugInPatternObserver(namespace, name, factoryClassName);
    }

    private static void handleVariable(Configuration configuration, Element element)
    {
        String variableName = element.getAttributes().getNamedItem("name").getTextContent();
        String type = element.getAttributes().getNamedItem("type").getTextContent();

        Class variableType;
        try
        {
            variableType = JavaClassHelper.getClassForSimpleName(type);
        }
        catch (EventAdapterException ex)
        {
            throw new ConfigurationException("Invalid variable type for variable '" + variableName + "': " + ex.getMessage());
        }

        Node initValueNode = element.getAttributes().getNamedItem("initialization-value");
        String initValue = null;
        if (initValueNode != null)
        {
            initValue = initValueNode.getTextContent();
        }

        configuration.addVariable(variableName, variableType, initValue);
    }

    private static void handlePluginLoaders(Configuration configuration, Element element)
    {
        String loaderName = element.getAttributes().getNamedItem("name").getTextContent();
        String className = element.getAttributes().getNamedItem("class-name").getTextContent();
        Properties properties = new Properties();
        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("init-arg"))
            {
                String name = subElement.getAttributes().getNamedItem("name").getTextContent();
                String value = subElement.getAttributes().getNamedItem("value").getTextContent();
                properties.put(name, value);
            }
        }
        configuration.addPluginLoader(loaderName, className, properties);
    }

    private static void handlePlugInEventRepresentation(Configuration configuration, Element element)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        String uri = element.getAttributes().getNamedItem("uri").getTextContent();
        String className = element.getAttributes().getNamedItem("class-name").getTextContent();
        String initializer = null;
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("initializer"))
            {
                DOMElementIterator nodeIter = new DOMElementIterator(subElement.getChildNodes());
                if (!nodeIter.hasNext())
                {
                    throw new ConfigurationException("Error handling initializer for plug-in event representation '" + uri + "', no child node found under initializer element, expecting an element node");
                }

                StringWriter output = new StringWriter();
                try
                {
                    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(nodeIter.next()), new StreamResult(output));
                }
                catch (TransformerException e)
                {
                    throw new ConfigurationException("Error handling initializer for plug-in event representation '" + uri + "' :" + e.getMessage(), e);
                }
                initializer = output.toString();
            }
        }

        URI uriParsed;
        try
        {
            uriParsed = new URI(uri);
        }
        catch (URISyntaxException ex)
        {
            throw new ConfigurationException("Error parsing URI '" + uri + "' as a valid java.net.URI string:" + ex.getMessage(), ex);
        }
        configuration.addPlugInEventRepresentation(uriParsed, className, initializer);
    }

    private static void handlePlugInEventType(Configuration configuration, Element element)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        List<URI> uris = new ArrayList<URI>();
        String name = element.getAttributes().getNamedItem("name").getTextContent();
        String initializer = null;
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("resolution-uri"))
            {
                String uriValue = subElement.getAttributes().getNamedItem("value").getTextContent();
                URI uri;
                try
                {
                    uri = new URI(uriValue);
                }
                catch (URISyntaxException ex)
                {
                    throw new ConfigurationException("Error parsing URI '" + uriValue + "' as a valid java.net.URI string:" + ex.getMessage(), ex);
                }
                uris.add(uri);
            }
            if (subElement.getNodeName().equals("initializer"))
            {
                DOMElementIterator nodeIter = new DOMElementIterator(subElement.getChildNodes());
                if (!nodeIter.hasNext())
                {
                    throw new ConfigurationException("Error handling initializer for plug-in event type '" + name + "', no child node found under initializer element, expecting an element node");
                }

                StringWriter output = new StringWriter();
                try
                {
                    TransformerFactory.newInstance().newTransformer().transform(new DOMSource(nodeIter.next()), new StreamResult(output));
                }
                catch (TransformerException e)
                {
                    throw new ConfigurationException("Error handling initializer for plug-in event type '" + name + "' :" + e.getMessage(), e);
                }
                initializer = output.toString();
            }
        }

        configuration.addPlugInEventType(name, uris.toArray(new URI[uris.size()]), initializer);
    }

    private static void handlePlugIneventTypeNameResolution(Configuration configuration, Element element)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        List<URI> uris = new ArrayList<URI>();
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("resolution-uri"))
            {
                String uriValue = subElement.getAttributes().getNamedItem("value").getTextContent();
                URI uri;
                try
                {
                    uri = new URI(uriValue);
                }
                catch (URISyntaxException ex)
                {
                    throw new ConfigurationException("Error parsing URI '" + uriValue + "' as a valid java.net.URI string:" + ex.getMessage(), ex);
                }
                uris.add(uri);
            }
        }

        configuration.setPlugInEventTypeResolutionURIs(uris.toArray(new URI[uris.size()]));
    }

    private static void handleRevisionEventType(Configuration configuration, Element element)
    {
        ConfigurationRevisionEventType revEventType = new ConfigurationRevisionEventType();
        String revTypeName = element.getAttributes().getNamedItem("name").getTextContent();

        if (element.getAttributes().getNamedItem("property-revision") != null)
        {
            String propertyRevision = element.getAttributes().getNamedItem("property-revision").getTextContent();
            ConfigurationRevisionEventType.PropertyRevision propertyRevisionEnum;
            try
            {
                propertyRevisionEnum = ConfigurationRevisionEventType.PropertyRevision.valueOf(propertyRevision.trim().toUpperCase());
                revEventType.setPropertyRevision(propertyRevisionEnum);
            }
            catch (RuntimeException ex)
            {
                throw new ConfigurationException("Invalid enumeration value for property-revision attribute '" + propertyRevision + "'");
            }
        }

        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        Set<String> keyProperties = new HashSet<String>();

        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("base-event-type"))
            {
                String name = subElement.getAttributes().getNamedItem("name").getTextContent();
                revEventType.addNameBaseEventType(name);
            }
            if (subElement.getNodeName().equals("delta-event-type"))
            {
                String name = subElement.getAttributes().getNamedItem("name").getTextContent();
                revEventType.addNameDeltaEventType(name);
            }
            if (subElement.getNodeName().equals("key-property"))
            {
                String name = subElement.getAttributes().getNamedItem("name").getTextContent();
                keyProperties.add(name);
            }
        }

        String[] keyProps = keyProperties.toArray(new String[keyProperties.size()]);
        revEventType.setKeyPropertyNames(keyProps);
        
        configuration.addRevisionEventType(revTypeName, revEventType);
    }

    private static void handleVariantStream(Configuration configuration, Element element)
    {
        ConfigurationVariantStream variantStream = new ConfigurationVariantStream();
        String varianceName = element.getAttributes().getNamedItem("name").getTextContent();

        if (element.getAttributes().getNamedItem("type-variance") != null)
        {
            String typeVar = element.getAttributes().getNamedItem("type-variance").getTextContent();
            ConfigurationVariantStream.TypeVariance typeVarianceEnum;
            try
            {
                typeVarianceEnum = ConfigurationVariantStream.TypeVariance.valueOf(typeVar.trim().toUpperCase());
                variantStream.setTypeVariance(typeVarianceEnum);
            }
            catch (RuntimeException ex)
            {
                throw new ConfigurationException("Invalid enumeration value for type-variance attribute '" + typeVar + "'");
            }
        }

        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("variant-event-type"))
            {
                String name = subElement.getAttributes().getNamedItem("name").getTextContent();
                variantStream.addEventTypeName(name);
            }
        }

        configuration.addVariantStream(varianceName, variantStream);
    }

    private static void handleEngineSettings(Configuration configuration, Element element)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("defaults"))
            {
                handleEngineSettingsDefaults(configuration, subElement);
            }
        }
    }

    private static void handleEngineSettingsDefaults(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("threading"))
            {
                handleDefaultsThreading(configuration, subElement);
            }
            if (subElement.getNodeName().equals("event-meta"))
            {
                handleDefaultsEventMeta(configuration, subElement);
            }
            if (subElement.getNodeName().equals("view-resources"))
            {
                handleDefaultsViewResources(configuration, subElement);
            }
            if (subElement.getNodeName().equals("logging"))
            {
                handleDefaultsLogging(configuration, subElement);
            }
            if (subElement.getNodeName().equals("variables"))
            {
                handleDefaultsVariables(configuration, subElement);
            }
            if (subElement.getNodeName().equals("stream-selection"))
            {
                handleDefaultsStreamSelection(configuration, subElement);
            }
            if (subElement.getNodeName().equals("time-source"))
            {
                handleDefaultsTimeSource(configuration, subElement);
            }
            if (subElement.getNodeName().equals("metrics-reporting"))
            {
                handleMetricsReporting(configuration, subElement);
            }
            if (subElement.getNodeName().equals("language"))
            {
                handleLanguage(configuration, subElement);
            }
            if (subElement.getNodeName().equals("expression"))
            {
                handleExpression(configuration, subElement);
            }
        }
    }

    private static void handleDefaultsThreading(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("listener-dispatch"))
            {
                String preserveOrderText = subElement.getAttributes().getNamedItem("preserve-order").getTextContent();
                Boolean preserveOrder = Boolean.parseBoolean(preserveOrderText);
                configuration.getEngineDefaults().getThreading().setListenerDispatchPreserveOrder(preserveOrder);

                if (subElement.getAttributes().getNamedItem("timeout-msec") != null)
                {
                    String timeoutMSecText = subElement.getAttributes().getNamedItem("timeout-msec").getTextContent();
                    Long timeoutMSec = Long.parseLong(timeoutMSecText);
                    configuration.getEngineDefaults().getThreading().setListenerDispatchTimeout(timeoutMSec);
                }

                if (subElement.getAttributes().getNamedItem("locking") != null)
                {
                    String value = subElement.getAttributes().getNamedItem("locking").getTextContent();
                    configuration.getEngineDefaults().getThreading().setListenerDispatchLocking(
                            ConfigurationEngineDefaults.Threading.Locking.valueOf(value.toUpperCase()));
                }
            }
            if (subElement.getNodeName().equals("insert-into-dispatch"))
            {
                String preserveOrderText = subElement.getAttributes().getNamedItem("preserve-order").getTextContent();
                Boolean preserveOrder = Boolean.parseBoolean(preserveOrderText);
                configuration.getEngineDefaults().getThreading().setInsertIntoDispatchPreserveOrder(preserveOrder);

                if (subElement.getAttributes().getNamedItem("timeout-msec") != null)
                {
                    String timeoutMSecText = subElement.getAttributes().getNamedItem("timeout-msec").getTextContent();
                    Long timeoutMSec = Long.parseLong(timeoutMSecText);
                    configuration.getEngineDefaults().getThreading().setInsertIntoDispatchTimeout(timeoutMSec);
                }

                if (subElement.getAttributes().getNamedItem("locking") != null)
                {
                    String value = subElement.getAttributes().getNamedItem("locking").getTextContent();
                    configuration.getEngineDefaults().getThreading().setInsertIntoDispatchLocking(
                            ConfigurationEngineDefaults.Threading.Locking.valueOf(value.toUpperCase()));
                }
            }
            if (subElement.getNodeName().equals("internal-timer"))
            {
                String enabledText = subElement.getAttributes().getNamedItem("enabled").getTextContent();
                Boolean enabled = Boolean.parseBoolean(enabledText);
                String msecResolutionText = subElement.getAttributes().getNamedItem("msec-resolution").getTextContent();
                Long msecResolution = Long.parseLong(msecResolutionText);
                configuration.getEngineDefaults().getThreading().setInternalTimerEnabled(enabled);
                configuration.getEngineDefaults().getThreading().setInternalTimerMsecResolution(msecResolution);
            }
            if (subElement.getNodeName().equals("threadpool-inbound"))
            {
                ThreadPoolConfig result = parseThreadPoolConfig(subElement);
                configuration.getEngineDefaults().getThreading().setThreadPoolInbound(result.isEnabled());
                configuration.getEngineDefaults().getThreading().setThreadPoolInboundNumThreads(result.getNumThreads());
                configuration.getEngineDefaults().getThreading().setThreadPoolInboundCapacity(result.getCapacity());
            }
            if (subElement.getNodeName().equals("threadpool-outbound"))
            {
                ThreadPoolConfig result = parseThreadPoolConfig(subElement);
                configuration.getEngineDefaults().getThreading().setThreadPoolOutbound(result.isEnabled());
                configuration.getEngineDefaults().getThreading().setThreadPoolOutboundNumThreads(result.getNumThreads());
                configuration.getEngineDefaults().getThreading().setThreadPoolOutboundCapacity(result.getCapacity());
            }
            if (subElement.getNodeName().equals("threadpool-timerexec"))
            {
                ThreadPoolConfig result = parseThreadPoolConfig(subElement);
                configuration.getEngineDefaults().getThreading().setThreadPoolTimerExec(result.isEnabled());
                configuration.getEngineDefaults().getThreading().setThreadPoolTimerExecNumThreads(result.getNumThreads());
                configuration.getEngineDefaults().getThreading().setThreadPoolTimerExecCapacity(result.getCapacity());
            }
            if (subElement.getNodeName().equals("threadpool-routeexec"))
            {
                ThreadPoolConfig result = parseThreadPoolConfig(subElement);
                configuration.getEngineDefaults().getThreading().setThreadPoolRouteExec(result.isEnabled());
                configuration.getEngineDefaults().getThreading().setThreadPoolRouteExecNumThreads(result.getNumThreads());
                configuration.getEngineDefaults().getThreading().setThreadPoolRouteExecCapacity(result.getCapacity());
            }
        }
    }

    private static ThreadPoolConfig parseThreadPoolConfig(Element parentElement)
    {
        String enabled = getRequiredAttribute(parentElement, "enabled");
        boolean isEnabled = Boolean.parseBoolean(enabled);

        String numThreadsStr = getRequiredAttribute(parentElement, "num-threads");
        int numThreads = Integer.parseInt(numThreadsStr);

        String capacityStr = getOptionalAttribute(parentElement, "capacity");
        Integer capacity = null;
        if (capacityStr != null)
        {
            capacity = Integer.parseInt(capacityStr);
        }

        return new ThreadPoolConfig(isEnabled, numThreads, capacity);
    }

    private static void handleDefaultsViewResources(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("share-views"))
            {
                String valueText = subElement.getAttributes().getNamedItem("enabled").getTextContent();
                Boolean value = Boolean.parseBoolean(valueText);
                configuration.getEngineDefaults().getViewResources().setShareViews(value);
            }
            if (subElement.getNodeName().equals("allow-multiple-expiry-policy"))
            {
                String valueText = subElement.getAttributes().getNamedItem("enabled").getTextContent();
                Boolean value = Boolean.parseBoolean(valueText);
                configuration.getEngineDefaults().getViewResources().setAllowMultipleExpiryPolicies(value);
            }
        }
    }

    private static void handleDefaultsLogging(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("execution-path"))
            {
                String valueText = subElement.getAttributes().getNamedItem("enabled").getTextContent();
                Boolean value = Boolean.parseBoolean(valueText);
                configuration.getEngineDefaults().getLogging().setEnableExecutionDebug(value);
            }
            if (subElement.getNodeName().equals("timer-debug"))
            {
                String valueText = subElement.getAttributes().getNamedItem("enabled").getTextContent();
                Boolean value = Boolean.parseBoolean(valueText);
                configuration.getEngineDefaults().getLogging().setEnableTimerDebug(value);
            }
        }
    }

    private static void handleDefaultsVariables(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("msec-version-release"))
            {
                String valueText = subElement.getAttributes().getNamedItem("value").getTextContent();
                Long value = Long.parseLong(valueText);
                configuration.getEngineDefaults().getVariables().setMsecVersionRelease(value);
            }
        }
    }

    private static void handleDefaultsStreamSelection(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("stream-selector"))
            {
                String valueText = subElement.getAttributes().getNamedItem("value").getTextContent();
                if (valueText == null)
                {
                    throw new ConfigurationException("No value attribute supplied for stream-selector element");
                }
                StreamSelector defaultSelector;
                if (valueText.toUpperCase().trim().equals("ISTREAM"))
                {
                    defaultSelector = StreamSelector.ISTREAM_ONLY;
                }
                else if (valueText.toUpperCase().trim().equals("RSTREAM"))
                {
                    defaultSelector = StreamSelector.RSTREAM_ONLY;
                }
                else if (valueText.toUpperCase().trim().equals("IRSTREAM"))
                {
                    defaultSelector = StreamSelector.RSTREAM_ISTREAM_BOTH;
                }
                else
                {
                    throw new ConfigurationException("Value attribute for stream-selector element invalid, " +
                            "expected one of the following keywords: istream, irstream, rstream");
                }
                configuration.getEngineDefaults().getStreamSelection().setDefaultStreamSelector(defaultSelector);
            }
        }
    }

    private static void handleDefaultsTimeSource(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("time-source-type"))
            {
                String valueText = subElement.getAttributes().getNamedItem("value").getTextContent();
                if (valueText == null)
                {
                    throw new ConfigurationException("No value attribute supplied for time-source element");
                }
                ConfigurationEngineDefaults.TimeSourceType timeSourceType;
                if (valueText.toUpperCase().trim().equals("NANO"))
                {
                    timeSourceType = ConfigurationEngineDefaults.TimeSourceType.NANO;
                }
                else if (valueText.toUpperCase().trim().equals("MILLI"))
                {
                    timeSourceType = ConfigurationEngineDefaults.TimeSourceType.MILLI;
                }
                else
                {
                    throw new ConfigurationException("Value attribute for time-source element invalid, " +
                            "expected one of the following keywords: nano, milli");
                }
                configuration.getEngineDefaults().getTimeSource().setTimeSourceType(timeSourceType);
            }
        }
    }

    private static void handleMetricsReporting(Configuration configuration, Element parentElement)
    {
        String enabled = getRequiredAttribute(parentElement, "enabled");
        boolean isEnabled = Boolean.parseBoolean(enabled);
        configuration.getEngineDefaults().getMetricsReporting().setEnableMetricsReporting(isEnabled);

        String engineInterval = getOptionalAttribute(parentElement, "engine-interval");
        if (engineInterval != null)
        {
            configuration.getEngineDefaults().getMetricsReporting().setEngineInterval(Long.parseLong(engineInterval));
        }

        String statementInterval = getOptionalAttribute(parentElement, "statement-interval");
        if (statementInterval != null)
        {
            configuration.getEngineDefaults().getMetricsReporting().setStatementInterval(Long.parseLong(statementInterval));
        }

        String threading = getOptionalAttribute(parentElement, "threading");
        if (threading != null)
        {
            configuration.getEngineDefaults().getMetricsReporting().setThreading(Boolean.parseBoolean(threading));
        }

        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("stmtgroup"))
            {
                String name = getRequiredAttribute(subElement, "name");
                long interval = Long.parseLong(getRequiredAttribute(subElement, "interval"));

                ConfigurationMetricsReporting.StmtGroupMetrics metrics = new ConfigurationMetricsReporting.StmtGroupMetrics();
                metrics.setInterval(interval);
                configuration.getEngineDefaults().getMetricsReporting().addStmtGroup(name, metrics);

                String defaultInclude = getOptionalAttribute(subElement, "default-include");
                if (defaultInclude != null)
                {
                    metrics.setDefaultInclude(Boolean.parseBoolean(defaultInclude));
                }
                
                String numStmts = getOptionalAttribute(subElement, "num-stmts");
                if (numStmts != null)
                {
                    metrics.setNumStatements(Integer.parseInt(numStmts));
                }

                String reportInactive = getOptionalAttribute(subElement, "report-inactive");
                if (reportInactive != null)
                {
                    metrics.setReportInactive(Boolean.parseBoolean(reportInactive));
                }

                handleMetricsReportingPatterns(metrics, subElement);
            }
        }
    }

    private static void handleLanguage(Configuration configuration, Element parentElement)
    {
        String sortUsingCollator = getOptionalAttribute(parentElement, "sort-using-collator");
        if (sortUsingCollator != null)
        {
            boolean isSortUsingCollator = Boolean.parseBoolean(sortUsingCollator);
            configuration.getEngineDefaults().getLanguage().setSortUsingCollator(isSortUsingCollator);
        }
    }

    private static void handleExpression(Configuration configuration, Element parentElement)
    {
        String integerDivision = getOptionalAttribute(parentElement, "integer-division");
        if (integerDivision != null)
        {
            boolean isIntegerDivision = Boolean.parseBoolean(integerDivision);
            configuration.getEngineDefaults().getExpression().setIntegerDivision(isIntegerDivision);
        }
        String divZero = getOptionalAttribute(parentElement, "division-by-zero-is-null");
        if (divZero != null)
        {
            boolean isDivZero = Boolean.parseBoolean(divZero);
            configuration.getEngineDefaults().getExpression().setDivisionByZeroReturnsNull(isDivZero);
        }
        String udfCache = getOptionalAttribute(parentElement, "udf-cache");
        if (udfCache != null)
        {
            boolean isUdfCache = Boolean.parseBoolean(udfCache);
            configuration.getEngineDefaults().getExpression().setUdfCache(isUdfCache);
        }
        String selfSubselectPreeval = getOptionalAttribute(parentElement, "self-subselect-preeval");
        if (selfSubselectPreeval != null)
        {
            boolean isSelfSubselectPreeval = Boolean.parseBoolean(selfSubselectPreeval);
            configuration.getEngineDefaults().getExpression().setSelfSubselectPreeval(isSelfSubselectPreeval);
        }
    }

    private static void handleMetricsReportingPatterns(ConfigurationMetricsReporting.StmtGroupMetrics groupDef, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("include-regex"))
            {
                String text = subElement.getChildNodes().item(0).getTextContent();
                groupDef.getPatterns().add(new Pair<StringPatternSet, Boolean>(new StringPatternSetRegex(text), true));
            }
            if (subElement.getNodeName().equals("exclude-regex"))
            {
                String text = subElement.getChildNodes().item(0).getTextContent();
                groupDef.getPatterns().add(new Pair<StringPatternSet, Boolean>(new StringPatternSetRegex(text), false));
            }
            if (subElement.getNodeName().equals("include-like"))
            {
                String text = subElement.getChildNodes().item(0).getTextContent();
                groupDef.getPatterns().add(new Pair<StringPatternSet, Boolean>(new StringPatternSetLike(text), true));
            }
            if (subElement.getNodeName().equals("exclude-like"))
            {
                String text = subElement.getChildNodes().item(0).getTextContent();
                groupDef.getPatterns().add(new Pair<StringPatternSet, Boolean>(new StringPatternSetLike(text), false));
            }
        }
    }

    private static void handleDefaultsEventMeta(Configuration configuration, Element parentElement)
    {
        DOMElementIterator nodeIterator = new DOMElementIterator(parentElement.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals("class-property-resolution"))
            {
                String styleText = subElement.getAttributes().getNamedItem("style").getTextContent();
                Configuration.PropertyResolutionStyle value = Configuration.PropertyResolutionStyle.valueOf(styleText.toUpperCase());
                configuration.getEngineDefaults().getEventMeta().setClassPropertyResolutionStyle(value);
            }
        }
    }

    private static Properties handleProperties(Element element, String propElementName)
    {
        Properties properties = new Properties();
        DOMElementIterator nodeIterator = new DOMElementIterator(element.getChildNodes());
        while (nodeIterator.hasNext())
        {
            Element subElement = nodeIterator.next();
            if (subElement.getNodeName().equals(propElementName))
            {
                String name = subElement.getAttributes().getNamedItem("name").getTextContent();
                String value = subElement.getAttributes().getNamedItem("value").getTextContent();
                properties.put(name, value);
            }
        }
        return properties;
    }

    /**
     * Returns an input stream from an application resource in the classpath.
     * @param resource to get input stream for
     * @return input stream for resource
     */
    protected static InputStream getResourceAsStream(String resource)
    {
        String stripped = resource.startsWith("/") ?
                resource.substring(1) : resource;

        InputStream stream = null;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        if (classLoader!=null) {
            stream = classLoader.getResourceAsStream( stripped );
        }
        if ( stream == null ) {
            ConfigurationParser.class.getResourceAsStream( resource );
        }
        if ( stream == null ) {
            stream = ConfigurationParser.class.getClassLoader().getResourceAsStream( stripped );
        }
        if ( stream == null ) {
            throw new EPException( resource + " not found" );
        }
        return stream;
    }

    private static String getOptionalAttribute(Node node, String key)
    {
        Node valueNode = node.getAttributes().getNamedItem(key);
        if (valueNode != null)
        {
            return valueNode.getTextContent();
        }
        return null;
    }

    private static String getRequiredAttribute(Node node, String key)
    {
        Node valueNode = node.getAttributes().getNamedItem(key);
        if (valueNode == null)
        {
            throw new ConfigurationException("Required attribute by name '" + key + "' not found");
        }
        return valueNode.getTextContent();
    }

    private static class ThreadPoolConfig
    {
        private boolean enabled;
        private int numThreads;
        private Integer capacity;

        public ThreadPoolConfig(boolean enabled, int numThreads, Integer capacity)
        {
            this.enabled = enabled;
            this.numThreads = numThreads;
            this.capacity = capacity;
        }

        public boolean isEnabled()
        {
            return enabled;
        }

        public int getNumThreads()
        {
            return numThreads;
        }

        public Integer getCapacity()
        {
            return capacity;
        }
    }

    private static Log log = LogFactory.getLog(ConfigurationParser.class);
}
