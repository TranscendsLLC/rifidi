/* 
 *  ReportReceiverSingleton.java
 *  Created:	May 25, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ecspecview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.reports;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.core.databinding.observable.IChangeListener;
import org.eclipse.core.databinding.observable.list.IListChangeListener;
import org.eclipse.core.databinding.observable.list.ListChangeEvent;
import org.eclipse.core.databinding.observable.list.ListDiffEntry;
import org.eclipse.core.databinding.observable.list.ObservableList;
import org.eclipse.core.databinding.observable.list.WritableList;
import org.eclipse.core.runtime.Preferences.IPropertyChangeListener;
import org.eclipse.core.runtime.Preferences.PropertyChangeEvent;
import org.eclipse.swt.widgets.Display;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECReports;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * Non-threadsafe report receiver singleton.
 * This class handles the ECReports that are sent from the server.
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ReportReceiverSingleton implements IPropertyChangeListener,
		IListChangeListener {
	private static ReportReceiverSingleton rrs = null;
	private ObservableList list;
	private static final Log logger = LogFactory
			.getLog(ReportReceiverSingleton.class);
	private JAXBContext context;
	private Unmarshaller umarsh;
	private Thread thread;
	private ReceiveRunner runny;
	// spec name - subscriberobject
	private HashMap<String, IReportSubscriber> subscriberMap = new HashMap<String, IReportSubscriber>();

	/**
	 * The private constructor - singleton.
	 */
	private ReportReceiverSingleton() {
		list = new WritableList();
		try {
			context = JAXBContext.newInstance(ReportAnswer.class,
					ECReports.class);
			umarsh = context.createUnmarshaller();

		} catch (JAXBException e) {
			logger.fatal("Unable to create JAXB marshaller: " + e);
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
		
	}

	/**
	 * Getting the instance - method - not threadsafe!
	 * 
	 * @return Instance of the ReportReceiverSingleton.
	 */
	public static ReportReceiverSingleton getInstance() {
		if (rrs == null) {
			rrs = new ReportReceiverSingleton();

		}
		return rrs;
	}

	public void addSubscriber(String specName, IReportSubscriber subscriber) {
		subscriberMap.put(specName, subscriber);
		if(subscriberMap.size()==1)
			list.addListChangeListener(this);
	}

	public void removeSubscriber(String specName) {
		subscriberMap.remove(specName);
		if(subscriberMap.size()==0)
			list.removeChangeListener((IChangeListener)this);
	}
	
	public IReportSubscriber getSubscriber(String specName){
		return subscriberMap.get(specName);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.databinding.observable.list.IListChangeListener#
	 * handleListChange
	 * (org.eclipse.core.databinding.observable.list.ListChangeEvent)
	 */
	@Override
	public void handleListChange(ListChangeEvent event) {
		for (ListDiffEntry entry : event.diff.getDifferences()) {
			if (entry.isAddition()) {
				ECReports ecReports = null;
				String key = "";
				try {
					ecReports = (ECReports) entry.getElement();
					key = ecReports.getSpecName();
				} catch (ClassCastException e) {
					logger.error(e);
				}

				if (subscriberMap.containsKey(key)) {
					// notify subscriber
					IReportSubscriber subscriber = subscriberMap.get(key);
					subscriber.pushReport(ecReports);
				}
				// TODO: check name viewer.add(model, entry.getElement());
			} else {
				logger.debug("Key not found.");
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.core.runtime.Preferences.IPropertyChangeListener#propertyChange
	 * (org.eclipse.core.runtime.Preferences.PropertyChangeEvent)
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
						ReportAnswer answer = (ReportAnswer) umarsh
								.unmarshal(streamy);
						Display.getDefault().asyncExec(
										new ListUpdater(answer.reports));
					} catch (JAXBException e) {
						logger.warn(e);
					}
					sock.close();
				}
				socket.close();
				socket = null;
			} catch (IOException e) {
				logger.warn(e);
			}
		}

		public void killSocket() {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					logger.warn(e);
				}
			}
		}

	}
}
