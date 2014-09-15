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
@XmlRootElement(name = "Response")
public class AppResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2588852972212099383L;

	@XmlElementWrapper(required = true, name="Apps")
	@XmlElement(name = "App")
	private List<AppNameDTO> apps;

	public List<AppNameDTO> getApps() {
		return apps;
	}

	public void setApps(List<AppNameDTO> apps) {
		this.apps = apps;
	}
	
	
}
