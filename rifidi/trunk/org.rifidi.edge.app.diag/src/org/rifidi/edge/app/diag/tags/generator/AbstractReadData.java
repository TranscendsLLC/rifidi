/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

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
