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
import java.lang.reflect.Array;


/** Simple version of a Trie structure.
 * 
 *  See Wikipedia for information about Tries.
 *
 *  Note that this version assumes chars contain values between 0 and
 *  255 (i.e. UTF-8 or similar).
 */
public class PrefixTree<T>
{

    private class PrefixNode {
	private PrefixNode [] node;

	// list is null if non-leaf
	private List<T> list;

	public PrefixNode() {
	    node = (PrefixNode[]) Array.newInstance(PrefixNode.class, 256);
	}

	public PrefixNode [] getNode() { return node; }

	public PrefixNode getNode(char c) { return node[c]; }

	public void setNode(char c, PrefixNode n) { 
	    node[c] = n;
	}

	public List<T> getList() { return list; }

	public boolean isMarked() { return list != null; }

	public void add(T obj) { 
	    if (list == null) 
		list = new ArrayList<T>();
	    list.add(obj);
	}

    }
     
    PrefixNode root = new PrefixNode();

    public void insert1(String s, T obj) {
	// add a prefix to the tree 
	PrefixNode n = root;
	for (int i = 0; i < s.length(); i++) {
	    char c = s.charAt(i);

	    PrefixNode next = n.getNode(c);
	    if (next == null) {
		next = new PrefixNode();
		n.setNode(c, next);
	    }
	    n = next;
	}
	assert n != null;
	n.add(obj);
    }

    
    // recursive version
    public void insert(String s, T obj) {
	assert s != null : "Attempt to insert a null string into prefix tree";
	myInsert(root, s, obj);
    }

    private void myInsert(PrefixNode n, String s, T obj) { 
	if (s.equals("")) 
	    n.add(obj);
	else {
	    char c = s.charAt(0);
	    PrefixNode next = n.getNode(c);
	    if (next == null) {
		next = new PrefixNode();
		n.setNode(c, next);
	    }
	    myInsert(next, s.substring(1), obj);
	}
    }


    public List<T> search(String s) {
	return mySearch(root, s);
    }

    private List<T> mySearch(PrefixNode n, String s) { 
	if (s.equals(""))
	    return n.getList();
	else {
	    char c = s.charAt(0);
	    PrefixNode next = n.getNode(c);
	    if (next == null) {
		if (n.isMarked()) 
		    return n.getList();
		else
		    return new ArrayList<T>();
	    }
	    else
		return mySearch(next, s.substring(1));
	}
    }
}
