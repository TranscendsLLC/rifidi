/**
 * 
 */
package org.rifidi.edge.client.sal.properties.edgeserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.osgi.service.prefs.Preferences;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteEdgeServerState;
import org.rifidi.edge.client.model.sal.preferences.EdgeServerPreferences;

/**
 * @author kyle
 * 
 */
public class EdgeServerConnectionInfo extends AbstractPropertySection implements
		FocusListener, PropertyChangeListener {

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
		Composite composite = super.getWidgetFactory().createFlatFormComposite(
				parent);
		composite.setLayout(new GridLayout(2, false));

		// IP
		CLabel iplabel = super.getWidgetFactory().createCLabel(composite,
				"IP Address");
		iplabel.setLayoutData(new GridData());
		ipText = super.getWidgetFactory().createText(composite, "");
		GridData ipgridData = new GridData();
		ipgridData.horizontalAlignment = GridData.FILL;
		ipgridData.grabExcessHorizontalSpace = true;
		ipgridData.widthHint = 150;
		ipText.setLayoutData(ipgridData);
		ipText.addFocusListener(this);
		widgets.add(ipText);

		// RMI Port
		CLabel rmiPortLabel = super.getWidgetFactory().createCLabel(composite,
				"RMI Port");
		rmiPortLabel.setLayoutData(new GridData());
		rmiPortSpinner = new Spinner(composite, SWT.BORDER);
		GridData rmiPortGridData = new GridData();
		rmiPortGridData.horizontalAlignment = GridData.FILL;
		rmiPortGridData.grabExcessHorizontalSpace = true;
		rmiPortGridData.widthHint = 150;
		rmiPortSpinner.setLayoutData(rmiPortGridData);
		rmiPortSpinner.addFocusListener(this);
		rmiPortSpinner.setMinimum(0);
		rmiPortSpinner.setMaximum(Integer.MAX_VALUE);
		widgets.add(rmiPortSpinner);
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

		if (server != null) {
			if (this.server != null && this.server != server) {
				server.removePropertyChangeListener(this);
			}
			this.server = server;
			this.server.addPropertyChangeListener(this);
		}

		Preferences node = new DefaultScope()
				.getNode("org.rifidi.edge.client.model");

		String ip = node.get(EdgeServerPreferences.EDGE_SERVER_IP,
				EdgeServerPreferences.EDGE_SERVER_IP_DEFAULT);
		ipText.setText(ip);

		String rmiPort = node.get(EdgeServerPreferences.EDGE_SERVER_PORT_RMI,
				EdgeServerPreferences.EDGE_SERVER_PORT_RMI_DEFAULT);
		try {
			rmiPortSpinner.setSelection(Integer.parseInt(rmiPort));
		} catch (NumberFormatException e) {
			rmiPortSpinner.setSelection(0);
		}

		if (server.getState() == RemoteEdgeServerState.DISCONNECTED) {
			for (Control w : widgets) {
				w.setEnabled(true);
			}

		} else {
			for (Control w : widgets) {
				w.setEnabled(false);
			}
		}
	}

	private String textOnFocus;

	@Override
	public void focusGained(FocusEvent e) {
		if (e.widget == ipText) {
			textOnFocus = ipText.getText();
		}

	}

	@Override
	public void focusLost(FocusEvent e) {
		Preferences node = new DefaultScope()
				.getNode("org.rifidi.edge.client.model");

		if (e.widget == ipText) {
			if (!ipText.getText().equals(textOnFocus)) {
				node
						.put(EdgeServerPreferences.EDGE_SERVER_IP, ipText
								.getText());
			}
		} else if (e.widget == this.rmiPortSpinner) {
			if (!rmiPortSpinner.getText().equals(textOnFocus)) {
				node.put(EdgeServerPreferences.EDGE_SERVER_PORT_RMI,
						rmiPortSpinner.getText());
			}
		}

		textOnFocus = null;

	}

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

}
