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
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class BundleResponseMessageDTO implements Serializable {
	
	private static final long serialVersionUID = -2374503365147756524L;
	
	@XmlElementWrapper(required = true, name="bundles")
	@XmlElement(name = "bundles")
	private List<BundleDTO> bundles;

	public List<BundleDTO> getBundles() {
		return bundles;
	}

	public void setBundles(List<BundleDTO> bundles) {
		this.bundles = bundles;
	}

	
}
