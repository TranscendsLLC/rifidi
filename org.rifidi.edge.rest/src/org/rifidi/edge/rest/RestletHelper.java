package org.rifidi.edge.rest;

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.concurrent.ConcurrentMap;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Header;
import org.restlet.data.Reference;
import org.restlet.util.Series;
import org.rifidi.edge.api.CommandConfigurationDTO;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.daos.CommandDAO;
import org.rifidi.edge.daos.ReaderDAO;

/**
 * Helper for the restlet class
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class RestletHelper {

	public static String HEADERS_KEY = "org.restlet.http.headers";
	
	private SensorManagerService sensorManagerService;
	private CommandManagerService commandManagerService;
	private ReaderDAO readerDAO;
	private CommandDAO commandDAO;
	
	
	/**
	 * 
	 * @param sensorManagerService
	 */
	public RestletHelper(SensorManagerService sensorManagerService, CommandManagerService commandManagerService, ReaderDAO readerDAO, CommandDAO commandDAO) {
		this.sensorManagerService = sensorManagerService;
		this.commandManagerService = commandManagerService;
		this.readerDAO = readerDAO;
		this.commandDAO = commandDAO;
	}
	
	/**
	 * The reader data access object
	 * @return
	 */
	public ReaderDAO getReaderDAO() {
		return readerDAO;
	}
	
	/**
	 * The command data access object
	 * @return
	 */
	public CommandDAO getCommandDAO() {
		return commandDAO;
	}



	public static final String SUCCESS_MESSAGE = "Success";
	public static final String FAIL_MESSAGE = "Fail";
	public static final String WARNING_STATE = "Warning";

	/**
	 * Generate the success message for the rest response
	 * 
	 * @return
	 */
	public RestResponseMessageDTO generateSuccessMessage() {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(SUCCESS_MESSAGE);
		return message;
	}
	
	/**
	 * Generates the error message for the rest response
	 * 
	 * @param description
	 * @param currentState
	 * @return
	 */
	public RestResponseMessageDTO generateErrorMessage(String description, String currentState) {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(FAIL_MESSAGE);
		message.setDescription(description);
		message.setState(currentState);
		return message;
	}
	
	/**
	 * Generates a warning message for the rest response
	 * 
	 * @param description
	 * @return
	 */
	public RestResponseMessageDTO generateWarningMessage(String description) {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(SUCCESS_MESSAGE);
		message.setDescription(description);
		message.setState(WARNING_STATE);
		return message;
	}
	
	/**
	 * Generate the return string for the rest response
	 * 
	 * @param message
	 * @return
	 */
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
	
	/**
	 * Checks if reader given by reader id exists
	 * 
	 * @param strReaderIdthe
	 *            reader id to check
	 */
	public boolean readerExists(String strReaderId) {

		boolean readerExists = false;

		ReaderDTO readerDTO = sensorManagerService.getReader(strReaderId);

		if (readerDTO != null) {

			readerExists = true;
		}

		return readerExists;

	}

	/**
	 * Checks is command given by command id exists
	 * 
	 * @param strCommandId
	 *            command id to check
	 * @throws Exception
	 *             if command with command id does not exist
	 */
	public boolean commandExists(String strCommandId) {

		boolean commandExists = false;

		CommandConfigurationDTO commandConfigurationDTO = commandManagerService.getCommandConfiguration(strCommandId);
		;

		if (commandConfigurationDTO != null) {

			commandExists = true;
		}

		return commandExists;

	}
	
	/**
	 * Processes a chain of semicolon separated properties and checks whether it
	 * is a well formed pair
	 * 
	 * @param propertiesChain
	 *            separated values of properties, for example:
	 *            (prop1=val2;prop2=val2;prop3=val3)
	 * @return AttributeList containing the attributes
	 * @throws Exception
	 *             if any property has no recognizable value
	 */
	public AttributeList getProcessedAttributes(String propertiesChain) throws Exception {

		AttributeList attributes = new AttributeList();

		// Check if propertiesChain has properties to process...
		if (propertiesChain != null && !propertiesChain.isEmpty()) {

			String[] splitProp = propertiesChain.split(";");

			for (String pair : splitProp) {

				String[] prop = pair.split("=");

				// check if property has a property and a value
				if (prop.length == 2) {

					// It has property and value
					attributes.add(new Attribute(prop[0], prop[1]));

				} else {

					// Property with no recognizable value, for example
					// Port=123=456, or Port,
					throw new Exception("Property with no recognizable value: " + prop[0]);

				}
			}

		}

		return attributes;
	}
	
	@SuppressWarnings("unchecked")
	static Series<Header> getMessageHeaders(Message message) {
		ConcurrentMap<String, Object> attrs = message.getAttributes();
		Series<Header> headers = (Series<Header>) attrs.get(HEADERS_KEY);
		if (headers == null) {
			headers = new Series<Header>(Header.class);
			Series<Header> prev = (Series<Header>) attrs.putIfAbsent(HEADERS_KEY, headers);
			if (prev != null) {
				headers = prev;
			}
		}
		return headers;
	}

	/**
	 * Allows Cross-Origin Resource Sharing (CORS)
	 * 
	 * @param response
	 *            the response to allow CORS
	 */
	@SuppressWarnings("unchecked")
	public void setCorsHeaders(Response response) {

		Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get("org.restlet.http.headers");

		if (responseHeaders == null) {
			responseHeaders = new Series<Header>(Header.class);
			response.getAttributes().put("org.restlet.http.headers", responseHeaders);
		}

		responseHeaders.add(new Header("Access-Control-Allow-Headers", "Access-Control-Allow-Origin, Origin, X-Requested-With, Content-Type, Accept"));
		responseHeaders.add(new Header("Access-Control-Allow-Origin", "*"));

	}

	/**
	 * Sets the response headers for the rest command
	 * 
	 * @param request
	 * @param response
	 */
	@SuppressWarnings("unchecked")
	public void setResponseHeaders(Request request, Response response) {

		Series<Header> responseHeaders = (Series<Header>) response.getAttributes().get("org.restlet.http.headers");

		if (responseHeaders == null) {
			responseHeaders = new Series<Header>(Header.class);
			response.getAttributes().put("org.restlet.http.headers", responseHeaders);
		}

		Reference hostRef = (request.getResourceRef().getBaseRef() != null) ? request.getResourceRef().getBaseRef() : request.getResourceRef();

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
}
