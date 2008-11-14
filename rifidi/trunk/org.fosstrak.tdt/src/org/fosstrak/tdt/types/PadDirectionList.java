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
 * Class PadDirectionList.
 * 
 * @version $Revision$ $Date$
 */
public class PadDirectionList implements java.io.Serializable {


      //--------------------------/
     //- Class/Member Variables -/
    //--------------------------/

    /**
     * The LEFT type
     */
    public static final int LEFT_TYPE = 0;

    /**
     * The instance of the LEFT type
     */
    public static final PadDirectionList LEFT = new PadDirectionList(LEFT_TYPE, "LEFT");

    /**
     * The RIGHT type
     */
    public static final int RIGHT_TYPE = 1;

    /**
     * The instance of the RIGHT type
     */
    public static final PadDirectionList RIGHT = new PadDirectionList(RIGHT_TYPE, "RIGHT");

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

    private PadDirectionList(int type, java.lang.String value) 
     {
        super();
        this.type = type;
        this.stringValue = value;
    } //-- org.fosstrak.tdt.types.PadDirectionList(int, java.lang.String)


      //-----------/
     //- Methods -/
    //-----------/

    /**
     * Method enumerate
     * 
     * Returns an enumeration of all possible instances of
     * PadDirectionList
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
     * Returns the type of this PadDirectionList
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
        members.put("LEFT", LEFT);
        members.put("RIGHT", RIGHT);
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
     * Returns the String representation of this PadDirectionList
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
     * Returns a new PadDirectionList based on the given String
     * value.
     * 
     * @param string
     * @return PadDirectionList
     */
    public static org.fosstrak.tdt.types.PadDirectionList valueOf(java.lang.String string)
    {
        java.lang.Object obj = null;
        if (string != null) obj = _memberTable.get(string);
        if (obj == null) {
            String err = "'" + string + "' is not a valid PadDirectionList";
            throw new IllegalArgumentException(err);
        }
        return (PadDirectionList) obj;
    } //-- org.fosstrak.tdt.types.PadDirectionList valueOf(java.lang.String) 

}
