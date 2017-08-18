/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tag")
public class BundleDTO implements Serializable {

	private static final long serialVersionUID = 933098455362041420L;

	@XmlElement(name = "name")
	private String name;

	@XmlElement(name = "id")
	private Long id;
	

	@XmlElement(name = "state")
	private String state;


	public String getName() {
		return name;
	}


	public void setName(String name) {
		this.name = name;
	}


	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getState() {
		return state;
	}


	public void setState(String state) {
		this.state = state;
	}
	
	
}
