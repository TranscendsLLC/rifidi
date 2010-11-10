package org.rifidi.edge.client.monitoring.tagview.controls;

import org.eclipse.core.databinding.observable.map.IObservableMap;
import org.eclipse.jface.databinding.viewers.ObservableMapLabelProvider;
import org.eclipse.jface.databinding.viewers.ObservableSetTreeContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.sal.SALPluginActivator;

public class FilterControl {

	public FilterControl(Composite parent, FormToolkit toolkit) {
		Section section = toolkit.createSection(parent, Section.TITLE_BAR
				| Section.EXPANDED | Section.TWISTIE);
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL);
		tableWrapData.grabHorizontal = true;
		section.setLayoutData(tableWrapData);

		Composite c = toolkit.createComposite(section);
		c.setLayout(new GridLayout(2, false));

		section.setText("Tag View Filter");

		Label l1 = toolkit.createLabel(c, "Reader Filter:");
		GridData labelData = new GridData();
		labelData.verticalAlignment = GridData.BEGINNING;
		l1.setLayoutData(labelData);

//		TableViewer readers = new TableViewer(c);
//		readers.getTable().setLayoutData(new GridData());
//		ObservableSetTreeContentProvider cp = new ObservableSetTreeContentProvider();
//		readers.setContentProvider(cp);
//		readers.setInput(SALPluginActivator.getDefault().getSalModelService()
//				.getModel().getRemoteReaders());
//		readers.setLabelProvider(new ViewLabelProvider(null));
		section.setClient(c);

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
			RemoteReader reader = (RemoteReader) obj;
			return reader.getDisplayName();
		}
	}

}
