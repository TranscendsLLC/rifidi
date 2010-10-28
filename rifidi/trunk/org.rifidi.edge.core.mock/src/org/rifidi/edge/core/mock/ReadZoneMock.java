package org.rifidi.edge.core.mock;

import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.app.api.service.tagmonitor.ReadZone;

public class ReadZoneMock extends ReadZone {
	
	public List<String> mSetReader;
	public List<Boolean> mSetInclude;
	public boolean mCloneCalled;
	
	public ReadZoneMock(String readerID) {
		super(readerID);
		mSetReader = new ArrayList<String>();
		mSetInclude = new ArrayList<Boolean>();
		
		// TODO Auto-generated constructor stub
	}
	
	

	/**
	 * @param readerID
	 *            the readerID to set
	 */
	@Override
	public void setReaderID(String readerID) {
		mSetReader.add(readerID);
	}

	/**
	 * @param include
	 *            the include to set
	 */
	@Override
	public void setInclude(boolean include) {
		mSetInclude.add(new Boolean(include));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public ReadZone clone() {
		mCloneCalled = true;
		return this;
	}


	
}
