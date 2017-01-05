
package org.rifidi.edge.epcglobal.ale;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ImplementationException complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ImplementationException"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:epcglobal:ale:wsdl:1}ALEException"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="severity" type="{urn:epcglobal:ale:wsdl:1}ImplementationExceptionSeverity"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImplementationException", namespace = "urn:epcglobal:ale:wsdl:1", propOrder = {
    "severity"
})
public class ImplementationException
    extends ALEException
{

    @XmlElement(required = true)
    protected String severity;

    /**
     * Gets the value of the severity property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getSeverity() {
        return severity;
    }

    /**
     * Sets the value of the severity property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setSeverity(String value) {
        this.severity = value;
    }

}
