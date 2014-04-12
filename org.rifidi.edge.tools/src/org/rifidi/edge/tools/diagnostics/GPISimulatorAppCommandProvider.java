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
		StringBuffer retVal = new StringBuffer();
		retVal.append("  ---Diagnostic GPIO Simulation App Commands---\n");
		retVal.append("\tsimGPIHigh <readerID> <port1> [port2]*"
				+ " - Simulates setting the given GPI ports to high for "
				+ "the given reader\n");
		retVal.append("\tsimGPILow <readerID> <port1> [port2]*"
				+ " - Simulates setting the given GPI ports to low for "
				+ "the given reader\n");
		retVal.append("\tsimGPIFlashHigh <readerID> <seconds> <port1> [port2]*"
				+ " - Simulates setting the given GPI ports to high for "
				+ "the given reader, then setting them back to high after the "
				+ "given duration has passed\n");
		retVal.append("\tsimGPIFlashLow <readerID> <seconds> <port1> [port2]*"
				+ " - Simulates setting the given GPI ports to low for "
				+ "the given reader, then setting them back to high after the "
				+ "given duration has passed\n");
		return retVal.toString();
	}

}
