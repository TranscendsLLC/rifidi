package org.rifidi.edge.core.readersession.impl.enums;

/**
 * The Status of a ReaderSession
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public enum ReaderSessionStatus {
	/**
	 * ReaderSession is ready
	 */
	OK,
	/**
	 * ReaderSession is currently executing a command
	 */
	BUSY,
	/**
	 * ReaderSession is in a error state because of unexpected problems
	 */
	ERROR
}
