package org.rifidi.edge.client.ale.treeview.modelmanagerservice;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.ale.models.alelrserviceporttype.AleLrServicePortTypeWrapper;

/* 
 *  LrModelManagerService.java
 *  Created:	Apr 20, 2009
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
public class LrModelManagerService {

	private List<AleLrServicePortTypeWrapper> input = new ArrayList<AleLrServicePortTypeWrapper>();
	private Set<Viewer> viewers;
	private static LrModelManagerService instance;

	private LrModelManagerService() {
		viewers = new HashSet<Viewer>();
		input.add(new AleLrServicePortTypeWrapper());
	}

	public synchronized static LrModelManagerService getInstance() {
		if (instance == null) {
			instance = new LrModelManagerService();
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
}
