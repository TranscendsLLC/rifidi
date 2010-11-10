/* 
 *  ReportTabDataContainer.java
 *  Created:	May 26, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ecspecview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.reports;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * Since a CTabItem is not to be extended, we need some place to store the data
 * for the CTabItem in. We store the data in this Class.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportTabDataContainer implements IReportSubscriber {

	private CTabItem item;
	private ObservableList list;
	private TreeViewer viewer;
	private int reportCount = 0;
	private String specName = "";
	private Log logger = LogFactory.getLog(ReportTabDataContainer.class);

	/**
	 * The constructor.
	 * 
	 * @param parent
	 *            - CTabFolder where the CTabItem is to be placed.
	 * @param specName
	 *            - Name of the ECSpec we want the reports for.
	 */
	public ReportTabDataContainer(CTabFolder parent, String specName) {
		logger.trace("Creating a ReportTabDataContainer... ");
		this.specName = specName;
		/**
		 * making sure the report subscription gets removed when CTabItem is
		 * disposed
		 */
		item = new CTabItem(parent, SWT.NONE) {
			@Override
			public void dispose() {
				ReportReceiverSingleton.getInstance().removeSubscriber(
						ReportTabDataContainer.this.specName);
				super.dispose();
			}
		};
		/** Initializing the list of reports */
		list = new WritableList();
		/** Initializing the viewer for the CTabItem */
		viewer = new TreeViewer(parent, SWT.NONE);
		viewer.setContentProvider(new ReportTreeContentProvider());
		viewer.setLabelProvider(new ReportLabelProvider());
		viewer.setInput(list);
		item.setControl(viewer.getControl());
		/** Adding this object as a subscriber to the ReportReceiver */
		ReportReceiverSingleton.getInstance().addSubscriber(specName, this);
	}

	/**
	 * @return the item
	 */
	public CTabItem getItem() {
		return item;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.ale.reports.IReportSubscriber#pushReport(org.rifidi
	 * .edge.client.ale.api.xsd.ale.epcglobal.ECReport)
	 */
	@Override
	public void pushReport(ECReports reports) {
		logger.debug("pushing reports...");
		/** add report to list of reports */
		list.add(reports);
		/** increase report counter */
		reportCount++;
		/** show the counter on the report tab */
		item.setText("Reports(" + reportCount + ")");

	}

	/**
	 * Clear the list of reports and reset the report count and caption for the
	 * CTabItem.
	 */
	public void clear() {
		list.clear();
		reportCount = 0;
		item.setText("Reports");
	}

}
