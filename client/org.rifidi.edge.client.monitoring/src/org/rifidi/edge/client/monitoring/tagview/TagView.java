/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.monitoring.tagview.controls.StatisticsControl;
import org.rifidi.edge.client.monitoring.tagview.controls.TagControl;

/**
 * This class is the view for the Tags. It shows all tags seen and the number of
 * times each one has been reported.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagView extends ViewPart {

	/** The ID for this view */
	public static final String ID = "org.rifidi.edge.client.monitoring.tagview";
	private FormToolkit toolkit;
	private ScrolledForm form;
	private StatisticsControl statcontrol;
	private TagControl tagControl;


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		toolkit = new FormToolkit(parent.getDisplay());
		form = toolkit.createScrolledForm(parent);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		form.getBody().setLayout(layout);
		tagControl = new TagControl(form.getBody(), toolkit);
		statcontrol = new StatisticsControl(form.getBody(), toolkit);
		//new FilterControl(form.getBody(), toolkit);

	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		tagControl.dispose();
		statcontrol.dispose();
		super.dispose();
	}



	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		form.setFocus();

	}

}
