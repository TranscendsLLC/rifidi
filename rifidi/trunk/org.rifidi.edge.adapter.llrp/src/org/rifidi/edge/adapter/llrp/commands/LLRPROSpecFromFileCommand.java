/*
 *  LLRPRoSpecFromFileCommand.java
 *
 *  Created:	Apr 26, 2010
 *  Project:	Rifidi Edge Server - A middleware platform for RFID applications
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	GNU Public License (GPL)
 *  				http://www.opensource.org/licenses/gpl-3.0.html
 */
package org.rifidi.edge.adapter.llrp.commands;

import java.util.List;
import java.util.concurrent.TimeoutException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.generated.messages.ADD_ROSPEC;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC;
import org.llrp.ltk.generated.messages.GET_ROSPECS;
import org.llrp.ltk.generated.messages.GET_ROSPECS_RESPONSE;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.types.UnsignedInteger;
import org.rifidi.edge.adapter.llrp.AbstractLLRPCommand;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;

/**
 * This command takes an ADD_ROSPEC command loaded from a file and submits it.
 * It will delete all ROSpecs on the reader itself, add the loaded ROSpec, and
 * enable the ROSpec it just added.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class LLRPROSpecFromFileCommand extends AbstractLLRPCommand {

	private static final Log logger = LogFactory
			.getLog(LLRPROSpecFromFileCommand.class);

	/**
	 * The session.
	 */
	private LLRPReaderSession session = null;

	/**
	 * The ADD_ROSPEC command that will be loaded from a file and submitted.
	 */
	private ADD_ROSPEC addrospeccommand = null;

	/**
	 * @param commandID
	 */
	public LLRPROSpecFromFileCommand(String commandID) {
		super(commandID);
	}

	/**
	 * Sets the ROSpecCommand
	 * 
	 * @param addrospeccommand
	 */
	public void setAddrospeccommand(ADD_ROSPEC addrospeccommand) {
		this.addrospeccommand = addrospeccommand;

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.sensors.commands.TimeoutCommand#execute()
	 */
	@Override
	protected void execute() throws TimeoutException {
		this.session = (LLRPReaderSession) this.sensorSession;
		if (addrospeccommand == null) {
			logger.error("Can't add ROSpec: the ROSpec was not initialized "
					+ "correctly.  Check the XML file.  ");
			return;
		}
		// Find and delete all ROSpecs on the reader.
		GET_ROSPECS rospecs = new GET_ROSPECS();
		GET_ROSPECS_RESPONSE response = null;
		response = (GET_ROSPECS_RESPONSE) session.transact(rospecs);
		List<ROSpec> rospecList = response.getROSpecList();
		for (ROSpec rspec : rospecList) {
			DELETE_ROSPEC delROSpec = new DELETE_ROSPEC();
			delROSpec.setROSpecID(new UnsignedInteger(rspec.getROSpecID()
					.intValue()));

			// TODO: check the response?
			session.transact(delROSpec);

		}

		// Send the ADD_ROSPEC command
		// TODO: check the response?
		session.transact(addrospeccommand);

		// Enable the ROSpec
		ENABLE_ROSPEC enablerospec = new ENABLE_ROSPEC();
		enablerospec.setROSpecID(addrospeccommand.getROSpec().getROSpecID());
		// TODO: check the response?
		session.transact(enablerospec);

	}

}
