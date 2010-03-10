/**
 * 
 */
package org.rifidi.edge.app.diag.gpio;

import java.util.HashSet;
import java.util.Set;

import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.management.AbstractGPIOService;

/**
 * @author kyle
 * 
 */
public class GPIOApp{

	private AbstractGPIOService<?> gpioService;

	public void start() {

	}

	public void stop() {

	}

	public void setGPO(String readerID, Set<Integer> ports)
			throws CannotExecuteException {
		Set<Integer> translatedPorts = new HashSet<Integer>();
		for(Integer port : ports){
			translatedPorts.add(port - 1);
		}
		gpioService.setGPO(readerID, translatedPorts);

	}

	public void flashGPO(String readerID, int port, int seconds)
			throws CannotExecuteException {
		port = port-1;
		Set<Integer> bit = new HashSet<Integer>();
		bit.add(port);
		gpioService.flashGPO(readerID, seconds, bit);
	}

	public boolean testGPI(String readerID, int port)
			throws CannotExecuteException {
		port = port -1;
		return gpioService.testGPI(readerID, port);
	}

	/**
	 * @param gpioService
	 *            the gpioService to set
	 */
	public void setGpioService(AbstractGPIOService<?> gpioService) {
		this.gpioService = gpioService;
	}

}
