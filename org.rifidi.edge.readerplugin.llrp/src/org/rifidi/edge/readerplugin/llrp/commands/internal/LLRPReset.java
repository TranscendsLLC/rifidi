package org.rifidi.edge.readerplugin.llrp.commands.internal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.llrp.ltk.generated.enumerations.StatusCode;
import org.llrp.ltk.generated.messages.DELETE_ACCESSSPEC;
import org.llrp.ltk.generated.messages.DELETE_ACCESSSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC;
import org.llrp.ltk.generated.messages.DELETE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.GET_ACCESSSPECS;
import org.llrp.ltk.generated.messages.GET_ACCESSSPECS_RESPONSE;
import org.llrp.ltk.generated.messages.GET_ROSPECS;
import org.llrp.ltk.generated.messages.GET_ROSPECS_RESPONSE;
import org.llrp.ltk.generated.parameters.AccessSpec;
import org.llrp.ltk.generated.parameters.ROSpec;
import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.core.sensors.commands.Command;
import org.rifidi.edge.readerplugin.llrp.LLRPReaderSession;

/**
 * This command gets all the ROSpecs and acessSpecs on the reader and deletes
 * them all.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class LLRPReset extends Command {

	private final static Log logger = LogFactory.getLog(LLRPReset.class);

	public LLRPReset(String commandID) {
		super(commandID);
	}

	@Override
	public void run() {
		LLRPReaderSession session = (LLRPReaderSession) this.sensorSession;

		// Get a list of all ROSpecs on the reader
		GET_ROSPECS getROSpecs = new GET_ROSPECS();
		LLRPMessage ret = session.transact(getROSpecs);
		if (ret instanceof GET_ROSPECS_RESPONSE) {
			GET_ROSPECS_RESPONSE getRospecsResponse = (GET_ROSPECS_RESPONSE) ret;
			// step through each ROSpec and delete each one
			for (ROSpec rs : getRospecsResponse.getROSpecList()) {
				DELETE_ROSPEC drs = new DELETE_ROSPEC();
				drs.setROSpecID(rs.getROSpecID());
				LLRPMessage m = session.transact(drs);
				if (m instanceof DELETE_ROSPEC_RESPONSE) {
					DELETE_ROSPEC_RESPONSE drsr = (DELETE_ROSPEC_RESPONSE) m;
					if (!drsr.getLLRPStatus().getStatusCode().isValidValue(
							StatusCode.M_Success)) {
						logger.warn("Could not delete ROSpec with ID "
								+ drs.getROSpecID());
					}
				} else {
					logger.warn("Expecting DELETE_ROSPEC_RESPONSE"
							+ ",  but got message of type " + m.getName());
				}
			}
		} else {
			logger.warn("Expecting GET_ROSPECS_RESPONSE"
					+ ", but got message of type " + ret.getName());
		}

		// Get a list of all AccessSpecs on the reader
		GET_ACCESSSPECS getAccessSpecs = new GET_ACCESSSPECS();
		ret = session.transact(getAccessSpecs);
		if (ret instanceof GET_ACCESSSPECS_RESPONSE) {
			GET_ACCESSSPECS_RESPONSE getAccessspecsResponse = (GET_ACCESSSPECS_RESPONSE) ret;
			// step through each AccsessSpecs and delete each one
			for (AccessSpec as : getAccessspecsResponse.getAccessSpecList()) {
				DELETE_ACCESSSPEC das = new DELETE_ACCESSSPEC();
				das.setAccessSpecID(as.getAccessSpecID());
				LLRPMessage m = session.transact(das);
				if (m instanceof DELETE_ACCESSSPEC_RESPONSE) {
					DELETE_ACCESSSPEC_RESPONSE dasr = (DELETE_ACCESSSPEC_RESPONSE) m;
					if (!dasr.getLLRPStatus().getStatusCode().isValidValue(
							StatusCode.M_Success)) {
						logger.warn("Could not delete AccessSpec with ID "
								+ das.getAccessSpecID());
					}
				} else {
					logger.warn("Expecting DELETE_ACCESSSPEC_RESPONSE"
							+ ",  but got message of type " + m.getName());
				}
			}
		} else {
			logger.warn("Expecting GET_ACCESSSPEC_RESPONSE"
					+ ", but got message of type " + ret.getName());
		}
	}
}
