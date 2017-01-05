
package org.rifidi.edge.epcglobal.ale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Immediate complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Immediate"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="spec" type="{urn:epcglobal:ale:xsd:1}ECSpec"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Immediate", namespace = "urn:epcglobal:ale:wsdl:1", propOrder = {
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
