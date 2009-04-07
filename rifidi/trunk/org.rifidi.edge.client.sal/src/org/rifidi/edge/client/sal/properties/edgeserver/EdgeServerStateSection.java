/**
 * 
 */
package org.rifidi.edge.client.sal.properties.edgeserver;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.views.properties.tabbed.AbstractPropertySection;
import org.eclipse.ui.views.properties.tabbed.TabbedPropertySheetPage;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;

/**
 * @author kyle
 * 
 */
public class EdgeServerStateSection extends AbstractPropertySection implements
		PropertyChangeListener {

	private Text stateText = null;
	private RemoteEdgeServer server;

	/**
	 * 
	 */
	public EdgeServerStateSection() {
	}

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
		CLabel label = super.getWidgetFactory().createCLabel(composite,
				"Edge Server State:");
		label.setLayoutData(new GridData());
		stateText = super.getWidgetFactory().createText(composite, "");
		GridData gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.widthHint = 150;
		stateText.setLayoutData(gridData);
		stateText.setEditable(false);
		stateText.setEnabled(false);
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
		super.setInput(part, selection);
		Object input = ((IStructuredSelection) selection).getFirstElement();
		RemoteEdgeServer server = (RemoteEdgeServer) input;
		stateText.setText(server.getState().toString());
		if (server != null) {
			if (this.server != null && this.server != server) {
				server.removePropertyChangeListener(this);
			}
			this.server = server;
			this.server.addPropertyChangeListener(this);
		}
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RemoteEdgeServer.STATE_PROPERTY)) {
			stateText.setText(this.server.getState().toString());
		}

	}

}
