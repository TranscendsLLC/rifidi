/**
 * 
 */
package org.rifidi.edge.core.readers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.ServiceRegistration;
import org.rifidi.configuration.RifidiService;
import org.rifidi.edge.core.exceptions.NoReaderAvailableException;

/**
 * Factory that provides and manages reader instances {@link Reader}.<br/>
 * A factory creates and manages instances of readers. The factory itself holds
 * all configuration parameters and creates the readers according to these. The
 * connection to the reader combined with the given properties represents a
 * certain state of the reader which is then used by the command.<br/>
 * The returned reader objects are immutable and if some parameters of the
 * factory change while a reader has been aquired the aquired reader has to be
 * destroied and the process using the reader will have to aquire a new one.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractReaderConfiguration<T extends Reader> implements
		RifidiService {

	/** Service Registration with osgi */
	private ServiceRegistration registration;
	private static final Log logger = LogFactory
			.getLog(AbstractReaderConfiguration.class);

	/**
	 * @param registration
	 *            the registration to set
	 */
	public void setRegistration(ServiceRegistration registration) {
		this.registration = registration;
	}

	public void destroy() {
		if (registration != null) {
			registration.unregister();
			return;
		}
		logger.error("Tried to unregister service "
				+ "that was not yet registered!");
	}

	/**
	 * Try to aquire an instance of the reader.
	 * 
	 * @return
	 * @throws NoReaderAvailableException
	 */
	abstract public T aquireReader() throws NoReaderAvailableException;

	/**
	 * Release the reader.
	 * 
	 * @param reader
	 */
	abstract public void releaseReader(Object reader);

	/**
	 * Get the name of the reader. Has to be unique.
	 * 
	 * @return
	 */
	abstract public String getName();

	/**
	 * Get a description about the reader this factory is mapping to.
	 * 
	 * @return
	 */
	abstract public String getDescription();

}
