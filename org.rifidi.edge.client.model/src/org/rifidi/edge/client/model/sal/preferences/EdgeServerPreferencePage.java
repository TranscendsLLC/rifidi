package org.rifidi.edge.client.model.sal.preferences;

import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.rifidi.edge.client.model.SALModelPlugin;

/**
 * The contributed preference page for the RemoteEdgeServer
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class EdgeServerPreferencePage extends FieldEditorPreferencePage
		implements IWorkbenchPreferencePage {

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.preference.FieldEditorPreferencePage#createFieldEditors
	 * ()
	 */
	@Override
	protected void createFieldEditors() {

		addField(new StringFieldEditor(EdgeServerPreferences.EDGE_SERVER_IP,
				"Edge Server IP", getFieldEditorParent()));
		addField(new IntegerFieldEditor(
				EdgeServerPreferences.EDGE_SERVER_PORT_RMI,
				"Edge Server RMI Port", getFieldEditorParent()));
		addField(new IntegerFieldEditor(
				EdgeServerPreferences.EDGE_SERVER_PORT_JMS,
				"Edge Server JMS Port", getFieldEditorParent()));
		addField(new StringFieldEditor(
				EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE,
				"Edge Server JMS Notifications Queue Name",
				getFieldEditorParent()));
		addField(new StringFieldEditor(
				EdgeServerPreferences.EDGE_SERVER_JMS_QUEUE_TAGS,
				"Edge Server JMS Tags Queue Name", getFieldEditorParent()));
		addField(new StringFieldEditor(
				EdgeServerPreferences.EDGE_SERVER_RMI_USERNAME, "Username",
				getFieldEditorParent()));
		Composite composite = getFieldEditorParent();
		StringFieldEditor passwordeditor = new StringFieldEditor(
				EdgeServerPreferences.EDGE_SERVER_RMI_PASSWORD, "Password",
				composite);
		passwordeditor.getTextControl(composite).setEchoChar('*');
		addField(passwordeditor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	@Override
	public void init(IWorkbench workbench) {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.preference.PreferencePage#doGetPreferenceStore()
	 */
	@Override
	protected IPreferenceStore doGetPreferenceStore() {
		return SALModelPlugin.getDefault().getPreferenceStore();
	}

}
