/**
 * 
 */
package org.rifidi.edge.client.sal.wizards.submitjob;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;

/**
 * This is a composite that lets a user pick what kind of execution (recurring
 * or single-shot) a command should have, and also what the repeat interval
 * should be of recurring commands
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class IntervalChooserComposite{

	/** Composite to display timing */
	private Composite timingComposite;
	/** Spinner to let users choose the execution interval */
	private Spinner spinner;
	/** Whether or not the command is single-shot */
	private boolean recurring = true;
	/** Label for spinner */
	private Label spinnerLabel;

	/**
	 * Constructor
	 * 
	 * @param parent
	 * @param style
	 */
	public IntervalChooserComposite(Composite parent, int style) {

		// Button for singleshot
		Button button1 = new Button(parent, SWT.RADIO);
		button1.setText("One Time Execution");
		button1.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				recurring = false;
				spinner.setEnabled(false);
				spinnerLabel.setEnabled(false);
			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				recurring = false;
				spinner.setEnabled(false);
				spinnerLabel.setEnabled(false);
			}

		});

		// Button for recurring
		Button button2 = new Button(parent, SWT.RADIO);
		button2.setText("Recurring Execution");
		button2.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				recurring = true;
				spinnerLabel.setEnabled(true);
				spinner.setEnabled(true);

			}

			@Override
			public void widgetSelected(SelectionEvent e) {
				recurring = true;
				spinnerLabel.setEnabled(true);
				spinner.setEnabled(true);
			}
		});

		// display timing
		timingComposite = new Composite(parent, SWT.NONE);
		timingComposite.setLayout(new GridLayout(2, false));
		GridData timingCompositeGridData = new GridData(
				GridData.FILL_HORIZONTAL);
		timingCompositeGridData.horizontalIndent = 20;
		timingCompositeGridData.verticalIndent = 0;
		timingComposite.setLayoutData(timingCompositeGridData);
		spinnerLabel = new Label(timingComposite, SWT.None);
		spinnerLabel.setText("Interval (ms): ");
		spinner = new Spinner(timingComposite, SWT.BORDER);
		GridData spinnerGridData = new GridData();
		spinnerGridData.grabExcessHorizontalSpace = true;
		spinnerGridData.widthHint = 100;
		spinner.setLayoutData(spinnerGridData);
		spinner.setMinimum(0);
		spinner.setMaximum(Integer.MAX_VALUE);
		spinner.setSelection(1000);
		button2.setSelection(true);
	}

	/**
	 * Get the repeat interval from this widget
	 * 
	 * @return
	 */
	protected Long getInterval() {
		if (recurring) {
			return new Long(spinner.getSelection());
		} else
			return 0l;
	}

}
