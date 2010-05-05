/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;

import org.eclipse.core.databinding.observable.Realm;
import org.eclipse.core.databinding.observable.list.WritableList;

/**
 * @author kyle
 * 
 */
public class TagList extends WritableList {

	private int numTagsSeen;
	private long lastRefresh;
	private boolean pause = false;
	/**
	 * Property change support to enable views to be automatically updated when
	 * a property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/**
	 * @param toWrap
	 * @param elementType
	 */
	public TagList(List<TagModel> toWrap, Object elementType) {
		super(toWrap, elementType);
	}

	/**
	 * @param realm
	 * @param toWrap
	 * @param elementType
	 */
	public TagList(Realm realm, List<TagModel> toWrap, Object elementType) {
		super(realm, toWrap, elementType);
	}

	/**
	 * @param realm
	 */
	public TagList(Realm realm) {
		super(realm);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.core.databinding.observable.list.WritableList#clear()
	 */
	@Override
	public void clear() {
		super.clear();
		setNumTagsSeen(0);
		setLastRefresh(System.currentTimeMillis());
	}

	/**
	 * Clients should use this method to add tags to this datastructure. It
	 * checks to see if the tag already exists. If it does, then update the tag
	 * with relavent fields.
	 * 
	 * @param m
	 */
	public void addTag(TagModel m) {
		if (pause) {
			return;
		}
		if (m.getId() == null || m.getId().equals("")) {
			return;
		}
		if (numTagsSeen == 0) {
			setLastRefresh(System.currentTimeMillis());
		}
		setNumTagsSeen(numTagsSeen + 1);
		if (this.contains(m)) {
			TagModel tag = (TagModel) this.get(this.indexOf(m));
			tag.incCount();
			tag.setLastSeen(m.getLastSeen());
			tag.setRssi(m.getRssi());
			tag.setAntennaID(m.getAntennaID());
			tag.setReaderID(tag.getReaderID());
		} else {
			this.add(m);

		}
	}

	public void togglePause() {
		if(this.pause){
			pause = false;
		}else{
			pause=true;
		}
	}

	/**
	 * @return the numTagsSeen
	 */
	public int getNumTagsSeen() {
		return numTagsSeen;
	}

	/**
	 * @param numTagsSeen
	 *            the numTagsSeen to set
	 */
	private void setNumTagsSeen(int numTagsSeen) {
		int oldNumTagsSeen = this.numTagsSeen;
		this.numTagsSeen = numTagsSeen;
		this.propertyChangeSupport.firePropertyChange("numTagsSeen",
				oldNumTagsSeen, this.numTagsSeen);
	}

	/**
	 * @return the lastRefresh
	 */
	public long getLastRefresh() {
		return lastRefresh;
	}

	/**
	 * @param lastRefresh
	 *            the lastRefresh to set
	 */
	private void setLastRefresh(long lastRefresh) {
		long oldLastRefresh = this.lastRefresh;
		this.lastRefresh = lastRefresh;
		this.propertyChangeSupport.firePropertyChange("lastRefresh",
				oldLastRefresh, this.lastRefresh);
	}

	/**
	 * Add a property change listener to this model
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	/**
	 * Remove property change listener from this model.
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

}
