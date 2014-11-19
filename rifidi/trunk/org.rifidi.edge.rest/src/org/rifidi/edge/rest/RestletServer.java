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
				int port = Integer.parseInt(System
						.getProperty("org.rifidi.restlet.port"));
				logger.info("Starting restlet server on port: " + port);
				Component component = new Component();
				component.getServers().add(Protocol.HTTP, port);
				component.getClients().add(Protocol.FILE);

				component.getDefaultHost().attach(app);
				component.start();
			}

			// Check if ssl is enabled
			boolean sslEnabled = Boolean.parseBoolean(System
					.getProperty("org.rifidi.restlet.ssl.enabled"));

			if (sslEnabled) {
				logger.info("Restlet SSL Server enabled");

				// Get required jvm properties

				int sslPort = Integer.parseInt(System
						.getProperty("org.rifidi.restlet.ssl.port"));
				String keystorepath = System.getProperty("org.rifidi.home")
						+ File.separator
						+ System.getProperty("org.rifidi.restlet.ssl.keystorepath");
				String keystorepassword = System
						.getProperty("org.rifidi.restlet.ssl.keystorepassword");
				String keypassword = System
						.getProperty("org.rifidi.restlet.ssl.keypassword");
				String keystoretype = System
						.getProperty("org.rifidi.restlet.ssl.keystoretype");

				logger.info("Starting ssl restlet server on port: " + sslPort);

				Component sslComponent = new Component();

				Server server = sslComponent.getServers().add(Protocol.HTTPS,
						sslPort);

				server.getProtocols().add(Protocol.FILE);

				// sslComponent.getClients().add(Protocol.FILE);

				Series<Parameter> parameters = server.getContext()
						.getParameters();

				// parameters.add("sslContextFactory",
				// "org.restlet.ext.ssl.PkixSslContextFactory");
				parameters.add("sslContextFactory",
						"org.restlet.ext.jsslutils.PkixSslContextFactory");
				parameters.add("keystorePath", keystorepath);
				parameters.add("keystorePassword", keystorepassword);
				parameters.add("keyPassword", keypassword);
				parameters.add("keystoreType", keystoretype);

				sslComponent.getDefaultHost().attach(app);
				sslComponent.start();

			}

		} catch (Exception e) {
			// TODO Handle this
			e.printStackTrace();
		}
	}
}
