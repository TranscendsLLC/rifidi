
package org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for GetSubscribers complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="GetSubscribers">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="specName" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "GetSubscribers", propOrder = {
    "specName"
})
public class GetSubscribers {

    @XmlElement(required = true)
    protected String specName;

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

}
