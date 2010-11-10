/* 
 *  AleEventListener.java
 *  Created:	Jul 2, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ecspecview
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.logicalreaders;

/**
 * This listener allows to listen for events like reader added, deleted, spec
 * added, deleted,...
 * 
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */

public interface AleEventListener {
	/**
	 * This method gets called when an ale event happens.
	 * 
	 * @param aleEvent
	 * @param eventSubject
	 */
	public void eventOccurred(AleEvents aleEvent, String eventSubject);

}
