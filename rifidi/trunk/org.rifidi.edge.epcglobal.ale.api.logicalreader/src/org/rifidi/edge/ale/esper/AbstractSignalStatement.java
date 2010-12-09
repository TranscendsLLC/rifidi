/*
 * 
 * AbstractSignalStatement.java
 *  
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                   http://www.rifidi.org
 *                   http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the GPL License
 *                   A copy of the license is included in this distribution under RifidiEdge-License.txt 
 */
/**
 * 
 */
package org.rifidi.edge.ale.esper;

import java.util.List;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.rifidi.edge.ale.read.ALEReadAPI;
import org.rifidi.edge.notification.TagReadEvent;

import com.espertech.esper.client.EPAdministrator;

/**
 * Extend this class to listen for results from esper statements.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public abstract class AbstractSignalStatement implements SignalStatement,
		StatementController {
	private CopyOnWriteArraySet<SignalListener> listeners;

	protected EPAdministrator administrator;

	public AbstractSignalStatement(EPAdministrator administrator) {
		listeners = new CopyOnWriteArraySet<SignalListener>();
		this.administrator = administrator;
	}

	/**
	 * Helper method for generating a String to be used in esper inside a
	 * every-distinct statement.
	 * 
	 * @param primarykeys
	 * @return
	 */
	protected String assembleKeys(Set<String> primarykeys) {
		StringBuilder builder = new StringBuilder();
		for (String key : primarykeys) {
			builder.append("res.tag.");
			builder.append(key);
			builder.append("?,");
		}
		builder.deleteCharAt(builder.length() - 1);
		return builder.toString();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * esperplayground.esper.SignalStatement#registerSignalListener(esperplayground
	 * .esper.SignalListener)
	 */
	@Override
	public void registerSignalListener(SignalListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeesperplayground.esper.SignalStatement#unregisterSignalListener(
	 * esperplayground.esper.SignalListener)
	 */
	@Override
	public void unregisterSignalListener(SignalListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Shortcut to inform all listeners about a start signal.
	 * 
	 * @param type
	 * @param cause
	 */
	protected void signalStart(ALEReadAPI.TriggerCondition condition,
			Object cause) {
		for (SignalListener listener : listeners) {
			listener.startSignal(condition, cause);
		}
	}

	/**
	 * Shortcut to inform all listeners that a statement has stopped.
	 * 
	 * @param type
	 * @param cause
	 * @param events
	 */
	protected void signalStop(ALEReadAPI.TriggerCondition condition,
			Object cause, List<TagReadEvent> events) {
		for (SignalListener listener : listeners) {
			listener.stopSignal(condition, cause, events);
		}
	}
}
