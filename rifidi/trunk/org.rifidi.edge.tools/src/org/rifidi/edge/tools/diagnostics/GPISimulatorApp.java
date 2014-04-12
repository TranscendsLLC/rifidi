/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.tools.diagnostics;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.api.AbstractRifidiApp;
import org.rifidi.edge.notification.GPIEvent;
import org.rifidi.edge.notification.GPOEvent;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class GPISimulatorApp extends AbstractRifidiApp {

	private static final Log logger = LogFactory.getLog(GPISimulatorApp.class);

	/**
	 * @param group
	 * @param name
	 */
	public GPISimulatorApp(String group, String name) {
		super(group, name);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#_start()
	 */
	protected void _start() {

		StatementAwareUpdateListener stateUpdateListener = getSessionStateUpdateListener();

		addStatement("select * from GPIEvent", stateUpdateListener);
		addStatement("select * from GPOEvent", stateUpdateListener);
	}
	
	/* (non-Javadoc)
	 * @see org.rifidi.edge.api.AbstractRifidiApp#lazyStart()
	 */
	@Override
	public boolean lazyStart() {
		String lazyStart= getProperty(LAZY_START, "true");
		return Boolean.parseBoolean(lazyStart);
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
							// System.out.println(eb.getUnderlying());
						}
						if (eb.getUnderlying() instanceof GPOEvent) {
							logger.debug(eb.getUnderlying());
							// System.out.println(eb.getUnderlying());
						}
					}
				}
			}
		};
	}

	/*
	 * Set the given GPO ports high.
	 */
	public void simGPIHigh(String readerID, Set<Integer> ports) {
		for (Integer port : ports) {
			GPIEvent gpi = new GPIEvent(readerID, port, true);
			getEPRuntime().sendEvent(gpi);
		}
	}

	/*
	 * Set the given GPO ports low.
	 */
	public void simGPILow(String readerID, Set<Integer> ports) {
		for (Integer port : ports) {
			GPIEvent gpi = new GPIEvent(readerID, port, false);
			getEPRuntime().sendEvent(gpi);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.api.AbstractRifidiApp#getCommandProider()
	 */
	@Override
	protected CommandProvider getCommandProvider() {
		GPISimulatorAppCommandProvider commandProvider = new GPISimulatorAppCommandProvider();
		commandProvider.setSimApp(this);
		return commandProvider;
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
				Thread.currentThread().interrupt();
			}
			if (highfirst) {
				simGPILow(readerID, ports);
			} else {
				simGPIHigh(readerID, ports);
			}
		}
	}
}
