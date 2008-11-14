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
import org.fosstrak.tdt.types.InputFormatList;
import org.fosstrak.tdt.types.ModeList;
import org.fosstrak.tdt.types.PadDirectionList;
import org.xml.sax.ContentHandler;

/**
 * Class Rule.
 * 
 * @version $Revision$ $Date$
 */
public class Rule implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _type
     */
    private org.fosstrak.tdt.types.ModeList _type;

    /**
     * Field _inputFormat
     */
    private org.fosstrak.tdt.types.InputFormatList _inputFormat;

    /**
     * Field _seq
     */
    private int _seq;

    /**
     * keeps track of state for field: _seq
     */
    private boolean _has_seq;

    /**
     * Field _newFieldName
     */
    private java.lang.String _newFieldName;

    /**
     * Field _characterSet
     */
    private java.lang.String _characterSet;

    /**
     * Field _padChar
     */
    private java.lang.String _padChar;

    /**
     * Field _padDir
     */
    private org.fosstrak.tdt.types.PadDirectionList _padDir;

    /**
     * Field _decimalMinimum
     */
    private java.lang.String _decimalMinimum;

    /**
     * Field _decimalMaximum
     */
    private java.lang.String _decimalMaximum;

    /**
     * Field _length
     */
    private java.lang.String _length;

    /**
     * Field _function
     */
    private java.lang.String _function;

    /**
     * Field _tableURL
     */
    private java.lang.String _tableURL;

    /**
     * Field _tableParams
     */
    private java.lang.String _tableParams;

    /**
     * Field _tableXPath
     */
    private java.lang.String _tableXPath;

    /**
     * Field _tableSQL
     */
    private java.lang.String _tableSQL;


      //----------------/
     //- Constructors -/
    //----------------/

    public Rule() 
     {
        super();
    } //-- org.fosstrak.tdt.Rule()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteSeq
     * 
     */
    public void deleteSeq()
    {
        this._has_seq= false;
    } //-- void deleteSeq() 

    /**
     * Returns the value of field 'characterSet'.
     * 
     * @return String
     * @return the value of field 'characterSet'.
     */
    public java.lang.String getCharacterSet()
    {
        return this._characterSet;
    } //-- java.lang.String getCharacterSet() 

    /**
     * Returns the value of field 'decimalMaximum'.
     * 
     * @return String
     * @return the value of field 'decimalMaximum'.
     */
    public java.lang.String getDecimalMaximum()
    {
        return this._decimalMaximum;
    } //-- java.lang.String getDecimalMaximum() 

    /**
     * Returns the value of field 'decimalMinimum'.
     * 
     * @return String
     * @return the value of field 'decimalMinimum'.
     */
    public java.lang.String getDecimalMinimum()
    {
        return this._decimalMinimum;
    } //-- java.lang.String getDecimalMinimum() 

    /**
     * Returns the value of field 'function'.
     * 
     * @return String
     * @return the value of field 'function'.
     */
    public java.lang.String getFunction()
    {
        return this._function;
    } //-- java.lang.String getFunction() 

    /**
     * Returns the value of field 'inputFormat'.
     * 
     * @return InputFormatList
     * @return the value of field 'inputFormat'.
     */
    public org.fosstrak.tdt.types.InputFormatList getInputFormat()
    {
        return this._inputFormat;
    } //-- org.fosstrak.tdt.types.InputFormatList getInputFormat() 

    /**
     * Returns the value of field 'length'.
     * 
     * @return String
     * @return the value of field 'length'.
     */
    public java.lang.String getLength()
    {
        return this._length;
    } //-- java.lang.String getLength() 

    /**
     * Returns the value of field 'newFieldName'.
     * 
     * @return String
     * @return the value of field 'newFieldName'.
     */
    public java.lang.String getNewFieldName()
    {
        return this._newFieldName;
    } //-- java.lang.String getNewFieldName() 

    /**
     * Returns the value of field 'padChar'.
     * 
     * @return String
     * @return the value of field 'padChar'.
     */
    public java.lang.String getPadChar()
    {
        return this._padChar;
    } //-- java.lang.String getPadChar() 

    /**
     * Returns the value of field 'padDir'.
     * 
     * @return PadDirectionList
     * @return the value of field 'padDir'.
     */
    public org.fosstrak.tdt.types.PadDirectionList getPadDir()
    {
        return this._padDir;
    } //-- org.fosstrak.tdt.types.PadDirectionList getPadDir() 

    /**
     * Returns the value of field 'seq'.
     * 
     * @return int
     * @return the value of field 'seq'.
     */
    public int getSeq()
    {
        return this._seq;
    } //-- int getSeq() 

    /**
     * Returns the value of field 'tableParams'.
     * 
     * @return String
     * @return the value of field 'tableParams'.
     */
    public java.lang.String getTableParams()
    {
        return this._tableParams;
    } //-- java.lang.String getTableParams() 

    /**
     * Returns the value of field 'tableSQL'.
     * 
     * @return String
     * @return the value of field 'tableSQL'.
     */
    public java.lang.String getTableSQL()
    {
        return this._tableSQL;
    } //-- java.lang.String getTableSQL() 

    /**
     * Returns the value of field 'tableURL'.
     * 
     * @return String
     * @return the value of field 'tableURL'.
     */
    public java.lang.String getTableURL()
    {
        return this._tableURL;
    } //-- java.lang.String getTableURL() 

    /**
     * Returns the value of field 'tableXPath'.
     * 
     * @return String
     * @return the value of field 'tableXPath'.
     */
    public java.lang.String getTableXPath()
    {
        return this._tableXPath;
    } //-- java.lang.String getTableXPath() 

    /**
     * Returns the value of field 'type'.
     * 
     * @return ModeList
     * @return the value of field 'type'.
     */
    public org.fosstrak.tdt.types.ModeList getType()
    {
        return this._type;
    } //-- org.fosstrak.tdt.types.ModeList getType() 

    /**
     * Method hasSeq
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasSeq()
    {
        return this._has_seq;
    } //-- boolean hasSeq() 

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
     * Sets the value of field 'characterSet'.
     * 
     * @param characterSet the value of field 'characterSet'.
     */
    public void setCharacterSet(java.lang.String characterSet)
    {
        this._characterSet = characterSet;
    } //-- void setCharacterSet(java.lang.String) 

    /**
     * Sets the value of field 'decimalMaximum'.
     * 
     * @param decimalMaximum the value of field 'decimalMaximum'.
     */
    public void setDecimalMaximum(java.lang.String decimalMaximum)
    {
        this._decimalMaximum = decimalMaximum;
    } //-- void setDecimalMaximum(java.lang.String) 

    /**
     * Sets the value of field 'decimalMinimum'.
     * 
     * @param decimalMinimum the value of field 'decimalMinimum'.
     */
    public void setDecimalMinimum(java.lang.String decimalMinimum)
    {
        this._decimalMinimum = decimalMinimum;
    } //-- void setDecimalMinimum(java.lang.String) 

    /**
     * Sets the value of field 'function'.
     * 
     * @param function the value of field 'function'.
     */
    public void setFunction(java.lang.String function)
    {
        this._function = function;
    } //-- void setFunction(java.lang.String) 

    /**
     * Sets the value of field 'inputFormat'.
     * 
     * @param inputFormat the value of field 'inputFormat'.
     */
    public void setInputFormat(org.fosstrak.tdt.types.InputFormatList inputFormat)
    {
        this._inputFormat = inputFormat;
    } //-- void setInputFormat(org.fosstrak.tdt.types.InputFormatList) 

    /**
     * Sets the value of field 'length'.
     * 
     * @param length the value of field 'length'.
     */
    public void setLength(java.lang.String length)
    {
        this._length = length;
    } //-- void setLength(java.lang.String) 

    /**
     * Sets the value of field 'newFieldName'.
     * 
     * @param newFieldName the value of field 'newFieldName'.
     */
    public void setNewFieldName(java.lang.String newFieldName)
    {
        this._newFieldName = newFieldName;
    } //-- void setNewFieldName(java.lang.String) 

    /**
     * Sets the value of field 'padChar'.
     * 
     * @param padChar the value of field 'padChar'.
     */
    public void setPadChar(java.lang.String padChar)
    {
        this._padChar = padChar;
    } //-- void setPadChar(java.lang.String) 

    /**
     * Sets the value of field 'padDir'.
     * 
     * @param padDir the value of field 'padDir'.
     */
    public void setPadDir(org.fosstrak.tdt.types.PadDirectionList padDir)
    {
        this._padDir = padDir;
    } //-- void setPadDir(org.fosstrak.tdt.types.PadDirectionList) 

    /**
     * Sets the value of field 'seq'.
     * 
     * @param seq the value of field 'seq'.
     */
    public void setSeq(int seq)
    {
        this._seq = seq;
        this._has_seq = true;
    } //-- void setSeq(int) 

    /**
     * Sets the value of field 'tableParams'.
     * 
     * @param tableParams the value of field 'tableParams'.
     */
    public void setTableParams(java.lang.String tableParams)
    {
        this._tableParams = tableParams;
    } //-- void setTableParams(java.lang.String) 

    /**
     * Sets the value of field 'tableSQL'.
     * 
     * @param tableSQL the value of field 'tableSQL'.
     */
    public void setTableSQL(java.lang.String tableSQL)
    {
        this._tableSQL = tableSQL;
    } //-- void setTableSQL(java.lang.String) 

    /**
     * Sets the value of field 'tableURL'.
     * 
     * @param tableURL the value of field 'tableURL'.
     */
    public void setTableURL(java.lang.String tableURL)
    {
        this._tableURL = tableURL;
    } //-- void setTableURL(java.lang.String) 

    /**
     * Sets the value of field 'tableXPath'.
     * 
     * @param tableXPath the value of field 'tableXPath'.
     */
    public void setTableXPath(java.lang.String tableXPath)
    {
        this._tableXPath = tableXPath;
    } //-- void setTableXPath(java.lang.String) 

    /**
     * Sets the value of field 'type'.
     * 
     * @param type the value of field 'type'.
     */
    public void setType(org.fosstrak.tdt.types.ModeList type)
    {
        this._type = type;
    } //-- void setType(org.fosstrak.tdt.types.ModeList) 

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
        return (org.fosstrak.tdt.Rule) Unmarshaller.unmarshal(org.fosstrak.tdt.Rule.class, reader);
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
