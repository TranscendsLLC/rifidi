package org.rifidi.edge.api.rmi.dto;

import java.io.Serializable;
import java.util.Set;

/**
 * A Data Transfer Object for CommandConfigurationFactories. For serializing
 * information about a CommandConfiguraitonFactoy
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class CommandConfigFactoryDTO implements Serializable {

	/** The default serial ID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader factory that this command config works with */
	private String readerFactoryID;
	/** The individual commands types that this factory produces */
	private Set<String> commandConfigTypeIDs;

	/**
	 * Constructor
	 * 
	 * @param readerFactoryID
	 *            The ID of the readerfactory that this command configuration
	 *            factory works with
	 * @param commandConfigTypeIDs
	 *            The command types that this factory produces
	 */
	public CommandConfigFactoryDTO(String readerFactoryID,
			Set<String> commandConfigTypeIDs) {
		this.readerFactoryID = readerFactoryID;
		this.commandConfigTypeIDs = commandConfigTypeIDs;
	}

	/**
	 * Returns the ReaderFactoryID for this class.
	 * 
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * Returns the CommandConfigTypeIDs for thsi class.
	 * 
	 * @return the commandConfigFactoryIDs
	 */
	public Set<String> getCommandConfigTypeIDs() {
		return commandConfigTypeIDs;
	}

}
