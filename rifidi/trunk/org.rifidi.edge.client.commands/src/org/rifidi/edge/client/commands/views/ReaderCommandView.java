package org.rifidi.edge.client.commands.views;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CTabFolder;
import org.eclipse.swt.custom.CTabItem;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.ViewPart;
import org.jdom.Element;
import org.rifidi.dynamicswtforms.ui.widgets.standard.StandardDynamicSWTForm;
import org.rifidi.edge.client.connections.remotereader.RemoteReader;
import org.rifidi.edge.client.connections.views.EdgeServerConnectionView;
import org.rifidi.edge.core.api.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;

public class ReaderCommandView extends ViewPart {

	private Log logger = LogFactory.getLog(ReaderCommandView.class);

	private RemoteReader remoteReader;

	private Composite parent;

	private CTabFolder currentComposite = null;

	public ReaderCommandView() {

	}

	@Override
	public void createPartControl(Composite parent) {
		this.parent = parent;
		getSite().getPage().addSelectionListener(EdgeServerConnectionView.ID,
				new RemoteReaderSelectionListener());
		ISelection selection = getSite().getPage().getSelection();
		if (selection instanceof TreeSelection) {
			Object o = ((TreeSelection) selection).getFirstElement();
			if (o instanceof RemoteReader) {
				RemoteReader remoteReader = (RemoteReader) o;
				update(remoteReader);
			}
		}
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void update(RemoteReader remoteReader) {
		this.remoteReader = remoteReader;
		// logger.debug("update");
		if (this.currentComposite != null) {
			currentComposite.dispose();
		}

		CTabFolder newFolder = new CTabFolder(parent, SWT.NONE);

		for (String commandName : remoteReader.getCommandNames()) {
			Element descriptor = remoteReader.getCommandAnnotation(commandName);
			logger.debug("drawing " + commandName);
			if (descriptor != null) {
				logger.debug("adding pane");
				CTabItem item = new CTabItem(newFolder, SWT.NULL);

				Composite tabComposite = new Composite(newFolder, SWT.NONE);
				tabComposite.setLayout(new GridLayout(1, false));

				Composite formComposite = new Composite(tabComposite, SWT.NONE);
				GridLayout formCompositeLayout = new GridLayout(1, false);
				formCompositeLayout.marginHeight = 0;
				formCompositeLayout.verticalSpacing = 0;
				formComposite.setLayout(formCompositeLayout);
				StandardDynamicSWTForm form = new StandardDynamicSWTForm(
						descriptor, false);
				form.createControls(formComposite);

				Composite buttonComposite = new Composite(tabComposite,
						SWT.NONE);
				buttonComposite.setLayout(new GridLayout(2, false));
				Button start = new Button(buttonComposite, SWT.PUSH);
				start.setText("Start");
				start.addSelectionListener(new StartCommandSelectionListener(
						form, commandName));
				if (!remoteReader.canExecuteCommand()) {
					start.setEnabled(false);
				}
				Button stop = new Button(buttonComposite, SWT.PUSH);
				stop
						.addSelectionListener(new StopCommandSelectionListener(
								form));
				stop.setText("Stop");
				if (!remoteReader.currentlyExecutingCommand().equalsIgnoreCase(
						commandName)) {
					stop.setEnabled(false);
				}

				item.setControl(tabComposite);
				item.setText(commandName);
			}
		}

		this.currentComposite = newFolder;
		this.currentComposite.setSelection(0);
		this.currentComposite.setSimple(false);
		parent.layout();

	}

	private class StartCommandSelectionListener implements SelectionListener {

		private StandardDynamicSWTForm form;
		private String commandName;

		public StartCommandSelectionListener(StandardDynamicSWTForm form,
				String commandName) {
			this.form = form;
			this.commandName = commandName;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			logger.debug("Starting Command: " + form.getXMLAsString(false));
			HashMap<String, String> propertyValue = form
					.getWidgetNameValueMap();
			Set<CommandArgument> args = new HashSet<CommandArgument>();
			for (String argName : propertyValue.keySet()) {
				args.add(new CommandArgument(argName, propertyValue
						.get(argName), false));
			}
			CommandConfiguration cc = new CommandConfiguration(commandName,
					args);
			remoteReader.executeCommand(cc);
			update(remoteReader);
		}

	}

	private class StopCommandSelectionListener implements SelectionListener {

		private StandardDynamicSWTForm form;

		public StopCommandSelectionListener(StandardDynamicSWTForm form) {
			this.form = form;
		}

		@Override
		public void widgetDefaultSelected(SelectionEvent e) {

		}

		@Override
		public void widgetSelected(SelectionEvent e) {
			logger.debug("Stopping Command: " + form.getXMLAsString(false));
			remoteReader.stopCommand();
			update(remoteReader);

		}

	}

	private class RemoteReaderSelectionListener implements ISelectionListener {

		public RemoteReaderSelectionListener() {
			logger.debug("RemoteReaderSelectionListener created");
		}

		@Override
		public void selectionChanged(IWorkbenchPart part, ISelection selection) {
			// logger.debug("Selection " + selection.getClass().getName());
			if (selection instanceof TreeSelection) {
				Object o = ((TreeSelection) selection).getFirstElement();
				if (o instanceof RemoteReader) {
					RemoteReader remoteReader = (RemoteReader) o;
					update(remoteReader);
				} else {
					// cleanup();
				}
			}

		}
	}

}
