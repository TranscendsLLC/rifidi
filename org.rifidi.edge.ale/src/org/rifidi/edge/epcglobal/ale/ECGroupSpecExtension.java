
package org.rifidi.edge.epcglobal.ale;

import java.util.HashMap;
import java.util.Map;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.namespace.QName;


/**
 * <p>Java class for ECGroupSpecExtension complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ECGroupSpecExtension"&gt;
 *   &lt;complexContent&gt;
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="fieldspec" type="{urn:epcglobal:ale:xsd:1}ECFieldSpec" minOccurs="0"/&gt;
 *         &lt;element name="extension" type="{urn:epcglobal:ale:xsd:1}ECGroupSpecExtension" minOccurs="0"/&gt;
 *       &lt;/sequence&gt;
 *       &lt;anyAttribute processContents='lax'/&gt;
 *     &lt;/restriction&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ECGroupSpecExtension", propOrder = {
    "fieldspec",
    "extension"
})
public class ECGroupSpecExtension {

    protected ECFieldSpec fieldspec;
    protected ECGroupSpecExtension extension;
    @XmlAnyAttribute
    private Map<QName, String> otherAttributes = new HashMap<QName, String>();

    /**
     * Gets the value of the fieldspec property.
     * 
     * @return
     *     possible object is
     *     {@link ECFieldSpec }
     *     
     */
    public ECFieldSpec getFieldspec() {
        return fieldspec;
    }

    /**
     * Sets the value of the fieldspec property.
     * 
     * @param value
     *     allowed object is
     *     {@link ECFieldSpec }
     *     
     */
    public void setFieldspec(ECFieldSpec value) {
        this.fieldspec = value;
    }

    /**
     * Gets the value of the extension property.
     * 
     * @return
     *     possible object is
     *     {@link ECGroupSpecExtension }
     *     
     */
    public ECGroupSpecExtension getExtension() {
        return extension;
    }

    /**
     * Sets the value of the extension property.
     * 
     * @param value
     *     allowed object is
     *     {@link ECGroupSpecExtension }
     *     
     */
    public void setExtension(ECGroupSpecExtension value) {
        this.extension = value;
    }

    /**
     * Gets a map that contains attributes that aren't bound to any typed property on this class.
     * 
     * <p>
     * the map is keyed by the name of the attribute and 
     * the value is the string value of the attribute.
     * 
     * the map returned by this method is live, and you can add new attribute
     * by updating the map directly. Because of this design, there's no setter.
     * 
     * 
     * @return
     *     always non-null
     */
    public Map<QName, String> getOtherAttributes() {
        return otherAttributes;
    }

}
