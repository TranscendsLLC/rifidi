/**
 * 
 */
package org.rifidi.edge.client.sal.properties.readers;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashSet;
import java.util.Set;

import javax.management.Attribute;
import javax.management.MBeanInfo;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.rifidi.edge.client.mbean.ui.MBeanInfoWidgetListener;
import org.rifidi.edge.client.model.sal.RemoteReader;
import org.rifidi.edge.client.model.sal.properties.ReaderPropertyBean;
import org.rifidi.edge.client.sal.properties.mbeanwidgets.FlatFormSectionComposite;

/**
 * @author Kyle Neumeier
 * 
 */
public class ReaderPropertySection extends AbstractPropertySection implements
		MBeanInfoWidgetListener, PropertyChangeListener {

	private MBeanInfo info;
	private RemoteReader reader;
	private FlatFormSectionComposite composite;

	public ReaderPropertySection(MBeanInfo mbeanInfo, RemoteReader reader,
			String category) {
		this.info = mbeanInfo;
		this.reader = reader;
		Set<String> categories = new HashSet<String>();
		categories.add(category);
		composite = new FlatFormSectionComposite(mbeanInfo, categories, true);
		composite.addListner(this);
		reader.addPropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls
	 * (org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);
		composite.createControls(parent, super.getWidgetFactory());
		for (Attribute attr : reader.getAttributes().asList()) {
			composite.setValue(attr);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		super.dispose();
		reader.removePropertyChangeListener(this);
		composite.removeListner(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput
	 * (org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		// TODO Auto-generated method stub
		super.setInput(part, selection);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.MBeanInfoWidgetListener#dataChanged(java
	 * .lang.String)
	 */
	@Override
	public void dataChanged(String widgetName, String newData) {
		// TODO: verify the data?
		this.reader.updateProperty(this.composite.getAttribute(widgetName));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.MBeanInfoWidgetListener#keyReleased()
	 */
	@Override
	public void keyReleased(String widget) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.MBeanInfoWidgetListener#clean(java.lang
	 * .String)
	 */
	@Override
	public void clean(String widgetName) {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {
		if (arg0.getPropertyName().equals(
				ReaderPropertyBean.READER_PROPERTY_BEAN)) {
			ReaderPropertyBean bean = (ReaderPropertyBean) arg0.getNewValue();
			composite.setValue(bean.getAttribute());
		}
	}

}
