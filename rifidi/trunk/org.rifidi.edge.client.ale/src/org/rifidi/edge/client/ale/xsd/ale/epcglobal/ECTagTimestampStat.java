
package org.rifidi.edge.client.ale.xsd.ale.epcglobal;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.datatype.XMLGregorianCalendar;


/**
 * <p>Java class for ECTagTimestampStat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ECTagTimestampStat">
 *   &lt;complexContent>
 *     &lt;extension base="{urn:epcglobal:ale:xsd:1}ECTagStat">
 *       &lt;sequence>
 *         &lt;element name="firstSightingTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *         &lt;element name="lastSightingTime" type="{http://www.w3.org/2001/XMLSchema}dateTime"/>
 *       &lt;/sequence>
 *     &lt;/extension>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ECTagTimestampStat", propOrder = {
    "firstSightingTime",
    "lastSightingTime"
})
public class ECTagTimestampStat
    extends ECTagStat
{

    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar firstSightingTime;
    @XmlElement(required = true)
    @XmlSchemaType(name = "dateTime")
    protected XMLGregorianCalendar lastSightingTime;

    /**
     * Gets the value of the firstSightingTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getFirstSightingTime() {
        return firstSightingTime;
    }

    /**
     * Sets the value of the firstSightingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setFirstSightingTime(XMLGregorianCalendar value) {
        this.firstSightingTime = value;
    }

    /**
     * Gets the value of the lastSightingTime property.
     * 
     * @return
     *     possible object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public XMLGregorianCalendar getLastSightingTime() {
        return lastSightingTime;
    }

    /**
     * Sets the value of the lastSightingTime property.
     * 
     * @param value
     *     allowed object is
     *     {@link XMLGregorianCalendar }
     *     
     */
    public void setLastSightingTime(XMLGregorianCalendar value) {
        this.lastSightingTime = value;
    }

}
