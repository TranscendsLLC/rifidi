package org.rifidi.edge.rest.exception;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class NotLLRPReaderTypeException 
		extends Exception 
		implements Serializable {
	
	public NotLLRPReaderTypeException(String strReaderId, String strCurrentReaderType) {
		super("Reader with id " + strReaderId + " of type " + strCurrentReaderType + " is not a LLRP reader type");
		// TODO Auto-generated constructor stub
	}
	
}
