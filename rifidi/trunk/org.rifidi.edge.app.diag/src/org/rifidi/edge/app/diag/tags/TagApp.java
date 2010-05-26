package org.rifidi.edge.app.diag.tags;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.app.api.AbstractRifidiApp;
import org.rifidi.edge.core.app.api.AppState;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EventBean;

/**
 * This is an application that lets the user query recently seen tags and tags
 * that can currently be seen.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagApp extends AbstractRifidiApp {

	/** The amount of time to keep tags around as recent tags */
	private String recentTagTimeout;

	/**
	 * 
	 * @param group
	 * @param name
	 */
	public TagApp(String group, String name) {
		super(group, name);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#lazyStart()
	 */
	@Override
	public boolean lazyStart() {
		String lazyStart= getProperty(LAZY_START, "true");
		return Boolean.parseBoolean(lazyStart);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#_start()
	 */
	@Override
	protected void _start() {

		// esper statement that creates a window.
		addStatement("create window recenttags.win:time(" + recentTagTimeout
				+ ") as TagReadEvent");

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
	List<TagReadEvent> getRecentTags(String readerID) {
		if (getState() != AppState.STARTED) {
			return new ArrayList<TagReadEvent>();
		}
		List<TagReadEvent> recentTags = new LinkedList<TagReadEvent>();
		EPOnDemandQueryResult result = executeQuery("select * from recenttags where readerID=\""
				+ readerID + "\"");
		if (result.getArray() != null) {
			for (EventBean event : result.getArray()) {
				TagReadEvent tag = (TagReadEvent) event.getUnderlying();
				recentTags.add(tag);
			}
		}
		return recentTags;
	}

	/**
	 * 
	 * @param readerID
	 * @return
	 */
	List<TagReadEvent> getCurrentTags(String readerID) {
		if (getState() != AppState.STARTED) {
			return new ArrayList<TagReadEvent>();
		}
		List<TagReadEvent> currentTags = new LinkedList<TagReadEvent>();
		EPOnDemandQueryResult result = executeQuery("select * from curtags where readerID=\""
				+ readerID + "\"");
		if (result.getArray() != null) {
			for (EventBean event : result.getArray()) {
				TagReadEvent tag = (TagReadEvent) event.getUnderlying();
				currentTags.add(tag);
			}
		}
		return currentTags;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#initialize()
	 */
	@Override
	public void initialize() {
		this.recentTagTimeout = getProperty("RecentTagTimeout", "10 min");
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.AbstractRifidiApp#getCommandProider()
	 */
	@Override
	protected CommandProvider getCommandProider() {
		TagAppCommandProvider provider = new TagAppCommandProvider();
		provider.setTagApp(this);
		return provider;
	}
	
	

}
