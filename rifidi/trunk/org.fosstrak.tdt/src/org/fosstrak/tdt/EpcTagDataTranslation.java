/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package org.fosstrak.tdt;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Date;
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class EpcTagDataTranslation.
 * 
 * @version $Revision$ $Date$
 */
public class EpcTagDataTranslation implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _version
     */
    private java.lang.String _version;

    /**
     * Field _date
     */
    private java.util.Date _date;

    /**
     * Field _epcTDSVersion
     */
    private java.lang.String _epcTDSVersion;

    /**
     * Field _schemeList
     */
    private java.util.ArrayList _schemeList;


      //----------------/
     //- Constructors -/
    //----------------/

    public EpcTagDataTranslation() 
     {
        super();
        _schemeList = new ArrayList();
    } //-- org.fosstrak.tdt.EpcTagDataTranslation()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addScheme
     * 
     * 
     * 
     * @param vScheme
     */
    public void addScheme(org.fosstrak.tdt.Scheme vScheme)
        throws java.lang.IndexOutOfBoundsException
    {
        _schemeList.add(vScheme);
    } //-- void addScheme(org.fosstrak.tdt.Scheme) 

    /**
     * Method addScheme
     * 
     * 
     * 
     * @param index
     * @param vScheme
     */
    public void addScheme(int index, org.fosstrak.tdt.Scheme vScheme)
        throws java.lang.IndexOutOfBoundsException
    {
        _schemeList.add(index, vScheme);
    } //-- void addScheme(int, org.fosstrak.tdt.Scheme) 

    /**
     * Method clearScheme
     * 
     */
    public void clearScheme()
    {
        _schemeList.clear();
    } //-- void clearScheme() 

    /**
     * Method enumerateScheme
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateScheme()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_schemeList.iterator());
    } //-- java.util.Enumeration enumerateScheme() 

    /**
     * Returns the value of field 'date'.
     * 
     * @return Date
     * @return the value of field 'date'.
     */
    public java.util.Date getDate()
    {
        return this._date;
    } //-- java.util.Date getDate() 

    /**
     * Returns the value of field 'epcTDSVersion'.
     * 
     * @return String
     * @return the value of field 'epcTDSVersion'.
     */
    public java.lang.String getEpcTDSVersion()
    {
        return this._epcTDSVersion;
    } //-- java.lang.String getEpcTDSVersion() 

    /**
     * Method getScheme
     * 
     * 
     * 
     * @param index
     * @return Scheme
     */
    public org.fosstrak.tdt.Scheme getScheme(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _schemeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.fosstrak.tdt.Scheme) _schemeList.get(index);
    } //-- org.fosstrak.tdt.Scheme getScheme(int) 

    /**
     * Method getScheme
     * 
     * 
     * 
     * @return Scheme
     */
    public org.fosstrak.tdt.Scheme[] getScheme()
    {
        int size = _schemeList.size();
        org.fosstrak.tdt.Scheme[] mArray = new org.fosstrak.tdt.Scheme[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.fosstrak.tdt.Scheme) _schemeList.get(index);
        }
        return mArray;
    } //-- org.fosstrak.tdt.Scheme[] getScheme() 

    /**
     * Method getSchemeCount
     * 
     * 
     * 
     * @return int
     */
    public int getSchemeCount()
    {
        return _schemeList.size();
    } //-- int getSchemeCount() 

    /**
     * Returns the value of field 'version'.
     * 
     * @return String
     * @return the value of field 'version'.
     */
    public java.lang.String getVersion()
    {
        return this._version;
    } //-- java.lang.String getVersion() 

    /**
     * Method isValid
     * 
     * 
     * 
     * @return boolean
     */
    public boolean isValid()
    {
        try {
            validate();
        }
        catch (org.exolab.castor.xml.ValidationException vex) {
            return false;
        }
        return true;
    } //-- boolean isValid() 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param out
     */
    public void marshal(java.io.Writer out)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, out);
    } //-- void marshal(java.io.Writer) 

    /**
     * Method marshal
     * 
     * 
     * 
     * @param handler
     */
    public void marshal(org.xml.sax.ContentHandler handler)
        throws java.io.IOException, org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        
        Marshaller.marshal(this, handler);
    } //-- void marshal(org.xml.sax.ContentHandler) 

    /**
     * Method removeScheme
     * 
     * 
     * 
     * @param vScheme
     * @return boolean
     */
    public boolean removeScheme(org.fosstrak.tdt.Scheme vScheme)
    {
        boolean removed = _schemeList.remove(vScheme);
        return removed;
    } //-- boolean removeScheme(org.fosstrak.tdt.Scheme) 

    /**
     * Sets the value of field 'date'.
     * 
     * @param date the value of field 'date'.
     */
    public void setDate(java.util.Date date)
    {
        this._date = date;
    } //-- void setDate(java.util.Date) 

    /**
     * Sets the value of field 'epcTDSVersion'.
     * 
     * @param epcTDSVersion the value of field 'epcTDSVersion'.
     */
    public void setEpcTDSVersion(java.lang.String epcTDSVersion)
    {
        this._epcTDSVersion = epcTDSVersion;
    } //-- void setEpcTDSVersion(java.lang.String) 

    /**
     * Method setScheme
     * 
     * 
     * 
     * @param index
     * @param vScheme
     */
    public void setScheme(int index, org.fosstrak.tdt.Scheme vScheme)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _schemeList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _schemeList.set(index, vScheme);
    } //-- void setScheme(int, org.fosstrak.tdt.Scheme) 

    /**
     * Method setScheme
     * 
     * 
     * 
     * @param schemeArray
     */
    public void setScheme(org.fosstrak.tdt.Scheme[] schemeArray)
    {
        //-- copy array
        _schemeList.clear();
        for (int i = 0; i < schemeArray.length; i++) {
            _schemeList.add(schemeArray[i]);
        }
    } //-- void setScheme(org.fosstrak.tdt.Scheme) 

    /**
     * Sets the value of field 'version'.
     * 
     * @param version the value of field 'version'.
     */
    public void setVersion(java.lang.String version)
    {
        this._version = version;
    } //-- void setVersion(java.lang.String) 

    /**
     * Method unmarshal
     * 
     * 
     * 
     * @param reader
     * @return Object
     */
    public static java.lang.Object unmarshal(java.io.Reader reader)
        throws org.exolab.castor.xml.MarshalException, org.exolab.castor.xml.ValidationException
    {
        return (org.fosstrak.tdt.EpcTagDataTranslation) Unmarshaller.unmarshal(org.fosstrak.tdt.EpcTagDataTranslation.class, reader);
    } //-- java.lang.Object unmarshal(java.io.Reader) 

    /**
     * Method validate
     * 
     */
    public void validate()
        throws org.exolab.castor.xml.ValidationException
    {
        org.exolab.castor.xml.Validator validator = new org.exolab.castor.xml.Validator();
        validator.validate(this);
    } //-- void validate() 

}
