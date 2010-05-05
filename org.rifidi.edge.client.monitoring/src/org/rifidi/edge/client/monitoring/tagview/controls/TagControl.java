/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.controls;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.databinding.beans.BeansObservables;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableListContentProvider;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.rifidi.edge.client.monitoring.tagview.model.TagModel;
import org.rifidi.edge.client.monitoring.tagview.model.TagModelProviderSingleton;

/**
 * @author kyle
 * 
 */
public class TagControl {

	/** The table viewer to use */
	private TableViewer viewer;
	/** The model */
	private WritableList tags;
	/** The date format to use to display timestamps */
	private SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss:SSS");

	public TagControl(Composite parent, FormToolkit toolkit) {

		Section section = toolkit.createSection(parent, Section.TITLE_BAR
				| Section.EXPANDED | Section.TWISTIE);
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL_GRAB);
		tableWrapData.rowspan = 2;
		tableWrapData.grabVertical = true;
		section.setLayoutData(tableWrapData);

		Composite c = toolkit.createComposite(section);
		c.setLayout(new GridLayout());

		section.setText("Tag View");

		// Set up the table
		viewer = new TableViewer(c, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP
				| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BOTTOM);
		viewer.getTable().setLinesVisible(true);
		viewer.getTable().setHeaderVisible(true);
		GridData gd = new GridData(GridData.FILL_VERTICAL);
		gd.grabExcessVerticalSpace = true;
		// gd.heightHint=600;
		viewer.getTable().setLayoutData(gd);

		// Set the colummn headers
		String[] columnNames = { "", "Tag ID", "Count", "RSSI", "First Seen",
				"Last Seen", "Reader ID", "Antenna ID" };
		int[] columnWidths = new int[] { 30, 210, 55, 60, 105, 105, 90, 60 };
		List<TableColumn> tableColumns = new ArrayList<TableColumn>();
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(viewer.getTable(),
					SWT.LEFT);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
			tableColumns.add(tableColumn);
		}

		// Hook observable list up to table
		ObservableListContentProvider cp = new ObservableListContentProvider();
		viewer.setContentProvider(cp);
		tags = TagModelProviderSingleton.getInstance().getTags();
		IObservableMap[] attributes = BeansObservables.observeMaps(cp
				.getKnownElements(), TagModel.class, new String[] { "count" });
		viewer.setLabelProvider(new ViewLabelProvider(attributes));

		// set the input
		viewer.setInput(tags);
		section.setClient(c);
	}

	public void dispose() {

	}

	/**
	 * A Label Provider for the tags table. Automatically updates for fields.
	 * 
	 * @author Kyle Neumeier - kyle@pramari.com
	 * 
	 */
	class ViewLabelProvider extends ObservableMapLabelProvider implements
			ITableLabelProvider {

		/**
		 * @param attributeMap
		 */
		public ViewLabelProvider(IObservableMap[] attributeMap) {
			super(attributeMap);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider#
		 * getColumnText(java.lang.Object, int)
		 */
		@Override
		public String getColumnText(Object obj, int index) {
			TagModel tag = (TagModel) obj;
			if (index == 0) {
				return Integer.toString(tags.indexOf(obj) + 1);
			} else if (index == 1) {
				return tag.getId();
			} else if (index == 2) {
				return Integer.toString(tag.getCount());
			} else if (index == 3) {
				return Float.toString(tag.getRssi());
			} else if (index == 4) {
				return dateFormat.format(tag.getFirstSeen());
			} else if (index == 5) {
				return dateFormat.format(tag.getLastSeen());
			} else if (index == 6) {
				return tag.getReaderID();
			} else if (index == 7) {
				return Integer.toString(tag.getAntennaID());
			}
			return "";
		}

	}

}
