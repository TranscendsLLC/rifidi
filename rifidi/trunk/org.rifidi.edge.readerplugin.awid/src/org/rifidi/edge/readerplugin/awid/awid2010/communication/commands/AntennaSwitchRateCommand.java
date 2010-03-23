/**
 * 
 */
package org.rifidi.edge.readerplugin.awid.awid2010.communication.commands;

/**
 * @author kyle
 * 
 */
public class AntennaSwitchRateCommand extends AbstractAwidCommand {

	public AntennaSwitchRateCommand(byte antenna1Rate, byte antenna2Rate) {

		rawmessage = new byte[] { 0x07, 0x00, 0x1D, antenna1Rate, antenna2Rate };

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Antenna Switch Rate Command";
	}

}
