package org.rifidi.edge.client.ale.treeview.modelmanagerservice;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.models.aleserviceporttype.AleServicePortTypeWrapper;
import org.rifidi.edge.client.ale.models.serviceprovider.SpecDataManager;

/* 
 *  ModelManagerService.java
 *  Created:	Apr 17, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.treeview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ModelManagerService {

	List<SpecDataManager> input = new ArrayList<SpecDataManager>();
	private Set<Viewer> viewers;
	private static ModelManagerService instance;

	private ModelManagerService() {
		viewers = new HashSet<Viewer>();
		input.add(new AleServicePortTypeWrapper());
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

	public void removeViewer(Viewer viewer) {
		this.viewers.remove(viewer);
		viewer.setInput(input);
	}

	public void setModel(List list) {
		this.input.clear();
		this.input = list;
		for (Viewer viewer : viewers) {
			viewer.setInput(input);
		}
	}

}
