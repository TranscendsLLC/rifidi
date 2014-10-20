package org.rifidi.edge.rest.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionDoesNotExistException 
		extends Exception 
		implements Serializable {
	
	public SessionDoesNotExistException(String strReaderId, String strSessionId) {
		super("Session with id " + strSessionId + " does not exist for reader with id " + strReaderId);
		// TODO Auto-generated constructor stub
	}
	
}
