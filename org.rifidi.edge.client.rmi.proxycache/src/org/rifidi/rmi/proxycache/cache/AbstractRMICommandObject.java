/**
 * 
 */
package org.rifidi.rmi.proxycache.cache;

import org.rifidi.rmi.proxycache.Activator;
import org.rifidi.rmi.proxycache.exceptions.AuthenticationException;
import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.security.context.SecurityContextHolder;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * An abstract class that should be extended by concrete implementations of
 * Command objects.
 * 
 * T is the return value of the remote call. If the return value is null, T
 * should be of type Object
 * 
 * E is a placeholder for a method-specific exception that can be thrown by the
 * remote call. If such an exception does not exist, E can be of type
 * RuntimeException so the client is not forced to handle a checked exception
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class AbstractRMICommandObject<T, E extends Exception> {

	/** The server description to use for this command object */
	private ServerDescription _serverDescription;
	/** A reference to the ProxyCache */
	private RMIProxyCache proxyCache;

	/***
	 * Constructor
	 * 
	 * @param serverDescription
	 *            The ServerDescription to use to look up or create the
	 *            appropriate RMIProxyFactoryBean
	 */
	public AbstractRMICommandObject(ServerDescription serverDescription) {
		_serverDescription = serverDescription;
		proxyCache = Activator.getActivator().getProxyCache();
	}

	/***
	 * The method invoked by the client
	 * 
	 * @return an object of type T
	 * @throws ServerUnavailable
	 *             If the remote method invocation failed
	 * @throws E
	 *             if there is a method-specific checked exception being thrown
	 */
	public T makeCall() throws ServerUnavailable, AuthenticationException, E {
		RmiProxyFactoryBean bean = proxyCache.getProxy(_serverDescription);

		// set the authentication to use if not null
		if (null != _serverDescription.get_authentication()) {
			SecurityContextHolder.getContext().setAuthentication(
					_serverDescription.get_authentication());
		}
		try {
			// make the call
			return performRemoteCall(bean.getObject());
		} catch (org.springframework.security.AuthenticationException ex) {
			throw new AuthenticationException(
					"Invalid security credentials when making remote call ", ex);
		}
	}

	/**
	 * Concrete subclasses should use this method to make the actual remote call
	 * on the remote stub;
	 * 
	 * <code>
	 * ((RemoteInterface)remoteObject).remoteMethod();
	 * </code>
	 * 
	 * @param remoteObject
	 *            The remoteObject
	 * @return Object of type T
	 * @throws E
	 */
	protected abstract T performRemoteCall(Object remoteObject) throws E;

}
