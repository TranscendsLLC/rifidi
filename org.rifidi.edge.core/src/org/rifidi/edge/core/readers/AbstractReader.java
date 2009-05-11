/**
 * 
 */
package org.rifidi.edge.core.readers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.management.AttributeList;

import org.rifidi.configuration.Configuration;
import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;
import org.rifidi.edge.core.api.rmi.dto.SessionDTO;

/**
 * A reader creates and manages instances of sessions. The reader itself holds
 * all configuration parameters and creates the sessions according to these. The
 * returned readerSession objects are immutable and if some parameters of the
 * factory change after a session has been created, the created session will
 * retain its configuration until it is destroyed and a new one is created
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractReader<T extends ReaderSession> extends
		RifidiService {

	/**
	 * Create a new reader session. If there are no more sessions available null
	 * is returned.
	 * 
	 * @return
	 */
	public abstract ReaderSession createReaderSession();

	/**
	 * Get all currently created reader sessions. The Key is the ID of the
	 * session, and the value is the actual session
	 * 
	 * @return
	 */
	abstract public Map<String, ReaderSession> getReaderSessions();

	/**
	 * Destroy a reader session.
	 * 
	 * @param session
	 */
	abstract public void destroyReaderSession(ReaderSession session);

	/**
	 * Send properties that have been modified to the physical reader
	 */
	abstract public void applyPropertyChanges();

	/***
	 * This method returns the Data Transfer Object for this Reader
	 * 
	 * @param config
	 *            The Configuration Object for this AbstractReader
	 * @return A data transfer object for this reader
	 */
	public ReaderDTO getDTO(Configuration config) {
		String readerID = config.getServiceID();
		String factoryID = config.getFactoryID();
		AttributeList attrs = config.getAttributes(config.getAttributeNames());
		List<SessionDTO> sessionDTOs = new ArrayList<SessionDTO>();
		for (ReaderSession s : this.getReaderSessions().values()) {
			sessionDTOs.add(s.getDTO());
		}
		ReaderDTO dto = new ReaderDTO(readerID, factoryID, attrs, sessionDTOs);
		return dto;
	}
}
