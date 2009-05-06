package org.rifidi.edge.client.model.sal.properties;

import javax.management.Attribute;
//TODO: Comments
public class AttributeChangedEvent {

	/** The name of the state property */
	public static final String ATTRIBUTE_CHANGED_EVENT = "org.rifidi.edge.client.model.sal.modelobject.attribute";
	/** The modelID */
	private String modelID;
	/** The attribute to change */
	private Attribute attribute;
	private String hashstring;

	/**
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
	 * @return the modelID
	 */
	public String getModelID() {
		return modelID;
	}

	/**
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
