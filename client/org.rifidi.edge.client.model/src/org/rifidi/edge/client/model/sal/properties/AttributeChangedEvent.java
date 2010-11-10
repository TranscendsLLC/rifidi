/*
 * AttributeChangedEvent.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.model.sal.properties;

import javax.management.Attribute;

/**
 * An event that triggers when an attribute changes.  
 * 
 * @author jochen
 */
public class AttributeChangedEvent {

	/** The name of the state property */
	public static final String ATTRIBUTE_CHANGED_EVENT = "org.rifidi.edge.client.model.sal.modelobject.attribute";
	/** The modelID */
	private String modelID;
	/** The attribute to change */
	private Attribute attribute;
	private String hashstring;

	/**
	 * 
	 * 
	 * @param modelID
	 * @param attribute
	 */
	public AttributeChangedEvent(String modelID, Attribute attribute) {
		super();
		this.modelID = modelID;
		this.attribute = attribute;
		if (attribute != null) {
			this.hashstring = ATTRIBUTE_CHANGED_EVENT + "." + modelID + "."
					+ attribute.getName() + "." + attribute.getValue();
		} else {
			this.hashstring = ATTRIBUTE_CHANGED_EVENT + "." + modelID + "."
					+ attribute;
		}
	}

	/**
	 * Returns the Model ID.  
	 * 
	 * @return the modelID
	 */
	public String getModelID() {
		return modelID;
	}

	/**
	 * Returns the attribute in question.  
	 * 
	 * @return the attribute
	 */
	public Attribute getAttribute() {
		return attribute;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof AttributeChangedEvent) {
			AttributeChangedEvent bean = (AttributeChangedEvent) obj;
			return this.hashstring.equals(bean.hashstring);
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return hashstring.hashCode();
	}

}
