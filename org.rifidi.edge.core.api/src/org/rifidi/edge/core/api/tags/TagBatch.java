/**
 * 
 */
package org.rifidi.edge.core.api.tags;

import java.io.Serializable;
import java.util.Set;

/**
 * @author kyle
 *
 */
public class TagBatch implements Serializable{

	/**SerialVersion ID*/
	private static final long serialVersionUID = 1L;
	
	private String readerID;
	
	private long timestamp;
	
	private Set<TagDTO> tags;
	
	

}
