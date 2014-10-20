package org.rifidi.edge.rest.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReaderDoesNotExistException 
		extends Exception 
		implements Serializable {
	
	public ReaderDoesNotExistException(String strReaderId) {
		super("Reader with id " + strReaderId + " does not exist");
		// TODO Auto-generated constructor stub
	}
	
}
