
package org.rifidi.edge.epcglobal.ale.api.read.ws;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;


/**
 * <p>Java class for Define complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Define">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="specName" type="{http://www.w3.org/2001/XMLSchema}string"/>
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
@XmlType(name = "Define", propOrder = {
    "specName",
    "spec"
})
public class Define {

    @XmlElement(required = true)
    protected String specName;
    @XmlElement(required = true)
    protected ECSpec spec;

    /**
     * Gets the value of the specName property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSpecName() {
        return specName;
    }

    /**
     * Sets the value of the specName property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSpecName(String value) {
        this.specName = value;
    }

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
