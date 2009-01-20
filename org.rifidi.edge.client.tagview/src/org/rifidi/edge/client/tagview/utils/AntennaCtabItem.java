/* 
 *  AntennaCtabItem.java
 *  Created:	Jan 16, 2009
 *  Project:	RiFidi org.rifidi.edge.client.tagview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.tagview.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.nebula.widgets.pgroup.PGroup;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.TableColumn;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class AntennaCtabItem extends CTabItem {
	private TableViewer viewer;
	private Listener selChangeListener = new Listener() {
		public void handleEvent(Event event) {
			viewer.getTable().setSortColumn((TableColumn) event.widget);
			viewer.refresh();
		}
	};

	public AntennaCtabItem(CTabFolder parent, int antennaNumber) {
		super(parent, SWT.SMOOTH);
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(new FillLayout());
		setText("Antenna #" + antennaNumber);

		PGroup group = new PGroup(composite, SWT.SMOOTH);
		group.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//		group.setText(make a variable)
		FillLayout layout = new FillLayout();
		// layout.type = SWT.HORIZONTAL;
		group.setLayout(layout);
		group.setForeground(Display.getCurrent()
				.getSystemColor(SWT.COLOR_WHITE));
		viewer = new TableViewer(group, SWT.MULTI | SWT.FULL_SELECTION
				| SWT.BORDER);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		viewer.setLabelProvider(new TagTableLabelProvider());
		viewer.setContentProvider(new TagTableContentProvider());
//		viewer.setSorter(new NameSorter());
		// viewer.setInput(getViewSite());
		String[] columnNames = { "Tag ID", "Time Stamp", "Antenna ID",
				"Signal Strength", "Velocity" };
		int[] columnWidths = new int[] { 225, 120, 80, 110, 80 };
		int[] columnAlignments = new int[] { SWT.LEFT, SWT.LEFT, SWT.LEFT,
				SWT.LEFT, SWT.LEFT };

		List<TableColumn> tableColumns = new ArrayList<TableColumn>();
		// create columns and add listeners to them
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(viewer.getTable(),
					columnAlignments[i]);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
			tableColumn.pack();
			// Add Listener
			tableColumn.addListener(SWT.Selection, this.selChangeListener);
			tableColumns.add(tableColumn);

		}

		setControl(composite);

	}

	/**
	 * @return the viewer
	 */
	public TableViewer getViewer() {
		return viewer;
	}

}
