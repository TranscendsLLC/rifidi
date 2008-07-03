package org.rifidi.edge.readerplugin.dummy;

/**
 * @author Jerry Maine - jerry@pramari.com
 * Gets the types of error for the DummyPlugin to cause. Really useful for debugging the core.
 */
public enum EDummyError {
	CONNECT, DISCONNECT, GET_NEXT_TAGS, SEND_CUSTOM_COMMAND,
	CONNECT_RUNTIME, DISCONNECT_RUNTIME, GET_NEXT_TAGS_RUNTIME, SEND_CUSTOM_COMMAND_RUNTIME,	
	SEND_CUSTOM_COMMAND2, RANDOM,
	NONE;
}
