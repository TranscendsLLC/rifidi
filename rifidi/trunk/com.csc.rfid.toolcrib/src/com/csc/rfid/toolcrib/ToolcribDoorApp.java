/**
 * 
 */
package com.csc.rfid.toolcrib;

import java.util.LinkedList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * This is the application for the Door. It's a bit different from the other two
 * applications because it requires the pressure mat and the door latch. If
 * latch event happens first, then it's a check out. If the mat event happens
 * first, it's a check in.
 * 
 * 
 * GPI triggers: 1=Latch, 2=Mat
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ToolcribDoorApp extends ToolcribApp {

	/** The logger for this class */
	private Log logger = LogFactory.getLog(ToolcribDoorApp.class);

	@Override
	public void start() {
		logger.debug("Starting CSC Door App");
		String doorReaderID = System.getProperty("com.csc.door_reader");
		StatementAwareUpdateListener listener = createListener();
		EPStatement statement = esperService.getProvider().getEPAdministrator()
				.createEPL(
						"select * from pattern [every "
								// Mat high event
								+ "(firstGPIEvent=GPIEvent(readerID='"
								+ doorReaderID
								+ "',port=2,state=true) -> "
								// followed by a door high event
								+ "(GPIEvent(readerID='"
								+ doorReaderID
								+ "', port=1, state=true) "
								// without a mat low event
								+ "and not GPIEvent(readerID='"
								+ doorReaderID
								+ "',port=2,state=false)) "
								// where the door event happens within 10
								// seconds of the mat high event
								+ "where timer:within(10 sec)) "
								// followed by a door low event
								+ "-> (GPIEvent(readerID='" + doorReaderID
								+ "', port=1, state=false) -> "
								// Followed by some tags
								+ "tags= ReadCycle(readerID='" + doorReaderID
								+ "')[select * from tags] "
								// where the tags were seen within 5 seconds of
								// the door low event
								+ "until timer:interval(5 sec))" + "]");
		statement.addListener(listener);

		EPStatement statement2 = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select * from pattern [every "
								// Door is high
								+ "(firstGPIEvent=GPIEvent(readerID='"
								+ doorReaderID
								+ "',port=1,state=true) -> "
								// followed by a mat high event
								+ "GPIEvent(readerID='"
								+ doorReaderID
								+ "', port=2, state=true) "
								// where the mat event happens within 10 seconds
								// of the door high event
								+ "where timer:within(10 sec)) "
								// followed by a door low event
								+ "-> (GPIEvent(readerID='" + doorReaderID
								+ "', port=1, state=false) -> "
								// Followed by some tags
								+ "tags= ReadCycle(readerID='" + doorReaderID
								+ "')[select * from tags] "
								// where the tags were seen within 5 secs of the
								// door low event
								+ "until timer:interval(5 sec))" + "]");
		statement2.addListener(listener);

	}

	/**
	 * Create the listener for the Door app.
	 * 
	 * @return
	 */
	private StatementAwareUpdateListener createListener() {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						GPIEvent event = (GPIEvent) b.get("firstGPIEvent");
						TagReadEvent[] tagReadEvents = (TagReadEvent[]) b
								.get("tags");
						List<CSCTag> tags = new LinkedList<CSCTag>();
						// convert TagReadEvents into CSCTags.
						for (TagReadEvent tre : tagReadEvents) {
							CSCTag tag = new CSCTag();
							tag.setAntenna(tre.getAntennaID());
							tag.setEpc(((EPCGeneration2Event) tre.getTag())
									.getEpc());
							tag.setReaderID(tre.getReaderID());
							tag.setRssi(((Float) tre.getExtraInformation().get(
									"RSSI")).toString());
							tag.setSpeed((Float) tre.getExtraInformation().get(
									"Speed"));
							tags.add(tag);
						}
						boolean onWatchlist = onWatchlist(tags);
						boolean inbound;
						if (event.getPort() == 2) {
							inbound = true;
							if (tagReadEvents != null && !tags.isEmpty()) {
								// System.out.println("Check IN: "
								// + tagReadEvents.length + " tags");
								triggerLight(tags.get(0).getReaderID(),
										onWatchlist, inbound);

								writeLog(tags.get(0).getEpc(), tags.get(0)
										.getReaderID(), inbound, onWatchlist);
							} else {
								// System.out.println("Check IN: no tags");
							}
						} else if (event.getPort() == 1) {
							inbound = false;
							if (tagReadEvents != null && !tags.isEmpty()) {
								// System.out.println("Check OUT: "
								// + tagReadEvents.length + " tags");
								triggerLight(tags.get(0).getReaderID(),
										onWatchlist, inbound);

								writeLog(tags.get(0).getEpc(), tags.get(0)
										.getReaderID(), inbound, onWatchlist);
							} else {
								// System.out.println("Check OUT: no tags");
							}
						}
					}
				}
			}
		};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.csc.rfid.toolcrib.ToolcribApp#determineDirection(org.rifidi.edge.
	 * core.services.notification.data.gpio.GPIEvent)
	 */
	@Override
	protected boolean determineDirection(GPIEvent gpievent) {
		// Unused for the door
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.csc.rfid.toolcrib.ToolcribApp#triggerLight(java.lang.String,
	 * boolean, boolean)
	 */
	@Override
	protected void triggerLight(String readerID, boolean onWatchList,
			boolean inbound) {
		try {
			if (onWatchList) {
				// Trigger watchlist (red) GPO 2
				this.gpoController
						.flashGPO(readerID, FLASH_TIME, RED_LIGHT_GPO);
			} else if (inbound) {
				// Trigger inbound (yellow) GPO 3
				this.gpoController.flashGPO(readerID, FLASH_TIME,
						YELLOW_LIGHT_GPO);
			} else {
				// Trigger outbound (green) GPO 4
				this.gpoController.flashGPO(readerID, FLASH_TIME,
						GREEN_LIGHT_GPO);
			}
		} catch (CannotExecuteException e) {
			logger.warn("Problem executing GPO: " + e.getMessage());
		}
	}

}
