/*
 * RequestExecuterSingleton.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */

package org.rifidi.edge.client.model.sal.commands;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.widgets.Display;

/**
 * This is an executor that serializes requests to the RemoteEdgeServer model.
 * Once it is started, it runs in a loop, waiting for a command to be put on the
 * queue. Once a command is put on the queue, it will take it off and execute
 * the two methods in the command: first it calls the execute() method and then
 * calls the executeEclipse() method using asycExec on the eclipse thread
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RequestExecuterSingleton implements Runnable {

	/** A queue of requests */
	private BlockingQueue<RemoteEdgeServerCommand> requestQueue;
	/** The singleton for this object */
	private static RequestExecuterSingleton instance;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(RequestExecuterSingleton.class);

	/***
	 * Constructor
	 */
	private RequestExecuterSingleton() {
		requestQueue = new LinkedBlockingQueue<RemoteEdgeServerCommand>();
	}

	/**
	 * Return the instance for this singleton
	 * 
	 * @return
	 */
	public synchronized static RequestExecuterSingleton getInstance() {
		if (instance == null) {
			instance = new RequestExecuterSingleton();
		}
		return instance;
	}

	/**
	 * Put a command on the queue to be executed. This method does not block.
	 * 
	 * @param request
	 *            The command to be executed
	 * @return true if the command was added to the queue, false if the command
	 *         was not added
	 */
	public boolean scheduleRequest(RemoteEdgeServerCommand request) {
		boolean success =  requestQueue.offer(request);
		if(!success){
			logger.warn("Cannot schedule command : " + request.getType());
		}
		return success;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (true) {
			try {
				final RemoteEdgeServerCommand request = requestQueue.take();
				logger.debug("executing: " + request.getType());
				request.execute();
				Display.getDefault().syncExec(new Runnable() {
					@Override
					public void run() {
						request.executeEclipse();
					}

				});
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				break;
			}
		}

	}

}
