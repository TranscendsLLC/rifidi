/* 
 * DisplayDataComposite.java
 *  Created:	Aug 13, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.rifidi.edge.client.sitewizard.composite.bottom.BottomCompositeSingleton;
import org.rifidi.edge.client.sitewizard.composite.bottom.DialogContent;
import org.rifidi.edge.client.sitewizard.composite.data.SiteWizardData;
import org.rifidi.edge.client.sitewizard.creator.ReaderObject;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class DisplayDataComposite extends DataComposite {

	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(DisplayDataComposite.class);

	/**
	 * 
	 * @param parent
	 * @param style
	 */
	public DisplayDataComposite(Composite parent, int style) {
		super(parent, style);

		logger.debug("Making a display data composite");

		this.setLayout(new GridLayout(2, true));

		Label name_id = new Label(this, SWT.NONE);
		name_id.setText("Name: ");

		Label name_actual = new Label(this, SWT.NONE);
		name_actual.setText(SiteWizardData.getInstance().getName());

		Label description_id = new Label(this, SWT.NONE);
		description_id.setText("Description: ");

		Label description_actual = new Label(this, SWT.NONE);
		String desc = SiteWizardData.getInstance().getDesc();
		if (desc == null) {
			desc = "";
		}
		description_actual.setText(desc);

		Label ip_id = new Label(this, SWT.NONE);
		ip_id.setText("IP: ");

		Label ip_actual = new Label(this, SWT.NONE);
		logger.debug("IP is: " + SiteWizardData.getInstance().getIP());
		ip_actual.setText(SiteWizardData.getInstance().getIP());

		Label port_id = new Label(this, SWT.NONE);
		port_id.setText("Port: ");

		Label port_actual = new Label(this, SWT.NONE);
		port_actual.setText(SiteWizardData.getInstance().getPort());

		Label template_id = new Label(this, SWT.NONE);
		template_id.setText("Template: ");

		Label template_actual = new Label(this, SWT.NONE);
		if (SiteWizardData.getInstance().getTemplate() != null) {
			template_actual.setText(SiteWizardData.getInstance().getTemplate()
					.getName());
		} else {
			template_actual.setText(SiteWizardData.getInstance()
					.getDefaultTemplate().getName());
		}

		Label reader_id = new Label(this, SWT.NONE);
		reader_id.setText("Reader: ");

		Label reader_actual = new Label(this, SWT.NONE);
		String readers = new String("");
		if (!SiteWizardData.getInstance().isCustom()) {
			for (ReaderObject rc : SiteWizardData.getInstance()
					.getDefaultReaders()) {
				readers += rc.getName() + "\n";
			}
		} else {
			for (ReaderObject rc : SiteWizardData.getInstance().getReaders()) {
				readers += rc.getName() + "\n";
			}
		}
		reader_actual.setText(readers);

		this.layout();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#areYouDone()
	 */
	@Override
	public DialogContent areYouDone() {
		return new DialogContent(true, "");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sitewizard.composite.DataComposite#
	 * getNextDataComposite()
	 */
	@Override
	public DataComposite getNextDataComposite() {
		return new FinalDataComposite(parent, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#remakeDataComposite
	 * (org.eclipse.swt.widgets.Composite, int)
	 */
	@Override
	public DataComposite remakeDataComposite(Composite parent, int style) {
		return new DisplayDataComposite(parent, style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#updateNextButton
	 * ()
	 */
	@Override
	protected void updateNextButton() {
		BottomCompositeSingleton.getInstance().setNextButtonText(
				BottomCompositeSingleton.CREATE_TEXT);
		BottomCompositeSingleton.getInstance().setNextEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#updatePrevButton
	 * ()
	 */
	@Override
	protected void updatePrevButton() {
		BottomCompositeSingleton.getInstance().setPreviousEnabled();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#fillOutInfo()
	 */
	@Override
	public void fillOutInfo() {
		// Nothing
	}
}
