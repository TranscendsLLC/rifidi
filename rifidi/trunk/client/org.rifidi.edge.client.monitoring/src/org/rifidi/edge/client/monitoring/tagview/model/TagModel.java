/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Date;

/**
 * The model for a single tag. Two TagModel objects are equal if their IDs are
 * the same.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagModel {

	/** The ID of the tag */
	private String id;
	/** The number of times the tag has been seen */
	private int count;
	/** The latest RSSI value of the tag */
	private float rssi;
	/** The time the tag was first seen */
	private Date firstSeen;
	/** The time the tag was seen last. */
	private Date lastSeen;
	/** The reader ID of the reader that saw the tag */
	private String readerID;
	/** The antenna ID the tag was seen on */
	private int antennaID;
	/**
	 * Property change support to enable views to be automatically updated when
	 * a property changes.
	 */
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(
			this);

	/**
	 * Constructor
	 */
	public TagModel(String ID, long firstSeen) {
		this.count = 1;
		this.rssi = 0f;
		this.id = ID;
		this.firstSeen = new Date(firstSeen);
		this.lastSeen = new Date(firstSeen);
		this.readerID = "";
		this.antennaID = 0;
	}

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @return the count
	 */
	public int getCount() {
		return count;
	}

	/**
	 * Use this method to increment the count if the tag already exists in the
	 * list of tags already seen.
	 */
	public void incCount() {
		this.count = count + 1;
		this.propertyChangeSupport
				.firePropertyChange("count", count - 1, count);
	}

	/**
	 * @return the rssi
	 */
	public float getRssi() {
		return rssi;
	}

	/**
	 * @param rssi
	 *            the rssi to set
	 */
	public void setRssi(float rssi) {
		float oldRssi = this.rssi;
		this.rssi = rssi;
		this.propertyChangeSupport.firePropertyChange("rssi", oldRssi,
				this.rssi);
	}

	/**
	 * @return the time the tag was first seen
	 */
	public Date getFirstSeen() {
		return firstSeen;
	}

	/**
	 * 
	 * @return The time the tags was last seen
	 */
	public Date getLastSeen() {
		return lastSeen;
	}

	/**
	 * 
	 * @param lastSeen
	 *            - the time the tag was seen last
	 */
	public void setLastSeen(Date lastSeen) {
		Date oldLastSeen = this.lastSeen;
		this.lastSeen = lastSeen;
		this.propertyChangeSupport.firePropertyChange("lastSeen", oldLastSeen,
				this.lastSeen);
	}

	/**
	 * @return the readerID
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * @param readerID
	 *            the readerID to set
	 */
	public void setReaderID(String readerID) {
		String oldReaderID = this.readerID;
		this.readerID = readerID;
		this.propertyChangeSupport.firePropertyChange("readerID", oldReaderID,
				this.readerID);
	}

	/**
	 * @return the antennaID
	 */
	public int getAntennaID() {
		return antennaID;
	}

	/**
	 * @param antennaID
	 *            the antennaID to set
	 */
	public void setAntennaID(int antennaID) {
		int oldAntennaID = this.antennaID;
		this.antennaID = antennaID;
		this.propertyChangeSupport.firePropertyChange("antennaID",
				oldAntennaID, this.antennaID);
	}

	/**
	 * Add a property change listener to this model
	 * 
	 * @param propertyName
	 * @param listener
	 */
	public void addPropertyChangeListener(String propertyName,
			PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	/**
	 * Remove property change listener from this model.
	 * 
	 * @param listener
	 */
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return id;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object arg0) {
		if (arg0 != null) {
			if (arg0 instanceof TagModel) {
				if (((TagModel) arg0).id.equals(this.id)) {
					return true;
				}
			}
		}
		return false;
	}

}
