/**
 * 
 */
package org.rifidi.edge.core.sensors.base;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.LinkedBlockingQueue;

import javax.management.AttributeList;

import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.api.rmi.dto.SessionDTO;
import org.rifidi.edge.core.sensors.LogicalSensor;
import org.rifidi.edge.core.sensors.PhysicalSensor;
import org.rifidi.edge.core.sensors.SensorSession;
import org.rifidi.edge.core.sensors.exceptions.DuplicateSubscriptionException;
import org.rifidi.edge.core.sensors.exceptions.NotSubscribedException;
import org.rifidi.edge.core.sensors.impl.LogicalSensorImpl;
import org.rifidi.edge.core.services.notification.data.TagReadEvent;

/**
 * A reader creates and manages instances of sessions. The reader itself holds
 * all configuration parameters and creates the sessions according to these. The
 * returned sensorSession objects are immutable and if some parameters of the
 * factory change after a session has been created, the created session will
 * retain its configuration until it is destroyed and a new one is created
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractSensor<T extends SensorSession> extends
		RifidiService implements PhysicalSensor {
	/** Sensors connected to this connectedSensors. */
	protected Set<LogicalSensorImpl> connectedSensors;
	/**
	 * Receivers are objects that need to gather tag reads. The tag reads are
	 * stored in a queue.
	 */
	protected Map<Object, LinkedBlockingQueue<Set<TagReadEvent>>> receiversToQueues;

	/**
	 * Create a new reader session. If there are no more sessions available null
	 * is returned.
	 * 
	 * @return
	 */
	abstract public SensorSession createReaderSession();

	/**
	 * Get all currently created reader sessions. The Key is the ID of the
	 * session, and the value is the actual session
	 * 
	 * @return
	 */
	abstract public Map<String, SensorSession> getReaderSessions();

	/**
	 * Destroy a reader session.
	 * 
	 * @param session
	 */
	abstract public void destroyReaderSession(SensorSession session);

	/**
	 * Send properties that have been modified to the physical reader
	 */
	abstract public void applyPropertyChanges();

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#receive(java.lang.Object)
	 */
	@Override
	public Set<TagReadEvent> receive(Object receiver) {
		Set<TagReadEvent> ret = new HashSet<TagReadEvent>();
		Set<Set<TagReadEvent>> process = new HashSet<Set<TagReadEvent>>();
		receiversToQueues.get(receiver).drainTo(process);
		for (Set<TagReadEvent> tags : receiversToQueues.get(receiver)) {
			ret.addAll(tags);
		}
		return ret;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#subscribe(java.lang.Object)
	 */
	@Override
	public synchronized void subscribe(Object receiver)
			throws DuplicateSubscriptionException {
		if (!receiversToQueues.containsKey(receiver)) {
			receiversToQueues.put(receiver,
					new LinkedBlockingQueue<Set<TagReadEvent>>());
			return;
		}
		throw new DuplicateSubscriptionException(receiver
				+ " is already subscribed to " + getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#unsubscribe(java.lang.Object)
	 */
	@Override
	public synchronized void unsubscribe(Object receiver)
			throws NotSubscribedException {
		if (receiversToQueues.containsKey(receiver)) {
			receiversToQueues.remove(receiver);
		}
		throw new NotSubscribedException(receiver + " is not subscribed to "
				+ getID());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#connectSensor(org.rifidi.
	 * edge.core.sensors.LogicalSensorImpl)
	 */
	@Override
	public void connectSensor(LogicalSensorImpl sensor) {
		connectedSensors.add(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.sensors.PhysicalSensor#disconnectSensor(org.rifidi
	 * .edge.core.sensors.PhysicalSensor)
	 */
	@Override
	public void disconnectSensor(PhysicalSensor sensor) {
		connectedSensors.remove(sensor);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.core.sensors.PhysicalSensor#getConnectedSensors()
	 */
	@Override
	public Set<LogicalSensorImpl> getConnectedSensors() {
		Set<LogicalSensorImpl> ret = new HashSet<LogicalSensorImpl>();
		ret.addAll(connectedSensors);
		return ret;
	}

	/**
	 * 
	 * @param tagReads
	 */
	protected void send(Set<TagReadEvent> tagReads) {
		for (LinkedBlockingQueue<Set<TagReadEvent>> queue : receiversToQueues
				.values()) {
			queue.add(tagReads);
		}
		for (LogicalSensor sensor : connectedSensors) {
			sensor.send(tagReads);
		}
	}
	
	/***
	 * This method returns the Data Transfer Object for this Reader
	 * 
	 * @param config
	 *            The Configuration Object for this AbstractSensor
	 * @return A data transfer object for this reader
	 */
	public ReaderDTO getDTO(Configuration config) {
		String readerID = config.getServiceID();
		String factoryID = config.getFactoryID();
		AttributeList attrs = config.getAttributes(config.getAttributeNames());
		List<SessionDTO> sessionDTOs = new ArrayList<SessionDTO>();
		for (SensorSession s : this.getReaderSessions().values()) {
			sessionDTOs.add(s.getDTO());
		}
		ReaderDTO dto = new ReaderDTO(readerID, factoryID, attrs, sessionDTOs);
		return dto;
	}
}
