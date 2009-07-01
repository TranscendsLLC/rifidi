/**
 * 
 */
package org.rifidi.rmi.proxycache.cache;

import org.rifidi.rmi.proxycache.exceptions.ServerUnavailable;
import org.springframework.remoting.RemoteLookupFailureException;
import org.springframework.remoting.rmi.RmiProxyFactoryBean;
import org.springframework.security.Authentication;
import org.springframework.security.context.rmi.ContextPropagatingRemoteInvocationFactory;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * The purpose of this class is to provide the information necessary to reach a
 * remote object stub (i.e. an RMI server).
 * 
 * It is used as a key to be able to cache RMIProxyFactoryBeans. Each stub
 * available on the server should have a separate concrete implementation of
 * this class
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public abstract class ServerDescription {

	/** The hostName of the Remote Stub */
	private String _serverIP;

	/** The port of the Remote Stub */
	private int _serverPort;

	/** The name of the stub in the RMI registry */
	private String _stubName;

	/** A unique ID for this ServerDescription */
	private String _hashString;

	/** The service interface of the Remote Service */
	private Class<?> _serviceInterface;

	/** If true, pass the thread's SecurityContext to the server */
	private Authentication _authentication;

	/**
	 * Constructor.
	 * 
	 * @param serverIP
	 *            The IP address of the remote machine that has the RMI registry
	 *            which contains the stub
	 * @param serverPort
	 *            The port of the remote machine that has the RMI registry which
	 *            contains the stub
	 * @param stubName
	 *            The name of the stub in the RMI registry
	 */
	public ServerDescription(String serverIP, int serverPort, String stubName,
			Class<?> serviceInterface, Authentication authentication) {
		_serverIP = serverIP;
		_stubName = stubName;
		_serverPort = serverPort;
		_serviceInterface = serviceInterface;
		_authentication = authentication;
		_hashString = _serverIP + _stubName + String.valueOf(_serverPort);
	}

	/**
	 * Constructor.
	 * 
	 * @param serverIP
	 *            The IP address of the remote machine that has the RMI registry
	 *            which contains the stub
	 * @param serverPort
	 *            The port of the remote machine that has the RMI registry which
	 *            contains the stub
	 * @param stubName
	 *            The name of the stub in the RMI registry
	 */
	public ServerDescription(String serverIP, int serverPort, String stubName,
			Class<?> serviceInterface) {
		this(serverIP, serverPort, stubName, serviceInterface, null);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return _hashString.hashCode();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object object) {
		if (!(object instanceof ServerDescription)) {
			return false;
		}
		ServerDescription otherServerDescription = (ServerDescription) object;
		return _hashString.equals(otherServerDescription.getHashstring());
	}

	/**
	 * @return the unique string
	 */
	private String getHashstring() {
		return _hashString;
	}

	/**
	 * @return the IP address of the remote machine
	 */
	public String getServerIP() {
		return _serverIP;
	}

	/**
	 * @return the port number of the remote machine
	 */
	public int getServerPort() {
		return _serverPort;
	}

	/**
	 * @return The name of the remote stub in the RMI registry
	 */
	public String getStubName() {
		return _stubName;
	}

	/**
	 * @return the _authentication
	 */
	Authentication get_authentication() {
		return _authentication;
	}

	/**
	 * Creates an RMIProxyFactoryBean for the information in this server
	 * description
	 * 
	 * @return an RMIProxyFactoryBean that is used to make remote calls to the
	 *         server
	 * @throws ServerUnavailable
	 *             If the server is not available
	 */
	RmiProxyFactoryBean createProxy() throws ServerUnavailable {
		RmiProxyFactoryBean proxy = new RmiProxyFactoryBean();
		proxy.setCacheStub(true);
		proxy.setRefreshStubOnConnectFailure(true);
		proxy.setServiceInterface(_serviceInterface);
		proxy.setServiceUrl("rmi://" + _serverIP + ":" + _serverPort + "/"
				+ _stubName);
		if (_authentication != null) {
			proxy
					.setRemoteInvocationFactory(new ContextPropagatingRemoteInvocationFactory());
		}
		try {
			proxy.afterPropertiesSet();
		} catch (RemoteLookupFailureException ex) {
			throw new ServerUnavailable();
		}
		return proxy;
	}
}
