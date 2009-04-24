/**
 * 
 */
package org.rifidi.edge.client.alelr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReportReceiverViewPart extends ViewPart {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ReportReceiverViewPart.class);
	private TreeViewer viewer;
	private JAXBContext cont;
	private Unmarshaller unmarsh;
	private ObservableList list;

	/**
	 * 
	 */
	public ReportReceiverViewPart() {
		list = new WritableList();
		try {
			cont = JAXBContext.newInstance(ReportAnswer.class, ECReports.class);
			unmarsh = cont.createUnmarshaller();
		} catch (JAXBException e) {
			logger.fatal("Unabel to create JAXB marshaller: " + e);
		}
	}

	public void clear(){
		list.clear();
	}
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
//		IToolBarManager tbm = getViewSite().getActionBars().getToolBarManager();
		parent.setLayout(new FillLayout());
		viewer = new TreeViewer(parent, SWT.None);
		Thread thread = new Thread() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see java.lang.Thread#run()
			 */
			@Override
			public void run() {
				try {
					ServerSocket socket = new ServerSocket(10000);
					while (!isInterrupted()) {
						Socket sock = socket.accept();
						BufferedReader streamy = new BufferedReader(
								new InputStreamReader(sock.getInputStream()));
						try {
							ReportAnswer answer = (ReportAnswer) unmarsh
									.unmarshal(streamy);
							getViewSite().getShell().getDisplay().asyncExec(
									new ListUpdater(answer.reports));
						} catch (JAXBException e) {
							logger.warn(e);
						}
						sock.close();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				super.run();
			}

		};
		thread.start();
		viewer.setContentProvider(new ReportTreeContentProvider());
		viewer.setLabelProvider(new ReportLabelProvider());
		viewer.setInput(list);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private class ListUpdater implements Runnable {
		private ECReports reports;

		public ListUpdater(ECReports reports) {
			super();
			this.reports = reports;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		@Override
		public void run() {
			list.add(reports);
		}

	}
}
