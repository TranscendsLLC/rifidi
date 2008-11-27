/*
 *  VariableAddress.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.ale.fields.generic;

/**
 * @author kyle
 * 
 */
public class VariableAddress {

	private int _memBank;
	private String _oid;

	public VariableAddress(int memBank, String oid) {
		_memBank = memBank;
		_oid = oid;
	}

	/**
	 * @return the _memBank
	 */
	public int get_memBank() {
		return _memBank;
	}

	/**
	 * @return the _oid
	 */
	public String get_oid() {
		return _oid;
	}

}
