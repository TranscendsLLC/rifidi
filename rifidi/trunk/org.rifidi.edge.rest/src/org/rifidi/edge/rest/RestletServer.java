/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.File;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Application;
import org.restlet.Component;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.util.Series;

/**
 * 
 * 
 * @author matt
 */
public class RestletServer {

	/** Logger */
	private final Log logger = LogFactory.getLog(getClass());

	/**
	 * 
	 */
	public RestletServer(Application app) {
		try {
			
			logger.info("RestletServer called");
			boolean restletEnabled = Boolean.parseBoolean(System
					.getProperty("org.rifidi.restlet.enabled"));
			if (restletEnabled) {
				
				int port = Integer.parseInt(System.getProperty("org.rifidi.restlet.port"));
				logger.info("Starting restlet server on port: " + port);
				Component component = new Component();
				Server jettyServer = new Server(component.getContext(), Protocol.HTTP, port);
				Series<Parameter> parameters = jettyServer.getContext().getParameters();
				
				//jetty parameters
				addArguments(parameters);
				component.getServers().add(jettyServer); 
				component.getClients().add(Protocol.FILE);

				component.getDefaultHost().attach(app);
				component.start();
				Activator.addToComponents(component);
			}

			// Check if ssl is enabled
			boolean sslEnabled = Boolean.parseBoolean(System.getProperty("org.rifidi.restlet.ssl.enabled"));

			if (sslEnabled) {
				logger.info("Restlet SSL Server enabled");

				// Get required jvm properties

				int sslPort = Integer.parseInt(System.getProperty("org.rifidi.restlet.ssl.port"));
				String keystorepath = System.getProperty("org.rifidi.home")
						+ File.separator
						+ System.getProperty("org.rifidi.restlet.ssl.keystorepath");
				String keystorepassword = System.getProperty("org.rifidi.restlet.ssl.keystorepassword");
				String keypassword = System.getProperty("org.rifidi.restlet.ssl.keypassword");
				String keystoretype = System.getProperty("org.rifidi.restlet.ssl.keystoretype");

				logger.info("Starting ssl restlet server on port: " + sslPort);

				Component sslComponent = new Component();
				Server sjettyServer = new Server(sslComponent.getContext(), Protocol.HTTPS, sslPort);

				sjettyServer.getProtocols().add(Protocol.FILE);

				Series<Parameter> parameters = sjettyServer.getContext().getParameters();
				
				parameters.add("sslContextFactory", "org.restlet.ext.jsslutils.PkixSslContextFactory");
				parameters.add("keystorePath", keystorepath);
				parameters.add("keystorePassword", keystorepassword);
				parameters.add("keyPassword", keypassword);
				parameters.add("keystoreType", keystoretype);
				//jetty parameters
				addArguments(parameters);
				
				sslComponent.getServers().add(sjettyServer);

				sslComponent.getDefaultHost().attach(app);
				sslComponent.start();
				Activator.addToComponents(sslComponent);
			}

		} catch (Exception e) {
			// TODO Handle this
			e.printStackTrace();
		}
	}
	
	public static void addArguments(Series<Parameter> parameters) {
		String[] args = new String[] { "threadPool.minThreads",
				"threadPool.maxThreads", "threadPool.threadsPriority",
				"threadPool.idleTimeout", "threadPool.stopTimeout",
				"connector.acceptors", "connector.selectors",
				"connector.acceptQueueSize", "connector.idleTimeout",
				"connector.soLingerTime", "connector.stopTimeout",
				"http.headerCacheSize", "http.requestHeaderSize",
				"http.responseHeaderSize", "http.outputBufferSize",
				"lowResource.period", "lowResource.threads",
				"lowResource.maxMemory", "lowResource.maxConnections",
				"lowResource.idleTimeout", "lowResource.stopTimeout",
				"spdy.version", "spdy.pushStrategy" };
		for (String arg : args) {
			String prop = System.getProperty("org.rifidi.restlet." + arg);
			if (prop != null) {
				parameters.add(arg, prop);
			}
		}
	}
}
