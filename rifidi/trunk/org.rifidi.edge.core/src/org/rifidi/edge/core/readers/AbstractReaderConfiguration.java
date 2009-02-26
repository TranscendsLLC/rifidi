/**
 * 
 */
package org.rifidi.edge.core.readers;

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
public abstract class AbstractReaderConfiguration<T extends Reader> extends
		RifidiService {

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
	 * This method is intended to be a short-lived command that makes a
	 * connection to the reader in order to aquire and also to set properties on
	 * it. Certain reader types may open a concurrent connection to do this.
	 * Resulting properties should be put in JMX properties of a
	 * ReaderConfiguration
	 */
	abstract public void configureReader();

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
