/*
 *  GPISimulatorAppCommandProvider.java
 *
 *  Created:	Mar 12, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.app.diag.gpio;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * @author Matthew Dean
 * 
 */
public class GPISimulatorAppCommandProvider implements CommandProvider {

	private volatile GPISimulatorApp simApp;

	/**
	 * @return the simApp
	 */
	public GPISimulatorApp getSimApp() {
		return simApp;
	}

	/**
	 * @param simApp the simApp to set
	 */
	public void setSimApp(GPISimulatorApp simApp) {
		this.simApp = simApp;
	}

	/**
	 * Command for changing the given GPIs to high.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _simGPIHigh(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: simGPIHigh <readerID> <port1> [port2]* ");
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
			intp.println("Usage: simGPIHigh <readerID> <port1> [port2]* ");
			return null;
		} else {
			this.simApp.simGPIHigh(readerArg, ports);
		}
		return null;
	}

	/**
	 * Command for changing the given GPIs to low.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _simGPILow(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: simGPILow <readerID> <readerID> <port1> [port2]*");
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
			intp.println("Usage: simGPILow <readerID> <readerID> <port1> [port2]*");
			return null;
		} else {
			this.simApp.simGPILow(readerArg, ports);
		}
		return null;
	}

	/**
	 * The command object for the GPIFlashHigh command. This command will flash
	 * the GPIs high for a few seconds, and then return them to low.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _simGPIFlashHigh(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: simGPIFlashHigh <readerID> <seconds> <port1> [port2]*");
			return null;
		}

		String secondString = intp.nextArgument();
		if (secondString == null || secondString.equals("")) {
			intp.println("Usage: simGPIFlashHigh <readerID> <seconds> <port1> [port2]*");
			return null;
		}
		Integer seconds;
		try {
			seconds = Integer.parseInt(secondString);
		} catch (NumberFormatException e) {
			intp.println("Usage: simGPIFlashHigh <readerID> <seconds> <port1> [port2]*");
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
			intp.println("Usage: simGPIFlashHigh <readerID> <seconds> <port1> [port2]*");
			return null;
		} else {
			this.simApp.simGPIFlashHigh(readerArg, seconds, ports);
		}
		return null;
	}

	/**
	 * The command object for the GPIFlashLow command. This command will flash
	 * the GPIs low for a few seconds, and then return them to high.
	 * 
	 * @param intp
	 * @return
	 */
	public Object _simGPIFlashLow(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: simGPIFlashLow <readerID> <seconds> <port1> [port2]*");
			return null;
		}

		String secondString = intp.nextArgument();
		if (secondString == null || secondString.equals("")) {
			intp.println("Usage: simGPIFlashLow <readerID> <seconds> <port1> [port2]*");
			return null;
		}
		Integer seconds;
		try {
			seconds = Integer.parseInt(secondString);
		} catch (NumberFormatException e) {
			intp.println("Usage: simGPIFlashLow <readerID> <seconds> <port1> [port2]*");
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
			intp.println("Usage: gpiFlashLow <readerID> <seconds> <port1> [port2]*");
			return null;
		} else {
			this.simApp.simGPIFlashLow(readerArg, seconds, ports);
		}
		return null;
	}

	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return "";
	}

}
