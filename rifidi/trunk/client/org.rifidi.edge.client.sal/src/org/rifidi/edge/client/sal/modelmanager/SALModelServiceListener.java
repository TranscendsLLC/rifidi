/*
 * SALModelServiceListener.java
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

import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * This is an interface that anyone who needs a model injected into them should
 * use
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface SALModelServiceListener {
	/**
	 * Called when the model changes or becomes available
	 * 
	 * @param model
	 */
	public void setModel(RemoteEdgeServer model);
}
