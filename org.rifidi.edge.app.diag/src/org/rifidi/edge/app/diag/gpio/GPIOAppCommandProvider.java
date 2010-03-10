/**
 * 
 */
package org.rifidi.edge.app.diag.gpio;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;

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
		// TODO Auto-generated method stub
		return null;
	}

}
