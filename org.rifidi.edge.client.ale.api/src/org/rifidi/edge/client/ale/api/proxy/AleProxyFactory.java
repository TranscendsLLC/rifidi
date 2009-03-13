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
	private String baseUrl = null;
	private Log logger = LogFactory.getLog(AleProxyFactory.class);
	private ALEServicePortType sp = null;
	private ALELRServicePortType lrsp;

	/**
	 * 
	 */
	public AleProxyFactory(String baseUrl) {
		super();
		this.baseUrl = baseUrl;
	}

	public ALEServicePortType getAleServicePortType() {
		if (sp == null && !baseUrl.isEmpty()) {
			// init ALE
			String fullUrl = baseUrl + "/ALEService";
			logger
					.debug("\nJaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();");
			JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
			logger
					.debug("\naleFactory.setServiceClass(ALEServicePortType.class);");
			aleFactory.setServiceClass(ALEServicePortType.class);
			logger.debug("\naleEndPoint = " + fullUrl);
			aleFactory.setAddress(fullUrl);
			logger
					.debug("\naleProxy = (ALEServicePortType) aleFactory.create();");
			sp = (ALEServicePortType) aleFactory.create();
		}
		return sp;

	}

	public ALELRServicePortType getAleLrServicePortType() {

		if (lrsp == null && !baseUrl.isEmpty()) {
			// init ALELR
			String fullUrl = baseUrl + "/ALEService";
			logger
					.debug("\nJaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();");
			JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
			logger
					.debug("\nlrFactory.setServiceClass(ALELRServicePortType.class);");
			lrFactory.setServiceClass(ALELRServicePortType.class);
			logger.debug("\nlrFactory.setAddress(" + fullUrl + ");");
			lrFactory.setAddress(fullUrl);
			logger
					.debug("\nreaderProxy = (ALELRServicePortType) lrFactory.create();");
			lrsp = (ALELRServicePortType) lrFactory.create();
		}
		return lrsp;

	}

	/**
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}
}
