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
 * Non-threadsafe report receiver singleton. This class handles the ECReports
 * that are sent from the server. Replaces ReportRecieverViewPart.
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
		/** initializing list of reports */
		list = new WritableList();
		/** creating JAXB context and unmarshaller */
		try {
			context = JAXBContext.newInstance(ReportAnswer.class,
					ECReports.class);
			umarsh = context.createUnmarshaller();

		} catch (JAXBException e) {
			logger.fatal("Unable to create JAXB marshaller: " + e);
		}
		/** getting report destination from the preferences */
		try {
			String adr[] = Activator.getDefault().getPreferenceStore()
					.getString(Activator.REPORT_RECEIVER_ADR).split(":");
			/** initializing runnable with the data gained from preference store */
			runny = new ReceiveRunner(new InetSocketAddress(adr[0], Integer
					.parseInt(adr[1])));
			/** handing our thread the runnable to execute */
			thread = new Thread(runny);
			/** starting the thread */
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
			logger.trace("creating new ReportReceiverSingleton...");
			rrs = new ReportReceiverSingleton();

		}
		return rrs;
	}

	/**
	 * Adding a report subscriber to this singleton.
	 * 
	 * @param specName
	 *            - name of the spec to subscribe to for reports
	 * @param subscriber
	 *            - the subscriber object
	 */
	public void addSubscriber(String specName, IReportSubscriber subscriber) {
		logger.trace("adding Subscriber " + specName + " to ReportReceivers");
		subscriberMap.put(specName, subscriber);
		if (subscriberMap.size() == 1)
			list.addListChangeListener(this);
	}

	/**
	 * Remove the report subscriber for the respective spec.
	 * 
	 * @param specName
	 *            - name of the spec to remove the subscriber from
	 */
	public void removeSubscriber(String specName) {
		logger.trace("removing Subscriber " + specName
				+ " from ReportReceivers");
		subscriberMap.remove(specName);
		if (subscriberMap.size() == 0)
			list.removeChangeListener((IChangeListener) this);
	}

	/**
	 * Get the subscriber object for the respective ECSpec.
	 * 
	 * @param specName
	 * @return subscriber object
	 */
	public IReportSubscriber getSubscriber(String specName) {
		logger.trace("Retrieving Subscriber for " + specName);
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
		logger.trace("handling listChange...");
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
				/** If there is a subscriber to the reports of this spec... */
				if (subscriberMap.containsKey(key)) {
					/** ... let's get that subscriber ... */
					IReportSubscriber subscriber = subscriberMap.get(key);
					/** ... and send him the reports. */
					subscriber.pushReport(ecReports);
				} else {
					logger.trace("Key not found.");
				}
				// TODO: check name viewer.add(model, entry.getElement());
			} else {
				logger.trace("entry is not an addition...");
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
		/**
		 * if report receiver destination is changed stop and start with new
		 * address
		 */
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

	/**
	 * This Runnable updates the list and is being injected into the eclipse
	 * thread.
	 * 
	 */
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
			logger.trace("Listupdater adding reports...");
			list.add(reports);
		}

	}

	/**
	 * This runnable opens the socket for the reports to come in from the
	 * server. It also injects the ListUpdater runnable into the eclipse thread
	 * when there is something to update.
	 * 
	 */
	private class ReceiveRunner implements Runnable {
		private InetSocketAddress addr;
		private ServerSocket socket;

		public ReceiveRunner(InetSocketAddress addr) {
			super();
			this.addr = addr;
		}

		public void run() {
			try {
				logger.trace("new serversocket...");
				socket = new ServerSocket();
				System.out.println("socket.bind...");
				socket.bind(addr);
				logger.trace("while socket !closed and thread !interrupted");
				while (!socket.isClosed()
						&& !Thread.currentThread().isInterrupted()) {
					logger.trace("socket accept...");
					Socket sock = socket.accept();
					logger
							.trace("new buffered reader : sock.getinputstream...");
					BufferedReader streamy = new BufferedReader(
							new InputStreamReader(sock.getInputStream()));
					try {
						logger.trace("unmarshal answer...");
						ReportAnswer answer = (ReportAnswer) umarsh
								.unmarshal(streamy);
						logger.trace("starting listupdater runnable");
						Display.getDefault().asyncExec(
								new ListUpdater(answer.reports));
					} catch (JAXBException e) {
						logger.warn(e);
					}
					logger.trace("closing socket...");
					sock.close();
				}
				logger.trace("closing serversocket...");
				socket.close();
				socket = null;
			} catch (IOException e) {
				logger.warn(e);
			}
		}

		/**
		 * Closes the socket.
		 */
		public void killSocket() {
			logger.trace("killsocket called...");
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
