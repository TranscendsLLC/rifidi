/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.eclipse.core.databinding.observable.map.ObservableMap;
import org.eclipse.core.databinding.observable.map.WritableMap;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.WritableSet;
import org.rifidi.edge.client.model.sal.properties.DirtyReaderPropertyBean;
import org.rifidi.edge.client.model.sal.properties.ReaderPropertyBean;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;

/**
 * A model object that represents a reader on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteReader {

	/** The ID of the Reader */
	private ReaderDTO readerDTO;
	/** The set of remote session belonging to this reader */
	private ObservableMap remoteSessions;
	/** The set of tags that has been seen by this reader */
	private ObservableSet tags;
	/** The remote reader factory that created this reader */
	private RemoteReaderFactory factory;
	/** The properties of this reader */
	private AttributeList attributes;
	/** The property change support for this class */
	private final PropertyChangeSupport pcs;
	/** A hashmap of property changes from the UI */
	private HashMap<String, Attribute> updatedProperties;

	/**
	 * Constructor
	 * 
	 * @param readerDTO
	 *            The DTO of the reader
	 * @param factory
	 *            The RemoteReaderFactory that created this reader
	 */
	public RemoteReader(ReaderDTO readerDTO, RemoteReaderFactory factory) {
		super();
		this.readerDTO = readerDTO;
		remoteSessions = new WritableMap();
		tags = new WritableSet();
		for (SessionDTO dto : readerDTO.getSessions()) {
			_addSession(new RemoteSession(readerDTO.getReaderID(), readerDTO
					.getReaderFactoryID(), dto));
		}
		attributes = this.readerDTO.getAttributes();
		this.factory = factory;
		this.pcs = new PropertyChangeSupport(this);
		updatedProperties = new HashMap<String, Attribute>();
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return readerDTO.getReaderID();
	}

	/**
	 * 
	 * @return the factory ID of this reader
	 */
	public String getFactoryID() {
		return this.readerDTO.getReaderFactoryID();
	}

	/**
	 * Helper method to add a session
	 * 
	 * @param session
	 *            The session to add
	 */
	private void _addSession(final RemoteSession session) {
		remoteSessions.getRealm().asyncExec(new Runnable() {
			@Override
			public void run() {
				remoteSessions.put(session.getSessionID(), session);
			}
		});
	}

	/**
	 * @return the remoteSessions
	 */
	public ObservableMap getRemoteSessions() {
		return remoteSessions;
	}

	/**
	 * @return The tags seen by this reader
	 */
	public ObservableSet getTags() {
		return tags;
	}

	/**
	 * @return the factory
	 */
	public RemoteReaderFactory getFactory() {
		return factory;
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
		ReaderPropertyBean oldBean = new ReaderPropertyBean(this.getID(), a);
		ReaderPropertyBean newBean = new ReaderPropertyBean(this.getID(),
				attribute);
		pcs.firePropertyChange(ReaderPropertyBean.READER_PROPERTY_BEAN,
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
			DirtyReaderPropertyBean oldEvent = new DirtyReaderPropertyBean(this
					.getID(), false);
			DirtyReaderPropertyBean newEvent = new DirtyReaderPropertyBean(this
					.getID(), true);
			pcs.firePropertyChange(
					DirtyReaderPropertyBean.DIRTY_EVENT_PROPERTY, oldEvent,
					newEvent);
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
			DirtyReaderPropertyBean oldEvent = new DirtyReaderPropertyBean(this
					.getID(), true);
			DirtyReaderPropertyBean newEvent = new DirtyReaderPropertyBean(this
					.getID(), false);
			pcs.firePropertyChange(
					DirtyReaderPropertyBean.DIRTY_EVENT_PROPERTY, oldEvent,
					newEvent);

			// Change the attributes back to their old values
			for (Attribute attr : this.attributes.asList()) {
				if (attrToRestore.keySet().contains(attr.getName())) {
					ReaderPropertyBean oldBean = new ReaderPropertyBean(this
							.getID(), attrToRestore.get(attr.getName()));
					ReaderPropertyBean newBean = new ReaderPropertyBean(this
							.getID(), attr);
					pcs.firePropertyChange(
							ReaderPropertyBean.READER_PROPERTY_BEAN, oldBean,
							newBean);
				}
			}
		}
	}

	/**
	 * Send the list of changed properties to the reader and update them on the
	 * reader. Must be called from whithin eclipse thread
	 */
	public void synchUpdatedProperties() {
		if (updatedProperties.size() > 0) {
			HashMap<String, Attribute> attrsTosynch = new HashMap<String, Attribute>(
					this.updatedProperties);
			this.updatedProperties.clear();
			DirtyReaderPropertyBean oldEvent = new DirtyReaderPropertyBean(this
					.getID(), true);
			DirtyReaderPropertyBean newEvent = new DirtyReaderPropertyBean(this
					.getID(), false);
			pcs.firePropertyChange(
					DirtyReaderPropertyBean.DIRTY_EVENT_PROPERTY, oldEvent,
					newEvent);
			
			
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
}
