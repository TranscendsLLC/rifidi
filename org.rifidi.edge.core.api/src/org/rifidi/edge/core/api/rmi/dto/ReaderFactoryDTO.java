/**
 * 
 */
package org.rifidi.edge.core.api.rmi.dto;
//TODO: Comments
import java.io.Serializable;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ReaderFactoryDTO implements Serializable {

	/** The default SerialVersuinID */
	private static final long serialVersionUID = 1L;
	/** The ID of the reader factory */
	private String readerFactoryID;
	/** The display name of the Reader Factory */
	private String readerFactoryDisplayName;
	/** The description of the reader factory */
	private String readerFactoryDescription;

	/***
	 * Constructor
	 * 
	 * @param readerFactoryID
	 * @param readerFactoryDisplayName
	 * @param readerFactoryDescription
	 */
	public ReaderFactoryDTO(String readerFactoryID,
			String readerFactoryDisplayName, String readerFactoryDescription) {
		super();
		this.readerFactoryID = readerFactoryID;
		this.readerFactoryDisplayName = readerFactoryDisplayName;
		this.readerFactoryDescription = readerFactoryDescription;
	}

	/**
	 * @return the readerFactoryID
	 */
	public String getReaderFactoryID() {
		return readerFactoryID;
	}

	/**
	 * @return the readerFactoryDisplayName
	 */
	public String getReaderFactoryDisplayName() {
		return readerFactoryDisplayName;
	}

	/**
	 * @return the readerFactoryDescription
	 */
	public String getReaderFactoryDescription() {
		return readerFactoryDescription;
	}

}
