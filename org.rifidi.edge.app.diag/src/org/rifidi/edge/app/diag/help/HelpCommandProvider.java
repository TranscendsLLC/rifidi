/*
 *  HelpCommandProvider.java
 *
 *  Created:	Mar 12, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.app.diag.help;

import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * This class provides help for all the diagnostic commands
 * 
 * @author Matthew Dean
 */
public class HelpCommandProvider implements CommandProvider {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuffer retVal = new StringBuffer();

		retVal.append("---Rifidi Edge Server Diagnostic Commands---\n");
		retVal.append("  ----GPIO Test Commands----\n");
		retVal.append("\ttestGPI <readerID> <port> - "
				+ "Returns the GPI value of the given reader's port");
		retVal.append("\tsetGPO <readerID> [<port>]*"
				+ " - Sets the given ports to high.  Any ports not "
				+ "mentioned that are currently high will be "
				+ "set to low.  \n");
		retVal.append("\tflashGPO <readerID> <port> - "
				+ "Flashes the given GPO port high for 4 seconds.  \n");
		retVal.append("  ----GPIO Simulation Commands----\n");
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
		retVal.append("  ----Reader Tag Commands----\n");
		retVal.append("\trecenttags <readerID> - Prints out a list of "
				+ "tags recently seen on the given reader.  \n");
		retVal.append("\tcurrenttags <readerID> - Prints out a list of tags"
				+ "currently seen by the given reader.  \n");

		return retVal.toString();
	}

}
