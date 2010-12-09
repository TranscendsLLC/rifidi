/*
 * 
 * ECTagStat.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */

package org.rifidi.edge.ale.api.read.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for ECTagStat complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ECTagStat">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="profile" type="{urn:epcglobal:ale:xsd:1}ECStatProfileName"/>
 *         &lt;element name="statBlocks" minOccurs="0">
 *           &lt;complexType>
 *             &lt;complexContent>
 *               &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *                 &lt;sequence>
 *                   &lt;element name="statBlock" type="{urn:epcglobal:ale:xsd:1}ECReaderStat" maxOccurs="unbounded" minOccurs="0"/>
 *                 &lt;/sequence>
 *               &lt;/restriction>
 *             &lt;/complexContent>
 *           &lt;/complexType>
 *         &lt;/element>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ECTagStat", propOrder = {
    "profile",
    "statBlocks"
})
@XmlSeeAlso({
    ECTagTimestampStat.class
})
public class ECTagStat {

    @XmlElement(required = true)
    protected String profile;
    protected ECTagStat.StatBlocks statBlocks;

    /**
     * Gets the value of the profile property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getProfile() {
        return profile;
    }

    /**
     * Sets the value of the profile property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setProfile(String value) {
        this.profile = value;
    }

    /**
     * Gets the value of the statBlocks property.
     * 
     * @return
     *     possible object is
     *     {@link ECTagStat.StatBlocks }
     *     
     */
    public ECTagStat.StatBlocks getStatBlocks() {
        return statBlocks;
    }

    /**
     * Sets the value of the statBlocks property.
     * 
     * @param value
     *     allowed object is
     *     {@link ECTagStat.StatBlocks }
     *     
     */
    public void setStatBlocks(ECTagStat.StatBlocks value) {
        this.statBlocks = value;
    }


    /**
     * <p>Java class for anonymous complex type.
     * 
     * <p>The following schema fragment specifies the expected content contained within this class.
     * 
     * <pre>
     * &lt;complexType>
     *   &lt;complexContent>
     *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
     *       &lt;sequence>
     *         &lt;element name="statBlock" type="{urn:epcglobal:ale:xsd:1}ECReaderStat" maxOccurs="unbounded" minOccurs="0"/>
     *       &lt;/sequence>
     *     &lt;/restriction>
     *   &lt;/complexContent>
     * &lt;/complexType>
     * </pre>
     * 
     * 
     */
    @XmlAccessorType(XmlAccessType.FIELD)
    @XmlType(name = "", propOrder = {
        "statBlock"
    })
    public static class StatBlocks {

        protected List<ECReaderStat> statBlock;

        /**
         * Gets the value of the statBlock property.
         * 
         * <p>
         * This accessor method returns a reference to the live list,
         * not a snapshot. Therefore any modification you make to the
         * returned list will be present inside the JAXB object.
         * This is why there is not a <CODE>set</CODE> method for the statBlock property.
         * 
         * <p>
         * For example, to add a new item, do as follows:
         * <pre>
         *    getStatBlock().add(newItem);
         * </pre>
         * 
         * 
         * <p>
         * Objects of the following type(s) are allowed in the list
         * {@link ECReaderStat }
         * 
         * 
         */
        public List<ECReaderStat> getStatBlock() {
            if (statBlock == null) {
                statBlock = new ArrayList<ECReaderStat>();
            }
            return this.statBlock;
        }

    }

}
