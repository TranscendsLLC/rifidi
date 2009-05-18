
package org.rifidi.edge.client.model.sal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.rifidi.edge.client.model.sal.properties.AttributeChangedEvent;
import org.rifidi.edge.client.model.sal.properties.RemoteObjectDirtyEvent;

/**
 * Some Model objects (such as RemoteObjects) have MBean Attributes associated
 * with them. This class abstracts some of that functionality into a super class
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public abstract class AbstractAttributeContributorModelObject {
	/** The properties of this reader */
	private AttributeList attributes;
	/** The property change support for this class */
	private final PropertyChangeSupport pcs;
	/** A hashmap of property changes from the UI */
	private HashMap<String, Attribute> updatedProperties;
	/** The ID of the model object */
	private String modelID;

	/**
	 * Constructor.  
	 * 
	 * @param id
	 * @param attributeList
	 */
	public AbstractAttributeContributorModelObject(String id,
			AttributeList attributeList) {
		pcs = new PropertyChangeSupport(this);
		updatedProperties = new HashMap<String, Attribute>();
		this.modelID = id;
		this.attributes = attributeList;
	}

	/**
	 * Get a copy of the Attribute List
	 * 
	 * @return
	 */
	public AttributeList getAttributes() {
		return new AttributeList(this.attributes);
	}

	/**
	 * Get an attribute with the given name
	 * 
	 * @param attributeName
	 * @return
	 */
	public Attribute getAttribute(String attributeName) {
		for (Attribute a : this.attributes.asList()) {
			if (a.getName().equals(attributeName)) {
				return a;
			}
		}
		return null;
	}

	/**
	 * Must happen in the eclipse thread. To be called when there is a change in
	 * an attribute value on the remote (model) side (not on the view side)
	 * 
	 * @param attribute
	 *            The new attribute
	 */
	protected void setAttribute(Attribute attribute) {
		Attribute a = getAttribute(attribute.getName());
		if (a != null) {
			attributes.remove(a);
		}
		attributes.add(attribute);
		AttributeChangedEvent oldBean = new AttributeChangedEvent(modelID, a);
		AttributeChangedEvent newBean = new AttributeChangedEvent(modelID,
				attribute);
		pcs.firePropertyChange(AttributeChangedEvent.ATTRIBUTE_CHANGED_EVENT,
				oldBean, newBean);
	}

	/**
	 * Add a PropertyChangedListener
	 * 
	 * @param listener
	 *            The listener to add
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.addPropertyChangeListener(listener);
	}

	/**
	 * Remote a PropertyChangedListener
	 * 
	 * @param listener
	 *            The listener to remove
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		this.pcs.removePropertyChangeListener(listener);
	}

	/**
	 * This method is intended to be used by the UI for when a user changes a
	 * property of a reader. This method simply collects all changes. The
	 * changes will be propagated by the reader by a separate UI action.
	 * 
	 * This call must happen in the eclipse thread
	 * 
	 * @param attribute
	 */
	public void updateProperty(Attribute attribute) {
		boolean wasEmpty = updatedProperties.isEmpty();
		this.updatedProperties.put(attribute.getName(), attribute);
		if (wasEmpty) {
			RemoteObjectDirtyEvent oldEvent = new RemoteObjectDirtyEvent(
					modelID, false);
			RemoteObjectDirtyEvent newEvent = new RemoteObjectDirtyEvent(
					modelID, true);
			pcs.firePropertyChange(RemoteObjectDirtyEvent.DIRTY_EVENT_PROPERTY,
					oldEvent, newEvent);
		}

	}

	/**
	 * This method clears the updated properties list and fires a property
	 * change to listeners. It must be called from within the eclipse thread
	 */
	public void clearUpdatedProperties() {
		if (updatedProperties.size() > 0) {
			HashMap<String, Attribute> attrToRestore = new HashMap<String, Attribute>(
					this.updatedProperties);
			this.updatedProperties.clear();
			RemoteObjectDirtyEvent oldEvent = new RemoteObjectDirtyEvent(
					modelID, true);
			RemoteObjectDirtyEvent newEvent = new RemoteObjectDirtyEvent(
					modelID, false);
			pcs.firePropertyChange(RemoteObjectDirtyEvent.DIRTY_EVENT_PROPERTY,
					oldEvent, newEvent);

			// Change the attributes back to their old values
			for (Attribute attr : this.attributes.asList()) {
				if (attrToRestore.keySet().contains(attr.getName())) {
					AttributeChangedEvent oldBean = new AttributeChangedEvent(
							modelID, attrToRestore.get(attr.getName()));
					AttributeChangedEvent newBean = new AttributeChangedEvent(
							modelID, attr);
					pcs.firePropertyChange(
							AttributeChangedEvent.ATTRIBUTE_CHANGED_EVENT,
							oldBean, newBean);
				}
			}
		}
	}

	/**
	 * Send the list of changed properties to the reader and update them on the
	 * reader. Must be called from whithin eclipse thread
	 */
	public void synchUpdatedProperties(RemoteEdgeServer server) {
		if (updatedProperties.size() > 0) {
			HashMap<String, Attribute> attrsTosynch = new HashMap<String, Attribute>(
					this.updatedProperties);

			AttributeList list = new AttributeList();
			for (Attribute attr : attrsTosynch.values()) {
				list.add(attr);
			}

			doSynchAttributeChange(server, modelID, list);

			this.updatedProperties.clear();
			RemoteObjectDirtyEvent oldEvent = new RemoteObjectDirtyEvent(
					modelID, true);
			RemoteObjectDirtyEvent newEvent = new RemoteObjectDirtyEvent(
					modelID, false);
			pcs.firePropertyChange(RemoteObjectDirtyEvent.DIRTY_EVENT_PROPERTY,
					oldEvent, newEvent);

		}
	}

	/**
	 * Returns true if the Reader contains property changes that have not yet
	 * been committed to the reader
	 * 
	 * @return
	 */
	public boolean isDirty() {
		return !updatedProperties.isEmpty();
	}

	/**
	 * Subclasses should put the logic here to send the list of changed
	 * properties to the server
	 * 
	 * @param server
	 * @param modelID
	 * @param list
	 */
	protected abstract void doSynchAttributeChange(RemoteEdgeServer server,
			String modelID, AttributeList list);

}
