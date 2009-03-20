/* 
 *  WsCommand.java
 *  Created:	Mar 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.connection
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.connection.commands;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public interface WsCommand {

	public Object makeCall();
}
