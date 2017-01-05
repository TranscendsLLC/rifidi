
package org.rifidi.edge.epcglobal.alelr;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Clase Java para ImplementationException complex type.
 * 
 * <p>El siguiente fragmento de esquema especifica el contenido que se espera que haya en esta clase.
 * 
 * <pre>
 * &lt;complexType name="ImplementationException"&gt;
 *   &lt;complexContent&gt;
 *     &lt;extension base="{urn:epcglobal:alelr:wsdl:1}ALEException"&gt;
 *       &lt;sequence&gt;
 *         &lt;element name="severity" type="{urn:epcglobal:alelr:wsdl:1}ImplementationExceptionSeverity"/&gt;
 *       &lt;/sequence&gt;
 *     &lt;/extension&gt;
 *   &lt;/complexContent&gt;
 * &lt;/complexType&gt;
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ImplementationException", propOrder = {
    "severity"
})
public class ImplementationException
    extends ALEException
{

    @XmlElement(required = true)
    protected String severity;

    /**
     * Obtiene el valor de la propiedad severity.
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
     * Define el valor de la propiedad severity.
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
