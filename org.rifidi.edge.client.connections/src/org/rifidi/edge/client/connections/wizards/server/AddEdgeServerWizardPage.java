package org.rifidi.edge.client.connections.wizards.server;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;

public class AddEdgeServerWizardPage extends WizardPage {
	static private Log logger = LogFactory.getLog(AddEdgeServerWizardPage.class);
	
	private Text serverURLText;
	private Text rmiPortNumberText;
	private int rmiPort = 1099;
	private String serverURL = "localhost";
	
	//TODO separate this to its own plugin.
	private int jmsPort = 61616;
	private Text jmsPortNumberText;

	protected AddEdgeServerWizardPage() {
		super("Edge Server Connection Settings");
		setTitle("Edge Server Connection Settings");
		setDescription("Please fill out the following fields to etablish a connection \n"
				+ "to the Edge Server.");
		logger.debug("Created wizard page.");
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NULL);

		GridLayout layout = new GridLayout();
		composite.setLayout(layout);
		layout.numColumns = 2;

		GridData gridData = new GridData(GridData.FILL_HORIZONTAL);

		Label IPlabel = new Label(composite, SWT.CENTER);
		IPlabel.setText("Enter server address");

		serverURLText = new Text(composite, SWT.FILL | SWT.BORDER);
		serverURLText.setLayoutData(gridData);
		serverURLText.setTextLimit(512);
		serverURLText.setText(serverURL);
		serverURLText.addListener(SWT.Verify, new Listener() {

			public void handleEvent(Event event) {
//				String text = event.text;
//				char[] chars = new char[text.length()];
//				text.getChars(0, chars.length, chars, 0);
//				for (int i = 0; i < chars.length; i++) {
//					if (('0' <= chars[i] && chars[i] <= '9')
//							|| ('.' == chars[i])) {
//						event.doit = true;
//						return;
//					}
//					event.doit = false;
//				}
				event.doit = true;
			}

		});
		serverURLText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateInput();
			}

		});


		Label portNumberLabel = new Label(composite, SWT.CENTER);
		portNumberLabel.setText("Enter server port number");

		rmiPortNumberText = new Text(composite, SWT.FILL | SWT.BORDER);
		rmiPortNumberText.setLayoutData(gridData);
		rmiPortNumberText.setTextLimit(5);
		rmiPortNumberText.setText("" + rmiPort);
		rmiPortNumberText.addListener(SWT.Verify, new Listener() {

			public void handleEvent(Event event) {
				String text = event.text;
				char[] chars = new char[text.length()];
				text.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if ('0' <= chars[i] && chars[i] <= '9') {
						event.doit = true;
						return;
					}
					event.doit = false;
				}

			}

		});
		rmiPortNumberText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateInput();
			}

		});


		Label jmsPortNumberLabel = new Label(composite, SWT.CENTER);
		jmsPortNumberLabel.setText("Enter JMS port number");

		jmsPortNumberText = new Text(composite, SWT.FILL | SWT.BORDER);
		jmsPortNumberText.setLayoutData(gridData);
		jmsPortNumberText.setTextLimit(5);
		jmsPortNumberText.setText("" + jmsPort);
		jmsPortNumberText.addListener(SWT.Verify, new Listener() {

			public void handleEvent(Event event) {
				String text = event.text;
				char[] chars = new char[text.length()];
				text.getChars(0, chars.length, chars, 0);
				for (int i = 0; i < chars.length; i++) {
					if ('0' <= chars[i] && chars[i] <= '9') {
						event.doit = true;
						return;
					}
					event.doit = false;
				}

			}

		});
		jmsPortNumberText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				updateInput();
			}

		});
		
		
		setPageComplete(true);
		setControl(composite);
	}
	/**
	 * This method is invoked every time information entered in the wizard is changed
	 */
	protected void updateInput() {

		String rmiPortString = rmiPortNumberText.getText();
		String jmsPortString = jmsPortNumberText.getText();
		String serverURLString = serverURLText.getText();
		try {
			if (serverURLString != null && rmiPortString != null && jmsPortString != null) {
				rmiPort = Integer.parseInt(rmiPortString);
				jmsPort = Integer.parseInt(jmsPortString);
				if ((rmiPort >= 0 && rmiPort <= 65535) && (jmsPort >= 0 && jmsPort <= 65535 )) {
					// TODO verify url
					serverURL = serverURLString;
					//logger.debug("Page complete");
					setPageComplete(true);
				} else {
					//logger.debug("Page not complete");
					setPageComplete(false);
				}
			} else {
				//logger.debug("Page not complete");
				setPageComplete(false);
			}
		} catch (NumberFormatException e){
			//logger.debug("Page not complete");
			setPageComplete(false);
		}
	}

	public int getPort() {
		return rmiPort;
	}

	public String getURL() {
		return serverURL;
	}

	/**
	 * @return the jmsPort
	 */
	public int getJmsPort() {
		return jmsPort;
	}
}
