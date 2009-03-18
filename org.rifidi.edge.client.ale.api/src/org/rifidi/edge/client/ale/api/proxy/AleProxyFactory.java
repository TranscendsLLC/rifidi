/* 
 *  AleProxyFactory.java
 *  Created:	Mar 11, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.api
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.api.proxy;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AleProxyFactory {
	private String AleEndpoint = "", AlelrEndpoint = "";
	private Log logger = LogFactory.getLog(AleProxyFactory.class);
	private ALEServicePortType sp = null;
	private ALELRServicePortType lrsp = null;

	/**
	 * 
	 */
	public AleProxyFactory(String AleEndpoint, String AlelrEndpoint) {
		this.AleEndpoint = AleEndpoint;
		this.AlelrEndpoint = AlelrEndpoint;
	}

	public ALEServicePortType getAleServicePortType() {
		if (sp == null && !AleEndpoint.isEmpty()) {
			// init ALE

			logger
					.debug("\nJaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();");
			JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
			logger
					.debug("\naleFactory.setServiceClass(ALEServicePortType.class);");
			aleFactory.setServiceClass(ALEServicePortType.class);
			logger.debug("\naleEndPoint = " + AleEndpoint);
			aleFactory.setAddress(AleEndpoint);
			logger
					.debug("\naleProxy = (ALEServicePortType) aleFactory.create();");
			sp = (ALEServicePortType) aleFactory.create();
		}
		return sp;

	}

	public ALELRServicePortType getAleLrServicePortType() {

		if (lrsp == null && !AlelrEndpoint.isEmpty()) {
			// init ALELR

			logger
					.debug("\nJaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();");
			JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
			logger
					.debug("\nlrFactory.setServiceClass(ALELRServicePortType.class);");
			lrFactory.setServiceClass(ALELRServicePortType.class);
			logger.debug("\nlrFactory.setAddress(" + AlelrEndpoint + ");");
			lrFactory.setAddress(AlelrEndpoint);
			logger
					.debug("\nreaderProxy = (ALELRServicePortType) lrFactory.create();");
			lrsp = (ALELRServicePortType) lrFactory.create();
		}
		return lrsp;

	}

	/**
	 * @return the aleEndpoint
	 */
	public String getAleEndpoint() {
		return AleEndpoint;
	}

	/**
	 * @param aleEndpoint
	 *            the aleEndpoint to set
	 */
	public void setAleEndpoint(String aleEndpoint) {
		AleEndpoint = aleEndpoint;
	}

	/**
	 * @return the alelrEndpoint
	 */
	public String getAlelrEndpoint() {
		return AlelrEndpoint;
	}

	/**
	 * @param alelrEndpoint
	 *            the alelrEndpoint to set
	 */
	public void setAlelrEndpoint(String alelrEndpoint) {
		AlelrEndpoint = alelrEndpoint;
	}

	public String getServerName() {
		String retVal = "";
		if (!AleEndpoint.isEmpty()) {
			try {
				URL url = new URL(AleEndpoint);
				retVal = url.getHost();
			} catch (MalformedURLException e) {
				logger.error(e.getMessage());
			} 
			

		}
		return retVal;
	}
}
