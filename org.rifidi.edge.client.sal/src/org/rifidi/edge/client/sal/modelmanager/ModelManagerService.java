package org.rifidi.edge.client.sal.modelmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * This class is a singleton that injects a RemoteEdgeServer into listeners.
 * 
 * TODO: look at how the problem of injecting a model into views is handled in
 * the ALE view.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ModelManagerService {

	/** The model to inject */
	private List<RemoteEdgeServer> model = new ArrayList<RemoteEdgeServer>();
	/** Listeners who should have model injected */
	private Set<ModelManagerServiceListener> controllers;
	/** The singleton instance of this */
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
	 * 
	 * @param controller
	 */
	public void addController(ModelManagerServiceListener controller) {
		this.controllers.add(controller);
		controller.setModel(model);
	}

	/**
	 * Removes the given controller from the map.
	 * 
	 * @param controller
	 */
	public void removeController(ModelManagerServiceListener controller) {
		this.controllers.remove(controller);
	}

	/**
	 * Inject the model
	 * 
	 * @param server
	 */
	public void setModel(RemoteEdgeServer server) {
		this.model.clear();
		this.model.add(server);
		for (ModelManagerServiceListener controller : controllers) {
			controller.setModel(model);
		}
	}

}
