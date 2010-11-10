/*
 * RemoteObjectDirtyEvent.java
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

/**
 * An event that gets fired when the 'dirty' state of a model object changes.
 * The ditry state changes to true when a user modifies a property of the model
 * object. It changes to false when the user commits the properties to the model
 * object
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class RemoteObjectDirtyEvent {

	/** The name of the state property */
	public static final String DIRTY_EVENT_PROPERTY = "org.rifidi.edge.client.model.sal.modelobject.dirty";
	/** The modelID */
	private String modelID;
	/** Whether or not the model is dirty */
	private boolean dirty;
	/** The hashstring for this object */
	private String hashString;

	/**
	 * Constructor.
	 * 
	 * @param modelID
	 * @param dirty
	 */
	public RemoteObjectDirtyEvent(String modelID, boolean dirty) {
		super();
		this.modelID = modelID;
		this.dirty = dirty;
		hashString = modelID + dirty;
	}

	/**
	 * Returns the modelID.
	 * 
	 * @return the modelID
	 */
	public String getModelID() {
		return modelID;
	}

	/**
	 * 
	 * @return true if the model is dirty
	 */
	public boolean isDirty() {
		return dirty;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof RemoteObjectDirtyEvent) {
			RemoteObjectDirtyEvent bean = (RemoteObjectDirtyEvent) obj;
			return this.hashString.equals(bean.hashString);
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
		return hashString.hashCode();
	}
}
