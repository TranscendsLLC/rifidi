/* 
 *  EcSpecModelWrapper.java
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
import org.rifidi.edge.client.ale.models.serviceprovider.IEcSpecDataManager;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class EcSpecModelWrapper {

	private IEcSpecDataManager parent = null;

	private String name;
	private Log logger = LogFactory.getLog(EcSpecModelWrapper.class);

	/**
	 * @param string
	 */
	public EcSpecModelWrapper(String name, IEcSpecDataManager parent) {
		this.name = name;
		this.parent = parent;
	}

	// public EcSpecModelWrapper(){
	//		
	// }

	/**
	 * Gets the corresponding spec for that string from the server, returns null
	 * if error occured.
	 * 
	 * @return ECSpec
	 */
	public ECSpec getEcSpec() {// loadfromfile-local
		if (isParentRemote()) {
			GetECSpec parms = new GetECSpec();
			parms.setSpecName(this.name);
			try {
				return ((AleServicePortTypeWrapper) this.parent)
						.getAleServicePortType().getECSpec(parms);
			} catch (ImplementationExceptionResponse e) {
				logger.error(e.getMessage());
			} catch (NoSuchNameExceptionResponse e) {
				logger.debug(e.getMessage());
			} catch (SecurityExceptionResponse e) {
				logger.error(e.getMessage());
			}

		}
		return new ECSpec();
		// if(parent instanceof LocalFileStorage){
		// unmarshal file
		// }

	}

	/**
	 * @return the parent
	 */
	public IEcSpecDataManager getParent() {
		return parent;
	}

	/**
	 * @param parent
	 *            the parent to set
	 */
	public void setParent(IEcSpecDataManager parent) {
		this.parent = parent;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	private void disconnectParent() {
		if (isParentRemote())
			((AleServicePortTypeWrapper) parent).disconnect();

	}

	public String define() { // save-local
		if (isParentRemote()) {
			AleServicePortTypeWrapper wrapper = (AleServicePortTypeWrapper) this.parent;
			Define parms = new Define();
			parms.setSpecName(this.name);
			parms.setSpec(getEcSpec());
			String retVal = "";
			try {
				wrapper.getAleServicePortType().define(parms);
				return retVal;
			} catch (ImplementationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (SecurityExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (ECSpecValidationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (DuplicateNameExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			}
			return retVal;
		} else
			return "parent unknown Object";
		// if(parent instanceof LocalFileStorage)

	}

	public String undefine() { // delete-local
		if (isParentRemote()) {
			AleServicePortTypeWrapper wrapper = (AleServicePortTypeWrapper) this.parent;
			Undefine parms = new Undefine();
			parms.setSpecName(this.name);
			String retVal = "";
			try {
				wrapper.getAleServicePortType().undefine(parms);
				return retVal;
			} catch (ImplementationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (NoSuchNameExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (SecurityExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			}
			return retVal;
		} else
			return "parent unknown Object";
	}

	public String subscribe(String notificationUri) {
		if (isParentRemote()) {
			Subscribe parms = new Subscribe();
			parms.setNotificationURI(notificationUri);
			parms.setSpecName(this.name);
			String retVal = "";
			try {
				((AleServicePortTypeWrapper) this.parent)
						.getAleServicePortType().subscribe(parms);
				return retVal;
			} catch (ImplementationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (NoSuchNameExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (DuplicateSubscriptionExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (SecurityExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (InvalidURIExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			}
			return retVal;
		} else
			return "parent unknown Object";
	}

	public String unsubscribe(String notificationUri) {
		if (isParentRemote()) {
			Unsubscribe parms = new Unsubscribe();
			parms.setNotificationURI(notificationUri);
			parms.setSpecName(this.name);
			String retVal = "";
			try {
				((AleServicePortTypeWrapper) this.parent)
						.getAleServicePortType().unsubscribe(parms);
				return retVal;
			} catch (ImplementationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (NoSuchNameExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (NoSuchSubscriberExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (SecurityExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (InvalidURIExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			}
			return retVal;
		} else
			return "parent unknown Object";
	}

	public String poll() {
		if (isParentRemote()) {
			Poll parms = new Poll();
			parms.setSpecName(this.name);
			String retVal = "";
			try {
				((AleServicePortTypeWrapper) this.parent)
						.getAleServicePortType().poll(parms);
				return retVal;
			} catch (ImplementationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (NoSuchNameExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (SecurityExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			}
			return retVal;
		} else
			return "parent unknown Object";

	}

	public String immediate() {
		String retVal = "";
		if (isParentRemote()) {
			Immediate parms = new Immediate();
			parms.setSpec(getEcSpec());

			try {
				((AleServicePortTypeWrapper) this.parent)
						.getAleServicePortType().immediate(parms);
			} catch (ImplementationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (SecurityExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			} catch (ECSpecValidationExceptionResponse e) {
				retVal = e.getMessage();
				logger.error(retVal);
			}
			return retVal;
		} else
			return "parent unknown Object";

	}

	public ArrayList<String> getSubscribers() {
		if (isParentRemote()) {
			GetSubscribers parms = new GetSubscribers();
			parms.setSpecName(this.name);
			ArrayList<String> al;
			try {
				al = (ArrayList<String>) ((AleServicePortTypeWrapper) this.parent)
						.getAleServicePortType().getSubscribers(parms)
						.getString();
				return al;
			} catch (ImplementationExceptionResponse e) {
				logger.error(e.getMessage());
			} catch (NoSuchNameExceptionResponse e) {
				logger.error(e.getMessage());
			} catch (SecurityExceptionResponse e) {
				logger.error(e.getMessage());
			}

		}
		return new ArrayList<String>();

	}

	private boolean isParentRemote() {
		if (parent instanceof AleServicePortTypeWrapper)
			return true;
		else
			return false;
	}

	private boolean isParentLocal() {
		// if(parent instanceof LocalFileStorage)return true;
		// else
		return false;
	}

}
