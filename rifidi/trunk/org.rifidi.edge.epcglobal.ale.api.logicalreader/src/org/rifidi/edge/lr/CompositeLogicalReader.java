/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.lr.ws.ReaderLoopExceptionResponse;
import org.rifidi.edge.lr.exceptions.ImmutableReaderException;
import org.rifidi.edge.lr.exceptions.ReaderInUseException;

/**
 * A logical reader that contains other readers.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface CompositeLogicalReader extends LogicalReader {
	/**
	 * Add a new reader to the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderInUseException
	 * @throws ImmutableReaderException
	 */
	void addReader(LogicalReader reader) throws ReaderInUseException,
			ImmutableReaderException;

	/**
	 * Add new readers to the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderInUseException
	 * @throws ImmutableReaderException
	 */
	void addReaders(Set<LogicalReader> reader) throws ReaderInUseException,
			ImmutableReaderException;

	/**
	 * Remove a reader from the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderInUseException
	 * @throws ImmutableReaderException
	 */
	void removeReader(LogicalReader reader) throws ReaderInUseException,
			ImmutableReaderException;

	/**
	 * Remove readers from the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderInUseException
	 * @throws ImmutableReaderException
	 */
	void removeReaders(Set<LogicalReader> reader) throws ReaderInUseException,
			ImmutableReaderException;

	/**
	 * Set the readers contained by the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderLoopExceptionResponse
	 * @throws ReaderInUseException
	 * @throws ImmutableReaderException
	 */
	void setReaders(Set<LogicalReader> reader)
			throws ReaderLoopExceptionResponse, ReaderInUseException,
			ImmutableReaderException;
}
