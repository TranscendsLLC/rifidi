/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.lr.data.LRSpec;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ValidationExceptionResponse;
import org.rifidi.edge.lr.exceptions.DuplicateReaderNameException;
import org.rifidi.edge.lr.exceptions.ImmutableReaderException;
import org.rifidi.edge.lr.exceptions.NoSuchReaderNameException;
import org.rifidi.edge.lr.exceptions.ReaderInUseException;

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
	 * @throws NoSuchReaderNameException
	 */
	LogicalReader getLogicalReaderByName(String name)
			throws NoSuchReaderNameException;

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
	void destroyLogicalReader(String name) throws NoSuchReaderNameException,
			ImmutableReaderException, ReaderInUseException;

	/**
	 * Create a new logical reader.
	 * 
	 * @param name
	 * @param lrSpec
	 * @param immutable
	 */
	void createLogicalReader(String name, LRSpec lrSpec, Boolean immuatable)
			throws DuplicateReaderNameException, NoSuchReaderNameException;
}
