/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Matthew Dean - matt@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class PropertyResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2958131583863210255L;

	@XmlElementWrapper(required = true, name="attributes")
	@XmlElement(name = "entry")
	private List<PropertyNameDTO> properties;

	public List<PropertyNameDTO> getProperties() {
		return properties;
	}

	public void setProperties(List<PropertyNameDTO> properties) {
		this.properties = properties;
	}
}
