/**
 * 
 */
package org.rifidi.edge.app.diag.gpio;

import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.core.sensors.exceptions.CannotExecuteException;
import org.rifidi.edge.core.sensors.management.AbstractGPIOService;

/**
 * 
 * 
 * @author kyle
 * @author matt
 */
public class GPIOApp {

	private static final Log logger = LogFactory.getLog(GPIOApp.class);

	private final Set<AbstractGPIOService<?>> gpioServiceList = new CopyOnWriteArraySet<AbstractGPIOService<?>>();

	public void start() {
		logger.debug("Starting GPIOApp");
	}

	public void stop() {
		logger.debug("Stopping GPIOApp");
	}

	public void setGPO(String readerID, Set<Integer> ports)
			throws CannotExecuteException {
		Set<Integer> translatedPorts = new HashSet<Integer>();
		for (Integer port : ports) {
			translatedPorts.add(port - 1);
		}
		for (AbstractGPIOService<?> service : this.gpioServiceList) {
			if (service.isReaderAvailable(readerID)) {
				service.setGPO(readerID, translatedPorts);
			}
		}
	}

	public void flashGPO(String readerID, int port, int seconds)
			throws CannotExecuteException {
		port = port - 1;
		Set<Integer> bit = new HashSet<Integer>();
		bit.add(port);
		for (AbstractGPIOService<?> service : this.gpioServiceList) {
			if (service.isReaderAvailable(readerID)) {
				service.flashGPO(readerID, seconds, bit);
			}
		}
	}

	public boolean testGPI(String readerID, int port)
			throws CannotExecuteException {
		port = port - 1;
		for (AbstractGPIOService<?> service : this.gpioServiceList) {
			if (service.isReaderAvailable(readerID)) {
				service.testGPI(readerID, port);
			}
		}
		throw new CannotExecuteException();
	}

	/**
	 * @param gpioService
	 *            the gpioService to set
	 */
	public void onBind(AbstractGPIOService<?> gpioService,
			Dictionary<String, String> parameters) {
		logger.debug("Binding: " + gpioService);
		this.gpioServiceList.add(gpioService);
	}

	/**
	 * @param gpioService
	 *            the gpioService to set
	 */
	public void onUnbind(AbstractGPIOService<?> gpioService,
			Dictionary<String, String> parameters) {
		this.gpioServiceList.remove(gpioService);
	}

	/**
	 * Called by spring.
	 * 
	 * @param gpioSet
	 */
	public void setServiceSet(Set<AbstractGPIOService<?>> gpioSet) {
		// Should we clear the local list before we iterate through the loop?
		for (AbstractGPIOService<?> service : gpioSet) {
			logger.debug("Adding a GPIO service in the setter: " + service);
			this.gpioServiceList.add(service);
		}
	}

	/**
	 * @return
	 */
	public Set<AbstractGPIOService<?>> getServiceSet() {
		return this.gpioServiceList;
	}
}
