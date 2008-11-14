/*
 * Copyright (C) 2007 University of Cambridge
 *
 * This file is part of Fosstrak (www.fosstrak.org).
 *
 * Fosstrak is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1, as published by the Free Software Foundation.
 *
 * Fosstrak is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Fosstrak; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA  02110-1301  USA
 */

package org.fosstrak.tdt;

import java.util.*;

/**
 * NoisyMap is a front-end for a String to String Map that 
 * prints a message to System.out every time an entry is updated.
 * It is intended for testing purposes only.
 */


public class NoisyMap implements Map<String,String> {
	private Map<String,String> map;
	public NoisyMap(Map<String,String> map){
	    this.map = map;
	}
	public void clear() { map.clear(); }
	     //Removes all mappings from this map (optional operation).
	public  boolean 	containsKey(Object key) 
	{
	    return map.containsKey(key);
	}
	     //          Returns true if this map contains a mapping for the specified key.
	public  boolean 	containsValue(Object value)
	{
	    return map.containsValue(value);
	}
	     //          Returns true if this map maps one or more keys to the specified value.
	public Set<Map.Entry<String,String>> 	entrySet()
	{
	    return map.entrySet();
	}
	     //          Returns a set view of the mappings contained in this map.
	public boolean 	equals(Object o) {
	    return map.equals(o);
	}
	     //          Compares the specified object with this map for equality.
	public String 	get(Object key) {
	    return map.get(key);
	}
	     //          Returns the value to which this map maps the specified key.
	public int 	hashCode()
	{
	    return map.hashCode();
	}
	     //          Returns the hash code value for this map.
	public boolean 	isEmpty()
    {
	return map.isEmpty();
    }
    //          Returns true if this map contains no key-value mappings.
    public Set<String> 	keySet()
    {
	return map.keySet();
    }
    //          Returns a set view of the keys contained in this map.
    public String 	put(String key, String value)
    {
	System.out.println("   " + key + " := " + value + " (" + value.length() + " chars)");
	return map.put(key, value);
    }
    //          Associates the specified value with the specified key in this map (optional operation).
    public void 	putAll(Map<? extends String,? extends String> t)
    {
	map.putAll(t);
    }
    //          Copies all of the mappings from the specified map to this map (optional operation).
    public String 	remove(Object key)
    {
	return map.remove(key);
    }
    //          Removes the mapping for this key from this map if it is present (optional operation).
    public int 	size()
    {
	return map.size();
    }
    //          Returns the number of key-value mappings in this map.
    public Collection<String> 	values()
    {
	return map.values();
    }
    //          Returns a collection view of the values contained in this map.
 
}
