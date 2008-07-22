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
	 * ReaderSession is currently executing a script command
	 */
	EXECUTING_SCRIPT,
	/**
	 * RederSession has caused a script to yield in order to execute a property
	 */
	EXECUTING_PROPERTY_WITH_YIELDED_SCRIPT,

	/**
	 * ReaderSession is executing a Property command and no script has been
	 * yielded
	 */
	EXECUTING_PROPERTY_WITH_NO_YIELDED_SCRIPT,
	/**
	 * ReaderSession is in a error state because of unexpected problems
	 */
	ERROR
}
