
package org.rifidi.edge.wsmanagement;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.rifidi.edge.core.services.soap.WSManagementService;
import org.rifidi.edge.core.services.soap.WebService;

/**
 * This implementation uses CXF and the OSGi http service for publishing web
 * services.
 * 
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class WSManagementServiceCxfImpl implements WSManagementService {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(WSManagementServiceCxfImpl.class);
	/** Map containing all currently registered services and their endpoints. */
	private Map<WebService, Endpoint> services;
	/** OSGi Http service. */
	private HttpService httpService;
	/**
	 * List of web services that need to be registered as soon as the http
	 * service becomes availabel.
	 */
	private Set<WebService> awaitingRegistration;

	/**
	 * Constructor.
	 */
	public WSManagementServiceCxfImpl() {
		services = new HashMap<WebService, Endpoint>();
		awaitingRegistration = new HashSet<WebService>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.wsmanagement.WSManagementService#registerService(org.
	 * rifidi.edge.wsmanagement.WebService, java.util.Dictionary)
	 */
	@Override
	public void registerService(WebService webService,
			Dictionary<String, String> parameters) {
		logger.info("Registering " + webService.getService() + " to "
				+ webService.getUrl());
		synchronized (services) {
			// if no http service is available add to the list of waiting
			// services
			if (httpService == null) {
				awaitingRegistration.add(webService);
				return;
			}
			// already registered
			if (services.containsKey(webService)) {
				return;
			}

			Endpoint endpoint = Endpoint.publish(
					webService.getUrl().toString(), webService.getService());
			services.put(webService, endpoint);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.wsmanagement.WSManagementService#unregisterService(org
	 * .rifidi.edge.wsmanagement.WebService, java.util.Dictionary)
	 */
	@Override
	public void unregisterService(WebService webService,
			Dictionary<String, String> parameters) {
		logger.info("Unregistering " + webService.getService() + " from "
				+ webService.getUrl());
		synchronized (services) {
			if (!awaitingRegistration.isEmpty()) {
				awaitingRegistration.remove(webService);
				return;
			}
			services.remove(webService).stop();
		}
	}

	/**
	 * @param httpService
	 *            the httpService to set
	 */
	public void setHttpService(HttpService httpService) {
		synchronized (services) {
			this.httpService = httpService;
			// register the cxf servlet
			CXFNonSpringServlet servlet = new CXFNonSpringServlet();
			try {
				httpService.registerServlet("/", servlet, null, null);
			} catch (ServletException e) {
				logger.fatal("Unable to register servlet: " + e);
			} catch (NamespaceException e) {
				logger.fatal("Unable to register servlet: " + e);
			}
			// add services waiting for registration
			for (WebService service : awaitingRegistration) {
				registerService(service, null);
			}
			awaitingRegistration.clear();
		}
	}

	/**
	 * Set the initial list of web services.
	 */
	public void setServices(Set<WebService> services) {
		Set<WebService> servicesCopy = new HashSet<WebService>(services);
		for (WebService service : servicesCopy) {
			registerService(service, null);
		}
	}
}
