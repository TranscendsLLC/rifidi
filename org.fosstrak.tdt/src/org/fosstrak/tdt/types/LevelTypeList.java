/*
 * This class was automatically generated with 
 * <a href="http://www.castor.org">Castor 0.9.7</a>, using an XML
 * Schema.
 * $Id$
 */

package org.fosstrak.tdt.types;

  //---------------------------------/
 //- Imported classes and packages -/
//---------------------------------/

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

/**
 * Class LevelTypeList.
 * 
 * @version $Revision$ $Date$
 */
public class LevelTypeList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The BINARY type
     */
    public static final int BINARY_TYPE = 0;

    /**
     * The instance of the BINARY type
     */
    public static final LevelTypeList BINARY = new LevelTypeList(BINARY_TYPE, "BINARY");

    /**
     * The TAG_ENCODING type
     */
    public static final int TAG_ENCODING_TYPE = 1;

    /**
     * The instance of the TAG_ENCODING type
     */
    public static final LevelTypeList TAG_ENCODING = new LevelTypeList(TAG_ENCODING_TYPE, "TAG_ENCODING");

    /**
     * The PURE_IDENTITY type
     */
    public static final int PURE_IDENTITY_TYPE = 2;

    /**
     * The instance of the PURE_IDENTITY type
     */
    public static final LevelTypeList PURE_IDENTITY = new LevelTypeList(PURE_IDENTITY_TYPE, "PURE_IDENTITY");

    /**
     * The LEGACY type
     */
    public static final int LEGACY_TYPE = 3;

    /**
     * The instance of the LEGACY type
     */
    public static final LevelTypeList LEGACY = new LevelTypeList(LEGACY_TYPE, "LEGACY");

    /**
     * The ONS_HOSTNAME type
     */
    public static final int ONS_HOSTNAME_TYPE = 4;

    /**
     * The instance of the ONS_HOSTNAME type
     */
    public static final LevelTypeList ONS_HOSTNAME = new LevelTypeList(ONS_HOSTNAME_TYPE, "ONS_HOSTNAME");

    /**
     * Field _memberTable
     */
    private static java.util.Hashtable _memberTable = init();

    /**
     * Field type
     */
    private int type = -1;

    /**
     * Field stringValue
     */
    private java.lang.String stringValue = null;


      //----------------/
     //- Constructors -/
    //----------------/

    private LevelTypeList(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- org.fosstrak.tdt.types.LevelTypeList(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * LevelTypeList
     * 
     * @return Enumeration
     */
    public static java.util.Enumeration enumerate()
    {
        return _memberTable.elements();
    } //-- java.util.Enumeration enumerate() 

    /**
     * Method getType
     * 
     * Returns the type of this LevelTypeList
     * 
     * @return int
     */
    public int getType()
    {
        return this.type;
    } //-- int getType() 

    /**
     * Method init
     * 
     * 
     * 
     * @return Hashtable
     */
    private static java.util.Hashtable init()
    {
        Hashtable members = new Hashtable();
        members.put("BINARY", BINARY);
        members.put("TAG_ENCODING", TAG_ENCODING);
        members.put("PURE_IDENTITY", PURE_IDENTITY);
        members.put("LEGACY", LEGACY);
        members.put("ONS_HOSTNAME", ONS_HOSTNAME);
        return members;
    } //-- java.util.Hashtable init() 

    /**
     * Method readResolve
     * 
     *  will be called during deserialization to replace the
     * deserialized object with the correct constant instance.
     * <br/>
     * 
     * @return Object
     */
    private java.lang.Object readResolve()
    {
        return valueOf(this.stringValue);
    } //-- java.lang.Object readResolve() 

    /**
     * Method toString
     * 
     * Returns the String representation of this LevelTypeList
     * 
     * @return String
     */
    public java.lang.String toString()
    {
        return this.stringValue;
    } //-- java.lang.String toString() 

    /**
     * Method valueOf
     * 
     * Returns a new LevelTypeList based on the given String value.
     * 
     * @param string
     * @return LevelTypeList
     */
    public static org.fosstrak.tdt.types.LevelTypeList valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid LevelTypeList";
            throw new IllegalArgumentException(err);
        }
        return (LevelTypeList) obj;
    } //-- org.fosstrak.tdt.types.LevelTypeList valueOf(java.lang.String) 

}
