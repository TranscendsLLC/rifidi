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
import org.rifidi.edge.sensors.CannotExecuteException;

/**
 * @author kyle
 * 
 */
public class GPIOAppCommandProvider implements CommandProvider {

	private volatile GPIOApp gpioApp;

	public Object _testGPI(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: testGPI <readerID> <port>");
			return null;
		}

		String portArg = intp.nextArgument();
		if (portArg == null || portArg.equals("")) {
			intp.println("Usage: testGPI <readerID> <port>");
			return null;
		}
		try {
			int port = Integer.parseInt(portArg);
			if (port < 0 || port > 3) {
				intp.println("port must be an integer 0-3");
				return null;
			}

			boolean status = gpioApp.testGPI(readerArg, port);
			if (status) {
				intp.println("Port " + port + " is high");
			} else {
				intp.println("Port " + port + " is low");
			}

		} catch (NumberFormatException e) {
			intp.println("port must be an integer 0-3");
		} catch (CannotExecuteException e) {
			intp.println(e.getMessage());
		}

		return null;
	}

	public Object _setGPO(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: setGPO <readerID> [<port>]*");
			return null;
		}

		Set<Integer> ports = new HashSet<Integer>();
		try {
			String portArg = intp.nextArgument();
			while (portArg != null && !portArg.isEmpty()) {
				ports.add(Integer.parseInt(portArg));
				portArg = intp.nextArgument();
			}
			System.out.println("SET PORTS: " + ports);
			gpioApp.setGPO(readerArg, ports);
		} catch (NumberFormatException ex) {
			intp.println("port must be an integer");
		} catch (CannotExecuteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	public Object _flashGPO(CommandInterpreter intp) {
		String readerArg = intp.nextArgument();
		if (readerArg == null || readerArg.equals("")) {
			intp.println("Usage: flashGPO <readerID> <port>");
			return null;
		}

		try {
			String portArg = intp.nextArgument();
			int port;
			if (portArg != null && !portArg.isEmpty()) {
				port = Integer.parseInt(portArg);
			} else {
				intp.println("port must be an integer");
				return null;
			}
			gpioApp.flashGPO(readerArg, port, 4);
		} catch (NumberFormatException ex) {
			intp.println("port must be an integer");
		} catch (CannotExecuteException e) {
			intp.println(e.getMessage());
		}
		return null;
	}

	/**
	 * @param gpioApp
	 *            the gpioApp to set
	 */
	public void setGpioApp(GPIOApp gpioApp) {
		this.gpioApp = gpioApp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer retVal = new StringBuffer();
		retVal.append("  ---Diagnostic GPIO App Commands---\n");
		retVal.append("\ttestGPI <readerID> <port> - "
				+ "Returns the GPI value of the given reader's port");
		retVal.append("\tsetGPO <readerID> [<port>]*"
				+ " - Sets the given ports to high.  Any ports not "
				+ "mentioned that are currently high will be "
				+ "set to low.  \n");
		retVal.append("\tflashGPO <readerID> <port> - "
				+ "Flashes the given GPO port high for 4 seconds.  \n");
		return retVal.toString();
	}

}
