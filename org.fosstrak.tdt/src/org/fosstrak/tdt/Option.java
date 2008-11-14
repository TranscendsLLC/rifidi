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
import java.util.Enumeration;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.ContentHandler;

/**
 * Class Option.
 * 
 * @version $Revision$ $Date$
 */
public class Option implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _optionKey
     */
    private java.lang.String _optionKey;

    /**
     * Field _pattern
     */
    private java.lang.String _pattern;

    /**
     * Field _grammar
     */
    private java.lang.String _grammar;

    /**
     * Field _fieldList
     */
    private java.util.ArrayList _fieldList;


      //----------------/
     //- Constructors -/
    //----------------/

    public Option() 
     {
        super();
        _fieldList = new ArrayList();
    } //-- org.fosstrak.tdt.Option()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method addField
     * 
     * 
     * 
     * @param vField
     */
    public void addField(org.fosstrak.tdt.Field vField)
        throws java.lang.IndexOutOfBoundsException
    {
        _fieldList.add(vField);
    } //-- void addField(org.fosstrak.tdt.Field) 

    /**
     * Method addField
     * 
     * 
     * 
     * @param index
     * @param vField
     */
    public void addField(int index, org.fosstrak.tdt.Field vField)
        throws java.lang.IndexOutOfBoundsException
    {
        _fieldList.add(index, vField);
    } //-- void addField(int, org.fosstrak.tdt.Field) 

    /**
     * Method clearField
     * 
     */
    public void clearField()
    {
        _fieldList.clear();
    } //-- void clearField() 

    /**
     * Method enumerateField
     * 
     * 
     * 
     * @return Enumeration
     */
    public java.util.Enumeration enumerateField()
    {
        return new org.exolab.castor.util.IteratorEnumeration(_fieldList.iterator());
    } //-- java.util.Enumeration enumerateField() 

    /**
     * Method getField
     * 
     * 
     * 
     * @param index
     * @return Field
     */
    public org.fosstrak.tdt.Field getField(int index)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fieldList.size())) {
            throw new IndexOutOfBoundsException();
        }
        
        return (org.fosstrak.tdt.Field) _fieldList.get(index);
    } //-- org.fosstrak.tdt.Field getField(int) 

    /**
     * Method getField
     * 
     * 
     * 
     * @return Field
     */
    public org.fosstrak.tdt.Field[] getField()
    {
        int size = _fieldList.size();
        org.fosstrak.tdt.Field[] mArray = new org.fosstrak.tdt.Field[size];
        for (int index = 0; index < size; index++) {
            mArray[index] = (org.fosstrak.tdt.Field) _fieldList.get(index);
        }
        return mArray;
    } //-- org.fosstrak.tdt.Field[] getField() 

    /**
     * Method getFieldCount
     * 
     * 
     * 
     * @return int
     */
    public int getFieldCount()
    {
        return _fieldList.size();
    } //-- int getFieldCount() 

    /**
     * Returns the value of field 'grammar'.
     * 
     * @return String
     * @return the value of field 'grammar'.
     */
    public java.lang.String getGrammar()
    {
        return this._grammar;
    } //-- java.lang.String getGrammar() 

    /**
     * Returns the value of field 'optionKey'.
     * 
     * @return String
     * @return the value of field 'optionKey'.
     */
    public java.lang.String getOptionKey()
    {
        return this._optionKey;
    } //-- java.lang.String getOptionKey() 

    /**
     * Returns the value of field 'pattern'.
     * 
     * @return String
     * @return the value of field 'pattern'.
     */
    public java.lang.String getPattern()
    {
        return this._pattern;
    } //-- java.lang.String getPattern() 

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
     * Method removeField
     * 
     * 
     * 
     * @param vField
     * @return boolean
     */
    public boolean removeField(org.fosstrak.tdt.Field vField)
    {
        boolean removed = _fieldList.remove(vField);
        return removed;
    } //-- boolean removeField(org.fosstrak.tdt.Field) 

    /**
     * Method setField
     * 
     * 
     * 
     * @param index
     * @param vField
     */
    public void setField(int index, org.fosstrak.tdt.Field vField)
        throws java.lang.IndexOutOfBoundsException
    {
        //-- check bounds for index
        if ((index < 0) || (index > _fieldList.size())) {
            throw new IndexOutOfBoundsException();
        }
        _fieldList.set(index, vField);
    } //-- void setField(int, org.fosstrak.tdt.Field) 

    /**
     * Method setField
     * 
     * 
     * 
     * @param fieldArray
     */
    public void setField(org.fosstrak.tdt.Field[] fieldArray)
    {
        //-- copy array
        _fieldList.clear();
        for (int i = 0; i < fieldArray.length; i++) {
            _fieldList.add(fieldArray[i]);
        }
    } //-- void setField(org.fosstrak.tdt.Field) 

    /**
     * Sets the value of field 'grammar'.
     * 
     * @param grammar the value of field 'grammar'.
     */
    public void setGrammar(java.lang.String grammar)
    {
        this._grammar = grammar;
    } //-- void setGrammar(java.lang.String) 

    /**
     * Sets the value of field 'optionKey'.
     * 
     * @param optionKey the value of field 'optionKey'.
     */
    public void setOptionKey(java.lang.String optionKey)
    {
        this._optionKey = optionKey;
    } //-- void setOptionKey(java.lang.String) 

    /**
     * Sets the value of field 'pattern'.
     * 
     * @param pattern the value of field 'pattern'.
     */
    public void setPattern(java.lang.String pattern)
    {
        this._pattern = pattern;
    } //-- void setPattern(java.lang.String) 

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
        return (org.fosstrak.tdt.Option) Unmarshaller.unmarshal(org.fosstrak.tdt.Option.class, reader);
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
