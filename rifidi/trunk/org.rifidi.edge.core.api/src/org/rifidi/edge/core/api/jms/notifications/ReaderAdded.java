package org.rifidi.edge.core.api.jms.notifications;

import java.io.Serializable;

public class ReaderAdded implements Serializable{

	/** Serial Version ID for this message*/
	private static final long serialVersionUID = 1L;
	
	private String id;
	
	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	public ReaderAdded(String id){
		this.id= id;
	}

}
