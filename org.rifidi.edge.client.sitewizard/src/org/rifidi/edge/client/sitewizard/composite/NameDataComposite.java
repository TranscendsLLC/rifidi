/* 
 * NameDataComposite.java
 *  Created:	Aug 7, 2008
 *  Project:	RiFidi Emulator - A Software Simulation Tool for RFID Devices
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.sitewizard.composite;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.rifidi.edge.client.sitewizard.composite.bottom.BottomCompositeSingleton;
import org.rifidi.edge.client.sitewizard.composite.bottom.DialogContent;
import org.rifidi.edge.client.sitewizard.composite.data.SiteWizardData;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class NameDataComposite extends DataComposite {

	/**
	 * 
	 */
	private static Log logger = LogFactory
			.getLog(BottomCompositeSingleton.class);

	// Labels
	private Label name_label = null;
	private Label desc_label = null;
	private Label ip_label = null;
	private Label port_label = null;

	// Text boxes
	private Text name_text = null;
	private Text desc_text = null;
	private Text ip_text = null;
	private Text port_text = null;

	/**
	 * A composite that holds the name information for the composite.
	 * 
	 * @param parent
	 * @param style
	 */
	public NameDataComposite(Composite parent, int style) {
		super(parent, style);

		GridData fill_h_1 = new GridData(GridData.FILL_HORIZONTAL);
		GridData fill_h_2 = new GridData(GridData.FILL_HORIZONTAL);
		GridData fill_h_3 = new GridData(GridData.FILL_HORIZONTAL);
		GridData fill_h_4 = new GridData(GridData.FILL_HORIZONTAL);

		this.setLayout(new GridLayout(2, false));
		GridData gridDat = new GridData();
		gridDat.grabExcessHorizontalSpace = true;
		this.setLayoutData(gridDat);

		name_label = new Label(this, SWT.NONE);
		name_label.setText("Name: ");

		name_text = new Text(this, SWT.BORDER);
		name_text.setTextLimit(30);
		name_text.setLayoutData(fill_h_1);
		name_text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {

			}

			@Override
			public void keyReleased(KeyEvent e) {
				SiteWizardData.getInstance().setName(name_text.getText());
				updateNextButton();
			}
		});

		desc_label = new Label(this, SWT.NONE);
		desc_label.setText("Description: ");

		desc_text = new Text(this, SWT.BORDER);
		desc_text.setTextLimit(30);
		desc_text.setLayoutData(fill_h_2);
		desc_text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Nothing
			}

			@Override
			public void keyReleased(KeyEvent e) {
				SiteWizardData.getInstance().setDesc(desc_text.getText());
				updateNextButton();
			}
		});

		ip_label = new Label(this, SWT.NONE);
		ip_label.setText("IP: ");

		ip_text = new Text(this, SWT.BORDER);
		ip_text.setTextLimit(30);
		ip_text.setLayoutData(fill_h_3);
		ip_text.setText(SiteWizardData.getInstance().getIP());
		ip_text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Nothing
			}

			@Override
			public void keyReleased(KeyEvent e) {
				SiteWizardData.getInstance().setIP(ip_text.getText());
				updateNextButton();
			}
		});

		port_label = new Label(this, SWT.NONE);
		port_label.setText("Port: ");

		port_text = new Text(this, SWT.BORDER);
		port_text.setTextLimit(7);
		port_text.setLayoutData(fill_h_4);
		port_text.setText(SiteWizardData.getInstance().getPort());
		port_text.addKeyListener(new KeyListener() {
			@Override
			public void keyPressed(KeyEvent e) {
				// Nothing
			}

			@Override
			public void keyReleased(KeyEvent e) {
				SiteWizardData.getInstance().setPort(port_text.getText());
				updateNextButton();
			}
		});

		parent.layout(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sitewizard.composite.DataComposite#
	 * getNextDataComposite()
	 */
	@Override
	public DataComposite getNextDataComposite() {
		if (SiteWizardData.getInstance().isCustom()) {
			return new TemplateDataComposite(parent, SWT.NONE);
		}
		// Return the summary screen
		return new DisplayDataComposite(parent, SWT.NONE);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#areYouDone()
	 */
	@Override
	public DialogContent areYouDone() {
		if (SiteWizardData.getInstance().getName() == null
				|| SiteWizardData.getInstance().getName().equals("")) {
			return new DialogContent(false, "You need to enter a name");
		} else if ((SiteWizardData.getInstance().getIP() == null || SiteWizardData
				.getInstance().getIP().equals(""))
				&& !isAValidIP(SiteWizardData.getInstance().getIP())) {
			return new DialogContent(false, "Must enter a valid IP");
		} else if (isAValidPort(SiteWizardData.getInstance().getPort())) {
			return new DialogContent(false, "Must enter a valid port");
		}
		return new DialogContent(true, "");
	}

	/**
	 * 
	 * 
	 * @param alpha
	 * @return
	 */
	public boolean isAValidPort(String alpha) {
		int test = -1;
		try {
			test = Integer.parseInt(alpha);
		} catch (NumberFormatException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		if (test > 1 && test < 65535) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * 
	 * @param ip
	 * @return
	 */
	public boolean isAValidIP(String ip) {
		try {
			InetAddress.getByName(ip);
		} catch (UnknownHostException e) {
			return false;
		} catch (NullPointerException e) {
			return false;
		}
		return true;
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
		return new NameDataComposite(parent, style);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#updateNextButton
	 * ()
	 */
	@Override
	public void updateNextButton() {
		logger.debug("updating the next button in namedatacomposite");
		BottomCompositeSingleton.getInstance().setNextButtonText(
				BottomCompositeSingleton.NEXT_TEXT);
		if (this.areYouDone().isDone()) {
			BottomCompositeSingleton.getInstance().setNextEnabled();
		} else {
			BottomCompositeSingleton.getInstance().setNextDisabled();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#updatePrevButton
	 * ()
	 */
	@Override
	public void updatePrevButton() {
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
		logger.debug("filling out the info in the namedatacomposite: "
				+ this.name_text);
		this.desc_text.setText(SiteWizardData.getInstance().getDesc());
		this.name_text.setText(SiteWizardData.getInstance().getName());
		this.ip_text.setText(SiteWizardData.getInstance().getIP());
		this.port_text.setText(SiteWizardData.getInstance().getPort());
		logger.debug("done filling out info in the namedatacomposite");

		this.layout();
	}
}
