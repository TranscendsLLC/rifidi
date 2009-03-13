/**
 * 
 */
package org.rifidi.edge.lr;

import java.util.Set;

import org.rifidi.edge.epcglobal.ale.api.lr.ws.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.lr.ws.ReaderLoopExceptionResponse;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public interface CompositeLogicalReader extends LogicalReader {
	/**
	 * Add a new reader to the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderLoopExceptionResponse
	 * @throws InUseExceptionResponse
	 * @throws ImmutableReaderExceptionResponse
	 */
	void addReader(LogicalReader reader) throws ReaderLoopExceptionResponse,
			InUseExceptionResponse, ImmutableReaderExceptionResponse;

	/**
	 * Add new readers to the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderLoopExceptionResponse
	 * @throws InUseExceptionResponse
	 * @throws ImmutableReaderExceptionResponse
	 */
	void addReaders(Set<LogicalReader> reader)
			throws ReaderLoopExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse;

	/**
	 * Remove a reader from the composite reader.
	 * 
	 * @param reader
	 * @throws InUseExceptionResponse
	 * @throws ImmutableReaderExceptionResponse
	 */
	void removeReader(LogicalReader reader) throws InUseExceptionResponse,
			ImmutableReaderExceptionResponse;

	/**
	 * Remove readers from the composite reader.
	 * 
	 * @param reader
	 * @throws InUseExceptionResponse
	 * @throws ImmutableReaderExceptionResponse
	 */
	void removeReaders(Set<LogicalReader> reader)
			throws InUseExceptionResponse, ImmutableReaderExceptionResponse;

	/**
	 * Set the readers contained by the composite reader.
	 * 
	 * @param reader
	 * @throws ReaderLoopExceptionResponse
	 * @throws InUseExceptionResponse
	 * @throws ImmutableReaderExceptionResponse
	 */
	void setReaders(Set<LogicalReader> reader)
			throws ReaderLoopExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse;
}
