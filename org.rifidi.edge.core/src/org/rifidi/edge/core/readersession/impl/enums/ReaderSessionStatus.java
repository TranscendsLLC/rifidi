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
	 * ReaderSession is currently executing a asynchronous command
	 */
	EXECUTING_COMMAND,
	/**
	 * RederSession has caused a command in order to execute a synchronous property
	 */
	EXECUTING_PROPERTY_WITH_YIELDED_COMMAND,

	/**
	 * ReaderSession is executing a synchronous Property and no asynchronous command has been
	 * yielded
	 */
	EXECUTING_PROPERTY_WITH_NO_YIELDED_COMMAND,
	/**
	 * ReaderSession is in a error state because of unexpected problems
	 */
	ERROR
}
