
package org.rifidi.edge.client.ale.wsdl.epcglobal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec;


/**
 * <p>Java class for Immediate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Immediate">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="spec" type="{urn:epcglobal:ale:xsd:1}ECSpec"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Immediate", propOrder = {
    "spec"
})
public class Immediate {

    @XmlElement(required = true)
    protected ECSpec spec;

    /**
     * Gets the value of the spec property.
     * 
     * @return
     *     possible object is
     *     {@link ECSpec }
     *     
     */
    public ECSpec getSpec() {
        return spec;
    }

    /**
     * Sets the value of the spec property.
     * 
     * @param value
     *     allowed object is
     *     {@link ECSpec }
     *     
     */
    public void setSpec(ECSpec value) {
        this.spec = value;
    }

}
