/*
 * Copyright (c) 2013 Transcends, LLC.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the GNU Public License v2.0
 * which accompanies this distribution, and is available at
 * http://www.gnu.org/licenses/old-licenses/gpl-2.0.html
 */
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
