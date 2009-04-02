/**
 * 
 */
package org.rifidi.edge.core.api.tags;

import java.io.Serializable;
import java.math.BigInteger;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagDTO implements Serializable {

	/** SerialVersionID */
	private static final long serialVersionUID = 1L;

	private BigInteger tagID;

	private int antennaNumber;

	private long timestamp;

}
