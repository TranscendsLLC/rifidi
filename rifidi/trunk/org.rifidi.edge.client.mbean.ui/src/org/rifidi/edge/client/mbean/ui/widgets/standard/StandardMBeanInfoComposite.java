package org.rifidi.edge.client.mbean.ui.widgets.standard;

import java.util.HashMap;
import java.util.Set;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rifidi.edge.client.mbean.ui.AbstractMBeanInfoComposite;
import org.rifidi.edge.client.mbean.ui.widgets.abstractwidgets.AbstractWidget;
import org.rifidi.edge.client.mbean.ui.widgets.data.FloatWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.data.IntegerWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.data.LongWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.data.StringWidgetData;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardFloatWidget;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardIntegerWidget;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StandardLongWidget;
import org.rifidi.edge.client.mbean.ui.widgets.standard.impl.StringWidget;

/**
 * This is the default implementation of a MBeanInfoComposite. It looks like a
 * table with one column having labels and the other column having widgets. In
 * addition, it can display errors by showing a red x near the widget that has a
 * problem.
 * 
 * It uses standard SWT widgets
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class StandardMBeanInfoComposite extends AbstractMBeanInfoComposite {

	/** If true, display errors */
	private boolean displayErrors;
	/** A list of error icons to turn on and off */
	private HashMap<String, Label> errorIcons;
	/** The logger for this class */
	private static final Log logger = LogFactory
			.getLog(StandardMBeanInfoComposite.class);

	/**
	 * Constructor
	 * 
	 * @param formRoot
	 *            - The MBeanInfo object to use
	 * @param filterCategories
	 *            A set of strings to filter for
	 * @param include
	 *            if true, the filter categories are interpreted to mean display
	 *            only the categories in the set. If false, it means display all
	 *            categories except for those in the set
	 * @param displayErrors
	 *            If true, display red xs by controls which do not pass
	 *            validation
	 */
	public StandardMBeanInfoComposite(MBeanInfo formRoot,
			Set<String> filterCategories, boolean include, boolean displayErrors) {
		super(formRoot, filterCategories, include);
		this.displayErrors = displayErrors;
		this.errorIcons = new HashMap<String, Label>();
	}

	/**
	 * Create the control
	 * 
	 * @param parent
	 *            - the composite to draw on
	 */
	public void createControls(Composite parent) {

		// create the main composite for this form
		Composite formComposite = new Composite(parent, SWT.NONE);
		GridLayout formCompositeLayout = new GridLayout(1, false);
		formCompositeLayout.marginHeight = 0;
		formComposite.setLayout(formCompositeLayout);

		// create the composite that all the widgets will go into
		Composite widgetCompsoite;

		if (this.displayErrors) {
			// if we should display errors, add a column for error icons
			widgetCompsoite = new Composite(formComposite, SWT.NONE);
			GridLayout widgetCompositeLayout = new GridLayout();
			widgetCompositeLayout.makeColumnsEqualWidth = false;
			widgetCompositeLayout.numColumns = 3;
			widgetCompsoite.setLayout(widgetCompositeLayout);

		} else {
			// if we should not display errors, just make two columns
			widgetCompsoite = new Composite(formComposite, SWT.NONE);
			GridLayout widgetCompositeLayout = new GridLayout();
			widgetCompositeLayout.makeColumnsEqualWidth = false;
			widgetCompositeLayout.numColumns = 2;
			if (formRoot.getAttributes().length == 0) {
				widgetCompositeLayout.verticalSpacing = 0;
				widgetCompositeLayout.marginHeight = 1;
			}
			widgetCompsoite.setLayout(widgetCompositeLayout);
		}

		Set<MBeanAttributeInfo> children = filterAttributes();

		// step through each MBeanAttributeInfo and create a widget for it
		for (MBeanAttributeInfo child : children) {
			boolean skip = false;
			// createWidget
			String type = child.getType();
			AbstractWidget<?> widget = null;

			if (type.equals(Boolean.class.getName())) {
				// widget = new StandardBooleanWidget(new
				// BooleanWidgetData(child));
			} else if (type.equals(Float.class.getName())) {
				widget = new StandardFloatWidget<FloatWidgetData>(
						new FloatWidgetData(child));
			} else if (type.equals(Integer.class.getName())) {
				widget = new StandardIntegerWidget<IntegerWidgetData>(
						new IntegerWidgetData(child));
			} else if (type.equals(String.class.getName())) {
				widget = new StringWidget<StringWidgetData>(
						new StringWidgetData(child));
			} else if (type.equals(Long.class.getName())) {
				widget = new StandardLongWidget<LongWidgetData>(
						new LongWidgetData(child));
			} else {
				logger.warn("No widget found for type " + type);
				skip = true;
			}
			if (!skip) {
				addWidget(widget);
			}
		}

		for (AbstractWidget<?> widget : getSortedListOfWidgets()) {
			if (this.displayErrors) {
				Label imageLabel = new Label(widgetCompsoite, SWT.NONE);
				// TODO: remove this and add image manually
				Image x = JFaceResources.getImage("dialog_message_error_image");
				imageLabel.setImage(x);
				imageLabel.setVisible(false);
				errorIcons.put(widget.getElementName(), imageLabel);
			}

			// add a label
			widget.createLabel(widgetCompsoite);
			widget.createControl(widgetCompsoite);
		}
	}

	/**
	 * {@link AbstractMBeanInfoComposite#setError(String, String)}
	 */
	@Override
	public void setError(String widgetName, String message) {
		AbstractWidget<?> widget = getWidget(widgetName);
		if (widget != null) {
			errorIcons.get(widget.getElementName()).setVisible(true);
		}
	}

	/**
	 * {@link AbstractMBeanInfoComposite#unsetError(String)}
	 */
	@Override
	public void unsetError(String widgetName) {
		AbstractWidget<?> widget = getWidget(widgetName);
		if (widget != null) {
			errorIcons.get(widget.getElementName()).setVisible(false);
		}
	}
}
