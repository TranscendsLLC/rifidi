/**
 * 
 */
package org.rifidi.edge.adapter.llrp;

import org.llrp.ltk.generated.messages.ADD_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.messages.ENABLE_ROSPEC_RESPONSE;
import org.llrp.ltk.generated.parameters.LLRPStatus;
import org.llrp.ltk.types.LLRPMessage;
import org.rifidi.edge.notification.ReaderExceptionEvent;
import org.rifidi.edge.sensors.AbstractSensor;

/**
 * Handles exceptions generated from LLRP.  
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class LLRPExceptionHandler {
	
	private final String readerID;
	private final AbstractSensor<?> sensor;
	
	
	/**
	 * 
	 */
	public LLRPExceptionHandler(String readerID, AbstractSensor<?> sensor) {
		this.readerID = readerID;
		this.sensor = sensor;
	}
	/**
	 * Check the message coming back for errors, then send the status off if it has 
	 * 
	 * @param message
	 */
	public void checkForErrors(LLRPMessage message) {
		if(message instanceof ADD_ROSPEC_RESPONSE) {
			ADD_ROSPEC_RESPONSE response = (ADD_ROSPEC_RESPONSE) message;
			if(isFieldError(response.getLLRPStatus().getStatusCode().toString())) {
				this.sendErrorEvent(response.getLLRPStatus());
			}
		} else if(message instanceof ENABLE_ROSPEC_RESPONSE) {
			ENABLE_ROSPEC_RESPONSE response = (ENABLE_ROSPEC_RESPONSE) message;
			if(isFieldError(response.getLLRPStatus().getStatusCode().toString())) {
				this.sendErrorEvent(response.getLLRPStatus());
			}
		}
	}
	
	/**
	 * Does the message have an error status
	 * 
	 * @param status
	 * @return
	 */
	private boolean isFieldError(String status) {
		return status.contains("M_FieldError");
	}
	
	/**
	 * Send the event
	 * 
	 * @param status
	 */
	private void sendErrorEvent(LLRPStatus status) {
		ReaderExceptionEvent event = new ReaderExceptionEvent(this.readerID, System.currentTimeMillis(), status.getErrorDescription().toString(), status.getStatusCode().toString());
		this.sensor.sendEvent(event);
	}
}
