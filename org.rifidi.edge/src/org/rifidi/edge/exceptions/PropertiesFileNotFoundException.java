package org.rifidi.edge.exceptions;

public class PropertiesFileNotFoundException
		extends Exception{

	public PropertiesFileNotFoundException(String groupName, String appName) {
		
		super("Application properties file not found for groupName: " + groupName 
				+ " and appName: " + appName);
	}
	
	public PropertiesFileNotFoundException(String groupName) {
		
		super("Group properties file not found for groupName: " + groupName);
	}
	
	public PropertiesFileNotFoundException(String groupName, String appName, String readZoneName) {
		
		super("Readzone properties file not found for groupName: " + groupName 
				+ " and appName: " + appName + " and readzone: " + readZoneName);
	}

}
