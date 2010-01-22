package org.rifidi.edge.app.tracking.dao;

/**
 * Interface for a Data Access Object that looks up a Logical Reader Name
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public interface LogicalReadersDAO {

	/**
	 * Get a logical reader name for the given id and antnna
	 * 
	 * @param readerID
	 * @param antenna
	 * @return
	 */
	public String getLogicaReaderName(String readerID, int antenna);

}
