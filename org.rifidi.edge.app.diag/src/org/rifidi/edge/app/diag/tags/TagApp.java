package org.rifidi.edge.app.diag.tags;

import java.util.LinkedList;
import java.util.List;

import org.rifidi.edge.core.app.api.RifidiApp;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EventBean;

public class TagApp extends RifidiApp{
	
	private String recentTagTimeout;
	
	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.core.app.api.RifidiApp#start()
	 */
	@Override
	public void start(){
		
		// esper statement that creates a window.
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window recenttags.win:time("+recentTagTimeout+")" +
			"(tag_ID String, readerID String, antennaID int, timestamp long)"));
		
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
				"create window curtags.std:firstunique(tag_ID,readerID,antennaID).win:time(10 sec)" +
				"as recenttags"));
		
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"insert into recenttags select cast(tag.epc?, String) as tag_ID , "
			+ "readerID, antennaID, timestamp from ReadCycle[select * from tags]"));
		
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"insert into curtags select tag_ID,readerID,antennaID,timestamp from recenttags"));
		
	}

	/**
	 * 
	 * @param readerID
	 * @return
	 */
	List<TagData> getRecentTags(String readerID) {
		List<TagData> recentTags = new LinkedList<TagData>();
		String query = "select * from recenttags where readerID=\""+readerID+"\"";
		EPOnDemandQueryResult result = esperService.getProvider()
				.getEPRuntime().executeQuery(query);
		if (result.getArray() != null) {
			for (EventBean event : result.getArray()) {
				TagData data = new TagData();
				data.setEpc((String)event.get("tag_ID"));
				data.setAntenna((Integer)event.get("antennaID"));
				data.setReaderID(readerID);
				data.setTimestamp((Long)event.get("timestamp"));
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
		List<TagData> recentTags = new LinkedList<TagData>();
		String query = "select * from curtags where readerID=\""+readerID+"\"";
		EPOnDemandQueryResult result = esperService.getProvider()
				.getEPRuntime().executeQuery(query);
		if (result.getArray() != null) {
			for (EventBean event : result.getArray()) {
				TagData data = new TagData();
				data.setEpc((String)event.get("tag_ID"));
				data.setAntenna((Integer)event.get("antennaID"));
				data.setReaderID(readerID);
				data.setTimestamp((Long)event.get("timestamp"));
				recentTags.add(data);
			}
		}
		return recentTags;
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
