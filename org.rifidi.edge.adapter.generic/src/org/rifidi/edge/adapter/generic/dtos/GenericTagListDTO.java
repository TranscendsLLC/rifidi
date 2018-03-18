/**
 * 
 */
package org.rifidi.edge.adapter.generic.dtos;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;


/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tags")
public class GenericTagListDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4606944400855243965L;
	
	
	@XmlElementWrapper(required = true, name="tags")
	@XmlElement(name = "tag")
	private List<GenericTagDTO> tags;

	public List<GenericTagDTO> getTags() {
		return tags;
	}

	public void setTags(List<GenericTagDTO> tags) {
		this.tags = tags;
	}
}
