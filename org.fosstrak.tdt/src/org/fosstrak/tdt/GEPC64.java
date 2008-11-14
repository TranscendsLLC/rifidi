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
 * Class GEPC64.
 * 
 * @version $Revision$ $Date$
 */
public class GEPC64 implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _date
     */
    private java.util.Date _date;

    /**
     * Field _entryList
     */
    private java.util.ArrayList _entryList;


      //----------------/
     //- Constructors -/
    //----------------/

    public GEPC64() 
     {
        super();
        _entryList = new ArrayList();
    } //-- org.fosstrak.tdt.GEPC64()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addEntry
     * 
     * 
     * 
     * @param vEntry
     */
    public void addEntry(org.fosstrak.tdt.Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _entryList.add(vEntry);
    } //-- void addEntry(org.fosstrak.tdt.Entry) 

    /**
     * Method addEntry
     * 
     * 
     * 
     * @param index
     * @param vEntry
     */
    public void addEntry(int index, org.fosstrak.tdt.Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        _entryList.add(index, vEntry);
    } //-- void addEntry(int, org.fosstrak.tdt.Entry) 

    /**
     * Method clearEntry
     * 
     */
    public void clearEntry()
    {
        _entryList.clear();
    } //-- void clearEntry() 

    /**
     * Method enumerateEntry
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateEntry()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_entryList.iterator());
    } //-- java.util.Enumeration enumerateEntry() 

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
     * Method getEntry
     * 
     * 
     * 
     * @param index
     * @return Entry
     */
    public org.fosstrak.tdt.Entry getEntry(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _entryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.fosstrak.tdt.Entry) _entryList.get(index);
    } //-- org.fosstrak.tdt.Entry getEntry(int) 

    /**
     * Method getEntry
     * 
     * 
     * 
     * @return Entry
     */
    public org.fosstrak.tdt.Entry[] getEntry()
    {
        int size = _entryList.size();
        org.fosstrak.tdt.Entry[] mArray = new org.fosstrak.tdt.Entry[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.fosstrak.tdt.Entry) _entryList.get(index);
        }
        return mArray;
    } //-- org.fosstrak.tdt.Entry[] getEntry() 

    /**
     * Method getEntryCount
     * 
     * 
     * 
     * @return int
     */
    public int getEntryCount()
    {
        return _entryList.size();
    } //-- int getEntryCount() 

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
     * Method removeEntry
     * 
     * 
     * 
     * @param vEntry
     * @return boolean
     */
    public boolean removeEntry(org.fosstrak.tdt.Entry vEntry)
    {
        boolean removed = _entryList.remove(vEntry);
        return removed;
    } //-- boolean removeEntry(org.fosstrak.tdt.Entry) 

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
     * Method setEntry
     * 
     * 
     * 
     * @param index
     * @param vEntry
     */
    public void setEntry(int index, org.fosstrak.tdt.Entry vEntry)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _entryList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _entryList.set(index, vEntry);
    } //-- void setEntry(int, org.fosstrak.tdt.Entry) 

    /**
     * Method setEntry
     * 
     * 
     * 
     * @param entryArray
     */
    public void setEntry(org.fosstrak.tdt.Entry[] entryArray)
    {
        //-- copy array
        _entryList.clear();
        for (int i = 0; i < entryArray.length; i++) {
            _entryList.add(entryArray[i]);
        }
    } //-- void setEntry(org.fosstrak.tdt.Entry) 

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
        return (org.fosstrak.tdt.GEPC64) Unmarshaller.unmarshal(org.fosstrak.tdt.GEPC64.class, reader);
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
