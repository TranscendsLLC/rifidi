/*
 *  TestBarcodeCommandProvider.java
 *
 *  Created:	Apr 28, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.readerplugin.barcode.test;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

/**
 * This class provides the commands for testing a barcode reader.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class TestBarcodeCommandProvider implements CommandProvider {

	/** Object to send the message */
	public volatile TestBarcode testBarcode;

	/**
	 * 
	 * @param intp
	 * @return
	 */
	public Object _barcode(CommandInterpreter intp) {
		String arg = intp.nextArgument();
		String msgtosend = "";
		if (arg == null || arg.equals("")) {
			msgtosend = "000001044";
		} else {
			StringBuilder b = new StringBuilder();
			for (char c : arg.toCharArray()) {
				if(Character.isDigit(c)){
					b.append(c);
				}
			}
			msgtosend = b.toString();
		}

		testBarcode.sendMessage(msgtosend);
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		StringBuilder retval = new StringBuilder();
		retval.append("---Barcode Commands---\n");
		retval.append("\tbarcode [<tagid>]");
		retval.append(" - Sends a tag that you enter to the barcode "
				+ "reader.  If you enter a tagid, the value must "
				+ "be 10 characters long.  If it isn't, an error "
				+ "will be thrown.  \n");

		return retval.toString();
	}

	/**
	 * Called by spring.
	 * 
	 * @param testbar
	 */
	public void setTestBarcode(TestBarcode testbar) {
		this.testBarcode = testbar;
	}

}
