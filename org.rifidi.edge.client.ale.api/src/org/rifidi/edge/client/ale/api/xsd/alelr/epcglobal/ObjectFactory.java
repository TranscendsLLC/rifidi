
package org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _LRProperty_QNAME = new QName("urn:epcglobal:ale:xsd:1", "LRProperty");
    private final static QName _LRSpec_QNAME = new QName("urn:epcglobal:ale:xsd:1", "LRSpec");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link LRSpec }
     * 
     */
    public LRSpec createLRSpec() {
        return new LRSpec();
    }

    /**
     * Create an instance of {@link LRSpec.Properties }
     * 
     */
    public LRSpec.Properties createLRSpecProperties() {
        return new LRSpec.Properties();
    }

    /**
     * Create an instance of {@link LRSpec.Readers }
     * 
     */
    public LRSpec.Readers createLRSpecReaders() {
        return new LRSpec.Readers();
    }

    /**
     * Create an instance of {@link LRProperty }
     * 
     */
    public LRProperty createLRProperty() {
        return new LRProperty();
    }

    /**
     * Create an instance of {@link LRSpecExtension }
     * 
     */
    public LRSpecExtension createLRSpecExtension() {
        return new LRSpecExtension();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRProperty }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:ale:xsd:1", name = "LRProperty")
    public JAXBElement<LRProperty> createLRProperty(LRProperty value) {
        return new JAXBElement<LRProperty>(_LRProperty_QNAME, LRProperty.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link LRSpec }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "urn:epcglobal:ale:xsd:1", name = "LRSpec")
    public JAXBElement<LRSpec> createLRSpec(LRSpec value) {
        return new JAXBElement<LRSpec>(_LRSpec_QNAME, LRSpec.class, null, value);
    }

}
