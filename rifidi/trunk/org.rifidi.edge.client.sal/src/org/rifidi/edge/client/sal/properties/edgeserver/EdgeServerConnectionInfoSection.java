
package org.rifidi.edge.client.sal.properties.edgeserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.rifidi.edge.client.model.SALModelPlugin;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteEdgeServerState;
import org.rifidi.edge.client.model.sal.preferences.EdgeServerPreferences;

/**
 * 
 * 
 * @author kyle
 */
public class EdgeServerConnectionInfoSection extends AbstractPropertySection
		implements FocusListener, PropertyChangeListener,
		IPropertyChangeListener {

	private List<Control> widgets = new ArrayList<Control>();
	private Text ipText = null;
	private Spinner rmiPortSpinner = null;
	private Spinner jmsPortSpinner = null;
	private Text notificationJMSQText = null;
	private Text tagsJMSQText = null;
	private RemoteEdgeServer server;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#createControls
	 * (org.eclipse.swt.widgets.Composite,
	 * org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage)
	 */
	@Override
	public void createControls(Composite parent,
			TabbedPropertySheetPage tabbedPropertySheetPage) {
		super.createControls(parent, tabbedPropertySheetPage);

		Section section = super.getWidgetFactory().createSection(parent,
				Section.EXPANDED | Section.TITLE_BAR);
		section.setLayout(new TableWrapLayout());
		section.setText("Connection Information");

		Composite composite = super.getWidgetFactory().createFlatFormComposite(
				section);
		TableWrapLayout layout = new TableWrapLayout();
		layout.numColumns = 2;
		layout.makeColumnsEqualWidth = false;
		composite.setLayout(layout);
		composite.setLayoutData(new TableWrapData());

		// IP
		CLabel iplabel = super.getWidgetFactory().createCLabel(composite,
				"IP Address");
		iplabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		ipText = super.getWidgetFactory().createText(composite, "", SWT.None);
		TableWrapData ipLayoutData = new TableWrapData(TableWrapData.FILL_GRAB);
		ipText.setLayoutData(ipLayoutData);
		ipText.addFocusListener(this);
		widgets.add(ipText);

		// RMI Port
		CLabel rmiPortLabel = super.getWidgetFactory().createCLabel(composite,
				"RMI Port");
		rmiPortLabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		rmiPortSpinner = new Spinner(composite, SWT.BORDER);
		TableWrapData rmiPortGridData = new TableWrapData(
				TableWrapData.FILL_GRAB);
		rmiPortSpinner.setLayoutData(rmiPortGridData);
		rmiPortSpinner.addFocusListener(this);
		rmiPortSpinner.setMinimum(0);
		rmiPortSpinner.setMaximum(Integer.MAX_VALUE);
		widgets.add(rmiPortSpinner);

		// JMS Port
		CLabel jmsPortLabel = super.getWidgetFactory().createCLabel(composite,
				"JMS Port");
		jmsPortLabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		jmsPortSpinner = new Spinner(composite, SWT.BORDER);
		TableWrapData jmsPortGridData = new TableWrapData(
				TableWrapData.FILL_GRAB);
		jmsPortSpinner.setLayoutData(jmsPortGridData);
		jmsPortSpinner.addFocusListener(this);
		jmsPortSpinner.setMinimum(0);
		jmsPortSpinner.setMaximum(Integer.MAX_VALUE);
		widgets.add(jmsPortSpinner);

		// Notification Queue
		CLabel jmsQueueLabel = super.getWidgetFactory().createCLabel(composite,
				"Notification Queue Name");
		jmsQueueLabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		notificationJMSQText = super.getWidgetFactory().createText(composite,
				"", SWT.None);
		TableWrapData notificationJMSLayoutData = new TableWrapData(
				TableWrapData.FILL_GRAB);
		notificationJMSQText.setLayoutData(notificationJMSLayoutData);
		notificationJMSQText.addFocusListener(this);
		widgets.add(notificationJMSQText);

		// Tags Queue
		CLabel jmsTagsQueueLabel = super.getWidgetFactory().createCLabel(
				composite, "Tags Queue Name");
		jmsTagsQueueLabel.setLayoutData(new TableWrapData(TableWrapData.RIGHT));
		tagsJMSQText = super.getWidgetFactory().createText(composite, "",
				SWT.None);
		TableWrapData tagsJMSLayoutData = new TableWrapData(
				TableWrapData.FILL_GRAB);
		tagsJMSQText.setLayoutData(tagsJMSLayoutData);
		tagsJMSQText.addFocusListener(this);
		widgets.add(tagsJMSQText);

		section.setClient(composite);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#setInput
	 * (org.eclipse.ui.IWorkbenchPart, org.eclipse.jface.viewers.ISelection)
	 */
	@Override
	public void setInput(IWorkbenchPart part, ISelection selection) {
		Object input = ((IStructuredSelection) selection).getFirstElement();
		RemoteEdgeServer server = (RemoteEdgeServer) input;

		if (server != null && this.server != server) {
			this.server = server;

			SALModelPlugin.getDefault().getPreferenceStore()
					.addPropertyChangeListener(this);

			String ip = SALModelPlugin.getDefault().getPreferenceStore()
					.getString(EdgeServerPreferences.EDGE_SERVER_IP);
			ipText.setText(ip);

			String rmiPort = SALModelPlugin.getDefault().getPreferenceStore()
					.getString(EdgeServerPreferences.EDGE_SERVER_PORT_RMI);
			try {
				rmiPortSpinner.setSelection(Integer.parseInt(rmiPort));
			} catch (NumberFormatException e) {
				rmiPortSpinner.setSelection(0);
			}

			this.notificationJMSQText.setText(SALModelPlugin.getDefault()
					.getPreferenceStore().getString(
							EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE));

			String jmsPort = SALModelPlugin.getDefault().getPreferenceStore()
					.getString(EdgeServerPreferences.EDGE_SERVER_PORT_JMS);
			try {
				jmsPortSpinner.setSelection(Integer.parseInt(jmsPort));
			} catch (NumberFormatException e) {
				rmiPortSpinner.setSelection(0);
			}

			this.tagsJMSQText.setText(SALModelPlugin.getDefault()
					.getPreferenceStore().getString(
							EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS));

			if (server.getState() == RemoteEdgeServerState.DISCONNECTED) {
				for (Control w : widgets) {
					w.setEnabled(true);
				}

			} else {
				for (Control w : widgets) {
					w.setEnabled(false);
				}
			}
			this.server.addPropertyChangeListener(this);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.views.properties.tabbed.AbstractPropertySection#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		SALModelPlugin.getDefault().getPreferenceStore()
				.removePropertyChangeListener(this);
		this.server.removePropertyChangeListener(this);
		for (Control c : widgets) {
			c.dispose();
		}
	}

	private String textOnFocus;

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusGained(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusGained(FocusEvent e) {
		if (e.widget == ipText) {
			textOnFocus = ipText.getText();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.swt.events.FocusListener#focusLost(org.eclipse.swt.events.FocusEvent)
	 */
	@Override
	public void focusLost(FocusEvent e) {

		if (e.widget == ipText) {
			if (!ipText.getText().equals(textOnFocus)) {
				SALModelPlugin.getDefault().getPreferenceStore().setValue(
						EdgeServerPreferences.EDGE_SERVER_IP, ipText.getText());
			}
		} else if (e.widget == this.rmiPortSpinner) {
			if (!rmiPortSpinner.getText().equals(textOnFocus)) {
				SALModelPlugin.getDefault().getPreferenceStore().setValue(
						EdgeServerPreferences.EDGE_SERVER_PORT_RMI,
						rmiPortSpinner.getText());
			}
		} else if (e.widget == this.notificationJMSQText) {
			if (!notificationJMSQText.getText().equals(textOnFocus)) {
				SALModelPlugin.getDefault().getPreferenceStore().setValue(
						EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE,
						notificationJMSQText.getText());
			}
		} else if (e.widget == this.jmsPortSpinner) {
			if (!jmsPortSpinner.getText().equals(textOnFocus)) {
				SALModelPlugin.getDefault().getPreferenceStore().setValue(
						EdgeServerPreferences.EDGE_SERVER_PORT_JMS,
						jmsPortSpinner.getText());
			}
		} else if (e.widget == this.tagsJMSQText) {
			if (!tagsJMSQText.getText().equals(textOnFocus)) {
				SALModelPlugin.getDefault().getPreferenceStore().setValue(
						EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS,
						tagsJMSQText.getText());
			}
		}

		textOnFocus = null;

	}

	/*
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RemoteEdgeServer.STATE_PROPERTY)) {
			RemoteEdgeServerState state = (RemoteEdgeServerState) evt
					.getNewValue();
			if (state == RemoteEdgeServerState.CONNECTED) {
				for (Control w : widgets) {
					w.setEnabled(false);
				}
			} else {
				for (Control w : widgets) {
					w.setEnabled(true);
				}
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(org.eclipse.jface.util.PropertyChangeEvent event) {
		if (event.getProperty().equals(EdgeServerPreferences.EDGE_SERVER_IP)) {
			ipText.setText(event.getNewValue().toString());
		} else if (event.getProperty().equals(
				EdgeServerPreferences.EDGE_SERVER_PORT_RMI)) {
			this.rmiPortSpinner.setSelection(Integer.parseInt(event
					.getNewValue().toString()));
		} else if (event.getProperty().equals(
				EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE)) {
			notificationJMSQText.setText(event.getNewValue().toString());
		} else if (event.getProperty().equals(
				EdgeServerPreferences.EDGE_SERVER_PORT_JMS)) {
			this.jmsPortSpinner.setSelection(Integer.parseInt(event
					.getNewValue().toString()));
		} else if (event.getProperty().equals(
				EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS)) {
			tagsJMSQText.setText(event.getNewValue().toString());
		}

	}

}
