/**
 * 
 */
package org.rifidi.edge.client.model.sal;

import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;

/**
 * @author kyle
 * 
 */
public class RemoteReader {

	/** The ID of the Reader */
	private ReaderDTO readerDTO;

	public RemoteReader(ReaderDTO readerDTO) {
		super();
		this.readerDTO = readerDTO;
	}

	/**
	 * @return the iD
	 */
	public String getID() {
		return readerDTO.getReaderID();
	}
	
	/**
	 * @return the readerDTO
	 */
	public ReaderDTO getReaderDTO() {
		return readerDTO;
	}
}
