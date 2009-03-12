package org.rifidi.edge.core.jms.notifications;

import java.io.Serializable;

import org.rifidi.edge.core.api.rmi.dto.ReaderDTO;

public class ReaderAdded implements Serializable{

	/** Serial Version ID for this message*/
	private static final long serialVersionUID = 1L;
	
	private ReaderDTO readerDTO;
	
	public ReaderAdded(ReaderDTO dto){
		this.readerDTO = dto;
	}

	/**
	 * @return the readerDTO
	 */
	public ReaderDTO getReaderDTO() {
		return readerDTO;
	}

}
