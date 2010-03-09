/**
 * 
 */
package com.csc.rfid.toolcrib.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.EPCGeneration2Event;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

import com.csc.rfid.toolcrib.test.esper.CollectionType;
import com.csc.rfid.toolcrib.test.esper.StartCollectionEvent;
import com.csc.rfid.toolcrib.test.esper.StopCollectionEvent;
import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * @author Owner
 * 
 */
public class DataCollectorApp {

	/** Esper service */
	private volatile EsperManagementService esperService;
	/** All statements that have been defined so far */
	private final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();
	private final List<TestRunData> data = new ArrayList<TestRunData>();
	private Logger dataLogger;
	private final Log logger = LogFactory.getLog(DataCollectorApp.class);

	public void start() {
		configureLogger();
		esperService.getProvider().getEPAdministrator().getConfiguration()
				.addEventType("StartCollectionEvent",
						StartCollectionEvent.class);
		esperService.getProvider().getEPAdministrator().getConfiguration()
				.addEventType("StopCollectionEvent", StopCollectionEvent.class);

		// esper statement that listens to add and remove events from the window
		EPStatement queryAllTags = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select * from pattern[every start=StartCollectionEvent -> "
								+ "tag=ReadCycle[select * from tags] "
								+ "until StopCollectionEvent]");

		// add a listener to the above statement
		queryAllTags.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {

				if (arg0 != null) {
					for (EventBean b : arg0) {
						ArrayList<TagReadEvent> tagReadEvents = new ArrayList<TagReadEvent>();
						TagReadEvent[] tags = (TagReadEvent[]) b.get("tag");
						if (tags != null) {
							for (TagReadEvent e : tags) {
								tagReadEvents.add(e);
							}
						}
						StartCollectionEvent startEvent = (StartCollectionEvent) (b
								.get("start"));
						TestRunData testRun = new TestRunData(tagReadEvents,
								startEvent.getCollectionType());
						data.add(testRun);
						dataLogger.debug(testRun.getPrintString());
						logger.info(testRun.getPrintString());
						BitSet ports = new BitSet();

						if (tags != null && tags.length > 0) {
							ports.set(0);
						} else {
							ports.clear(0);
						}

					}
				}

			}
		});
		statements.add(queryAllTags);

		EPStatement upStateListener = esperService.getProvider()
				.getEPAdministrator().createEPL("select * from SessionUpEvent");

		EPStatement downStateListener = esperService.getProvider()
				.getEPAdministrator().createEPL(
						"select * from SessionDownEvent");

		EPStatement gpiListener = esperService.getProvider()
				.getEPAdministrator().createEPL("select * from GPIEvent");

		StatementAwareUpdateListener listener = new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean event : arg0) {
						logger.info(event.getUnderlying());
					}
				}

			}
		};

		upStateListener.addListener(listener);
		downStateListener.addListener(listener);
		gpiListener.addListener(listener);

		statements.add(upStateListener);
		statements.add(downStateListener);
		statements.add(gpiListener);
		

		EPStatement queryGPIs = esperService.getProvider().getEPAdministrator().createEPL(
			"select * from pattern [every gpievent=GPIEvent(state=false, readerID='Alien_1')->" +
				"tags=ReadCycle(readerID='Alien_1')[select * from tags] " +
				"until timer:interval(10 sec)]");
		queryGPIs.addListener(new StatementAwareUpdateListener() {

			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean b : arg0) {
						ArrayList<TagReadEvent> tagReadEvents = new ArrayList<TagReadEvent>();
						TagReadEvent[] tags = (TagReadEvent[]) b.get("tags");
						GPIEvent gpiEvent = (GPIEvent) b.get("gpievent");
						if (tags != null) {
							for (TagReadEvent e : tags) {
								tagReadEvents.add(e);
							}
						}
						if (gpiEvent.getPort() == 1 && !tagReadEvents.isEmpty()) {
							System.out.println("Inbound: "
									+ ((EPCGeneration2Event) tagReadEvents.get(
											0).getTag()).getEpc());
						} else if (gpiEvent.getPort() == 2
								&& !tagReadEvents.isEmpty()) {
							System.out.println("Outbound: "
									+ ((EPCGeneration2Event) tagReadEvents.get(
											0).getTag()).getEpc());
						}

					}
				}

			}
		});
		statements.add(queryGPIs);

		logger.info("Toolcrib test app started.");
	}

	public TestRunData getLastTestRunData() {
		if (data.size() > 0)
			return this.data.get(data.size() - 1);
		else
			return null;
	}

	public void stop() {
		deconfigureLogger();
		for (EPStatement statement : statements) {
			statement.destroy();
		}
		esperService.getProvider().getEPAdministrator().getConfiguration()
				.removeEventType("StartCollectionEvent", true);
		esperService.getProvider().getEPAdministrator().getConfiguration()
				.removeEventType("StopCollectionEvent", true);
		logger.info("toolcrib test app stopped");
	}

	public void startCollection(CollectionType type) {
		esperService.getProvider().getEPRuntime().sendEvent(
				new StartCollectionEvent(type));
	}

	public void stopCollection() {
		esperService.getProvider().getEPRuntime().sendEvent(
				new StopCollectionEvent());
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

	private void configureLogger() {
		try {
			dataLogger = Logger.getRootLogger().getLoggerRepository()
					.getLogger("TOOLCRIB");
			dataLogger.setLevel(Level.ALL);
			PatternLayout layout = new PatternLayout("%n%m");
			String appFolder = "applications";
			String fileSep = System.getProperty("file.separator");
			String fileName = "toolcrib-test.log";
			String path = appFolder + fileSep + "toolcrib-test" + fileSep
					+ fileName;
			FileAppender appender = new RollingFileAppender(layout, path);
			dataLogger.addAppender(appender);
		} catch (IOException e) {
			logger.error("IOException when configuring Toolcrib logger");
		}
	}

	private void deconfigureLogger() {
		dataLogger.removeAllAppenders();
	}

}
