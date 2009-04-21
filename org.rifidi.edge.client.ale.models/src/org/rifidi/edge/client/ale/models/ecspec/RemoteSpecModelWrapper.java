/* 
 *  RemoteSpecModelWrapper.java
 *  Created:	Apr 13, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.models
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.models.ecspec;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Define;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ECSpecValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.GetECSpec;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.GetSubscribers;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Immediate;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.InvalidURIExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Poll;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Subscribe;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Undefine;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.Unsubscribe;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.serviceprovider.SpecDataManager;
import org.rifidi.edge.client.ale.models.serviceprovider.SpecModelWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class RemoteSpecModelWrapper extends SpecModelWrapper {

	private Log logger = LogFactory.getLog(RemoteSpecModelWrapper.class);
	

	/**
	 * @param name
	 * @param parent
	 */
	public RemoteSpecModelWrapper(String name, SpecDataManager parent) {
		super(name, parent);

	}

	/**
	 * Gets the corresponding spec for that string from the server, returns null
	 * if error occured.
	 * 
	 * @return ECSpec
	 */
	public ECSpec getExtEcSpec() {// loadfromfile-local

		GetECSpec parms = new GetECSpec();
		parms.setSpecName(this.name);
		try {
			return ((AleServicePortTypeWrapper) this.parent)
					.getAleServicePortType().getECSpec(parms);
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (NoSuchNameExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (SecurityExceptionResponse e) {
			logger.debug(e.getMessage());
		}

		return new ECSpec();

	}

	// private void disconnectParent() {
	// ((AleServicePortTypeWrapper) parent).disconnect();
	//
	// }

	public String define() { // save-local

		AleServicePortTypeWrapper wrapper = (AleServicePortTypeWrapper) this.parent;
		Define parms = new Define();
		parms.setSpecName(this.name);
		parms.setSpec(this.ecSpec);
		String retVal = "";
		try {
			wrapper.getAleServicePortType().define(parms);

		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ECSpecValidationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (DuplicateNameExceptionResponse e) {
			logger.debug(e.getMessage() + "trying undefine->define");
			undefine();
			define();
		}
		return retVal;

	}

	public String undefine() { // delete-local

		AleServicePortTypeWrapper wrapper = (AleServicePortTypeWrapper) this.parent;
		Undefine parms = new Undefine();
		parms.setSpecName(this.name);
		String retVal = "";
		try {
			wrapper.getAleServicePortType().undefine(parms);

		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;

	}

	public String subscribe(String notificationUri) {

		Subscribe parms = new Subscribe();
		parms.setNotificationURI(notificationUri);
		parms.setSpecName(this.name);
		String retVal = "";
		try {
			((AleServicePortTypeWrapper) this.parent).getAleServicePortType()
					.subscribe(parms);

		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (DuplicateSubscriptionExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (InvalidURIExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;

	}

	public String unsubscribe(String notificationUri) {

		Unsubscribe parms = new Unsubscribe();
		parms.setNotificationURI(notificationUri);
		parms.setSpecName(this.name);
		String retVal = "";
		try {
			((AleServicePortTypeWrapper) this.parent).getAleServicePortType()
					.unsubscribe(parms);

		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchSubscriberExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (InvalidURIExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;

	}

	public String poll() {

		Poll parms = new Poll();
		parms.setSpecName(this.name);
		String retVal = "";
		try {
			((AleServicePortTypeWrapper) this.parent).getAleServicePortType()
					.poll(parms);

		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;

	}

	public String immediate() {
		String retVal = "";

		Immediate parms = new Immediate();
		parms.setSpec(getEcSpec());

		try {
			((AleServicePortTypeWrapper) this.parent).getAleServicePortType()
					.immediate(parms);
		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ECSpecValidationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;

	}

	public ArrayList<String> getSubscribers() {

		GetSubscribers parms = new GetSubscribers();
		parms.setSpecName(this.name);
		ArrayList<String> al;
		try {
			al = (ArrayList<String>) ((AleServicePortTypeWrapper) this.parent)
					.getAleServicePortType().getSubscribers(parms).getString();
			return al;
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (NoSuchNameExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (SecurityExceptionResponse e) {
			logger.debug(e.getMessage());
		}

		return new ArrayList<String>();

	}

}
