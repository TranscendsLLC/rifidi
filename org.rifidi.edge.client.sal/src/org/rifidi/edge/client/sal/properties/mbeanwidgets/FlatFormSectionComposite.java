/**
 * 
 */
package org.rifidi.edge.client.sal.properties.mbeanwidgets;

import java.util.HashMap;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.rifidi.edge.client.mbean.ui.AbstractMBeanInfoComposite;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.AbstractWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.data.IntegerWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.data.LongWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.data.StringWidgetData;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class FlatFormSectionComposite extends AbstractMBeanInfoComposite {

	private HashMap<String, Section> widgetNameToSection;

	public FlatFormSectionComposite(MBeanInfo info, Set<String> categories,
			boolean includeExclude) {
		super(info, categories, includeExclude);
		widgetNameToSection = new HashMap<String, Section>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.AbstractMBeanInfoComposite#createControls
	 * (org.eclipse.swt.widgets.Composite)
	 */
	public void createControls(Composite parent,
			TabbedPropertySheetWidgetFactory factory) {

		Set<MBeanAttributeInfo> children = filterAttributes();
		for (MBeanAttributeInfo info : children) {
			AbstractWidget<?> widget = null;
			if (String.class.getName().equals(info.getType())) {
				widget = new FlatFormStringMbeanWidget<StringWidgetData>(
						new StringWidgetData(info), factory);
			} else if (Integer.class.getName().equals(info.getType())) {
				widget = new FlatFormIntegerMbeanWidget<IntegerWidgetData>(
						new IntegerWidgetData(info), factory);
			} else if (Long.class.getName().equals(info.getType())) {
				widget = new FlatFormLongMbeanWidget<LongWidgetData>(
						new LongWidgetData(info), factory);
			}

			if (widget != null) {
				super.addWidget(widget);
			}

		}

		Composite composite = factory.createFlatFormComposite(parent);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 1;
		composite.setLayout(layout);

		for (AbstractWidget<?> widget : super.getSortedListOfWidgets()) {
			AbstractWidgetData data = widget.getWidgetData();
			Section section = factory.createSection(composite, Section.EXPANDED
					| Section.CLIENT_INDENT | Section.TITLE_BAR
					| Section.DESCRIPTION);
			String title = data.getDisplayName();
			if (title == null || title.equals("")) {
				title = data.getName();
			}
			String description = data.getDescription();
			section.setText(title);
			section.setDescription(description);
			section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
			section.setLayout(new TableWrapLayout());

			widgetNameToSection.put(widget.getElementName(), section);

			Composite sectionComposite = factory
					.createFlatFormComposite(section);
			sectionComposite.setLayoutData(new TableWrapData(
					TableWrapData.FILL_GRAB));
			sectionComposite.setLayout(new TableWrapLayout());
			widget.createControl(sectionComposite);
			section.setClient(sectionComposite);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.AbstractMBeanInfoComposite#setError(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void setError(String widgetName, String message) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.AbstractMBeanInfoComposite#unsetError
	 * (java.lang.String)
	 */
	@Override
	public void unsetError(String widgetName) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.AbstractMBeanInfoComposite#dataChanged
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public void dataChanged(String widgetName, String newData) {
		super.dataChanged(widgetName, newData);
		Section section = widgetNameToSection.get(widgetName);
		if (section != null) {
			String title = section.getText();
			if (title.charAt(0) != '*') {
				section.setText("*" + title);
				section.layout();
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.mbean.ui.AbstractMBeanInfoComposite#clean(java
	 * .lang.String)
	 */
	@Override
	public void clean(String widgetName) {
		super.clean(widgetName);
		Section section = widgetNameToSection.get(widgetName);
		if (section != null) {
			String title = section.getText();
			if (title.charAt(0) == '*') {
				title = title.substring(1);
				section.setText(title);
				section.layout();
			}
		}
	}
}
