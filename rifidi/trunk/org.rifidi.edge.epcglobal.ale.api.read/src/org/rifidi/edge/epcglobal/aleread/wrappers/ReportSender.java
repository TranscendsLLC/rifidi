/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.wrappers;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.services.notification.data.DatacontainerEvent;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReport;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECReports;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.ALEReadAPI;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiECSpec.ResultContainer;

/**
 * Very simple report sender. Takes in the reports, serialiyes them to XML and
 * pumps em out to the receivers.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ReportSender implements Runnable {
	/** Logger for this class. */
	private static final Log logger = LogFactory.getLog(ReportSender.class);
	/** Queue that receives the results */
	private LinkedBlockingQueue<ResultContainer> resultQueue;
	/** Threadsafe list for subscription uris. */
	private List<String> subscriptionURIs;
	/** JAXB for serializing the results. */
	private JAXBContext cont;
	private Marshaller marsh;
	/** Reports that need to be processed. */
	private List<RifidiReport> rifidiReports;
	/** Spec the reports get generated for. */
	private ECSpec spec;
	/** Name of the spec. */
	private String specName;

	/**
	 * Constructor.
	 */
	public ReportSender(Collection<RifidiReport> reports, String specName, ECSpec spec) {
		rifidiReports = new ArrayList<RifidiReport>();
		rifidiReports.addAll(reports);
		subscriptionURIs = new CopyOnWriteArrayList<String>();
		resultQueue = new LinkedBlockingQueue<ResultContainer>();
		this.specName = specName;
		this.spec = spec;
		try {
			cont = JAXBContext.newInstance(ReportAnswer.class, ECReports.class);
			marsh = cont.createMarshaller();
		} catch (JAXBException e) {
			logger.fatal("Unabel to create JAXB marshaller: " + e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Adds a new result to the internal queue.
	 * 
	 * @param container
	 */
	public void enqueueResultContainer(ResultContainer container) {
		try {
			resultQueue.put(container);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while (!Thread.currentThread().isInterrupted()) {
			try {
				ResultContainer container = resultQueue.take();
				ECReports reports = new ECReports();
				reports.setReports(new ECReports.Reports());
				reports.setInitiationCondition(ALEReadAPI.conditionToName
						.get(ALEReadAPI.TriggerCondition.REPEAT_PERIOD));
				GregorianCalendar gc = (GregorianCalendar) GregorianCalendar
						.getInstance();
				DatatypeFactory dataTypeFactory = null;
				try {
					dataTypeFactory = DatatypeFactory.newInstance();
				} catch (DatatypeConfigurationException ex) {
					logger.fatal("epic fail: " + ex);
				}
				reports.setALEID("Rifidi Edge Server");
				reports.setDate(dataTypeFactory.newXMLGregorianCalendar(gc));
				Set<DatacontainerEvent> events = new HashSet<DatacontainerEvent>();
				for (TagReadEvent tagRead : container.events) {
					events.add(tagRead.getTag());
				}
				// process the set of events
				for (RifidiReport rifidiReport : rifidiReports) {
					rifidiReport.processEvents(events);
					ECReport rep=rifidiReport.send();
					if(rep!=null){
						reports.getReports().getReport().add(rep);	
					}
				}
				
				reports.setInitiationCondition(ALEReadAPI.conditionToName
						.get(container.startReason));
				// TODO: dummy code, replace with correct typing
				if (container.startCause != null) {
					reports.setInitiationTrigger(ALEReadAPI.conditionToName
							.get(container.startReason));
				}
				reports.setTerminationCondition(ALEReadAPI.conditionToName
						.get(container.stopReason));
				// TODO: dummy code, replace with correct typing
				if (container.startCause != null) {
					reports.setTerminationTrigger(ALEReadAPI.conditionToName
							.get(container.startReason));
				}
				if (spec.isIncludeSpecInReports()) {
					reports.setECSpec(spec);
				}
				reports.setSpecName(specName);
				reports.setTotalMilliseconds(container.stopTime
						- container.startTime);
				// send it
				for (String uri : subscriptionURIs) {
					logger.debug("Sending report to " + uri);
					String[] str = uri.toString().split(":");
					try {
						Socket socket = new Socket(str[0], Integer
								.parseInt(str[1]));
						PrintWriter out = new PrintWriter(socket
								.getOutputStream(), true);

						try {
							ReportAnswer answer = new ReportAnswer();
							answer.reports = reports;
							marsh.marshal(answer, out);
						} catch (JAXBException e) {
							logger.fatal("Problem serializing to xml: "+e);
						}
						out.flush();
						out.close();
						socket.close();
					} catch (NumberFormatException e) {
						logger.warn(e);
					} catch (UnknownHostException e) {
						logger.warn(e);
					} catch (IOException e) {
						logger.warn(e);
					}
				}

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
	}

	/**
	 * Subscribe the given URI to this spec.
	 * 
	 * @param uri
	 * @throws DuplicateSubscriptionExceptionResponse
	 */
	public void subscribe(String uri)
			throws DuplicateSubscriptionExceptionResponse {
		subscriptionURIs.add(uri);
	}

	/**
	 * Unsubscribe the given URI from this spec.
	 * 
	 * @param uri
	 * @throws NoSuchSubscriberExceptionResponse
	 */
	public void unsubscribe(String uri)
			throws NoSuchSubscriberExceptionResponse {
		subscriptionURIs.remove(uri);
	}
}
