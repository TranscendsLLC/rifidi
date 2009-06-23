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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.draw2d.IFigure;
import org.eclipse.draw2d.LightweightSystem;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.MenuManager;
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
import org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.sal.SALPluginActivator;
import org.rifidi.edge.client.sal.modelmanager.SALModelService;
import org.rifidi.edge.client.sal.modelmanager.SALModelServiceListener;
import org.rifidi.edge.client.twodview.Activator;
import org.rifidi.edge.client.twodview.layers.FloorPlanLayer;
import org.rifidi.edge.client.twodview.layers.ListeningScalableLayeredPane;
import org.rifidi.edge.client.twodview.layers.ObjectLayer;
import org.rifidi.edge.client.twodview.listeners.SiteViewController;
import org.rifidi.edge.client.twodview.listeners.SiteViewKeyListener;
import org.rifidi.edge.client.twodview.listeners.SiteViewMouseWheelListener;
import org.rifidi.edge.client.twodview.sfx.ReaderAlphaImageFigure;
import org.rifidi.edge.client.twodview.util.DeserializerUtil;
import org.rifidi.edge.client.twodview.util.EdgeUi;
import org.rifidi.edge.client.twodview.util.ReaderPos;
import org.rifidi.edge.client.twodview.util.SerializerUtil;

/**
 * This class is the basis of the twodview. The view contains a layered pane
 * that hosts the different layers.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class SiteView extends ViewPart implements SALModelServiceListener,
		ITabbedPropertySheetPageContributor, IAdaptable {

	@SuppressWarnings("unused")
	private Log logger = LogFactory.getLog(SiteView.class);
	private final static String FILENAME = "SiteView.xml";
	public final static String ID = "org.rifidi.edge.client.twodview.views.SiteView";
	private ListeningScalableLayeredPane lp;
	private FloorPlanLayer floorplanLayer;
	private ObjectLayer objectLayer;
	private IFolder saveFolder;
	private RemoteEdgeServer server;
	private SiteViewController siteViewController;
	private SiteViewSelectionProvider selectionProvider;
	private IFile saveFile;
	private SALModelService modelService;

	/**
	 * The constructor.
	 */
	public SiteView() {
		modelService = SALPluginActivator.getDefault().getSALModelService();
		this.saveFolder = Activator.getSaveFolder();
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

		lp.add(floorplanLayer, 0);
		lp.add(objectLayer, 1);
		// lp.add(effectLayer, 2);
		// lp.add(noteLayer, 3);

		lws.setContents(lp);

		// Drop Target and DT-Listener for Drag and Drop

		DropTarget dt = new DropTarget(canvas, DND.DROP_MOVE);
		dt.setTransfer(new Transfer[] { TextTransfer.getInstance() });
		this.siteViewController = new SiteViewController(this, canvas);
		dt.addDropListener(siteViewController);

		MenuManager menuMgr = new MenuManager();
		menuMgr.add(new GroupMarker("twodview"));

		Menu menu = menuMgr.createContextMenu(canvas);
		canvas.setMenu(menu);
		modelService.registerListener(this);
		this.selectionProvider = new SiteViewSelectionProvider(lp);
		getSite().setSelectionProvider(selectionProvider);
		getSite().registerContextMenu(menuMgr, selectionProvider);
		/** The file where the data is saved */
		saveFile = saveFolder.getFile(FILENAME);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// Auto-generated method stub

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
	 * Returns the layered pane.
	 * 
	 * @return the layered pane
	 */
	public ListeningScalableLayeredPane getLayeredPane() {
		return lp;
	}

	/**
	 * Returns the site view controller.
	 * 
	 * @return site view controller
	 */
	public SiteViewController getController() {
		return this.siteViewController;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sal.modelmanager.SALModelServiceListener#setModel
	 * (java.lang.Object)
	 */
	@Override
	public void setModel(RemoteEdgeServer model) {
		this.server = model;
		if (this.siteViewController != null) {
			this.siteViewController.SetModel(server);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		this.modelService.unregisterListener(this);
		this.selectionProvider.dispose();
		super.dispose();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.ITabbedPropertySheetPageContributor
	 * #getContributorId()
	 */
	@Override
	public String getContributorId() {
		return "org.rifidi.edge.client.twodview.propertyContributor";
	}

	/**
	 * Saves the information of this view to a file.
	 */
	public void persist() {

		/** Creating an object to put the map information in */
		EdgeUi eui = new EdgeUi();
		/** Background picture */
		eui
				.setPathToImageFile(this.getFloorplanLayer()
						.getFloorPlanImageFile());
		/** scale factor of the map */
		eui.setScaleFactor(lp.getScale());
		/** offset of the map from the original position */
		eui.setxOffset(lp.getxOffset());
		eui.setyOffset(lp.getyOffset());
		/** getting all the readers on the map */
		ArrayList<IFigure> readerImages = (ArrayList<IFigure>) this.objectLayer
				.getChildren();
		/** Coordinates and ID of the readers in the map */
		HashSet<ReaderPos> readerPosSet = new HashSet<ReaderPos>();
		for (IFigure iFigure : readerImages) {
			try {
				ReaderAlphaImageFigure raif = (ReaderAlphaImageFigure) iFigure;
				ReaderPos rp = new ReaderPos();
				rp.setID(raif.getReaderId());
				rp.setX(raif.getBounds().x);
				rp.setY(raif.getBounds().y);
				readerPosSet.add(rp);

			} catch (ClassCastException e) {
				e.printStackTrace();
			}
		}

		/** Add the xy + id info to our object */
		eui.setReaderPositions(readerPosSet);
		/** Write it out to the savefile */
		SerializerUtil.serializeEdgeUi(eui, saveFile);

	}

	/**
	 * Retrieves the map information from the savefile and restores it.
	 */
	public void initMap() {

		/** check for the savefile */
		if (saveFile != null && saveFile.exists()) {

			/** getting stuff out of the file */
			EdgeUi eui = DeserializerUtil.deserializeEdgeUi(saveFile);
			/** do not change if there is no path to file */
			if (!eui.getPathToImageFile().isEmpty())
				this.floorplanLayer.setFloorplan(eui.getPathToImageFile());

			/** restore zoom */
			lp.setScale(eui.getScaleFactor());
			/**
			 * restore offset - needs to happen before the reader placement on
			 * the map since the coordinates of the readers are already saved
			 * for the zoom/translation
			 */
			((IFigure) this.floorplanLayer.getChildren().get(0))
					.setLocation(new Point(eui.getxOffset(), eui.getyOffset()));
			// lp.resetDeltaValues();
			/** get ids and positions of the persisted readers */
			HashSet<ReaderPos> readerPositions = (HashSet<ReaderPos>) eui
					.getReaderPositions();
			/** iterate over the set */
			for (ReaderPos readerPos : readerPositions) {
				/** if for some strange reason we do not have an id - skip it */
				if (!readerPos.getID().isEmpty()) {

					siteViewController.addReaderToMap(readerPos.getID(),
							readerPos.getX(), readerPos.getY(), false);

				}
			}
			lp.repaint();

		}

	}

}
