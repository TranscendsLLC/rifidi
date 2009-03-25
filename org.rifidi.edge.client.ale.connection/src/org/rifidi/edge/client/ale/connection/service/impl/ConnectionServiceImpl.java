/* 
 *  ConnectionServiceImpl.java
 *  Created:	Mar 19, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.connection
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.connection.service.impl;

import java.net.URL;
import java.util.ArrayList;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.connection.listeners.EndpointChangeListener;
import org.rifidi.edge.client.ale.connection.service.ConnectionService;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ConnectionServiceImpl implements ConnectionService {
	private ALEServicePortType aleServicePortType = null;
	private ALELRServicePortType lrServicePortType = null;
	private URL aleEndpoint = null;
	private URL lrEndpoint = null;
	private ArrayList<EndpointChangeListener> aleListeners = null;
	private ArrayList<EndpointChangeListener> lrListeners = null;
	private JaxWsProxyFactoryBean aleFactory = null;
	private JaxWsProxyFactoryBean lrFactory = null;

	/**
	 * 
	 */
	public ConnectionServiceImpl() {
		aleListeners = new ArrayList<EndpointChangeListener>();
		lrListeners = new ArrayList<EndpointChangeListener>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * getAleEndpoint()
	 */
	@Override
	public URL getAleEndpoint() {
		return aleEndpoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.ale.connection.service.ConnectionService#getLrEndpoint
	 * ()
	 */
	@Override
	public URL getLrEndpoint() {
		return lrEndpoint;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * setAleEndpoint(java.net.URL)
	 */
	@Override
	public void setAleEndpoint(URL endpoint) {
		URL temp = aleEndpoint;
		this.aleEndpoint = endpoint;
		fireAleChange(temp, endpoint);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.ale.connection.service.ConnectionService#setLrEndpoint
	 * (java.net.URL)
	 */
	@Override
	public void setLrEndpoint(URL endpoint) {
		URL temp = lrEndpoint;
		this.lrEndpoint = endpoint;
		fireLrChange(temp, endpoint);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * subscribeAleChangeListener
	 * (org.rifidi.edge.client.ale.webservice.listeners.EndpointChangeListener)
	 */
	@Override
	public void subscribeAleChangeListener(EndpointChangeListener listener) {
		aleListeners.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * subscribeLRChangeListener
	 * (org.rifidi.edge.client.ale.webservice.listeners.EndpointChangeListener)
	 */
	@Override
	public void subscribeLRChangeListener(EndpointChangeListener listener) {
		lrListeners.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * unsubscribeAleChangeListener
	 * (org.rifidi.edge.client.ale.webservice.listeners.EndpointChangeListener)
	 */
	@Override
	public void unsubscribeAleChangeListener(EndpointChangeListener listener) {
		aleListeners.remove(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * unsubscribeLRChangeListener
	 * (org.rifidi.edge.client.ale.webservice.listeners.EndpointChangeListener)
	 */
	@Override
	public void unsubscribeLRChangeListener(EndpointChangeListener listener) {
		lrListeners.remove(listener);

	}

	private void fireAleChange(URL oldEp, URL newEp) {
		if (aleFactory == null) {
			aleFactory = new JaxWsProxyFactoryBean();
			aleFactory.setServiceClass(ALEServicePortType.class);
		}

		aleFactory.setAddress(newEp.toString());
		aleServicePortType = (ALEServicePortType) aleFactory.create();
		for (EndpointChangeListener listener : aleListeners) {
			listener.endpointChanged(oldEp, newEp);
		}
	}

	private void fireLrChange(URL oldEp, URL newEp) {
		if (lrFactory == null) {
			lrFactory = new JaxWsProxyFactoryBean();
			lrFactory.setServiceClass(ALELRServicePortType.class);
		}

		lrFactory.setAddress(newEp.toString());
		lrServicePortType = (ALELRServicePortType) lrFactory.create();
		for (EndpointChangeListener listener : lrListeners) {
			listener.endpointChanged(oldEp, newEp);
		}
	}

	// /* (non-Javadoc)
	// * @see
	// org.rifidi.edge.client.ale.connection.service.ConnectionService#aleExec(java.lang.Object)
	// */
	// @Override
	// public void aleExec(Object executable) {
	// //if exec instanceof ... cast ... makeCall(Stub);
	//		
	// }
	//
	// /* (non-Javadoc)
	// * @see
	// org.rifidi.edge.client.ale.connection.service.ConnectionService#lrExec(java.lang.Object)
	// */
	// @Override
	// public void lrExec(Object executable) {
	// //if exec instanceof ... cast ... makeCall(Stub);
	//		
	// }

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * getAleLrServicePortType()
	 */
	@Override
	public ALELRServicePortType getAleLrServicePortType() {
		return lrServicePortType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * getAleServicePortType()
	 */
	@Override
	public ALEServicePortType getAleServicePortType() {
		return aleServicePortType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * getAleLrServicePortType(java.net.URL)
	 */
	@Override
	public ALELRServicePortType getAleLrServicePortType(URL endpoint) {
		setLrEndpoint(endpoint);
		return lrServicePortType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.ale.connection.service.ConnectionService#
	 * getAleServicePortType(java.net.URL)
	 */
	@Override
	public ALEServicePortType getAleServicePortType(URL endpoint) {
		setAleEndpoint(endpoint);
		return aleServicePortType;
	}

}
