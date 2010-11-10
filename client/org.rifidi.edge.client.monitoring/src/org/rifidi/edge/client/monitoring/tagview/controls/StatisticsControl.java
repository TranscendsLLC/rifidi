/**
 * 
 */
package org.rifidi.edge.client.monitoring.tagview.controls;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.rifidi.edge.client.monitoring.tagview.model.TagModelProviderSingleton;

/**
 * @author kyle
 * 
 */
public class StatisticsControl implements PropertyChangeListener {

	private int numTagsSeen;
	private long lastRefresh;
	private long durationTime = 0;
	private Label label_rate;
	private Label label_tagsSeen;
	private Label label_duration;

	public StatisticsControl(Composite parent, FormToolkit toolkit) {
		TagModelProviderSingleton.getInstance().getTags()
				.addPropertyChangeListener(this);

		Section section = toolkit.createSection(parent, Section.TITLE_BAR
				| Section.EXPANDED | Section.TWISTIE);
		TableWrapData tableWrapData = new TableWrapData(TableWrapData.FILL);
		tableWrapData.grabHorizontal = true;
		section.setLayoutData(tableWrapData);

		Composite c = toolkit.createComposite(section);
		c.setLayout(new GridLayout(2, false));

		section.setText("Tag Collection Statistics");

		Label l1 = toolkit.createLabel(c, "Tags/Sec:");
		l1.setLayoutData(new GridData());
		label_rate = toolkit.createLabel(c, "0");
		GridData labelLayout = new GridData();
		labelLayout.widthHint = 200;
		label_rate.setLayoutData(labelLayout);

		Label l2 = toolkit.createLabel(c, "Tags Seen:");
		l2.setLayoutData(new GridData());
		label_tagsSeen = toolkit.createLabel(c, "0");
		labelLayout.widthHint = 200;
		label_tagsSeen.setLayoutData(labelLayout);

		Label l3 = toolkit.createLabel(c, "Duration:");
		l3.setLayoutData(new GridData());
		label_duration = toolkit.createLabel(c, "0");
		labelLayout.widthHint = 200;
		label_duration.setLayoutData(labelLayout);

		section.setClient(c);
	}

	public void dispose() {
		TagModelProviderSingleton.getInstance().getTags()
				.removePropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals("numTagsSeen")) {
			numTagsSeen = (Integer) evt.getNewValue();
			this.label_tagsSeen.setText(Integer.toString(numTagsSeen));
		} else if (evt.getPropertyName().equals("lastRefresh")) {
			lastRefresh = (Long) evt.getNewValue();
			this.durationTime = 0;
		}

		long now = System.currentTimeMillis();
		long oldDuration = durationTime;
		durationTime = (now - lastRefresh) / 1000;
		if (durationTime > 0) {
			Float rate = new Float(numTagsSeen) / new Float(durationTime);
			this.label_rate.setText(Float.toString(rate));
			if (oldDuration != durationTime) {
				this.label_duration.setText(Long.toString(durationTime));
			}
		} else {
			this.label_rate.setText("0");
			this.label_duration.setText("0");
		}

	}

}
