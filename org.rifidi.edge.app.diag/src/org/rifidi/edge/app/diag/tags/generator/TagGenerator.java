/**
 * 
 */
package org.rifidi.edge.app.diag.tags.generator;

import org.rifidi.edge.core.services.notification.data.ReadCycle;

import com.espertech.esper.client.EPRuntime;

/**
 * @author Owner
 * 
 */
public class TagGenerator implements Runnable {

	private final ReadCycle readCycle;
	private final EPRuntime runtime;

	public TagGenerator(ReadCycle readCycle, EPRuntime epRuntime) {
		this.readCycle = readCycle;
		this.runtime = epRuntime;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		try{
			runtime.sendEvent(readCycle);
		}catch(Throwable e){
			e.printStackTrace();
		}
	}

}
