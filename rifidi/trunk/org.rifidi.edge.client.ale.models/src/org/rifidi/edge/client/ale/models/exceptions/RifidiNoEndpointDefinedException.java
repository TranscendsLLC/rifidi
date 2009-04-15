/* 
 *  RifidiNoEndpointDefinedException.java
 *  Created:	Apr 15, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.models
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.models.exceptions;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class RifidiNoEndpointDefinedException extends Throwable {

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		return "No Endpoint defined: "+ super.getMessage();
	}

}
