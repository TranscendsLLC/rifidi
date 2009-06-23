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
