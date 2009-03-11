/**
 * 
 */
package org.rifidi.edge.ws.aleread;

import javax.xml.ws.Endpoint;

import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.osgi.service.http.HttpService;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ALEServicePortTypeImpl;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class WebServiceRegistrator {
	private HttpService httpService;

	/**
	 * @return the httpService
	 */
	public HttpService getHttpService() {
		return httpService;
	}

	/**
	 * @param httpService
	 *            the httpService to set
	 */
	public void setHttpService(HttpService httpService) {
		this.httpService = httpService;
		try {
			CXFNonSpringServlet servlet = new CXFNonSpringServlet();
			httpService.registerServlet("/", servlet, null, null);
			ALEServicePortTypeImpl ale = new ALEServicePortTypeImpl();
			// Endpoint.create(ale);
			Endpoint.publish("http://127.0.0.1:8081/miiii", ale);
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
	}

}
