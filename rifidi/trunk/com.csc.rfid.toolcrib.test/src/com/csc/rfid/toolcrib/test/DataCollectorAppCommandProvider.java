/**
 * 
 */
package com.csc.rfid.toolcrib.test;

import org.eclipse.osgi.framework.console.CommandInterpreter;
import org.eclipse.osgi.framework.console.CommandProvider;

import com.csc.rfid.toolcrib.test.esper.CollectionType;

/**
 * @author Owner
 * 
 */
public class DataCollectorAppCommandProvider implements CommandProvider {

	private DataCollectorApp app;

	public Object _startC(CommandInterpreter intp) {
		String type = intp.nextArgument();
		if(type==null){
			System.out.println("Usage: startC [checkin|checkout]");
			return  null;
		}
		if(type.equals("checkin")){
			app.startCollection(CollectionType.CHECK_IN);
		}else if(type.equalsIgnoreCase("checkout")){
			app.startCollection(CollectionType.CHECK_OUT);
		}else{
			System.out.println("Usage: startC [checkin|checkout]");
		}
		return null;
	}

	public Object _stopC(CommandInterpreter intp) {
		app.stopCollection();
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.osgi.framework.console.CommandProvider#getHelp()
	 */
	@Override
	public String getHelp() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Called by spring
	 * 
	 * @param app
	 */
	public void setApp(DataCollectorApp app) {
		this.app = app;
	}

}
