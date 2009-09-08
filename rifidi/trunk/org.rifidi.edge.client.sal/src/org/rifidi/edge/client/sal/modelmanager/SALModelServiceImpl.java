/*
 * SALModelServiceImpl.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.client.sal.modelmanager;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * Implementation of SALModelService
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class SALModelServiceImpl implements SALModelService {

	/** The model to inject to listeners */
	private RemoteEdgeServer model;
	/** Listeners who should have model injected */
	private Set<SALModelServiceListener> controllers;

	/**
	 * Constructor
	 */
	public SALModelServiceImpl() {
		controllers = new HashSet<SALModelServiceListener>();
		model = new RemoteEdgeServer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.modelmanager.SALModelService#registerListener
	 * (org.rifidi.edge.client.sal.modelmanager.SALModelServiceListener)
	 */
	@Override
	public void registerListener(SALModelServiceListener listener) {
		if (model != null) {
			listener.setModel(model);
		}
		this.controllers.add(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.modelmanager.SALModelService#unregisterListener
	 * (org.rifidi.edge.client.sal.modelmanager.SALModelServiceListener)
	 */
	@Override
	public void unregisterListener(SALModelServiceListener listener) {
		this.controllers.remove(listener);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.client.sal.modelmanager.SALModelService#reload()
	 */
	@Override
	public void reload() {
		// TODO Implement me!

	}

}
