package org.rifidi.rmi.utils.remotecall;
//TODO: Comments
import java.rmi.Remote;
import java.rmi.RemoteException;

import org.rifidi.rmi.utils.exceptions.RetryException;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;
import org.rifidi.rmi.utils.retrystrategy.RetryStrategy;
import org.rifidi.rmi.utils.retrystrategy.impl.AdditiveWaitRetryStrategy;

/**
 * This class was inspired by the three oreilly.com articles on RMI by William
 * Grosso.
 * 
 * The purpose of this class is provide an abstract base class to write RMI
 * Command Objects (see the command object pattern). It is a little akward
 * because it uses generics to declare the return value and thrown exceptions of
 * make call.
 * 
 * Most command objects should not extend this class, and instead should use
 * 
 * @see ServerDescriptionBasedMethodCall
 * 
 * Kyle Neumeier - kyle@pramari.com
 * 
 */

public abstract class AbstractRemoteMethodCall<T, E extends Exception> {

	/**
	 * Make the remote call
	 * 
	 * @return The generic object. If there is no return object requried (i.e.
	 *         if method would normally be void), T can just be an object
	 * @throws ServerUnavailable
	 *             If there was a problem reaching the server (Retries have
	 *             given up)
	 * @throws E
	 *             if some custom exception happened. If the method does not
	 *             throw a custom exception, E can be a Runtime Exception.
	 */
	public T makeCall() throws ServerUnavailable, E {
		RetryStrategy strategy = getRetryStrategy();
		while (strategy.shouldRetry()) {
			Remote remoteObject = getRemoteObject();
			if (null == remoteObject) {
				throw new ServerUnavailable();
			}
			try {
				return performRemoteCall(remoteObject);
			} catch (RemoteException remoteException) {
				try {
					remoteExceptionOccurred(remoteException);
					strategy.remoteExceptionOccurred();
				} catch (RetryException retryException) {
					handleRetryException(remoteObject);
				}
			}
		}
		return null;
	}

	/*
	 * The next 4 methods define the core behavior. Of these, two must be
	 * implemented by the subclass (and so are left abstract). The remaining
	 * three can be altered to provide customized retry handling.
	 */

	/**
	 * getRemoteObject is a template method which should, in most cases, return
	 * the stub.
	 */

	protected abstract Remote getRemoteObject() throws ServerUnavailable;

	/**
	 * performRemoteCall is a template method which actually makes the remote
	 * method invocation. 
	 */
	protected abstract T performRemoteCall(Remote remoteObject)
			throws RemoteException, E;

	/**
	 * 
	 * @return The retry Stategy to use
	 */
	protected RetryStrategy getRetryStrategy() {
		return new AdditiveWaitRetryStrategy();
	}

	/**
	 * What to do if we need to retry a remote call
	 * 
	 * @param remoteObject
	 * @throws ServerUnavailable
	 */
	protected void handleRetryException(Remote remoteObject)
			throws ServerUnavailable {
		throw new ServerUnavailable();
	}

	/**
	 * A remote exception has occurred.
	 * 
	 * @param remoteException
	 */
	protected void remoteExceptionOccurred(RemoteException remoteException) {
		
	}
}