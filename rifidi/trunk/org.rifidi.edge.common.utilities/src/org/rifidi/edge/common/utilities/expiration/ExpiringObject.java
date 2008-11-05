/*
 *  ExpiringObject.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.common.utilities.expiration;

/**
 * This Object is used to represent an object whose value is "out of date" after
 * a given amount of time. It simply wraps the object with a time-to-live value.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ExpiringObject<V> {

	/**
	 * The object to store
	 */
	private V object;

	/**
	 * The time when this object was created
	 */
	private long timeCreated;

	/**
	 * The amount of time this object is good for
	 */
	private long timeToLive;

	/**
	 * Create a new Expiring object
	 * 
	 * @param object
	 *            The object that is stiored
	 * @param timeToLive
	 *            The time before this object is expired
	 */
	public ExpiringObject(V object, long timeToLive) {
		this.object = object;
		this.timeToLive = timeToLive;
		this.timeCreated = System.currentTimeMillis();
	}

	/**
	 * 
	 * @return The object if it has not expired. Return null otherwise.
	 */
	public V getIfNotExpired() {
		Long timeSinceCreated = System.currentTimeMillis() - timeCreated;
		if (timeSinceCreated < timeToLive) {
			return object;
		} else
			return null;
	}

	/**
	 * 
	 * @return The object whether or not it has expired
	 */
	public V getObject() {
		return object;
	}

}
