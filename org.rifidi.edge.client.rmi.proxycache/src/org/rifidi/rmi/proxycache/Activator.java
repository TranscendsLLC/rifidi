/*
 * Activator.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.rmi.proxycache;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.rmi.proxycache.cache.RMIProxyCache;

/**
 * Activator for this bundle.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class Activator implements BundleActivator {

	/** A reference to this activator */
	private static Activator activator;
	/** The cache that holds the RMIProxy objects */
	private RMIProxyCache proxyCache;

	@Override
	public void start(BundleContext arg0) throws Exception {
		activator = this;
		System.setProperty("javax.rmi.ssl.client.enabledCipherSuites",
				"SSL_DH_anon_WITH_RC4_128_MD5");
		this.proxyCache = new RMIProxyCache();

	}

	@Override
	public void stop(BundleContext arg0) throws Exception {
	}

	/** Get a reference to this activator */
	public static Activator getActivator() {
		return activator;
	}

	/** Get a reference to the ProxyCache */
	public RMIProxyCache getProxyCache() {
		return proxyCache;
	}

}
