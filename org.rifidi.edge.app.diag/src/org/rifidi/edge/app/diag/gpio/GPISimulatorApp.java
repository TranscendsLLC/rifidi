/*
 *  GPISimulatorApp.java
 *
 *  Created:	Mar 10, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.app.diag.gpio;

import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;
import org.rifidi.edge.core.services.notification.data.gpio.GPOEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class GPISimulatorApp {

	private static final Log logger = LogFactory.getLog(GPISimulatorApp.class);

	/** Esper service */
	protected volatile EsperManagementService esperService;

	protected final Set<EPStatement> statements = new CopyOnWriteArraySet<EPStatement>();

	/**
	 */
	public void start() {
		EPStatement query = esperService.getProvider().getEPAdministrator()
				.createEPL("select * from GPIEvent");
		statements.add(query);
		StatementAwareUpdateListener stateUpdateListener = getSessionStateUpdateListener();
		query.addListener(stateUpdateListener);
	}

	public StatementAwareUpdateListener getSessionStateUpdateListener() {
		return new StatementAwareUpdateListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see
			 * com.espertech.esper.client.StatementAwareUpdateListener#update
			 * (com.espertech.esper.client.EventBean[],
			 * com.espertech.esper.client.EventBean[],
			 * com.espertech.esper.client.EPStatement,
			 * com.espertech.esper.client.EPServiceProvider)
			 */
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				if (arg0 != null) {
					for (EventBean eb : arg0) {
						if (eb.getUnderlying() instanceof GPIEvent) {
							logger.debug(eb.getUnderlying());
							//System.out.println(eb.getUnderlying());
						}
						if (eb.getUnderlying() instanceof GPOEvent) {
							logger.debug(eb.getUnderlying());
							//System.out.println(eb.getUnderlying());
						}
					}
				}
			}
		};
	}

	/**
	 */
	public void stop() {
		for (EPStatement statement : statements) {
			statement.destroy();
		}
	}

	/*
	 * Set the given GPO ports high.
	 */
	public void simGPIHigh(String readerID, Set<Integer> ports) {
		for (Integer port : ports) {
			GPIEvent gpi = new GPIEvent(readerID, port, true);
			esperService.getProvider().getEPRuntime().sendEvent(gpi);
		}
	}

	/*
	 * Set the given GPO ports low.
	 */
	public void simGPILow(String readerID, Set<Integer> ports) {
		for (Integer port : ports) {
			GPIEvent gpi = new GPIEvent(readerID, port, false);
			esperService.getProvider().getEPRuntime().sendEvent(gpi);
		}
	}

	/*
	 * Flash the given GPO ports high for the given number of seconds.
	 */
	public void simGPIFlashHigh(String readerID, Integer seconds,
			Set<Integer> ports) {
		new GPIFlash(readerID, ports, seconds, true).start();
	}

	/*
	 * Flash the given GPO ports low for the given number of seconds.
	 */
	public void simGPIFlashLow(String readerID, Integer seconds,
			Set<Integer> ports) {
		new GPIFlash(readerID, ports, seconds, false).start();
	}

	/**
	 * Called by spring
	 * 
	 * @param esperService
	 *            the esperService to set
	 */
	public void setEsperService(EsperManagementService esperService) {
		//System.out.println("Setting esper");
		this.esperService = esperService;
	}


	/**
	 * A threaded class that can handle flashing the GPIs for a few seconds
	 * without delaying output.
	 * 
	 * @author Matthew Dean
	 */
	private class GPIFlash extends Thread {

		private String readerID;

		private Set<Integer> ports;

		private Integer seconds;

		private boolean highfirst;

		/**
		 * 
		 */
		public GPIFlash(String readerID, Set<Integer> ports, Integer seconds,
				boolean highfirst) {
			this.readerID = readerID;
			this.ports = ports;
			this.seconds = seconds;
			this.highfirst = highfirst;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		@Override
		public void run() {
			if (highfirst) {
				simGPIHigh(readerID, ports);
			} else {
				simGPILow(readerID, ports);
			}
			try {
				Thread.sleep(seconds * 1000);
			} catch (InterruptedException e) {
			}
			if (highfirst) {
				simGPILow(readerID, ports);
			} else {
				simGPIHigh(readerID, ports);
			}
		}
	}
}
