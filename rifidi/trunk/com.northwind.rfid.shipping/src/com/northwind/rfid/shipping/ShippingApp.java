/**
 * 
 */
package com.northwind.rfid.shipping;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;
import com.northwind.rfid.shipping.notifications.AlertNotification;
import com.northwind.rfid.shipping.notifications.EAlertType;
import com.northwind.rfid.shipping.notifications.EZone;
import com.northwind.rfid.shipping.notifications.ItemArrivalNotification;
import com.northwind.rfid.shipping.notifications.ItemDepartureNotification;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ShippingApp {

	/** Esper service */
	private volatile EsperManagementService esperService;
	/**All statements that have been defined so far*/
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	/**JMS Destination to send messages to*/
	private volatile Destination destination;
	/**JMS Template to use to send messages*/
	private volatile JmsTemplate template;

	/**
	 * Constructor
	 */
	public ShippingApp() {
		Activator.myApp=this;
	}

	/**
	 * Called by spring
	 * 
	 * @param esperService
	 */
	public void setEsperService(EsperManagementService esperService) {
		this.esperService = esperService;
		start();
		setUpAlerts();
		setupListeners();
	}
	
	/**
	 * Called by spring
	 * @param destination the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	/**
	 * Called by spring
	 * @param template the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * A method that starts the application
	 */
	private void start(){
		
		//create a named window that handles events at the Dock Door
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window dockdoor.std:firstunique(tag_ID) (tag_ID String)"));
		
		//create a named window that handles events at the Weight Station
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window weighstation.std:firstunique(tag_ID) (tag_ID String)"));
		
		//Insert information into the named windows
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"on ReadCycle[select * from tags]" +
			"insert into dockdoor select cast(tag.epc?, String) as tag_ID where readerID = 'Alien_1'" +
			"insert into weighstation select cast(tag.epc?, String) as tag_ID where readerID = 'Alien_2'"));
		
		//Remove events from the Dock Door Named Window
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"on pattern [every tag=dockdoor ->" +
				"(timer:interval(2 sec) and not dockdoor(tag_ID = tag.tag_ID))]" +
			"delete from dockdoor where tag_ID = tag.tag_ID"));
		
		//Remove events from the Dock Door Named Window
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
				"on pattern [every tag=weighstation ->" +
					"(timer:interval(2 sec) and not weighstation(tag_ID = tag.tag_ID))]" +
				"delete from weighstation where tag_ID = tag.tag_ID"));
	}
	
	/**
	 * A method that sets up business alerts
	 */
	private void setUpAlerts(){
		//Create  a window for alert messages
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window alerts.win:length(20) (alert_type int, tag_ID String)"));

		//create a window for item leaving the weighstation 
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window weighstation_recent.std:unique(tag_ID) (tag_ID String)"));
		
		//Insert items into weightstation_recent once they leave weighstation
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"insert rstream into weighstation_recent select tag_ID from weighstation"));
		
		//whenever an item is seen at the weighstation and then seen at the dockdoor, insert a new item into the alerts window
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"on pattern[every-distinct(tag.tag_ID) tag=weighstation_recent -> dockdoor(tag_ID = tag.tag_ID)] " +
			"insert into alerts " +
			"select 1 as alert_type, tag_ID as tag_ID " +
				"from weighstation_recent where tag_ID = tag.tag_ID"));
		
		//create a window for item leaving the dock door 
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window dockdoor_recent.std:unique(tag_ID) (tag_ID String)"));
		
		//Insert items into dockdoor_recent once they leave dockdoor
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"insert rstream into dockdoor_recent select tag_ID from dockdoor"));
		
		//Any time we see a new weighstation event, check to see if it is not already in dockdoor_recent.  If not, make a new alert.
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"insert into alerts " +
			"select 2 as alert_type, tag_ID as tag_ID " +
			"from weighstation as w " +
			"where not exists (select * from dockdoor_recent as d where d.tag_ID = w.tag_ID)"));
		
		//Create a new alert whenever a package departs from the dock door and is not seen at the weighstation with a given time period
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"on pattern[every-distinct(tag.tag_ID) tag=dockdoor_recent -> " +
			"(timer:interval(15 sec) and not weighstation(tag_ID =tag.tag_ID))] " +
			"insert into alerts " +
			"select 3 as alert_type, tag_ID as tag_ID " +
			"from dockdoor_recent where (tag_ID = tag.tag_ID)"));
		
	}
	
	/**
	 * A method that sets up listeners to handle esper events
	 */
	private void setupListeners(){
		
		//create a listener to handle Dock Door events 
		StatementAwareUpdateListener dockDoorListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arrivals, EventBean[] departures, EPStatement arg2,
					EPServiceProvider arg3) {
				if(arrivals!=null){
					for(EventBean bean : arrivals){
						String id = (String)bean.get("tag_ID");
						send(new ItemArrivalNotification(EZone.DOCK_DOOR, id));
					}
				}
				
				if(departures!=null){
					for(EventBean bean : departures){
						String id = (String)bean.get("tag_ID");
						send(new ItemDepartureNotification(EZone.DOCK_DOOR, id));
					}
				}
				
			}
		};
		
		//Create a query that is triggered on insert and remove events from Dock Door Window 
		EPStatement queryDockDoor = esperService.getProvider().getEPAdministrator().createEPL(
				"select irstream * from dockdoor");
		queryDockDoor.addListener(dockDoorListener);
		statements.add(queryDockDoor);
		
		//Create a listener to handle Weigh Station Events
		StatementAwareUpdateListener weighStationListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arrivals, EventBean[] departures, EPStatement arg2,
					EPServiceProvider arg3) {
				if(arrivals!=null){
					for(EventBean bean : arrivals){
						String id = (String)bean.get("tag_ID");
						send(new ItemArrivalNotification(EZone.WEIGH_STATION, id));
					}
				}
				
				if(departures!=null){
					for(EventBean bean : departures){
						String id = (String)bean.get("tag_ID");
						send(new ItemDepartureNotification(EZone.WEIGH_STATION, id));
					}
				}
				
			}
		};
		
		//Create a query that is triggered on insert and remove events from Weigh Station Window 
		EPStatement queryWeighStation= esperService.getProvider().getEPAdministrator().createEPL(
				"select irstream * from weighstation");
		queryWeighStation.addListener(weighStationListener);
		statements.add(queryWeighStation);
		
		// Create a listener to handle Alerts
		StatementAwareUpdateListener alertListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arrivals, EventBean[] departures,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arrivals != null) {
					for (EventBean bean : arrivals) {
						int alertType = (Integer) bean.get("alert_type");
						String id = (String) bean.get("tag_ID");
						AlertNotification alert = null;
						switch (alertType) {
						case 1:
							alert = new AlertNotification(EAlertType.Package_Moved_Backwards, id);
							break;
						case 2:
							alert = new AlertNotification(EAlertType.Package_Skipped_Dock_Door, id);
							break;
						case 3:
							alert = new AlertNotification(EAlertType.Package_is_Lost, id);
							break;

						}
						if (alert != null) {
							send(alert);
						}
					}
				}
			}
		};
		
		//Create a query that is triggered on insert and remove events from Weigh Station Window 
		EPStatement queryAlert= esperService.getProvider().getEPAdministrator().createEPL(
				"select * from alerts");
		queryAlert.addListener(alertListener);
		statements.add(queryAlert);
		
	}
	
	/**
	 * Iterate through all statements and stop them.
	 */
	protected void stop(){
		for(EPStatement statement : statements){
			statement.destroy();
		}
	}
	
	/**
	 * Method to use to send out notifications over JMS. It seriallizes the
	 * message into an array of bytes, then sends those bytes out over JMS
	 * 
	 * @param notification
	 */
	private void send(final Serializable notification) {
		if (template == null || destination == null) {
			// TODO: Log error message;
			return;
		}
		try {
			template.send(destination, new MessageCreator() {
				@Override
				public Message createMessage(Session arg0) throws JMSException {
					BytesMessage message = arg0.createBytesMessage();
					ByteArrayOutputStream bos = new ByteArrayOutputStream();
					try {
						ObjectOutput out = new ObjectOutputStream(bos);
						out.writeObject(notification);
						out.close();
						message.writeBytes(bos.toByteArray());
						return message;
					} catch (IOException e) {
						throw new JMSException(e.getMessage());
					}
				}
			});
		} catch (JmsException exception) {
			// TODO: log error message
			return;
		}
	}
}
