/**
 * 
 */
package org.rifidi.rmi.proxycache.cache;

import java.util.concurrent.ConcurrentHashMap;

import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;

/**
 * This is an object that caches RmiProxyFactoryBeans so that the same
 * RMIProxyFactoryBean may be shared between remote calls to the same stub. The
 * actual RemoteStubs are cached by the RMIProxyFactoryBean itself
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RMIProxyCache {

	/** The cache */
	private static ConcurrentHashMap<ServerDescription, RmiProxyFactoryBean> _serverDescriptionsToProxy;

	/**
	 * Constructor
	 */
	public RMIProxyCache() {
		_serverDescriptionsToProxy = new ConcurrentHashMap<ServerDescription, RmiProxyFactoryBean>();
	}

	/**
	 * Return a live RMIProxyFactoryBean for a given ServerDescription. If one
	 * is not available in the cache, attempt to create a new one. This method
	 * will not return null.
	 * 
	 * @param serverDescription
	 *            The ServerDescription for the RMIProxyFactoryBean
	 * @return The RMIProxyFactoryBean to make the remote call
	 * @throws ServerUnavailable
	 *             thrown if no RMIProxyFactoryBean can be created
	 */
	public RmiProxyFactoryBean getProxy(ServerDescription serverDescription)
			throws ServerUnavailable {
		RmiProxyFactoryBean retVal = _serverDescriptionsToProxy
				.get(serverDescription);
		if (retVal == null) {
			retVal = serverDescription.createProxy();
			_serverDescriptionsToProxy.put(serverDescription, retVal);
		}
		return retVal;
	}

	/**
	 * Remove an RMIProxyFactoryBean from the cache
	 * 
	 * @param serverDescription
	 */
	public void removeProxy(ServerDescription serverDescription) {
		_serverDescriptionsToProxy.remove(serverDescription);
	}

}
