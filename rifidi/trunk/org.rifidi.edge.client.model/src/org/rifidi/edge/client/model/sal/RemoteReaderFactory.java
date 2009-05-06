/**
 * 
 */
package org.rifidi.edge.client.model.sal;
//TODO: Comments
import javax.management.MBeanInfo;

import org.rifidi.edge.core.api.rmi.dto.ReaderFactoryDTO;

/**
 * Model object that represents a ReaderFactory on the server
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RemoteReaderFactory {

	/** The DTO associated with this factory */
	private ReaderFactoryDTO dto;
	/** The DTO that describes this factory */
	private MBeanInfo mbeanInfo;

	/**
	 * Constructor
	 * 
	 * @param dto
	 *            The ReaderFactoryDTO for this object
	 * @param mbeanInfo
	 *            The MBeanInfo that describes this factory
	 */
	public RemoteReaderFactory(ReaderFactoryDTO dto, MBeanInfo mbeanInfo) {
		super();
		this.dto = dto;
		this.mbeanInfo = mbeanInfo;
	}

	/**
	 * 
	 * @return The ID of this factory
	 */
	public String getID() {
		return dto.getReaderFactoryID();
	}

	/**
	 * 
	 * @return The display name for this factory
	 */
	public String getDisplayName() {
		return dto.getReaderFactoryDisplayName();
	}

	/**
	 * 
	 * @return The description of this reader factory
	 */
	public String getDescription() {
		return dto.getReaderFactoryDescription();
	}

	/**
	 * 
	 * @return The MBeanInfo that describes this factory
	 */
	public MBeanInfo getMbeanInfo() {
		return mbeanInfo;
	}

}
