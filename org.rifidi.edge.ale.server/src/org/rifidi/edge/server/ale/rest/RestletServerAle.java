/**
 * 
 */
package org.rifidi.edge.server.ale.rest;

import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.security.InvalidParameterException;

import javax.annotation.PostConstruct;
import javax.xml.namespace.QName;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.ws.Endpoint;
import javax.xml.ws.soap.SOAPBinding;
import javax.xml.ws.spi.http.HttpContext;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.impl.nio.bootstrap.HttpServer;
import org.restlet.Application;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.Parameter;
import org.restlet.data.Protocol;
import org.restlet.resource.Directory;
import org.restlet.routing.VirtualHost;
import org.restlet.util.Series;
import org.rifidi.edge.epcglobal.ale.ALEServicePortType;
import org.rifidi.edge.epcglobal.alelr.ALELRServicePortType;
import org.rifidi.edge.server.epcglobal.ale.TagHelper;
import org.rifidi.edge.server.epcglobal.alelr.services.RestoreService;
import org.rifidi.edge.utils.PersistenceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

/**
 * 
 * 
 * @author matt
 */
@Component
public class RestletServerAle extends Restlet {

	/* Logger */
	private final Log logger = LogFactory.getLog(getClass());

	@Autowired
	private ALEServicePortType aleServicePortType;

	@Autowired
	private ALELRServicePortType alelrServicePortType;
	
	private static Boolean isWindows = null;

	/**
	 * 
	 */
	public RestletServerAle() {

	}
	
	public static boolean IsWindows() {
		if (isWindows == null) {
			String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);
			if ((OS.indexOf("mac") >= 0) || (OS.indexOf("darwin") >= 0)) {
				isWindows = false;
			} else if (OS.indexOf("win") >= 0) {
				isWindows = true;
			} else if (OS.indexOf("nux") >= 0) {
				isWindows = false;
			} else {
				isWindows = false;
			}
		}
		return isWindows;
	}

	@PostConstruct
	public void init() throws Exception {
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
		try {
			System.out.println("RestletServerAle() call");

			logger.info("Hash Code: " + aleServicePortType.hashCode());

			logger.info("RestletServerAle called");
			boolean restletEnabled = Boolean.parseBoolean(System.getProperty("org.rifidi.ale.enabled"));
			if (restletEnabled) {

				int port = Integer.parseInt(System.getProperty("org.rifidi.ale.port"));
				String host = System.getProperty("org.rifidi.ale.host");

				if ((port < 1) || (port > 65535)) {
					throw new InvalidParameterException("Parameter 'org.rifidi.ale.port' is not between 1 and 65535.");
				}
				
				if ((host == null) || (host.isEmpty())) {
					host = "localhost";
					logger.warn("Using " + host + " as ALE server host name. To change it, set parameter 'org.rifidi.ale.host'.");
				}
				
				URL alelrXsdResource = RestletServerAle.class
						.getResource("/org/rifidi/edge/alelr/xsd/EPCglobal-ale-1_1-alelr.xsd");

				URL aleXsdResource = RestletServerAle.class
						.getResource("/org/rifidi/edge/ale/xsd/EPCglobal-ale-1_1-ale.xsd");
				
				URL alelrCommonXsdResource = RestletServerAle.class
						.getResource("/org/rifidi/edge/alelr/xsd/EPCglobal-ale-1_1-common.xsd");
				
				URL aleCommonXsdResource = RestletServerAle.class
						.getResource("/org/rifidi/edge/ale/xsd/EPCglobal-ale-1_1-common.xsd");

				URL alelrEpcGlobalXsdResource = RestletServerAle.class
						.getResource("/org/rifidi/edge/alelr/xsd/EPCglobal.xsd");
				
				URL aleEpcGlobalXsdResource = RestletServerAle.class
						.getResource("/org/rifidi/edge/ale/xsd/EPCglobal.xsd");

				URL alelrLogicalReadersXsdResource = RestletServerAle.class
						.getResource("/org/rifidi/edge/alelr/xsd/LogicalReaders.xsd");

				

				List<Source> metadataAlelr = new ArrayList<Source>();
				List<Source> metadataAle = new ArrayList<Source>();

				Source sourceAleXsd = new StreamSource(aleXsdResource.openStream());
				Source sourceAlelrXsd = new StreamSource(alelrXsdResource.openStream());

				Source sourceAleCommonXsd = new StreamSource(aleCommonXsdResource.openStream());
				Source sourceAlelrCommonXsd = new StreamSource(alelrCommonXsdResource.openStream());
				Source sourceAleEpcGlobalXsd = new StreamSource(aleEpcGlobalXsdResource.openStream());
				Source sourceAlelrEpcGlobalXsd = new StreamSource(alelrEpcGlobalXsdResource.openStream());
				Source sourceAlelrLogicalReaderXsd = new StreamSource(alelrLogicalReadersXsdResource.openStream());

				sourceAleXsd.setSystemId(aleXsdResource.toExternalForm());
				sourceAlelrXsd.setSystemId(alelrXsdResource.toExternalForm());
				sourceAleCommonXsd.setSystemId(aleCommonXsdResource.toExternalForm());
				sourceAlelrCommonXsd.setSystemId(alelrCommonXsdResource.toExternalForm());
				sourceAleEpcGlobalXsd.setSystemId(aleEpcGlobalXsdResource.toExternalForm());
				sourceAlelrEpcGlobalXsd.setSystemId(alelrEpcGlobalXsdResource.toExternalForm());
				sourceAlelrLogicalReaderXsd.setSystemId(alelrLogicalReadersXsdResource.toExternalForm());

				metadataAlelr.add(sourceAlelrXsd);
				metadataAlelr.add(sourceAlelrCommonXsd);
				metadataAlelr.add(sourceAlelrEpcGlobalXsd);
				metadataAlelr.add(sourceAlelrLogicalReaderXsd);
				
				metadataAle.add(sourceAleXsd);
				metadataAle.add(sourceAleCommonXsd);
				metadataAle.add(sourceAleEpcGlobalXsd);
				
				/*
				 * HttpServer server = new HttpServer().create(new
				 * InetSocketAddress(8080), 10);
				 * server.setExecutor(Executor.newFixedThreadPool(10));
				 * server.start(); HttpContext context =
				 * server.createContext("/test");
				 */

				// Publish alelr service
				logger.info("Starting alelr service on host " + host + " port " + port);
				Endpoint alelrEndPoint;
				if (IsWindows()) {
					alelrEndPoint = Endpoint.create(alelrServicePortType);
				} else {
					alelrEndPoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, alelrServicePortType);
				}
				alelrEndPoint.setMetadata(metadataAlelr);
				// alelrEndPoint.setProperties(properties);
				URI uriAlelr = URI.create("http://" + host + ":" + port + "/alelrservice");
				alelrEndPoint.publish(uriAlelr.toString());

				// Publish ale service
				logger.info("Starting ale service on host " + host + " port " + port);
				Endpoint aleEndPoint;
				if (IsWindows()) {
					aleEndPoint = Endpoint.create(aleServicePortType);
				} else {
					aleEndPoint = Endpoint.create(SOAPBinding.SOAP11HTTP_BINDING, aleServicePortType);
				}
				aleEndPoint.setMetadata(metadataAle);
				URI uriAle = URI.create("http://" + host + ":" + port + "/aleservice");
				aleEndPoint.publish(uriAle.toString());

				// //Restlet
				////
				// org.restlet.Component component = new
				// org.restlet.Component();
				// component.getServers().add(Protocol.HTTP, port);
				// component.getClients().add(Protocol.FILE);
				//
				// final String ROOT_URI =
				// "file:///d:/rifidi_workspace/ale.server2/src/org/rifidi/edge/ale/xsd";
				//
				// // Create an application
				// Application application = new Application() {
				// @Override
				// public org.restlet.Restlet createInboundRoot() {
				// return new Directory(getContext(), ROOT_URI);
				// }
				// };
				//
				// // Attach the application to the component and start it
				// VirtualHost virtualHost = component.getDefaultHost();
				// virtualHost.attach(application);
				//// component.start();
				//
				//// aleEndPoint.publish("http://localhost:" + port +
				// "/aleservice");
				//
				// //publish web service based on rest server:
				////
				////
				//// HttpContext httpContext = new HttpContext() {
				////
				//// @Override
				//// public String getPath() {
				//// // TODO Auto-generated method stub
				//// return "/myPath";
				//// }
				////
				//// @Override
				//// public Set<String> getAttributeNames() {
				//// // TODO Auto-generated method stub
				//// return null;
				//// }
				////
				//// @Override
				//// public Object getAttribute(String name) {
				//// // TODO Auto-generated method stub
				//// return null;
				//// }
				//// };
				////
				//// aleEndPoint.publish(virtualHost.getContext());
				////


			}

		} catch (Exception e) {
			// TODO Handle this
			if (e instanceof AleRestoreException) {
				throw e;
			} else {
				e.printStackTrace();
			}

		}
	}

}
