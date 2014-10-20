package org.rifidi.edge.rest.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SessionIsRunningLLRPEncodingException 
		extends Exception 
		implements Serializable {
	
	public SessionIsRunningLLRPEncodingException(String strReaderId, String strSessionId) {
		super("Session with id " + strSessionId + " of reader with id " + strReaderId + " is currently in the middle of encoding operations. Try again in a while");
		// TODO Auto-generated constructor stub
	}
	
}
