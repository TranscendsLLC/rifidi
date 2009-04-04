/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationExceptionResponse;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface LogicalReaderManagementService {
	/**
	 * Get a reader by its name. returns null if none is found.
	 * 
	 * @param name
	 * @return
	 */
	LogicalReader getLogicalReaderByName(String name)
			throws NoSuchNameExceptionResponse;

	/**
	 * Get a set containing the names of all currently available readers. The
	 * set is a copy and writes to it won't influence the list of available
	 * readers.
	 * 
	 * @return
	 */
	Set<String> getLogicalReaders();

	/**
	 * Check if a reader with the given name exists.
	 * 
	 * @param name
	 * @return
	 */
	boolean readerExists(String name);

	/**
	 * Destroy the reader with the given name.
	 * 
	 * @param name
	 */
	void destroyLogicalReader(String name) throws NoSuchNameExceptionResponse,
			ImmutableReaderExceptionResponse, InUseExceptionResponse;

	/**
	 * Create a new logical reader.
	 * 
	 * @param name
	 * @param lrSpec
	 * @param immutable
	 */
	void createLogicalReader(String name, LRSpec lrSpec, Boolean immuatable)
			throws DuplicateNameExceptionResponse, ValidationExceptionResponse;
}
