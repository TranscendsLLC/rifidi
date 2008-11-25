package org.rifidi.edge.client.properties.sections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.jdom.Element;
import org.rifidi.dynamicswtforms.ui.widgets.listeners.DynamicSWTWidgetListener;
import org.rifidi.dynamicswtforms.xml.constants.FormData;
import org.rifidi.edge.client.connections.exceptions.CannotExecutePropertyException;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.properties.dynamicSWTForms.TabbedPropertyForm;
import org.rifidi.edge.core.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.readerplugin.property.PropertyConfiguration;

/**
 * The ReaderPropertySection represents a single Section on the TabbedProperty
 * page. It corresponds on one property on the reader, though it may have more
 * than one widget (i.e. property element) associated with it
 * 
 * @author Kyle Neumeier
 * 
 */
public class ReaderPropertySection extends AbstractPropertySection implements
		DynamicSWTWidgetListener {

	/**
	 * The Logger for this class
	 */
	private Log logger = LogFactory.getLog(ReaderPropertySection.class);

	/**
	 * The XML property description
	 */
	private Element property;

	/**
	 * The remote reader object
	 */
	private RemoteReader remoteReader;

	/**
	 * The widget form
	 */
	private TabbedPropertyForm form;

	/**
	 * The name of the reader property this section is associated with
	 */
	private String propertyName;

	/**
	 * The composite that the form will be drawn on
	 */
	private Composite composite;

	/**
	 * Whether or not this propertySection should take up extra space. Should be
	 * true only if this is the last property section on this tab
	 */
	private boolean shouldTakeSpace;

	/**
	 * Create a TabbedProperty section.
	 * 
	 * @param property
	 *            The XML description of the property (i.e. the DynamicSWTForm
	 *            xml)
	 * @param shouldTakeSpace
	 *            Should be true only if this is the last property section on
	 *            this tab
	 */
	public ReaderPropertySection(Element property, boolean shouldTakeSpace) {
		if (property == null) {
			logger.debug("PROP IS NULL!");
		}
		this.property = property;
		this.propertyName = property.getAttributeValue(FormData.NAME.name());
		this.shouldTakeSpace = shouldTakeSpace;
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);
		this.composite = parent;
		form = new TabbedPropertyForm(property, this.getWidgetFactory());
		form.addListner(this);
		form.createControls(parent);
	}

	/**
	 * This method is called when a remote reader object is selected on the tree
	 * on the left
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		super.setInput(part, selection);
		if (selection instanceof TreeSelection) {
			Object o = ((TreeSelection) selection).getFirstElement();
			if (o instanceof RemoteReader) {
				this.remoteReader = (RemoteReader) o;
				Set<String> propertyNames = new HashSet<String>();
				propertyNames.add(propertyName);
				if (remoteReader.canExecuteProperty()) {
					try {
						PropertyConfiguration retVal = remoteReader
								.getProperties(propertyNames);
						updateProperty(retVal);
					} catch (CannotExecutePropertyException e) {
						logger.error(e);
					}
				}
				enableControls();
			}

		}
	}

	@Override
	public boolean shouldUseExtraSpace() {
		return this.shouldTakeSpace;
	}

	/**
	 * This method sets the widget to grey if the reader is not in a state to
	 * allow properties to be edited
	 */
	private void enableControls() {
		if (remoteReader.canExecuteProperty()) {
			this.form.enable();
		} else {
			this.form.disable();
		}

	}

	@Override
	public void dataChanged(String newData) {
		HashMap<String, String> propertyValue = form.getWidgetNameValueMap();
		Set<CommandArgument> args = new HashSet<CommandArgument>();
		for (String argName : propertyValue.keySet()) {
			args.add(new CommandArgument(argName, propertyValue.get(argName),
					false));
		}
		CommandConfiguration cc = new CommandConfiguration(propertyName, args);
		Set<CommandConfiguration> props = new HashSet<CommandConfiguration>();
		props.add(cc);
		PropertyConfiguration pc = new PropertyConfiguration(props);
		if (remoteReader.canExecuteProperty()) {
			try {
				PropertyConfiguration retVal = remoteReader.setProperties(pc);
				updateProperty(retVal);
			} catch (CannotExecutePropertyException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * This is a helper method that changes this widget to reflect the data in
	 * the PropertyConfiguration
	 * 
	 * @param propertyConfig
	 *            The PropertyConfiguration returned from a reader after a get
	 *            or a set
	 */
	private void updateProperty(PropertyConfiguration propertyConfig) {

		if (propertyConfig == null) {
			return;
		}

		// get the property response
		CommandConfiguration cc = propertyConfig.getProperty(propertyName);

		if (cc == null) {
			return;
		}
		if (cc.getArgNames() == null) {
			return;
		}

		// for each property element
		for (String name : cc.getArgNames()) {
			// get the value of the element
			String elementValue = cc.getArgValue(name);
			String errorMessage = null;
			if (cc.hasError(name)) {
				// if there was an error when executing the command, set
				// error
				errorMessage = elementValue;
			} else {
				// set the error on the UI form
				errorMessage = form.setValue(name, elementValue);
			}
			if (errorMessage == null) {
				// if there was no error message, make sure error is
				// unset on widget
				form.unsetError(name);
			} else {
				form.setError(name, errorMessage);
			}
		}
		composite.layout();

	}

	@Override
	public void keyReleased() {

	}
}
