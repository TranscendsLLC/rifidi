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
 * Class SchemeNameList.
 * 
 * @version $Revision$ $Date$
 */
public class SchemeNameList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The SGTIN-64 type
     */
    public static final int SGTIN_64_TYPE = 0;

    /**
     * The instance of the SGTIN-64 type
     */
    public static final SchemeNameList SGTIN_64 = new SchemeNameList(SGTIN_64_TYPE, "SGTIN-64");

    /**
     * The SGTIN-96 type
     */
    public static final int SGTIN_96_TYPE = 1;

    /**
     * The instance of the SGTIN-96 type
     */
    public static final SchemeNameList SGTIN_96 = new SchemeNameList(SGTIN_96_TYPE, "SGTIN-96");

    /**
     * The SGLN-64 type
     */
    public static final int SGLN_64_TYPE = 2;

    /**
     * The instance of the SGLN-64 type
     */
    public static final SchemeNameList SGLN_64 = new SchemeNameList(SGLN_64_TYPE, "SGLN-64");

    /**
     * The SGLN-96 type
     */
    public static final int SGLN_96_TYPE = 3;

    /**
     * The instance of the SGLN-96 type
     */
    public static final SchemeNameList SGLN_96 = new SchemeNameList(SGLN_96_TYPE, "SGLN-96");

    /**
     * The GRAI-64 type
     */
    public static final int GRAI_64_TYPE = 4;

    /**
     * The instance of the GRAI-64 type
     */
    public static final SchemeNameList GRAI_64 = new SchemeNameList(GRAI_64_TYPE, "GRAI-64");

    /**
     * The GRAI-96 type
     */
    public static final int GRAI_96_TYPE = 5;

    /**
     * The instance of the GRAI-96 type
     */
    public static final SchemeNameList GRAI_96 = new SchemeNameList(GRAI_96_TYPE, "GRAI-96");

    /**
     * The GIAI-64 type
     */
    public static final int GIAI_64_TYPE = 6;

    /**
     * The instance of the GIAI-64 type
     */
    public static final SchemeNameList GIAI_64 = new SchemeNameList(GIAI_64_TYPE, "GIAI-64");

    /**
     * The GIAI-96 type
     */
    public static final int GIAI_96_TYPE = 7;

    /**
     * The instance of the GIAI-96 type
     */
    public static final SchemeNameList GIAI_96 = new SchemeNameList(GIAI_96_TYPE, "GIAI-96");

    /**
     * The SSCC-64 type
     */
    public static final int SSCC_64_TYPE = 8;

    /**
     * The instance of the SSCC-64 type
     */
    public static final SchemeNameList SSCC_64 = new SchemeNameList(SSCC_64_TYPE, "SSCC-64");

    /**
     * The SSCC-96 type
     */
    public static final int SSCC_96_TYPE = 9;

    /**
     * The instance of the SSCC-96 type
     */
    public static final SchemeNameList SSCC_96 = new SchemeNameList(SSCC_96_TYPE, "SSCC-96");

    /**
     * The GID-96 type
     */
    public static final int GID_96_TYPE = 10;

    /**
     * The instance of the GID-96 type
     */
    public static final SchemeNameList GID_96 = new SchemeNameList(GID_96_TYPE, "GID-96");

    /**
     * The USDOD-64 type
     */
    public static final int USDOD_64_TYPE = 11;

    /**
     * The instance of the USDOD-64 type
     */
    public static final SchemeNameList USDOD_64 = new SchemeNameList(USDOD_64_TYPE, "USDOD-64");

    /**
     * The USDOD-96 type
     */
    public static final int USDOD_96_TYPE = 12;

    /**
     * The instance of the USDOD-96 type
     */
    public static final SchemeNameList USDOD_96 = new SchemeNameList(USDOD_96_TYPE, "USDOD-96");

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

    private SchemeNameList(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- org.fosstrak.tdt.types.SchemeNameList(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * SchemeNameList
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
     * Returns the type of this SchemeNameList
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
        members.put("SGTIN-64", SGTIN_64);
        members.put("SGTIN-96", SGTIN_96);
        members.put("SGLN-64", SGLN_64);
        members.put("SGLN-96", SGLN_96);
        members.put("GRAI-64", GRAI_64);
        members.put("GRAI-96", GRAI_96);
        members.put("GIAI-64", GIAI_64);
        members.put("GIAI-96", GIAI_96);
        members.put("SSCC-64", SSCC_64);
        members.put("SSCC-96", SSCC_96);
        members.put("GID-96", GID_96);
        members.put("USDOD-64", USDOD_64);
        members.put("USDOD-96", USDOD_96);
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
     * Returns the String representation of this SchemeNameList
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
     * Returns a new SchemeNameList based on the given String
     * value.
     * 
     * @param string
     * @return SchemeNameList
     */
    public static org.fosstrak.tdt.types.SchemeNameList valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid SchemeNameList";
            throw new IllegalArgumentException(err);
        }
        return (SchemeNameList) obj;
    } //-- org.fosstrak.tdt.types.SchemeNameList valueOf(java.lang.String) 

}
