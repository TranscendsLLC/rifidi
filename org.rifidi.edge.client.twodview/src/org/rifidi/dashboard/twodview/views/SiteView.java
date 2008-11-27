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
package org.rifidi.dashboard.twodview.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.TextTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.dashboard.twodview.layers.EffectLayer;
import org.rifidi.dashboard.twodview.layers.FloorPlanLayer;
import org.rifidi.dashboard.twodview.layers.ListeningScalableLayeredPane;
import org.rifidi.dashboard.twodview.layers.NoteLayer;
import org.rifidi.dashboard.twodview.layers.ObjectLayer;
import org.rifidi.dashboard.twodview.listeners.SiteViewDropTargetListener;
import org.rifidi.dashboard.twodview.listeners.SiteViewKeyListener;
import org.rifidi.dashboard.twodview.listeners.SiteViewMouseWheelListener;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class SiteView extends ViewPart implements ISelectionProvider {

	private Log logger = LogFactory.getLog(SiteView.class);

	public final static String ID = "org.rifidi.dashboard.twodview.views.SiteView";

	private FloorPlanLayer floorplanLayer;
	private ObjectLayer objectLayer;
	private EffectLayer effectLayer;
	private NoteLayer noteLayer;

	/**
	 * 
	 */
	public SiteView() {

		// TODO Auto-generated constructor stub
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

		ListeningScalableLayeredPane lp = new ListeningScalableLayeredPane();
		canvas.addListener(SWT.MouseWheel, new SiteViewMouseWheelListener(lp));
		canvas.addKeyListener(new SiteViewKeyListener(lp));

		floorplanLayer = new FloorPlanLayer();
		objectLayer = new ObjectLayer();
		effectLayer = new EffectLayer();
		noteLayer = new NoteLayer();

		lp.add(floorplanLayer, 0);
		lp.add(objectLayer, 1);
		lp.add(effectLayer, 2);
		lp.add(noteLayer, 3);

		lws.setContents(lp);

		// Drop Target and DT-Listener for Drag and Drop

		DropTarget dt = new DropTarget(canvas, DND.DROP_COPY | DND.DROP_MOVE
				| DND.DROP_LINK | DND.DROP_DEFAULT);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		dt.addDropListener(new SiteViewDropTargetListener(this, canvas));

		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker("twodview"));
		
		Menu menu = menuMgr.createContextMenu(canvas);
		canvas.setMenu(menu);
		getSite().registerContextMenu(menuMgr, this);
		logger.debug("twodview is here!!!");

		// try {
		// objectLayer.addReader(new ReaderAlphaImageFigure(Activator
		// .imageDescriptorFromPlugin(
		// "org.rifidi.edge.client.twodview",
		// "icons/reader-24x24.png").createImage(), null),
		// new Point(100, 100));
		// } catch (ReaderAlreadyInMapException e) {
		// // TODO Auto-generated catch block
		// e.printStackTrace();
		// }

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

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		logger.debug("addSelectionChangedListener");
	}

	@Override
	public ISelection getSelection() {
		logger.debug("getSelection");
		return null;
	}

	@Override
	public void removeSelectionChangedListener(
			ISelectionChangedListener listener) {
		logger.debug("removeSelectionChangedListener");
	}

	@Override
	public void setSelection(ISelection selection) {
		logger.debug("setSelection");
	}

}
