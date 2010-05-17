package org.rifidi.edge.app.diag.tags;

import java.util.LinkedList;
import java.util.List;

import org.rifidi.edge.core.app.api.RifidiApp;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EventBean;

public class TagApp extends RifidiApp {

	private String recentTagTimeout;

	/**
	 * Constructor
	 * 
	 * @param name
	 *            The application name
	 */
	public TagApp(String name) {
		super(name);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.RifidiApp#start()
	 */
	@Override
	public void start() {
		// TODO Auto-generated method stub
		super.start();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.RifidiApp#_start()
	 */
	@Override
	protected void _start() {

		// esper statement that creates a window.
		addStatement("create window recenttags.win:time("+ recentTagTimeout+ ") as TagReadEvent");

		addStatement("create window curtags.std:firstunique(tag.ID, readerID, antennaID).win:time(10 sec)"
				+ "as TagReadEvent");

		addStatement("insert into recenttags select * from ReadCycle[select * from tags]");

		addStatement("insert into curtags select * from recenttags");
	}

	/**
	 * 
	 * @param readerID
	 * @return
	 */
	List<TagData> getRecentTags(String readerID) {
		List<TagData> recentTags = new LinkedList<TagData>();
		EPOnDemandQueryResult result = executeQuery("select * from recenttags where readerID=\""
				+ readerID + "\"");
		if (result.getArray() != null) {
			for (EventBean event : result.getArray()) {
				TagData data = new TagData();
				TagReadEvent tag= (TagReadEvent)event.getUnderlying();
				data.setEpc(tag.getTag().getFormattedID());
				data.setAntenna(tag.getAntennaID());
				data.setReaderID(readerID);
				data.setTimestamp(tag.getTimestamp());
				recentTags.add(data);
			}
		}
		return recentTags;
	}

	/**
	 * 
	 * @param readerID
	 * @return
	 */
	List<TagData> getCurrentTags(String readerID) {
		List<TagData> currentTags = new LinkedList<TagData>();
		EPOnDemandQueryResult result = executeQuery("select * from curtags where readerID=\""
				+ readerID + "\"");
		if (result.getArray() != null) {
			for (EventBean event : result.getArray()) {
				TagData data = new TagData();
				TagReadEvent tag= (TagReadEvent)event.getUnderlying();
				data.setEpc(tag.getTag().getFormattedID());
				data.setAntenna(tag.getAntennaID());
				data.setReaderID(readerID);
				data.setTimestamp(tag.getTimestamp());
				currentTags.add(data);
			}
		}
		return currentTags;
	}

	/**
	 * Called by spring
	 * 
	 * @param timeout
	 */
	public void setRecentTagTimeout(String timeout) {
		this.recentTagTimeout = timeout;
	}
}
