/**
 * 
 */
package org.rifidi.edge.client.ale.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReportReceiverViewPart extends ViewPart implements
		IPropertyChangeListener {
	public static final String ID="org.rifidi.edge.client.ale.reports.ReportReceiverView";
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ReportReceiverViewPart.class);
	private TreeViewer viewer;
	private JAXBContext cont;
	private Unmarshaller unmarsh;
	private ObservableList list;
	private Thread thread;
	private ReceiveRunner runny;

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

	public void clear() {
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
		parent.setLayout(new FillLayout());
		viewer = new TreeViewer(parent, SWT.None);

		try {
			String adr[] = Activator.getDefault().getPreferenceStore()
					.getString(Activator.REPORT_RECEIVER_ADR).split(":");
			runny = new ReceiveRunner(new InetSocketAddress(adr[0], Integer
					.parseInt(adr[1])));
			thread = new Thread(runny);
			thread.start();
		} catch (Exception e) {
			logger.warn(e);
		}

		viewer.setContentProvider(new ReportTreeContentProvider());
		viewer.setLabelProvider(new ReportLabelProvider());
		viewer.setInput(list);
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(
				this);
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#dispose()
	 */
	@Override
	public void dispose() {
		super.dispose();
		Activator.getDefault().getPreferenceStore()
				.removePropertyChangeListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.util.IPropertyChangeListener#propertyChange(org.eclipse
	 * .jface.util.PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Activator.REPORT_RECEIVER_ADR)) {
			if (thread != null && thread.isAlive()) {
				runny.killSocket();
			}
			try {
				String adr[] = Activator.getDefault().getPreferenceStore()
						.getString(Activator.REPORT_RECEIVER_ADR).split(":");
				runny = new ReceiveRunner(new InetSocketAddress(adr[0], Integer
						.parseInt(adr[1])));
				thread = new Thread(runny);
				thread.start();
			} catch (Exception e) {
				logger.warn(e);
			}
			return;
		}

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

	private class ReceiveRunner implements Runnable {
		private InetSocketAddress addr;
		private ServerSocket socket;

		public ReceiveRunner(InetSocketAddress addr) {
			super();
			this.addr = addr;
		}

		public void run() {
			try {

				socket = new ServerSocket();
				socket.bind(addr);
				while (!socket.isClosed()
						&& !Thread.currentThread().isInterrupted()) {
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
				socket.close();
				socket=null;
			} catch (IOException e) {
				logger.warn(e);
			}
		}

		public void killSocket() {
			if(socket!=null){
				try {
					socket.close();
				} catch (IOException e) {
					logger.warn(e);
				}	
			}
		}

	}
}
