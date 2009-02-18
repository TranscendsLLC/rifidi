/**
 * 
 */
package org.rifidi.edge.newcore.readers;

import org.rifidi.edge.newcore.exceptions.NoReaderAvailableException;

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
public interface ReaderConfiguration<T extends Reader> {
	/**
	 * Try to aquire an instance of the reader.
	 * 
	 * @return
	 * @throws NoReaderAvailableException
	 */
	T aquireReader() throws NoReaderAvailableException;

	/**
	 * Release the reader.
	 * 
	 * @param reader
	 */
	void releaseReader(Object reader);

	/**
	 * Get the name of the reader. It should be unique but it doesn't have to
	 * be.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Get a description about the reader this factory is mapping to.
	 * 
	 * @return
	 */
	String getDescription();
}
