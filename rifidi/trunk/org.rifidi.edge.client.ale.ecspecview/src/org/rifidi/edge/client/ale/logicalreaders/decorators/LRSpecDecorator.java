
package org.rifidi.edge.client.ale.logicalreaders.decorators;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec;
import org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpecExtension;

/**
 * TODO: Class level comment.  
 * 
 * @author Jochen Mader - jochen@pramari.com
 */
public class LRSpecDecorator extends LRSpec {
	/** Name of the reader. */
	private String name;
	/** Decorated reader. */
	private LRSpec decorated;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param decorated
	 */
	public LRSpecDecorator(String name, LRSpec decorated) {
		super();
		this.name = name;
		this.decorated = decorated;
	}

	/**
	 * Get the name of the reader.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param obj
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		return decorated.equals(obj);
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#getAny()
	 */
	public List<Object> getAny() {
		return decorated.getAny();
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.alelr.xsd.epcglobal.Document#getCreationDate()
	 */
	public XMLGregorianCalendar getCreationDate() {
		return decorated.getCreationDate();
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#getExtension()
	 */
	public LRSpecExtension getExtension() {
		return decorated.getExtension();
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#getOtherAttributes()
	 */
	public Map<QName, String> getOtherAttributes() {
		return decorated.getOtherAttributes();
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#getProperties()
	 */
	public Properties getProperties() {
		return decorated.getProperties();
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#getReaders()
	 */
	public Readers getReaders() {
		return decorated.getReaders();
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.alelr.xsd.epcglobal.Document#getSchemaVersion()
	 */
	public BigDecimal getSchemaVersion() {
		return decorated.getSchemaVersion();
	}

	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return decorated.hashCode();
	}

	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#isIsComposite()
	 */
	public Boolean isIsComposite() {
		return decorated.isIsComposite();
	}

	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.alelr.xsd.epcglobal.Document#setCreationDate(javax.xml.datatype.XMLGregorianCalendar)
	 */
	public void setCreationDate(XMLGregorianCalendar value) {
		decorated.setCreationDate(value);
	}

	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#setExtension(org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpecExtension)
	 */
	public void setExtension(LRSpecExtension value) {
		decorated.setExtension(value);
	}

	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#setIsComposite(java.lang.Boolean)
	 */
	public void setIsComposite(Boolean value) {
		decorated.setIsComposite(value);
	}

	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#setProperties(org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec.Properties)
	 */
	public void setProperties(Properties value) {
		decorated.setProperties(value);
	}

	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec#setReaders(org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec.Readers)
	 */
	public void setReaders(Readers value) {
		decorated.setReaders(value);
	}

	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.alelr.xsd.epcglobal.Document#setSchemaVersion(java.math.BigDecimal)
	 */
	public void setSchemaVersion(BigDecimal value) {
		decorated.setSchemaVersion(value);
	}

	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return decorated.toString();
	}

}
