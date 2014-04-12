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

import org.rifidi.edge.notification.DatacontainerEvent;
import org.rifidi.edge.notification.TagReadEvent;

/**
 * @author kyle
 *
 */
public abstract class AbstractReadData<T extends DatacontainerEvent> {
	
	/** The reader ID that 'saw' this tag */
	private final String readerID;
	/** The antenna that saw this tag */
	private final int antennaID;
	/** The event */
	protected T tag;
	/**
	 * @param readerID
	 * @param antennaID
	 */
	public AbstractReadData(String id, String readerID, int antennaID) {
		this.readerID = readerID;
		this.antennaID = antennaID;
		tag = createTag(id);
	}
	
	protected abstract T createTag(String id);
	
	/**
	 * Return the reader this tag was seen on
	 * 
	 * @return
	 */
	public String getReaderID() {
		return readerID;
	}

	/**
	 * Return the TagReadEvent for this tag
	 * 
	 * @return
	 */
	public TagReadEvent getTagReadEvent() {
		return new TagReadEvent(readerID, tag, antennaID, System
				.currentTimeMillis());
	}
	
	

}
