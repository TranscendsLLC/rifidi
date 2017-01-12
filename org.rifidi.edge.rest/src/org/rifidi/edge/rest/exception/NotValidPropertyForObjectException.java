package org.rifidi.edge.rest.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotValidPropertyForObjectException 
		extends Exception 
		implements Serializable {
	
	private List<String> notValidPropertiesList = new ArrayList<String>();
	
	
	public NotValidPropertyForObjectException(String message) {
		super(message);
		// TODO Auto-generated constructor stub
	}
	
	public void addNotValidProperty(String prop){
		notValidPropertiesList.add(prop);
	}

	/**
	 * @return the notValidPropertiesList
	 */
	public List<String> getNotValidPropertiesList() {
		return notValidPropertiesList;
	}

	/**
	 * @param notValidPropertiesList the notValidPropertiesList to set
	 */
	public void setNotValidPropertiesList(List<String> notValidPropertiesList) {
		this.notValidPropertiesList = notValidPropertiesList;
	}
	
	
}
