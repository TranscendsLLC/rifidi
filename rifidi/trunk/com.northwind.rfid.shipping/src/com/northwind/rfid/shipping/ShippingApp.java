/**
 * 
 */
package com.northwind.rfid.shipping;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.core.services.esper.EsperManagementService;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ShippingApp {

	/** Esper service */
	private volatile EsperManagementService esperService;
	/**All statements that have been defined so far*/
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();

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
			"insert into dockdoor select cast(tag.epc?, String) as tag_ID where readerID = 'Alien9800_1'" +
			"insert into weighstation select cast(tag.epc?, String) as tag_ID where readerID = 'Alien9800_2'"));
		
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
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window alerts.win:length(20) (alert_type int, tag_ID String)"));

		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"create window weighstation_recent.std:unique(tag_ID) (tag_ID String)"));
		
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"insert rstream into weighstation_recent select tag_ID from weighstation"));
		
		statements.add(esperService.getProvider().getEPAdministrator().createEPL(
			"on pattern[every-distinct(tag.tag_ID) tag=weighstation_recent -> dockdoor(tag_ID = tag.tag_ID)] " +
			"insert into alerts " +
			"select 1 as alert_type, tag_ID as tag_ID " +
				"from weighstation_recent where tag_ID = tag.tag_ID"));
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
						System.out.println("Dock Door arrive: " + bean.get("tag_ID"));
					}
				}
				
				if(departures!=null){
					for(EventBean bean : departures){
						System.out.println("Dock Door depart: " + bean.get("tag_ID"));
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
						System.out.println("Weigh Station Arrival: " + bean.get("tag_ID"));
					}
				}
				
				if(departures!=null){
					for(EventBean bean : departures){
						System.out.println("Weigh Station Departure: " + bean.get("tag_ID"));
					}
				}
				
			}
		};
		
		//Create a query that is triggered on insert and remove events from Weigh Station Window 
		EPStatement queryWeighStation= esperService.getProvider().getEPAdministrator().createEPL(
				"select irstream * from weighstation");
		queryWeighStation.addListener(weighStationListener);
		statements.add(queryWeighStation);
		
		// Create a listener to handle Weigh Station Events
		StatementAwareUpdateListener alertListener = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arrivals, EventBean[] departures,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arrivals != null) {
					System.out.println("Arivals: " + arrivals.length);
					for (EventBean bean : arrivals) {
						int alertType = (Integer) bean.get("alert_type");
						String id = (String) bean.get("tag_ID");
						switch (alertType) {
						case 1:
							System.out.println("Package moved backwards: " + id );
							break;

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
}
