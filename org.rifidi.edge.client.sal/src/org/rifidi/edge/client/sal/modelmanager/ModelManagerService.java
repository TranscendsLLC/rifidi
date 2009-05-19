
package org.rifidi.edge.client.sal.modelmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class ModelManagerService {

	private List<RemoteEdgeServer> model = new ArrayList<RemoteEdgeServer>();
	private Set<ModelManagerServiceListener> controllers;
	private static ModelManagerService instance;

	/**
	 * Constructor.  
	 */
	private ModelManagerService() {
		controllers = new HashSet<ModelManagerServiceListener>();
		model.add(new RemoteEdgeServer());
	}

	/**
	 * Returns a singleton instance.  
	 * 
	 * @return
	 */
	public synchronized static ModelManagerService getInstance() {
		if (instance == null) {
			instance = new ModelManagerService();
		}
		return instance;
	}

	/**
	 * Adds the provided controller to the map.  
	 * TODO: Method level comment.  
	 * 
	 * @param controller
	 */
	public void addController(ModelManagerServiceListener controller) {
		this.controllers.add(controller);
		controller.setModel(model);
	}

	/**
	 * Removes the given controller from the map.  
	 * TODO: Method level comment.  
	 * 
	 * @param controller
	 */
	public void removeController(ModelManagerServiceListener controller) {
		this.controllers.remove(controller);
	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @param server
	 */
	public void setModel(RemoteEdgeServer server) {
		this.model.clear();
		this.model.add(server);
		for (ModelManagerServiceListener controller: controllers) {
			controller.setModel(model);
		}
	}

}
