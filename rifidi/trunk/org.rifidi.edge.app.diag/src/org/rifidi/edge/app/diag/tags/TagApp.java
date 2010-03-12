package org.rifidi.edge.app.diag.tags;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.espertech.esper.client.EPOnDemandQueryResult;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;

public class TagApp {
	
	/** Esper service */
	private volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	
	public void start(){
		
		// esper statement that creates a window.
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window recenttags.win:time(10 minute)" +
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
	
	public void stop(){
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}

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
	 * @param esperService
	 *            the esperService to set
	 */
	public void setEsperService(EsperManagementService esperService) {
		this.esperService = esperService;
	}

}
