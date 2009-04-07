/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Map;
import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ReaderLoopExceptionResponse;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface LogicalReader {

	/**
	 * Get the LRSpec for the reader.
	 * 
	 * @return
	 */
	LRSpec getLRSpec();

	/**
	 * Update the reader with the given properties and readers..
	 * 
	 * @param properties
	 * @param readers
	 */
	void update(Map<String, String> properties, Set<LogicalReader> readers)
			throws ImmutableReaderExceptionResponse, InUseExceptionResponse,
			ReaderLoopExceptionResponse;

	/**
	 * Get the name of the reader.
	 * 
	 * @return
	 */
	String getName();

	/**
	 * Get a property for the reader. If property doesn't exist null will be
	 * returned.
	 * 
	 * @param propertyName
	 * @return
	 */
	String getProperty(String propertyName);

	/**
	 * Set a property for the reader.
	 * 
	 * @param propertyName
	 * @param propertyValue
	 */
	void setProperty(String propertyName, String propertyValue)
			throws ImmutableReaderExceptionResponse, InUseExceptionResponse;

	/**
	 * Call to destroy the reader.
	 * 
	 * @throws ImmutableReaderExceptionResponse
	 * @throws InUseExceptionResponse
	 */
	void destroy() throws ImmutableReaderExceptionResponse,
			InUseExceptionResponse;

	/**
	 * Returns true if the reader is immutable.
	 * 
	 * @return
	 */
	boolean isImmutable();

	/**
	 * Returns true if the reader is currently in use.
	 * 
	 * @return
	 */
	boolean isInUse();

	/**
	 * Get a list of objects currently using the reader.
	 * 
	 * @return
	 */
	Set<Object> getUsers();

	/**
	 * Aquire the reader for using.
	 * 
	 * @param user
	 */
	void aquire(Object user);

	/**
	 * Release the reader.
	 * 
	 * @param user
	 */
	void release(Object user);

	/**
	 * Get readers contained by this reader.
	 * 
	 * @return
	 */
	Set<LogicalReader> getReaders();

}
