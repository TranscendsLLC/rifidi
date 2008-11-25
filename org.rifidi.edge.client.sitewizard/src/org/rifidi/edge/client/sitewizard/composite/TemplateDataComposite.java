/* 
 * TemplateDataComposite.java
 *  Created:	Aug 7, 2008
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
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ListViewer;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.rifidi.edge.client.sitewizard.composite.bottom.BottomCompositeSingleton;
import org.rifidi.edge.client.sitewizard.composite.bottom.DialogContent;
import org.rifidi.edge.client.sitewizard.composite.data.SiteWizardData;
import org.rifidi.edge.client.sitewizard.template.content.TemplateContent;
import org.rifidi.edge.client.sitewizard.template.content.TemplateContentLabelProvider;
import org.rifidi.edge.client.sitewizard.template.content.TemplateContentList;
import org.rifidi.edge.client.sitewizard.template.content.TemplateContentProvider;

/**
 * 
 * 
 * @author Matthew Dean - matt@pramari.com
 */
public class TemplateDataComposite extends DataComposite {

	/**
	 * 
	 */
	private static Log logger = LogFactory.getLog(TemplateDataComposite.class);

	/**
	 * 
	 */
	private ListViewer listViewer = null;

	/**
	 * @param parent
	 * @param style
	 */
	public TemplateDataComposite(Composite parent, int style) {
		super(parent, style);

		this.setLayout(new FillLayout());

		logger.debug("the ListViewer constructor");

		listViewer = new ListViewer(this, SWT.V_SCROLL);
		listViewer.setContentProvider(new TemplateContentProvider());
		listViewer.setInput(TemplateContentList.getInstance());
		listViewer.setLabelProvider(new TemplateContentLabelProvider());

		listViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				IStructuredSelection selection = (IStructuredSelection) event
						.getSelection();
				if (selection.getFirstElement() == null) {
					BottomCompositeSingleton.getInstance().setNextDisabled();
				} else {
					TemplateContent tc = (TemplateContent) selection
							.getFirstElement();
					SiteWizardData.getInstance().setTemplate(tc);
					SiteWizardData.getInstance().setTemplateSelection(selection);
					BottomCompositeSingleton.getInstance().setNextEnabled();
				}
				updateNextButton();
			}
		});

		listViewer.refresh();

		this.layout();

		logger.debug("End the ListViewer constructor");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sitewizard.composite.DataComposite#
	 * getNextDataComposite()
	 */
	@Override
	public DataComposite getNextDataComposite() {
		return new ReaderDataComposite(MasterComposite.getInstance()
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
		if (SiteWizardData.getInstance().getTemplate() == null) {
			return new DialogContent(false, "You must select a template");
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
		return new TemplateDataComposite(parent, style);
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

	@Override
	public void fillOutInfo() {
		if (SiteWizardData.getInstance().getTemplateSelection() != null) {
			logger.debug("setting the selection");
			this.listViewer.setSelection(SiteWizardData.getInstance()
					.getTemplateSelection());
		}
	}
}
