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
 * Class CompactionMethodList.
 * 
 * @version $Revision$ $Date$
 */
public class CompactionMethodList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The 32-bit type
     */
    public static final int VALUE_0_TYPE = 0;

    /**
     * The instance of the 32-bit type
     */
    public static final CompactionMethodList VALUE_0 = new CompactionMethodList(VALUE_0_TYPE, "32-bit");

    /**
     * The 16-bit type
     */
    public static final int VALUE_1_TYPE = 1;

    /**
     * The instance of the 16-bit type
     */
    public static final CompactionMethodList VALUE_1 = new CompactionMethodList(VALUE_1_TYPE, "16-bit");

    /**
     * The 8-bit type
     */
    public static final int VALUE_2_TYPE = 2;

    /**
     * The instance of the 8-bit type
     */
    public static final CompactionMethodList VALUE_2 = new CompactionMethodList(VALUE_2_TYPE, "8-bit");

    /**
     * The 7-bit type
     */
    public static final int VALUE_3_TYPE = 3;

    /**
     * The instance of the 7-bit type
     */
    public static final CompactionMethodList VALUE_3 = new CompactionMethodList(VALUE_3_TYPE, "7-bit");

    /**
     * The 6-bit type
     */
    public static final int VALUE_4_TYPE = 4;

    /**
     * The instance of the 6-bit type
     */
    public static final CompactionMethodList VALUE_4 = new CompactionMethodList(VALUE_4_TYPE, "6-bit");

    /**
     * The 5-bit type
     */
    public static final int VALUE_5_TYPE = 5;

    /**
     * The instance of the 5-bit type
     */
    public static final CompactionMethodList VALUE_5 = new CompactionMethodList(VALUE_5_TYPE, "5-bit");

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

    private CompactionMethodList(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- org.fosstrak.tdt.types.CompactionMethodList(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * CompactionMethodList
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
     * Returns the type of this CompactionMethodList
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
        members.put("32-bit", VALUE_0);
        members.put("16-bit", VALUE_1);
        members.put("8-bit", VALUE_2);
        members.put("7-bit", VALUE_3);
        members.put("6-bit", VALUE_4);
        members.put("5-bit", VALUE_5);
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
     * Returns the String representation of this
     * CompactionMethodList
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
     * Returns a new CompactionMethodList based on the given String
     * value.
     * 
     * @param string
     * @return CompactionMethodList
     */
    public static org.fosstrak.tdt.types.CompactionMethodList valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid CompactionMethodList";
            throw new IllegalArgumentException(err);
        }
        return (CompactionMethodList) obj;
    } //-- org.fosstrak.tdt.types.CompactionMethodList valueOf(java.lang.String) 

}
