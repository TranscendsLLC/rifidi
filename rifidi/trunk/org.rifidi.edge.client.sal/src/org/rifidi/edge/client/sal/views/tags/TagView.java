/**
 * 
 */
package org.rifidi.edge.client.sal.views.tags;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.set.ISetChangeListener;
import org.eclipse.core.databinding.observable.set.ObservableSet;
import org.eclipse.core.databinding.observable.set.SetChangeEvent;
import org.eclipse.jface.databinding.viewers.ObservableSetContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.RemoteTag;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagView extends ViewPart implements ISetChangeListener {

	public static final String ID = "org.rifidi.edge.client.sal.tags";
	private TableViewer table;
	private RemoteReader remoteReader = null;
	private static final Log logger = LogFactory.getLog(TagView.class);
	private ObservableSet tags;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		table = new TableViewer(parent, SWT.BORDER | SWT.V_SCROLL | SWT.WRAP
				| SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.BOTTOM);
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);

		String[] columnNames = { "", "Tag ID", "Antenna", "Time Stamp", };
		int[] columnWidths = new int[] { 21, 345, 60, 120 };
		int[] columnAlignments = new int[] { SWT.CENTER, SWT.LEFT, SWT.LEFT,
				SWT.LEFT };
		List<TableColumn> tableColumns = new ArrayList<TableColumn>();
		// create columns and add listeners to them
		for (int i = 0; i < columnNames.length; i++) {
			TableColumn tableColumn = new TableColumn(table.getTable(),
					columnAlignments[i]);
			tableColumn.setText(columnNames[i]);
			tableColumn.setWidth(columnWidths[i]);
			// Add Listener
			tableColumns.add(tableColumn);
		}

		table.setContentProvider(new ObservableSetContentProvider());
		table.setLabelProvider(new TagViewLabelProvider());

		this.getSite().setSelectionProvider(table);
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
	 * This method must be called from within eclipse
	 * 
	 * @param reader
	 */
	public void setReader(RemoteReader reader) {
		// only intialize this the first time
		if (remoteReader == null) {
			setPartName(reader.getID() + " Tags");
			this.remoteReader = reader;
			this.tags = reader.getTags();
			table.setInput(tags);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		this.tags.removeSetChangeListener(this);
	}

	@Override
	public void handleSetChange(SetChangeEvent event) {
		Set additions = event.diff.getAdditions();
		Set removals = event.diff.getRemovals();
		if (!additions.isEmpty()) {
			RemoteTag[] tags = new RemoteTag[additions.size()];
			additions.toArray(tags);
			table.add(tags);
		}
		if (!removals.isEmpty()) {
			RemoteTag[] tags = new RemoteTag[removals.size()];
			removals.toArray(tags);
			table.remove(tags);
		}

	}
}
