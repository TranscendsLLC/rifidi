package org.rifidi.edge.readerplugin.alien.commandobject;
//TODO: Comments
/**
 * This is an object to wrap CommandObjects along with the name of the property
 * that the command object will get/set
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class AlienCommandObjectWrapper {

	/** The name of the property that this command get/sets */
	private String propertyName;
	/** The command to be executed */
	private AlienCommandObject commandObject;

	/**
	 * @param propertyName
	 *            The name of the property that will be get/set
	 * @param commandObject
	 *            The command to be executed
	 */
	public AlienCommandObjectWrapper(String propertyName,
			AlienCommandObject commandObject) {
		this.propertyName = propertyName;
		this.commandObject = commandObject;
	}

	/**
	 * @return the propertyName
	 */
	public String getPropertyName() {
		return propertyName;
	}

	/**
	 * @return the commandObject
	 */
	public AlienCommandObject getCommandObject() {
		return commandObject;
	}

}
