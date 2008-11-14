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
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class GEPC64Entry.
 * 
 * @version $Revision$ $Date$
 */
public class GEPC64Entry implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _index
     */
    private int _index;

    /**
     * keeps track of state for field: _index
     */
    private boolean _has_index;

    /**
     * Field _companyPrefix
     */
    private java.lang.String _companyPrefix;


      //----------------/
     //- Constructors -/
    //----------------/

    public GEPC64Entry() 
     {
        super();
    } //-- org.fosstrak.tdt.GEPC64Entry()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteIndex
     * 
     */
    public void deleteIndex()
    {
        this._has_index= false;
    } //-- void deleteIndex() 

    /**
     * Returns the value of field 'companyPrefix'.
     * 
     * @return String
     * @return the value of field 'companyPrefix'.
     */
    public java.lang.String getCompanyPrefix()
    {
        return this._companyPrefix;
    } //-- java.lang.String getCompanyPrefix() 

    /**
     * Returns the value of field 'index'.
     * 
     * @return int
     * @return the value of field 'index'.
     */
    public int getIndex()
    {
        return this._index;
    } //-- int getIndex() 

    /**
     * Method hasIndex
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasIndex()
    {
        return this._has_index;
    } //-- boolean hasIndex() 

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
     * Sets the value of field 'companyPrefix'.
     * 
     * @param companyPrefix the value of field 'companyPrefix'.
     */
    public void setCompanyPrefix(java.lang.String companyPrefix)
    {
        this._companyPrefix = companyPrefix;
    } //-- void setCompanyPrefix(java.lang.String) 

    /**
     * Sets the value of field 'index'.
     * 
     * @param index the value of field 'index'.
     */
    public void setIndex(int index)
    {
        this._index = index;
        this._has_index = true;
    } //-- void setIndex(int) 

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
        return (org.fosstrak.tdt.GEPC64Entry) Unmarshaller.unmarshal(org.fosstrak.tdt.GEPC64Entry.class, reader);
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
