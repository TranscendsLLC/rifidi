/* 
 *  AleEditorSelectionListener.java
 *  Created:	Apr 9, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.editor
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ecspecview.views;

import java.util.ArrayList;

import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReportSpec;

/**
 * A selection listener for the ScrollableForm in the ECSpecView.
 * It takes care of the actions required by the RemoveReportSpec Button.
 * It both removes the ReportSpec from the form (UI), as well as from the ArrayList(Model).  
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 */
public class ALEEditorSelectionListener implements Listener {
	private ScrolledForm form = null;
	private ArrayList<Section> sections = null;
	private ECReportSpec spec;
	private ArrayList<ECReportSpec> repSpecs;

	/**
	 * Removes a report section
	 * @param form - form to remove it from
	 * @param sections - sections to remove
	 * @param spec - ECReportSpec to remove from the list
	 * @param repSpecs list to remove it from
	 */
	public ALEEditorSelectionListener(ScrolledForm form,
			ArrayList<Section> sections, ECReportSpec spec,
			ArrayList<ECReportSpec> repSpecs) {
		super();
		this.form = form;
		this.sections = sections;
		this.repSpecs = repSpecs;
		this.spec = spec;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.swt.widgets.Listener#handleEvent(org.eclipse.swt.widgets.
	 * Event)
	 */
	@Override
	public void handleEvent(Event event) {

		for (Section section : this.sections) {
			if (section.getClient() != null) {
				section.getClient().dispose();
			}
			section.dispose();

		}

		form.reflow(true);
		repSpecs.remove(spec);

	}

}
