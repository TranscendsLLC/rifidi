package org.rifidi.edge.demo1;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.jms.BytesMessage;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.rifidi.edge.core.services.esper.EsperManagementService;
import org.rifidi.edge.demo1.api.HasArrivedMessage;
import org.rifidi.edge.demo1.api.HasLeftMessage;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.osgi.context.BundleContextAware;

import com.espertech.esper.client.EPServiceProvider;
import com.espertech.esper.client.EPStatement;
import com.espertech.esper.client.EventBean;
import com.espertech.esper.client.StatementAwareUpdateListener;

public class DemoController implements BundleContextAware, BundleListener {

	private static final Log logger = LogFactory.getLog(DemoController.class);
	private volatile EsperManagementService esperManagementService;
	private final Set<EPStatement> statements;

	private volatile JmsTemplate template;
	private volatile Destination destination;

	private volatile Bundle bundle;

	private final StatementAwareUpdateListener outgoing;
	private final StatementAwareUpdateListener incoming;

	public DemoController() {
		statements = new CopyOnWriteArraySet<EPStatement>();
		incoming = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				for (EventBean bean : arg0) {
					if (template != null && destination != null) {
						if (logger.isDebugEnabled())
							logger.debug("sending arrival event "
									+ (String) bean.get("epc") + " "
									+ (Integer) bean.get("gateid"));
						template.send(destination, new DemoMessageCreator(
								new HasArrivedMessage((String) bean.get("epc"),
										(Integer) bean.get("gateid"))));
					}
				}
			}
		};
		outgoing = new StatementAwareUpdateListener() {
			@Override
			public void update(EventBean[] arg0, EventBean[] arg1,
					EPStatement arg2, EPServiceProvider arg3) {
				for (EventBean bean : arg0) {
					if (template != null && destination != null) {
						if (logger.isDebugEnabled())
							logger.debug("sending departure event"
									+ (String) bean.get("epc") + " "
									+ (Integer) bean.get("gateid"));
						template.send(destination, new DemoMessageCreator(
								new HasLeftMessage((String) bean.get("epc"),
										(Integer) bean.get("gateid"))));
					}
				}
			}
		};
	}

	/**
	 * Called by spring
	 * 
	 * @param esperManagementService
	 *            the esperManagementService to set
	 */
	public void setEsperManagementService(
			EsperManagementService esperManagementService) {
		this.esperManagementService = esperManagementService;
		start();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.springframework.osgi.context.BundleContextAware#setBundleContext(
	 * org.osgi.framework.BundleContext)
	 */
	@Override
	public void setBundleContext(BundleContext arg0) {
		this.bundle = arg0.getBundle();
		arg0.addBundleListener(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.osgi.framework.BundleListener#bundleChanged(org.osgi.framework.
	 * BundleEvent)
	 */
	@Override
	public void bundleChanged(BundleEvent arg0) {
		if (arg0.getBundle().equals(bundle)
				&& arg0.getType() == BundleEvent.STOPPED) {
			logger.info("Shutting down demo application.");

		}
	}

	/**
	 * @param template
	 *            the template to set
	 */
	public void setTemplate(JmsTemplate template) {
		this.template = template;
	}

	/**
	 * @param destination
	 *            the destination to set
	 */
	public void setDestination(Destination destination) {
		this.destination = destination;
	}

	public void startGate(Integer gateID) {
		statements.add(esperManagementService.getProvider()
				.getEPAdministrator().createEPL(
						"create window outside_gate_" + gateID
								+ ".win:time(10 sec) as TagReadEvent"));

		statements.add(esperManagementService.getProvider()
				.getEPAdministrator().createEPL(
						"create window inside_gate_" + gateID
								+ ".win:time(10 sec) as TagReadEvent"));

		statements
				.add(esperManagementService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"insert into outside_gate_"
										+ gateID
										+ " select * from ReadCycle[select * from tags where antennaID=0] where readerID='Alien9800_"
										+ gateID + "'"));

		statements
				.add(esperManagementService
						.getProvider()
						.getEPAdministrator()
						.createEPL(
								"insert into inside_gate_"
										+ gateID
										+ " select * from ReadCycle[select * from tags where antennaID=1] where readerID='Alien9800_"
										+ gateID + "'"));

		EPStatement goingInside;
		EPStatement goingOutside;
		goingInside = esperManagementService
				.getProvider()
				.getEPAdministrator()
				.createEPL(
						"select tag1.epc as epc,"
								+ gateID
								+ " as gateid from pattern[every tag1=outside_gate_"
								+ gateID + " -> ([0..]outside_gate_" + gateID
								+ " until inside_gate_" + gateID
								+ "(epc=tag1.epc) and not outside_gate_"
								+ gateID + "(epc=tag1.epc))]");

		goingOutside = esperManagementService
				.getProvider()
				.getEPAdministrator()
				.createEPL(
						"select tag1.epc as epc ,"
								+ gateID
								+ " as gateid from pattern[every tag1=inside_gate_"
								+ gateID + " -> ([0..]inside_gate_" + gateID
								+ " until outside_gate_" + gateID
								+ "(epc=tag1.epc) and not inside_gate_"
								+ gateID + "(epc=tag1.epc))]");
		statements.add(goingInside);
		statements.add(goingOutside);
		goingInside.addListener(incoming);
		goingOutside.addListener(outgoing);
	}

	public void start() {
		startGate(1);
		startGate(2);
	}

	private class DemoMessageCreator implements MessageCreator {

		private Serializable notification;

		/**
		 * @param notification
		 */
		public DemoMessageCreator(Serializable notification) {
			this.notification = notification;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.springframework.jms.core.MessageCreator#createMessage(javax.jms
		 * .Session)
		 */
		@Override
		public Message createMessage(Session arg0) throws JMSException {
			BytesMessage message = arg0.createBytesMessage();
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			try {
				ObjectOutput out = new ObjectOutputStream(bos);
				out.writeObject(notification);
				out.close();
				message.writeBytes(bos.toByteArray());
				return message;
			} catch (IOException e) {
				e.printStackTrace();
				throw new JMSException(e.getMessage());
			}
		}

	}
}
