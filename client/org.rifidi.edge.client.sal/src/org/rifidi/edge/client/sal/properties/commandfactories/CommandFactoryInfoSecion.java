/*
 * CommandFactoryInfoSecion.java
 * 
 * Created:     Oct 21st, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.properties.commandfactories;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.rifidi.edge.client.model.sal.RemoteCommandConfigFactory;

/**
 * This class is a section of a tabbed properties view for describing a command
 * factory
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class CommandFactoryInfoSecion extends AbstractPropertySection {

	/** The factory to describe */
	private RemoteCommandConfigFactory factory;

	/***
	 * Create a new Info Section
	 * 
	 * @param factory
	 */
	public CommandFactoryInfoSecion(RemoteCommandConfigFactory factory) {
		super();
		this.factory = factory;
	}

	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage aTabbedPropertySheetPage) {
		super.createControls(parent, aTabbedPropertySheetPage);

		//create an "Information section"
		Section section = super.getWidgetFactory().createSection(parent,
				Section.EXPANDED | Section.TITLE_BAR);
		section.setLayout(new TableWrapLayout());
		section.setText("Command Factory Information");

		Composite composite = super.getWidgetFactory().createFlatFormComposite(
				section);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);
		composite.setLayoutData(new TableWrapData());

		// Compatible reader types
		CLabel readerLabel = super.getWidgetFactory().createCLabel(composite,
				"Compatible Reader Type");
		readerLabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		Text readerText = super.getWidgetFactory().createText(composite, "",
				SWT.None | SWT.MULTI);
		TableWrapData readerLayoutData = new TableWrapData(
				TableWrapData.FILL_GRAB);
		readerText.setLayoutData(readerLayoutData);
		readerText.setEnabled(false);
		readerText.setText(factory.getReaderFactoryID());

		// Description
		CLabel descriptionLabel = super.getWidgetFactory().createCLabel(
				composite, "Description");
		descriptionLabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		Text descriptionText = super.getWidgetFactory().createText(composite,
				"", SWT.MULTI | SWT.WRAP);
		TableWrapData descriptionLayoutData = new TableWrapData(
				TableWrapData.FILL);
		descriptionLayoutData.maxWidth = 250;
		descriptionText.setLayoutData(descriptionLayoutData);
		descriptionText.setEnabled(false);
		descriptionText.setText(factory.getDescription());

		section.setClient(composite);
	}

}
