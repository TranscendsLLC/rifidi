/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.service;

import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.RifidiECSpec;
import org.rifidi.edge.epcglobal.aleread.Trigger;
import org.rifidi.edge.esper.EsperManagementService;

import com.espertech.esper.client.EPServiceProvider;

/**
 * This factory creates triggers based on their URI. Right now only the time
 * based trigger specified in ALE 1.1 is available.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class TriggerFactoryServiceImpl implements TriggerFactoryService {
	/** Esper instance to use. */
	private EPServiceProvider esper;


	/**
	 * @param esperManagement
	 *            the esperManagement to set
	 */
	public void setEsperManagement(EsperManagementService esperManagement) {
		esper = esperManagement.getProvider();
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.epcglobal.aleread.service.TriggerFactoryService#createTrigger(java.lang.String, org.rifidi.edge.epcglobal.aleread.RifidiECSpec)
	 */
	public Trigger createTrigger(String uri)
			throws InvalidURIExceptionResponse {

		// TODO: throw an exception if esper is not there{
		if (uri.startsWith("urn:epcglobal:ale:trigger:rtc:")) {
			String[] values = uri.substring(31).split(".");
			try {
				// <period>.<offset>.<timezone>
				// the first value is the period and has to be supplied
				Long period = Long.parseLong(values[0]);
				Long offset = 0l;
				Long timezone = 0l;
				// get the offset if it exists
				if (values.length > 1) {
					offset = Long.parseLong(values[1]);
				}
				// get the timezone if it exists
				if (values.length == 3) {
					if (!(values[2].charAt(0) == 'Z')) {
						String[] timeVals = values[2].substring(1).split(":");
						timezone = (long) (Integer.parseInt(timeVals[0]) * 60 + Integer
								.parseInt(timeVals[1])) * 60 * 1000;
						if (values[2].charAt(0) == '-') {
							timezone *= -1;
						}
					}
				}
				return new Trigger(period, offset, timezone, esper.getEPRuntime());
			} catch (NumberFormatException e) {
				throw new InvalidURIExceptionResponse(uri
						+ " is not a valid trigger URI.");
			} catch (ArrayIndexOutOfBoundsException e) {
				throw new InvalidURIExceptionResponse(uri
						+ " is not a valid trigger URI.");
			}
		}
		throw new InvalidURIExceptionResponse(uri
				+ " is not a valid trigger URI.");

	}
}
