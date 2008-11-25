package org.rifidi.edge.client.properties.dynamicSWTForms;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetWidgetFactory;
import org.jdom.Element;
import org.rifidi.dynamicswtforms.ui.form.AbstractDynamicSWTForm;
import org.rifidi.dynamicswtforms.ui.widgets.DynamicSWTFormWidget;
import org.rifidi.dynamicswtforms.ui.widgets.data.ChoiceWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.FloatWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.IntegerWidgetData;
import org.rifidi.dynamicswtforms.ui.widgets.data.StringWidgetData;
import org.rifidi.dynamicswtforms.xml.constants.FormData;
import org.rifidi.dynamicswtforms.xml.constants.FormElementType;
import org.rifidi.edge.client.properties.dynamicSWTForms.widgets.TabbedPropertyChoiceWidget;
import org.rifidi.edge.client.properties.dynamicSWTForms.widgets.TabbedPropertyNumberWidget;
import org.rifidi.edge.client.properties.dynamicSWTForms.widgets.TabbedPropertyStringWidget;

/**
 * A concrete implementation of AbstractDynamicSWTForm that draws the form using
 * the TabbedPropertySheetWidgetFactory
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TabbedPropertyForm extends AbstractDynamicSWTForm {

	private TabbedPropertySheetWidgetFactory factory;
	private List<Label> errorIcons;
	private Section formSection;

	public TabbedPropertyForm(Element formRoot,
			TabbedPropertySheetWidgetFactory widgetFactory) {
		super(formRoot);
		this.factory = widgetFactory;
		this.errorIcons = new ArrayList<Label>();
	}

	@Override
	public void createControls(Composite parent) {
		formSection = factory.createSection(parent, Section.DESCRIPTION
				| Section.TITLE_BAR | Section.EXPANDED);
		formSection.setText(formRoot.getAttributeValue(FormData.NAME.name()));

		Composite widgetCompsoite;

		widgetCompsoite = factory.createFlatFormComposite(formSection);
		GridLayout widgetCompositeLayout = new GridLayout();
		widgetCompositeLayout.makeColumnsEqualWidth = false;
		widgetCompositeLayout.numColumns = 3;
		widgetCompsoite.setLayout(widgetCompositeLayout);

		widgetCompsoite.setLayout(widgetCompositeLayout);

		List<Element> children = formRoot.getChildren();
		for (Element child : children) {
			// createWidget
			FormElementType type = null;
			DynamicSWTFormWidget widget = null;
			try {
				type = FormElementType.valueOf(child.getName());
			} catch (IllegalArgumentException ex) {
				// TODO: do something here
			}

			Label imageLabel = new Label(widgetCompsoite, SWT.NONE);
			// TODO: remove this and add image manually
			Image x = JFaceResources.getImage("dialog_message_error_image");
			imageLabel.setImage(x);
			imageLabel.setVisible(false);
			errorIcons.add(imageLabel);

			switch (type) {
			case BOOLEAN:
				// widget = new BooleanWidget(new BooleanWidgetData(child));
				break;
			case CHOICE:
				widget = new TabbedPropertyChoiceWidget(new ChoiceWidgetData(
						child), factory);
				break;
			case FLOAT:
				widget = new TabbedPropertyNumberWidget(new FloatWidgetData(
						child), factory);
				break;
			case INTEGER:
				widget = new TabbedPropertyNumberWidget(new IntegerWidgetData(
						child), factory);
				break;
			case STRING:
				widget = new TabbedPropertyStringWidget(new StringWidgetData(
						child), factory);
				break;
			}

			this.widgets.add(widget);
			widget.createLabel(widgetCompsoite);
			widget.createControl(widgetCompsoite);
			widget.addListener(this);
			formSection.setClient(widgetCompsoite);
		}

	}

	@Override
	public void setError(String widgetName, String message) {
		for (DynamicSWTFormWidget w : widgets) {
			if (w.getElementName().equalsIgnoreCase(widgetName)) {
				errorIcons.get(widgets.indexOf(w)).setVisible(true);
			}
		}
		this.formSection.setDescription(message);
		this.formSection.layout();
	}

	@Override
	public void unsetError(String widgetName) {
		for (DynamicSWTFormWidget w : widgets) {
			if (w.getElementName().equalsIgnoreCase(widgetName)) {
				errorIcons.get(widgets.indexOf(w)).setVisible(false);
			}
		}
		this.formSection.setDescription("");
		this.formSection.layout();
	}

}
