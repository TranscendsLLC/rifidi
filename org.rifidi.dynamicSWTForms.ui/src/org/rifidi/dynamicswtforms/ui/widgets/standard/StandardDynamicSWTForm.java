package org.rifidi.dynamicswtforms.ui.widgets.standard;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.jdom.Element;
import org.rifidi.dynamicswtforms.ui.exceptions.DynamicSWTFormInvalidXMLException;
import org.rifidi.dynamicswtforms.ui.form.AbstractDynamicSWTForm;
import org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget;
import org.rifidi.dynamicswtforms.ui.widgets.data.ChoiceWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.FloatWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.IntegerWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.StringWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.standard.impl.ChoiceWidget;
import org.rifidi.dynamicswtforms.ui.widgets.standard.impl.NumberWidget;
import org.rifidi.dynamicswtforms.ui.widgets.standard.impl.StringWidget;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;

/**
 * This is the default implementation of a Form. It looks like a table with one
 * column having labels and the other column having widgets. In addition, it can
 * display errors by showing a red x near the widget that has a problem
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class StandardDynamicSWTForm extends AbstractDynamicSWTForm {

	/**
	 * If true, display errors
	 */
	private boolean displayErrors;

	/**
	 * A list of error icons to turn on and off
	 */
	private ArrayList<Label> errorIcons;

	/**
	 * Creates a new form from a pre-processed xml
	 * 
	 * @param formRoot
	 *            The Form XML
	 * @param displayErrors
	 *            true if errors should be displayed
	 */
	public StandardDynamicSWTForm(Element formRoot, boolean displayErrors) {
		super(formRoot);
		this.displayErrors = displayErrors;
		this.errorIcons = new ArrayList<Label>();
	}

	/**
	 * 
	 * Creates a new form from an XML String
	 * 
	 * @param formRoot
	 *            The Form XML
	 * @param displayErrors
	 *            true if errors should be displayed
	 * @throws DynamicSWTFormInvalidXMLException
	 *             if there was a problem with the XML string
	 */
	public StandardDynamicSWTForm(String xml, boolean displayErrors)
			throws DynamicSWTFormInvalidXMLException {
		super(xml);
		this.displayErrors = displayErrors;
		this.errorIcons = new ArrayList<Label>();
	}

	/**
	 * {@link AbstractDynamicSWTForm#createControls(Composite)}
	 */
	@Override
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
			if (formRoot.getChildren().size() == 0) {
				widgetCompositeLayout.verticalSpacing = 0;
				widgetCompositeLayout.marginHeight = 1;
			}
			widgetCompsoite.setLayout(widgetCompositeLayout);
		}

		List<Element> children = formRoot.getChildren();

		// step through each XML node and create a widget for it
		for (Element child : children) {
			// createWidget
			FormElementType type = null;
			DynamicSWTFormWidget widget = null;
			try {
				type = FormElementType.valueOf(child.getName());
			} catch (IllegalArgumentException ex) {
				// TODO: do something here
			}

			if (this.displayErrors) {
				Label imageLabel = new Label(widgetCompsoite, SWT.NONE);
				// TODO: remove this and add image manually
				Image x = JFaceResources.getImage("dialog_message_error_image");
				imageLabel.setImage(x);
				imageLabel.setVisible(false);
				errorIcons.add(imageLabel);
			}

			switch (type) {
			case BOOLEAN:
				// widget = new BooleanWidget(new BooleanWidgetData(child));
				break;
			case CHOICE:
				widget = new ChoiceWidget(new ChoiceWidgetData(child));
				break;
			case FLOAT:
				widget = new NumberWidget(new FloatWidgetData(child));
				break;
			case INTEGER:
				widget = new NumberWidget(new IntegerWidgetData(child));
				break;
			case STRING:
				widget = new StringWidget(new StringWidgetData(child));
				break;
			}

			this.widgets.add(widget);

			// add a label
			widget.createLabel(widgetCompsoite);

			// add the newly created control
			widget.createControl(widgetCompsoite);

			// add a listner
			widget.addListener(this);
		}
	}

	/**
	 * {@link AbstractDynamicSWTForm#setError(String, String)}
	 */
	@Override
	public void setError(String widgetName, String message) {
		for (DynamicSWTFormWidget w : widgets) {
			if (w.getElementName().equalsIgnoreCase(widgetName)) {
				errorIcons.get(widgets.indexOf(w)).setVisible(true);
			}
		}
	}

	/**
	 * {@link AbstractDynamicSWTForm#unsetError(String)}
	 */
	@Override
	public void unsetError(String widgetName) {
		for (DynamicSWTFormWidget w : widgets) {
			if (w.getElementName().equalsIgnoreCase(widgetName)) {
				errorIcons.get(widgets.indexOf(w)).setVisible(false);
			}
		}
	}

}
