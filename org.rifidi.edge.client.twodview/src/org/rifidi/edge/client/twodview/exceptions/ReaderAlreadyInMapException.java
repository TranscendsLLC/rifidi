/* 
 *  ReaderAlreadyInMapException.java
 *  Created:	Nov 11, 2008
 *  Project:	RiFidi Dashboard - An RFID infrastructure monitoring tool
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.twodview.exceptions;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class ReaderAlreadyInMapException extends Exception {

	public ReaderAlreadyInMapException() {
		super();
		System.err.println("Reader already in map");
	}

	public ReaderAlreadyInMapException(String arg0, Throwable arg1) {
		super(arg0, arg1);
		System.err.println("Reader already in map: "+arg0);
	}

	public ReaderAlreadyInMapException(String arg0) {
		super(arg0);
		System.err.println("Reader already in map: "+arg0);
	}

	public ReaderAlreadyInMapException(Throwable arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}



}
