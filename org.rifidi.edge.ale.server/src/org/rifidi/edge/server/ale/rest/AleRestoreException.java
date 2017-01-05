package org.rifidi.edge.server.ale.rest;

import java.util.ArrayList;
import java.util.List;

public class AleRestoreException 
		extends Exception {

	private List<Exception> exceptionList = new ArrayList<>();
	
	public AleRestoreException() {
		super();
		// TODO Auto-generated constructor stub
	}

	public AleRestoreException(List<Exception> exceptionList) {
		super();
		// TODO Auto-generated constructor stub
		this.exceptionList = exceptionList;
	}
	
	public void addException(Exception e){
		exceptionList.add(e);
	}
	
	
	
}
