/*
 *  ExpiringHashMap.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.common.utilities.expiration;

import java.util.HashMap;
import java.util.Set;

/**
 * 
 * This class wraps a hashmap that keeps up with "Expiring Objects". It is not
 * currently thread safe
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ExpiringHashMap<K, V> {

	private HashMap<K, ExpiringObject<V>> map;
	private long timeToLive;

	/**
	 * Create a new hashmap
	 * 
	 * @param timeToLive
	 *            The time each object should live before it expires
	 */
	public ExpiringHashMap(long timeToLive) {
		map = new HashMap<K, ExpiringObject<V>>();
		this.timeToLive = timeToLive;
	}

	/**
	 * Gets the object from the hashmap. If the object has expired, it returns
	 * null, but does not remote the object from the map
	 * 
	 * @param key
	 *            The key to the object
	 * @return The object if it has not expired. Returns null otherwise.
	 */
	public V get(K key) {
		ExpiringObject<V> obj = map.get(key);
		if (obj != null) {
			return obj.getIfNotExpired();
		}
		return null;
	}

	/**
	 * Puts the object in the hashmap
	 * 
	 * @param key
	 * @param value
	 */
	public void put(K key, V value) {
		ExpiringObject<V> obj = new ExpiringObject<V>(value, timeToLive);
		map.put(key, obj);
	}

	public void clear() {
		map.clear();
	}

	public Set<K> keySet() {
		return map.keySet();
	}

}
