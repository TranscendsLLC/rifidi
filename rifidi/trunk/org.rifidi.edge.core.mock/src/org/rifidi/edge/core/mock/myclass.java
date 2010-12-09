package org.rifidi.edge.core.mock;

import org.rifidi.edge.api.service.tagmonitor.ReadZone;

public class myclass {

	ReadZone mReadZone;
	void setReadZone(ReadZone r) {
		mReadZone = r;
	}
	
	void doSomethingIwantToTest() {
		mReadZone.setReaderID("my read");
		mReadZone.setReaderID("my read1");
		mReadZone.setReaderID("my read2");
		
		mReadZone.setInclude(false);
		mReadZone.setInclude(true);
		
		mReadZone.clone();
	}
 }
