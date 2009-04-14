/* 
 *  SiteView.java
 *  Created:	Aug 7, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.views;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.sal.modelmanager.ModelManagerService;
import org.rifidi.edge.client.sal.modelmanager.ModelManagerServiceListener;
import org.rifidi.edge.client.twodview.layers.EffectLayer;
import org.rifidi.edge.client.twodview.layers.FloorPlanLayer;
import org.rifidi.edge.client.twodview.layers.ListeningScalableLayeredPane;
import org.rifidi.edge.client.twodview.layers.NoteLayer;
import org.rifidi.edge.client.twodview.layers.ObjectLayer;
import org.rifidi.edge.client.twodview.listeners.SiteViewDropTargetListener;
import org.rifidi.edge.client.twodview.listeners.SiteViewKeyListener;
import org.rifidi.edge.client.twodview.listeners.SiteViewMouseWheelListener;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SiteView extends ViewPart implements ModelManagerServiceListener,
		ISelectionListener, ITabbedPropertySheetPageContributor, IAdaptable {

	private Log logger = LogFactory.getLog(SiteView.class);

	public final static String ID = "org.rifidi.edge.client.twodview.views.SiteView";
	private ListeningScalableLayeredPane lp;
	private FloorPlanLayer floorplanLayer;
	private ObjectLayer objectLayer;
	private EffectLayer effectLayer;
	private NoteLayer noteLayer;

	private List<RemoteEdgeServer> servers;
	private SiteViewDropTargetListener dropTargetListener;
	private SiteViewSelectionProvider selectionProvider;

	/**
	 * 
	 */
	public SiteView() {
		ModelManagerService.getInstance().addController(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());

		Canvas canvas = new Canvas(composite, SWT.NONE);

		canvas.setLayout(new FillLayout());

		// LWS holds the draw2d components
		LightweightSystem lws = new LightweightSystem(canvas);

		lp = new ListeningScalableLayeredPane();

		canvas.addListener(SWT.MouseWheel, new SiteViewMouseWheelListener(lp));
		canvas.addKeyListener(new SiteViewKeyListener(lp));

		floorplanLayer = new FloorPlanLayer();
		floorplanLayer.init();
		objectLayer = new ObjectLayer();
		effectLayer = new EffectLayer();
		noteLayer = new NoteLayer();

		lp.add(floorplanLayer, 0);
		lp.add(objectLayer, 1);
		// lp.add(effectLayer, 2);
		// lp.add(noteLayer, 3);

		lws.setContents(lp);

		// Drop Target and DT-Listener for Drag and Drop

		DropTarget dt = new DropTarget(canvas, DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		this.dropTargetListener = new SiteViewDropTargetListener(this, canvas);
		if (this.servers != null) {
			this.dropTargetListener.SetModel(this.servers.get(0));
		}
		dt.addDropListener(dropTargetListener);

		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker("twodview"));

		Menu menu = menuMgr.createContextMenu(canvas);
		canvas.setMenu(menu);
		this.selectionProvider = new SiteViewSelectionProvider(lp);
		getSite().setSelectionProvider(selectionProvider);
		getSite().registerContextMenu(menuMgr, selectionProvider);

		getSite().getWorkbenchWindow().getSelectionService()
				.addSelectionListener(this);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	/**
	 * @return the floorplanLayer
	 */
	public FloorPlanLayer getFloorplanLayer() {
		return floorplanLayer;
	}

	/**
	 * @return the objectLayer
	 */
	public ObjectLayer getObjectLayer() {
		return objectLayer;
	}

	/**
	 * @return the effectLayer
	 */
	public EffectLayer getEffectLayer() {
		return effectLayer;
	}

	/**
	 * @return the noteLayer
	 */
	public NoteLayer getNoteLayer() {
		return noteLayer;
	}

	public ListeningScalableLayeredPane getLayeredPane() {
		return lp;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.modelmanager.ModelManagerServiceListener#setModel
	 * (java.lang.Object)
	 */
	@Override
	public void setModel(Object model) {
		this.servers = (List<RemoteEdgeServer>) model;
		if (this.dropTargetListener != null) {
			this.dropTargetListener.SetModel(servers.get(0));
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		ModelManagerService.getInstance().removeController(this);
		this.selectionProvider.dispose();
		super.dispose();
	}

	/**
	 * TODO: REMOVE THIS LATER. ONLY FOR DEBUG PURPOSES!
	 */
	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		// logger.debug("Selection: " + selection);

	}

	@Override
	public String getContributorId() {
		return "org.rifidi.edge.client.sal.tabbedPropContributer";
	}

}
