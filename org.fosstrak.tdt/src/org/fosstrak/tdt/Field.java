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
import org.fosstrak.tdt.types.CompactionMethodList;
import org.fosstrak.tdt.types.PadDirectionList;
import org.xml.sax.ContentHandler;

/**
 * Class Field.
 * 
 * @version $Revision$ $Date$
 */
public class Field implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * Field _seq
     */
    private int _seq;

    /**
     * keeps track of state for field: _seq
     */
    private boolean _has_seq;

    /**
     * Field _name
     */
    private java.lang.String _name;

    /**
     * Field _bitLength
     */
    private int _bitLength;

    /**
     * keeps track of state for field: _bitLength
     */
    private boolean _has_bitLength;

    /**
     * Field _characterSet
     */
    private java.lang.String _characterSet;

    /**
     * Field _compaction
     */
    private org.fosstrak.tdt.types.CompactionMethodList _compaction;

    /**
     * Field _compression
     */
    private java.lang.String _compression;

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
    private int _length;

    /**
     * keeps track of state for field: _length
     */
    private boolean _has_length;


      //----------------/
     //- Constructors -/
    //----------------/

    public Field() 
     {
        super();
    } //-- org.fosstrak.tdt.Field()


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method deleteBitLength
     * 
     */
    public void deleteBitLength()
    {
        this._has_bitLength= false;
    } //-- void deleteBitLength() 

    /**
     * Method deleteLength
     * 
     */
    public void deleteLength()
    {
        this._has_length= false;
    } //-- void deleteLength() 

    /**
     * Method deleteSeq
     * 
     */
    public void deleteSeq()
    {
        this._has_seq= false;
    } //-- void deleteSeq() 

    /**
     * Returns the value of field 'bitLength'.
     * 
     * @return int
     * @return the value of field 'bitLength'.
     */
    public int getBitLength()
    {
        return this._bitLength;
    } //-- int getBitLength() 

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
     * Returns the value of field 'compaction'.
     * 
     * @return CompactionMethodList
     * @return the value of field 'compaction'.
     */
    public org.fosstrak.tdt.types.CompactionMethodList getCompaction()
    {
        return this._compaction;
    } //-- org.fosstrak.tdt.types.CompactionMethodList getCompaction() 

    /**
     * Returns the value of field 'compression'.
     * 
     * @return String
     * @return the value of field 'compression'.
     */
    public java.lang.String getCompression()
    {
        return this._compression;
    } //-- java.lang.String getCompression() 

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
     * Returns the value of field 'length'.
     * 
     * @return int
     * @return the value of field 'length'.
     */
    public int getLength()
    {
        return this._length;
    } //-- int getLength() 

    /**
     * Returns the value of field 'name'.
     * 
     * @return String
     * @return the value of field 'name'.
     */
    public java.lang.String getName()
    {
        return this._name;
    } //-- java.lang.String getName() 

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
     * Method hasBitLength
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasBitLength()
    {
        return this._has_bitLength;
    } //-- boolean hasBitLength() 

    /**
     * Method hasLength
     * 
     * 
     * 
     * @return boolean
     */
    public boolean hasLength()
    {
        return this._has_length;
    } //-- boolean hasLength() 

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
     * Sets the value of field 'bitLength'.
     * 
     * @param bitLength the value of field 'bitLength'.
     */
    public void setBitLength(int bitLength)
    {
        this._bitLength = bitLength;
        this._has_bitLength = true;
    } //-- void setBitLength(int) 

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
     * Sets the value of field 'compaction'.
     * 
     * @param compaction the value of field 'compaction'.
     */
    public void setCompaction(org.fosstrak.tdt.types.CompactionMethodList compaction)
    {
        this._compaction = compaction;
    } //-- void setCompaction(org.fosstrak.tdt.types.CompactionMethodList) 

    /**
     * Sets the value of field 'compression'.
     * 
     * @param compression the value of field 'compression'.
     */
    public void setCompression(java.lang.String compression)
    {
        this._compression = compression;
    } //-- void setCompression(java.lang.String) 

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
     * Sets the value of field 'length'.
     * 
     * @param length the value of field 'length'.
     */
    public void setLength(int length)
    {
        this._length = length;
        this._has_length = true;
    } //-- void setLength(int) 

    /**
     * Sets the value of field 'name'.
     * 
     * @param name the value of field 'name'.
     */
    public void setName(java.lang.String name)
    {
        this._name = name;
    } //-- void setName(java.lang.String) 

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
        return (org.fosstrak.tdt.Field) Unmarshaller.unmarshal(org.fosstrak.tdt.Field.class, reader);
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
