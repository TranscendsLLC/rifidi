package org.rifidi.edge.rest.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CommandNorReaderExistsException 
		extends Exception 
		implements Serializable {
	
	public CommandNorReaderExistsException(String strObjectId) {
		super("Neither reader nor command with id " + strObjectId + " exists");
		// TODO Auto-generated constructor stub
	}
	
}
