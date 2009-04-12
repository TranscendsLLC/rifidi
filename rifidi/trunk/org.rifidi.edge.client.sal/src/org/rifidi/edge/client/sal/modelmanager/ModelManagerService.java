/**
 * 
 */
package org.rifidi.edge.client.sal.modelmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * @author kyle
 * 
 */
public class ModelManagerService {

	private List<RemoteEdgeServer> model = new ArrayList<RemoteEdgeServer>();
	private Set<ModelManagerServiceListener> controllers;
	private static ModelManagerService instance;

	private ModelManagerService() {
		controllers = new HashSet<ModelManagerServiceListener>();
		model.add(new RemoteEdgeServer());
	}

	public synchronized static ModelManagerService getInstance() {
		if (instance == null) {
			instance = new ModelManagerService();
		}
		return instance;
	}

	public void addController(ModelManagerServiceListener controller) {
		this.controllers.add(controller);
		controller.setModel(model);
	}

	public void removeController(ModelManagerServiceListener controller) {
		this.controllers.remove(controller);
	}

	public void setModel(RemoteEdgeServer server) {
		this.model.clear();
		this.model.add(server);
		for (ModelManagerServiceListener controller: controllers) {
			controller.setModel(model);
		}
	}

}
