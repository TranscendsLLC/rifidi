
package org.rifidi.edge.epcglobal.ale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for Subscribe complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="Subscribe"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="specName" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *         &lt;element name="notificationURI" type="{http://www.w3.org/2001/XMLSchema}string"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "Subscribe", namespace = "urn:epcglobal:ale:wsdl:1", propOrder = {
    "specName",
    "notificationURI"
})
public class Subscribe {

    @XmlElement(required = true)
    protected String specName;
    @XmlElement(required = true)
    protected String notificationURI;

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
     * Gets the value of the notificationURI property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getNotificationURI() {
        return notificationURI;
    }

    /**
     * Sets the value of the notificationURI property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setNotificationURI(String value) {
        this.notificationURI = value;
    }

}
