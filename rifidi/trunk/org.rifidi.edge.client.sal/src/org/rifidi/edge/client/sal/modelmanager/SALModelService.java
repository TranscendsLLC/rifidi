/*
 * SALModelService.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.modelmanager;


/**
 * This is the interface for a service that creates a SAL model and distributes
 * it to Viewers
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface SALModelService {

	/**
	 * Registers a listener to changes to the SAL model
	 * 
	 * @param listener
	 */
	public void registerListener(SALModelServiceListener listener);

	/**
	 * Unregisters listeners to changes to the SAL model
	 * 
	 * @param listener
	 */
	public void unregisterListener(SALModelServiceListener listener);

	/**
	 * Reload the model and distribute to listeners and viewers
	 */
	public void reload();
}
