/* 
 * ReaderDataComposite.java
 *  Created:	Aug 6, 2008
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
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.sitewizard.composite.bottom.BottomCompositeSingleton;
import org.rifidi.edge.client.sitewizard.composite.bottom.DialogContent;
import org.rifidi.edge.client.sitewizard.composite.data.SiteWizardData;
import org.rifidi.edge.client.sitewizard.creator.ReaderObject;
import org.rifidi.edge.client.sitewizard.reader.content.ReaderLabelProvider;
import org.rifidi.edge.client.sitewizard.reader.content.ReaderObjectList;
import org.rifidi.edge.client.sitewizard.reader.content.ReaderObjectProvider;

/**
 * This composite will have you select which readers you want.
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class ReaderDataComposite extends DataComposite {

	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(ReaderDataComposite.class);

	/**
	 * 
	 */
	private CheckboxTreeViewer checkViewer = null;

	/**
	 * @param parent
	 * @param style
	 * @param data
	 */
	public ReaderDataComposite(Composite parent, int style) {
		super(parent, style);

		logger.debug("Starting the ReaderDataComposite");

		this.setLayout(new FillLayout());

		checkViewer = new CheckboxTreeViewer(this, SWT.V_SCROLL);
		checkViewer.getTree().setLayoutData(new GridData(GridData.FILL_BOTH));
		checkViewer.setContentProvider(new ReaderObjectProvider());
		checkViewer.setInput(ReaderObjectList.getInstance());
		checkViewer.setLabelProvider(new ReaderLabelProvider());
		checkViewer.addCheckStateListener(new ICheckStateListener() {
			@Override
			public void checkStateChanged(CheckStateChangedEvent event) {
				ReaderObject rc = (ReaderObject) event.getElement();
				if (event.getChecked()) {
					logger.debug("a reader was checked: " + rc.getName());
					SiteWizardData.getInstance().addReaderObject(rc);
					updateNextButton();
				} else {
					logger.debug("a reader was unchecked: " + rc.getName());
					SiteWizardData.getInstance().removeReaderObject(rc);
					updateNextButton();
				}
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
		return new DisplayDataComposite(MasterComposite.getInstance()
				.getMasterComposite(), SWT.FILL);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.client.sitewizard.composite.DataComposite#areYouDone()
	 */
	@Override
	public DialogContent areYouDone() {
		if (SiteWizardData.getInstance().getReaders().isEmpty()) {
			return new DialogContent(false, "Must select at least 1 reader");
		}
		return new DialogContent(true, "");
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
		return new ReaderDataComposite(parent, style);
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
		BottomCompositeSingleton.getInstance().setNextButtonText(
				BottomCompositeSingleton.NEXT_TEXT);
		if (this.areYouDone().isDone()) {
			BottomCompositeSingleton.getInstance().setNextEnabled();
		} else {
			BottomCompositeSingleton.getInstance().setNextEnabled();
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
		for (ReaderObject rc : SiteWizardData.getInstance()
				.getReaders()) {
			logger.debug("setting checked: " + rc.getName());
			boolean ret = checkViewer.setChecked(rc, true);
			logger.debug("ret is: " + ret);
		}
	}
}
