/**
 * 
 */
package com.csc.rfid.toolcrib;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * GPI triggers: 1=Latch, 2=Mat
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ToolcribDoorApp extends ToolcribApp {

	private Log logger = LogFactory.getLog(ToolcribDoorApp.class);

	@Override
	public void start() {
		logger.debug("Starting CSC Door App");
		String doorReaderID = System.getProperty("com.csc.door_reader");
		StatementAwareUpdateListener listener = createListener();
		EPStatement statement = esperService.getProvider().getEPAdministrator()
				.createEPL("select * from pattern [every " +
						//Mat high event
						"(firstGPIEvent=GPIEvent(readerID='Alien_1',port=2,state=true) -> " +
							//followed by a door high event
							"(GPIEvent(readerID='Alien_1', port=1, state=true) " +
								//without a mat low event
								"and not GPIEvent(readerID='Alien_1',port=2,state=false)) " +
							//where the door event happens within 10 secs of the mat high event
							"where timer:within(10 sec)) " +
						//followed by a door low event
						"-> (GPIEvent(readerID='Alien_1', port=1, state=false) -> " +
							//follwed by some tags
							"tags= ReadCycle(readerID='Alien_1')[select * from tags] " +
							//where the tags were seen within 5 secs of the door low event
							"until timer:interval(5 sec))" +
						"]");
		statement.addListener(listener);
		
		EPStatement statement2 = esperService.getProvider().getEPAdministrator()
			.createEPL("select * from pattern [every " +
				//Door is high
				"(firstGPIEvent=GPIEvent(readerID='Alien_1',port=1,state=true) -> " +
					//followed by a mat high event
					"GPIEvent(readerID='Alien_1', port=2, state=true) " +
					//where the mat event happens within 10 secs of the door high event
					"where timer:within(10 sec)) " +
				//followed by a door low event
				"-> (GPIEvent(readerID='Alien_1', port=1, state=false) -> " +
					//follwed by some tags
					"tags= ReadCycle(readerID='Alien_1')[select * from tags] " +
					//where the tags were seen within 5 secs of the door low event
					"until timer:interval(5 sec))" +
				"]");
		statement2.addListener(listener);

	}
	
	
	private StatementAwareUpdateListener createListener() {
		return new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						GPIEvent event = (GPIEvent) b.get("firstGPIEvent");
						TagReadEvent[] tags = (TagReadEvent[]) b
						.get("tags");
						if (event.getPort() == 2) {
							if (tags != null) {
								System.out.println("Check IN: " + tags.length
										+ " tags");
							} else {
								System.out.println("Check IN: no tags");
							}
						}
						else if(event.getPort()==1){
							if (tags != null) {
								System.out.println("Check OUT: " + tags.length
										+ " tags");
							} else {
								System.out.println("Check OUT: no tags");
							}
						}
					}
				}

			}
		};
	}

	@Override
	protected boolean determineDirection(GPIEvent gpievent) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	protected void triggerLight(String readerID, boolean onWatchList,
			boolean inbound) {
		// TODO Auto-generated method stub

	}

}
