/* 
 *  ConvenienceAleServicePort.java
 *  Created:	Feb 12, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.fca;

import java.util.ArrayList;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.wsdl.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ECSpecValidationExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.InvalidURIExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECReports;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ConvenienceAleServicePort {
	
	private ALEServicePortType aleProxy=null;
	private String endPoint=null;

	public String getEndpoint(){
		return endPoint;
	}
	
	public void setEndpoint(String endPointName){
		this.endPoint=endPointName;
		
	}
	
	public void define(){
	

	}

	public void undefine(String specName)
			throws ImplementationExceptionResponse,
			NoSuchNameExceptionResponse, SecurityExceptionResponse {
	
	}

	public ECSpec getECSpec(String specName)
			throws ImplementationExceptionResponse,
			NoSuchNameExceptionResponse, SecurityExceptionResponse {

		return null;

	}

	public ArrayList<String> getECSpecNames()
			throws ImplementationExceptionResponse, SecurityExceptionResponse {
		return null;
	}

	public void subscribe(String specName, String notificationUri)
			throws ImplementationExceptionResponse,
			NoSuchNameExceptionResponse,
			DuplicateSubscriptionExceptionResponse, SecurityExceptionResponse,
			InvalidURIExceptionResponse {
	
	}

	public void unsubscribe(String specName, String notificationUri)
			throws ImplementationExceptionResponse,
			NoSuchNameExceptionResponse, NoSuchSubscriberExceptionResponse,
			SecurityExceptionResponse, InvalidURIExceptionResponse {
	
	}

	public ECReports poll(String specName)
			throws ImplementationExceptionResponse,
			NoSuchNameExceptionResponse, SecurityExceptionResponse {
		return null;
	}

	public ECReports immediate(String specFilePath)
			throws ImplementationExceptionResponse, SecurityExceptionResponse,
			ECSpecValidationExceptionResponse {
		return null;
	}

	public ArrayList<String> getSubscribers(String specName)
			throws ImplementationExceptionResponse,
			NoSuchNameExceptionResponse, SecurityExceptionResponse {
		return null;
	
	}

	public String getStandardVersion() throws ImplementationExceptionResponse {
		return null;
	}

	public String getVendorVersion(EmptyParms parms)
			throws ImplementationExceptionResponse {
		return null;
	}
	
	private void init(){
		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
		aleFactory.setServiceClass(ALEServicePortType.class);
		aleFactory.setAddress(endPoint);
		aleProxy = (ALEServicePortType) aleFactory.create();

	}

}
