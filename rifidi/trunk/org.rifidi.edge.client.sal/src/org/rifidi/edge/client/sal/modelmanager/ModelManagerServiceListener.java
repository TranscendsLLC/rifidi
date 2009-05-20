package org.rifidi.edge.client.sal.modelmanager;

/**
 * This is an interface that anyone who needs a model injected into them should
 * use
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public interface ModelManagerServiceListener {
	/**
	 * Called when the model changes or becomes available
	 * 
	 * @param model
	 */
	public void setModel(Object model);
}
