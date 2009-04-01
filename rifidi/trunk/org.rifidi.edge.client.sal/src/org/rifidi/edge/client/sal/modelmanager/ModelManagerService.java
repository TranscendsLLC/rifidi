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

	List<RemoteEdgeServer> input = new ArrayList<RemoteEdgeServer>();
	private Set<Viewer> viewers;
	private static ModelManagerService instance;

	private ModelManagerService() {
		viewers = new HashSet<Viewer>();
		input.add(new RemoteEdgeServer());
	}

	public synchronized static ModelManagerService getInstance() {
		if (instance == null) {
			instance = new ModelManagerService();
		}
		return instance;
	}

	public void addViewer(Viewer viewer) {
		this.viewers.add(viewer);
		viewer.setInput(input);
	}

	public void removeViewwer(Viewer viewer) {
		this.viewers.remove(viewer);
	}

	public void setModel(RemoteEdgeServer server) {
		this.input.clear();
		this.input.add(server);
		for (Viewer viewer : viewers) {
			viewer.setInput(input);
		}
	}

}
