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

import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportTabDataContainer implements IReportSubscriber {

	private CTabItem item;
	private ObservableList list;
	private TreeViewer viewer;
	private int reportCount = 0;

	/**
	 * 
	 */
	public ReportTabDataContainer(CTabFolder parent, String specName) {
		final String tempSpecName = specName;
		item = new CTabItem(parent, SWT.NONE) {
			@Override
			public void dispose() {
				ReportReceiverSingleton.getInstance().removeSubscriber(
						tempSpecName);
				super.dispose();
			}
		};
		list = new WritableList();

		viewer = new TreeViewer(parent, SWT.NONE);
		viewer.setContentProvider(new ReportTreeContentProvider());
		viewer.setLabelProvider(new ReportLabelProvider());
		viewer.setInput(list);
		item.setControl(viewer.getControl());

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
		/** add report to list of reports */
		list.add(reports);
		/** increase report counter */
		reportCount++;
		/** show the counter on the report tab */
		item.setText("Reports(" + reportCount + ")");

	}

	public void clear() {
		list.clear();
		reportCount = 0;
		item.setText("Reports");
	}

}
