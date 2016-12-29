package org.rifidi.edge.adapter.generic;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.routing.Router;
import org.restlet.util.Series;
import org.rifidi.edge.adapter.generic.dtos.PingDTO;

public class GenericRestletApplication extends Application {

	/** Logger */
	private final Log logger = LogFactory.getLog(getClass());
	private Boolean debugmode;

	public GenericRestletApplication(Boolean debugmode) {
		this.debugmode = debugmode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.restlet.Application#createInboundRoot()
	 */
	@Override
	public Restlet createInboundRoot() {
		return this.initRestlet();
	}

	public Router initRestlet() {
		final GenericRestletApplication self = this;

		Restlet ping = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				setResponseHeaders(request, response);

				PingDTO ping = new PingDTO();
				ping.setTimestamp(Long.toString(System.currentTimeMillis()));
				response.setEntity(self.generateReturnString(ping), MediaType.TEXT_XML);
			}
		};

		Router router = new Router(getContext().createChildContext());
		router.attach("/ping", ping);

		return router;
	}

	private void setResponseHeaders(Request request, Response response) {

		Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get("org.restlet.http.headers");

		if (responseHeaders == null) {
			responseHeaders = new Series(Header.class);
			response.getAttributes().put("org.restlet.http.headers", responseHeaders);
		}

		Reference hostRef = (request.getResourceRef().getBaseRef() != null) ? request.getResourceRef().getBaseRef()
				: request.getResourceRef();

		if (hostRef.getHostDomain() != null) {

			String host = hostRef.getHostDomain();
			int hostRefPortValue = hostRef.getHostPort();

			if ((hostRefPortValue != -1) && (hostRefPortValue != request.getProtocol().getDefaultPort())) {
				host = hostRef.getScheme() + "://" + host + ':' + hostRefPortValue;
			}

			// addHeader(HeaderConstants.HEADER_HOST, host, headers);
			responseHeaders.add(new Header("Access-Control-Expose-Headers", "Host"));
			responseHeaders.add(new Header("Host", host));
		}

		setCorsHeaders(response);
	}

	/**
	 * Allows Cross-Origin Resource Sharing (CORS)
	 * 
	 * @param response
	 *            the response to allow CORS
	 */
	private void setCorsHeaders(Response response) {

		Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get("org.restlet.http.headers");

		if (responseHeaders == null) {
			responseHeaders = new Series(Header.class);
			response.getAttributes().put("org.restlet.http.headers", responseHeaders);
		}

		responseHeaders.add(new Header("Access-Control-Allow-Headers",
				"Access-Control-Allow-Origin, Origin, X-Requested-With, Content-Type, Accept"));
		responseHeaders.add(new Header("Access-Control-Allow-Origin", "*"));
	}

	public String generateReturnString(Serializable message) {
		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(message.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(message, writer);
			String content = writer.toString();
			writer.close();
			return content;
		} catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}
}
