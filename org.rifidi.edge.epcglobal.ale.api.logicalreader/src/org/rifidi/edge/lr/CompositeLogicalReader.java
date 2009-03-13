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
	void addReader(LogicalReader reader) throws ReaderLoopExceptionResponse,
			InUseExceptionResponse, ImmutableReaderExceptionResponse;

	void addReaders(Set<LogicalReader> reader)
			throws ReaderLoopExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse;

	void removeReader(LogicalReader reader) throws InUseExceptionResponse,
			ImmutableReaderExceptionResponse;

	void removeReaders(Set<LogicalReader> reader)
			throws InUseExceptionResponse, ImmutableReaderExceptionResponse;

	void setReaders(Set<LogicalReader> reader)
			throws ReaderLoopExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse;
}
