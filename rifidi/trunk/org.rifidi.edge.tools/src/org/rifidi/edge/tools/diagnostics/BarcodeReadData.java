/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.tools.diagnostics;

import org.rifidi.edge.notification.BarcodeTagEvent;

public class BarcodeReadData extends AbstractReadData<BarcodeTagEvent> {

	/**
	 * @param tagID
	 * @param readerID
	 * @param antennaID
	 */
	public BarcodeReadData(String tagID, String readerID, int antennaID) {
		super(tagID, readerID, antennaID);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.diagnostics.tags.generator.AbstractReadData#createTag(java
	 * .lang.String)
	 */
	@Override
	protected BarcodeTagEvent createTag(String id) {
		char[] chars = id.toCharArray();
		byte[] memBank = new byte[chars.length];
		for (int i = 0; i < chars.length; i++) {
			memBank[i] = (byte) chars[i];
		}
		BarcodeTagEvent tag = new BarcodeTagEvent();
		tag.setBarcode(memBank);
		return tag;
	}

}
