package org.rifidi.edge.rest;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */

/**
 * @author matt
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tag")
public class CurrentTagDTO implements Serializable {
	
	private static final long serialVersionUID = -3035982716629339545L;

	//The ID of the tag
	@XmlElement(name = "id")
	private String id;
	
	//The antenna of the tag
	@XmlElement(name = "antenna")
	private Integer antenna;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAntenna() {
		return antenna;
	}

	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}
	
}
