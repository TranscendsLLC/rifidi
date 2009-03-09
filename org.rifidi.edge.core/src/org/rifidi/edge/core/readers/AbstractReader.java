/**
 * 
 */
package org.rifidi.edge.core.readers;

import java.util.List;

import org.rifidi.configuration.RifidiService;

/**
 * Factory that provides and manages readerSession instances
 * {@link ReaderSession}.<br/>
 * A factory creates and manages instances of readers. The factory itself holds
 * all configuration parameters and creates the readers according to these. The
 * connection to the readerSession combined with the given properties represents
 * a certain state of the readerSession which is then used by the command.<br/>
 * The returned readerSession objects are immutable and if some parameters of
 * the factory change while a readerSession has been aquired the aquired
 * readerSession has to be destroyed and the process using the readerSession
 * will have to aquire a new one.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractReader<T extends ReaderSession>
		extends RifidiService {

	/**
	 * Get the name of the readerSession. Has to be unique.
	 * 
	 * @return
	 */
	abstract public String getName();

	/**
	 * Get a description about the readerSession this factory is mapping to.
	 * 
	 * @return
	 */
	abstract public String getDescription();

	/**
	 * Create a new reader session. If there are no more sessions available null
	 * is returned.
	 * 
	 * @return
	 */
	abstract public ReaderSession createReaderSession();

	/**
	 * Get all currently created reader sessions.
	 * 
	 * @return
	 */
	abstract public List<ReaderSession> getReaderSessions();

	/**
	 * Destroy a reader session.
	 * 
	 * @param session
	 */
	abstract public void destroyReaderSession(ReaderSession session);
	
	/**
	 * Apply Property changes to the reader
	 */
	abstract public void applyPropertyChanges();
}
