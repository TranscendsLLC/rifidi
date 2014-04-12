/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.util;

import java.util.Map;
import java.util.concurrent.Executor;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class exports a webservice. It is based of spring's
 * SimpleJaxWsServiceExporter and AbstractJaxWsServiceExporter. Like the
 * SimpleJaxWsServiceExporter, it has the following restriction:
 * 
 * <p>
 * Note that this exporter will only work if the JAX-WS runtime actually
 * supports publishing with an address argument, i.e. if the JAX-WS runtime
 * ships an internal HTTP server. This is the case with the JAX-WS runtime
 * that's inclued in Sun's JDK 1.6 but not with the standalone JAX-WS 2.1 RI.
 * 
 * You should supply a host that takes the form of "http://127.0.0.1", a port
 * number, and an object that is annotated with the WebService annotation. The
 * actual publication address will be appended to the base address. For example,
 * a service named "orderService", will be published by default to
 * http://127.0.0.1:8080/orderService.
 * 
 * In addition, you may supply a 'deploy' boolean that controls whether or not
 * the service should be exported when the start method is called.
 * 
 * The default host is http://127.0.0.1 and the default port is 8080. By
 * default, deploy is true.
 * 
 * This class is not thread safe. It is intended to be used in a spring
 * configuration xml.
 * 
 * @see javax.jws.WebService
 * @see javax.xml.ws.Endpoint#publish(String)
 * 
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class JaxWsServiceExporter {

	/** The host to export to */
	private String host;
	/** The port to export to */
	private Integer port;
	/** Whether or not to deploy the WS when the start method is called */
	private boolean deploy = true;
	/** The object with a WebService annotation */
	private Object service;
	/** A properties map that can be supplied to the webservice */
	private Map<String, Object> endpointProperties;
	/** An executor which can be supplied to the webservice */
	private Executor executor;
	/** The default host */
	private String defaultHost = "http://127.0.0.1";
	/** The default port */
	private Integer defaultPort = 8080;
	/** The full address of the web service once it has been deployed. */
	private String fullAddress;
	/** The endpoint to export */
	private Endpoint endpoint;
	/** The logger for this class */
	private Log logger = LogFactory.getLog(JaxWsServiceExporter.class);

	/**
	 * Set the property bag for the endpoint, including properties such as
	 * "javax.xml.ws.wsdl.service" or "javax.xml.ws.wsdl.port".
	 * 
	 * @see javax.xml.ws.Endpoint#setProperties
	 * @see javax.xml.ws.Endpoint#WSDL_SERVICE
	 * @see javax.xml.ws.Endpoint#WSDL_PORT
	 */
	public void setEndpointProperties(Map<String, Object> endpointProperties) {
		this.endpointProperties = endpointProperties;
	}

	/**
	 * Set the JDK concurrent executor to use for dispatching incoming requests
	 * to exported service instances.
	 * 
	 * @see javax.xml.ws.Endpoint#setExecutor
	 */
	public void setExecutor(Executor executor) {
		this.executor = executor;
	}

	/**
	 * Takes the form of 'http://127.0.0.1'
	 * 
	 * @param host
	 *            the host to set
	 */
	public void setHost(String host) {
		this.host = host;
	}

	/**
	 * @param port
	 *            the port to set
	 */
	public void setPort(Integer port) {
		this.port = port;
	}

	/**
	 * Must have a class-level WebService annotation
	 * 
	 * @param service
	 *            the service to set
	 */
	public void setService(Object service) {
		if (service == null) {
			throw new NullPointerException("The supplied service is null");
		}
		if (service.getClass().getAnnotation(WebService.class) == null) {
			throw new IllegalArgumentException(
					"The service must have a @WebService annotation");
		}
		this.service = service;
	}

	/**
	 * @param deploy
	 *            the deploy to set
	 */
	public void setDeploy(boolean deploy) {
		this.deploy = deploy;
	}

	/**
	 * The start method. Should be called after properties have been set. Will
	 * deploy the webservice.
	 */
	public void start() {
		if (deploy) {
			WebService annotation = service.getClass().getAnnotation(
					WebService.class);

			if (endpoint != null) {
				stop();
			}

			Endpoint endpoint = Endpoint.create(service);
			if (this.endpointProperties != null) {
				endpoint.setProperties(this.endpointProperties);
			}
			if (this.executor != null) {
				endpoint.setExecutor(this.executor);
			}
			if (this.host == null) {
				this.host = defaultHost;
			}
			if (this.port == null) {
				this.port = defaultPort;
			}
			fullAddress = host + ":" + port + "/" + annotation.serviceName();
			endpoint.publish(fullAddress);
			logger.info("Web Service published: " + fullAddress);
			this.endpoint = endpoint;

		}
	}

	/**
	 * Stops the web service.
	 */
	public void stop() {
		if (endpoint != null) {
			if (endpoint.isPublished()) {
				endpoint.stop();
				logger.info("Web Service stopped: " + fullAddress);
				endpoint = null;
				fullAddress = null;
			}
		}
	}
}
