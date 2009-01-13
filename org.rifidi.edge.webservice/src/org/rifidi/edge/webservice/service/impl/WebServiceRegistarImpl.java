/*
 *  WebServiceRegistarImpl.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.webservice.service.impl;

import java.util.HashMap;

import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.osgi.service.http.HttpService;
import org.rifidi.edge.webservice.service.WebServiceRegistar;
import org.rifidi.edge.webservice.testservice.TestService;
import org.rifidi.services.annotations.Inject;
import org.rifidi.services.registry.ServiceRegistry;

/**
 * @author kyle
 * 
 */
public class WebServiceRegistarImpl implements WebServiceRegistar {

	private CXFNonSpringServlet servlet;
	
	private HashMap<String, Object> unpublishedServices = new HashMap<String, Object>();
	
	private static Log logger = LogFactory.getLog(WebServiceRegistarImpl.class);

	public WebServiceRegistarImpl() {
		ServiceRegistry.getInstance().service(this);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ws.service.WebServiceRegistar#registerWebService()
	 */
	@Override
	public void registerWebService(String serviceName, Object service) {
		synchronized (this) {
			if( serviceName.charAt(0)!='/'){
				serviceName = "/"+serviceName;
			}
			if (servlet != null) {
				try{
					Endpoint.create(service);
					Endpoint.publish(serviceName, service);
				}catch(Throwable e){
					logger.error("Cannot Publish " + service, e);
					unpublishedServices.put(serviceName, service);
				}
				
			}else{
				unpublishedServices.put(serviceName, service);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.ws.service.WebServiceRegistar#unregisterWebServic()
	 */
	@Override
	public void unregisterWebServic(String serviceName) {
	}

	@Inject
	public void setHTTPService(HttpService service) {
		servlet = new CXFNonSpringServlet();
		try {
			service.registerServlet("/", servlet, null, null);
		} catch(Exception e){
			e.printStackTrace();
		}
		registerWebService("/TestService", new TestService());
		
		for(String s : unpublishedServices.keySet()){
			registerWebService(s, unpublishedServices.get(s));
			unpublishedServices.remove(s);
		}

	}

}
