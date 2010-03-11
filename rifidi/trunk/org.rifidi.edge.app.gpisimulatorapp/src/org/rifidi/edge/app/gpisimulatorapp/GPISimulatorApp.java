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
package org.rifidi.edge.app.gpisimulatorapp;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.core.services.notification.data.gpio.GPIEvent;

/**
 * 
 * 
 * @author Matthew Dean
 */
public class GPISimulatorApp {

	/** Esper service */
	protected volatile EsperManagementService esperService;

	public void start() {
	}

	public void stop() {
	}

	/**
	 * 
	 * @param readerID
	 * @param ports
	 */
	public void simGPIHigh(String readerID, Set<Integer> ports) {
		for (Integer port : ports) {
			GPIEvent gpi = new GPIEvent(readerID, port, true);
			esperService.getProvider().getEPRuntime().sendEvent(gpi);
		}
	}

	/**
	 * 
	 * @param readerID
	 * @param ports
	 */
	public void simGPILow(String readerID, Set<Integer> ports) {
		for (Integer port : ports) {
			GPIEvent gpi = new GPIEvent(readerID, port, false);
			esperService.getProvider().getEPRuntime().sendEvent(gpi);
		}
	}

	/**
	 * 
	 * 
	 * @param readerID
	 * @param seconds
	 * @param ports
	 */
	public void simGPIFlashHigh(String readerID, Integer seconds,
			Set<Integer> ports) {
		new GPIFlash(readerID, ports, seconds, true).start();
	}

	/**
	 * 
	 * @param readerID
	 * @param seconds
	 * @param ports
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
		this.esperService = esperService;
	}

	/**
	 * 
	 * @param intp
	 * @return
	 */
	public Object _simGPIHigh(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: gpiHigh <readerID> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}

		Set<Integer> ports = new HashSet<Integer>();
		boolean done = false;
		while (!done) {
			String nextArg = intp.nextArgument();
			if (nextArg != null && !nextArg.equals("")) {

				Integer port = Integer.parseInt(nextArg);
				ports.add(port);
			} else {
				done = true;
			}
		}
		if (ports.isEmpty()) {
			intp.println("Usage: gpiHigh <readerID> <port1> "
					+ "[port2] [port3] ...");
			return null;
		} else {
			this.simGPIHigh(readerArg, ports);
		}
		return null;
	}

	public Object _simGPILow(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: gpiLow <readerID> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}

		Set<Integer> ports = new HashSet<Integer>();
		boolean done = false;
		while (!done) {
			String nextArg = intp.nextArgument();
			if (nextArg != null && !nextArg.equals("")) {

				Integer port = Integer.parseInt(nextArg);
				ports.add(port);
			} else {
				done = true;
			}
		}
		if (ports.isEmpty()) {
			intp.println("Usage: gpiLow <readerID> <port1> "
					+ "[port2] [port3] ...");
			return null;
		} else {
			this.simGPILow(readerArg, ports);
		}
		return null;
	}

	public Object _simGPIFlashHigh(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: gpiFlashHigh <readerID> <seconds> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}

		String secondString = intp.nextArgument();
		if (secondString == null || secondString.equals("")) {
			intp.println("Usage: gpiFlashHigh <readerID> <seconds> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}
		Integer seconds;
		try {
			seconds = Integer.parseInt(secondString);
		} catch (NumberFormatException e) {
			intp.println("Usage: gpiFlashHigh <readerID> <seconds> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}

		Set<Integer> ports = new HashSet<Integer>();
		boolean done = false;
		while (!done) {
			String nextArg = intp.nextArgument();
			if (nextArg != null && !nextArg.equals("")) {

				Integer port = Integer.parseInt(nextArg);
				ports.add(port);
			} else {
				done = true;
			}
		}
		if (ports.isEmpty()) {
			intp.println("Usage: gpiFlashHigh <readerID> <port1> "
					+ "[port2] [port3] ...");
			return null;
		} else {
			this.simGPIFlashHigh(readerArg, seconds, ports);
		}
		return null;
	}

	public Object _simGPIFlashLow(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: gpiFlashLow <readerID> <seconds> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}

		String secondString = intp.nextArgument();
		if (secondString == null || secondString.equals("")) {
			intp.println("Usage: gpiFlashLow <readerID> <seconds> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}
		Integer seconds;
		try {
			seconds = Integer.parseInt(secondString);
		} catch (NumberFormatException e) {
			intp.println("Usage: gpiFlashLow <readerID> <seconds> <port1> "
					+ "[port2] [port3] ...");
			return null;
		}

		Set<Integer> ports = new HashSet<Integer>();
		boolean done = false;
		while (!done) {
			String nextArg = intp.nextArgument();
			if (nextArg != null && !nextArg.equals("")) {

				Integer port = Integer.parseInt(nextArg);
				ports.add(port);
			} else {
				done = true;
			}
		}
		if (ports.isEmpty()) {
			intp.println("Usage: gpiFlashHigh <readerID> <port1> "
					+ "[port2] [port3] ...");
			return null;
		} else {
			this.simGPIFlashLow(readerArg, seconds, ports);
		}
		return null;
	}

	/**
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
