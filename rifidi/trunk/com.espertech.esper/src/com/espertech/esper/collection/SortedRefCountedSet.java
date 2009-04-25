/**************************************************************************************
 * Copyright (C) 2008 EsperTech, Inc. All rights reserved.                            *
 * http://esper.codehaus.org                                                          *
 * http://www.espertech.com                                                           *
 * ---------------------------------------------------------------------------------- *
 * The software in this package is published under the terms of the GPL license       *
 * a copy of which has been included with this distribution in the license.txt file.  *
 **************************************************************************************/
package com.espertech.esper.collection;

import java.util.TreeMap;

/**
 * Sorted, reference-counting set based on a TreeMap implementation that stores keys and a reference counter for
 * each unique key value. Each time the same key is added, the reference counter increases.
 * Each time a key is removed, the reference counter decreases.
 */
public class SortedRefCountedSet<K>
{
    private TreeMap<K, Integer> refSet;

    /**
     * Constructor.
     */
    public SortedRefCountedSet()
    {
        refSet = new TreeMap<K, Integer>();
    }

    /**
     * Clear out the collection.
     */
    public void clear()
    {
        refSet.clear();
    }

    /**
     * Add a key to the set. Add with a reference count of one if the key didn't exist in the set.
     * Increase the reference count by one if the key already exists.
     * @param key to add
     */
    public void add(K key)
    {
        Integer value = refSet.get(key);
        if (value == null)
        {
            refSet.put(key, Integer.valueOf(1));
            return;
        }

        value++;
        refSet.put(key, value);
    }

    /**
     * Add a key to the set with the given number of references.
     * @param key to add
     * @param numReferences initial number of references
     */
    public void add(K key, int numReferences)
    {
        Integer value = refSet.get(key);
        if (value == null)
        {
            refSet.put(key, numReferences);
            return;
        }
        throw new IllegalArgumentException("Key '" + key + "' already in collection");
    }

    /**
     * Remove a key from the set. Removes the key if the reference count is one.
     * Decreases the reference count by one if the reference count is more then one.
     * @param key to add
     * @throws IllegalStateException is a key is removed that wasn't added to the map
     */
    public void remove(K key)
    {
        Integer value = refSet.get(key);
        if (value == null)
        {
            // This could happen if a sort operation gets a remove stream that duplicates events.
            // Generally points to an invalid combination of data windows.
            // throw new IllegalStateException("Attempting to remove key from map that wasn't added");
            return;
        }

        if (value == 1)
        {
            refSet.remove(key);
            return;
        }

        value--;
        refSet.put(key, value);
    }

    /**
     * Returns the largest key value, or null if the collection is empty.
     * @return largest key value, null if none
     */
    public K maxValue()
    {
        if (refSet.isEmpty())
        {
            return null;
        }
        return refSet.lastKey();
    }

    /**
     * Returns the smallest key value, or null if the collection is empty.
     * @return smallest key value, null if none
     */
    public K minValue()
    {
        if (refSet.isEmpty())
        {
            return null;
        }
        return refSet.firstKey();
    }
}
