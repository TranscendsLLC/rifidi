/**
 * 
 */
package org.rifidi.edge.regression.createsession;

import java.io.IOException;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;
import org.junit.Test;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESCreateReaderSessionCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESDeleteReaderSessionCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESGetAllReaderSessionIDsCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESServerDescription;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetAvailableReaderPluginsCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetReaderInfoAnnotationCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetReaderPluginXMLCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPServerDescription;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * @author Kyle Neumeier
 * 
 */
public class CreateReaderSession {

	private static final String IP = "127.0.0.1";
	private static final int PORT = 1099;
	private static final ESServerDescription edgeserverSD = new ESServerDescription(
			IP, PORT);
	private static final RPServerDescription readerPluginsSD = new RPServerDescription(
			IP, PORT);
	private static Log logger = LogFactory.getLog(CreateReaderSession.class);
	private static String alienRI = "org.rifidi.edge.readerplugin.alien.AlienReaderInfo";

	@Test
	public void testCreateReaderSession() {
		// First test the getAllReaderSessions call
		getAllReaderConnections();

		// next make sure alien reader plugin is found
		getAvailableReaderPlugins();

		// get the readerPluginXML for the alien reader
		getReaderPluginXML();

		String readerInfoAnnotation = getReaderInfoAnnotation();

		ReaderInfoWrapper readerInfo = createReaderInfo(readerInfoAnnotation);

		long readerID = createReaderConnection(readerInfo);

		deleteReaderSession(readerID);

	}

	public void getAllReaderConnections() {
		ESGetAllReaderSessionIDsCall command = new ESGetAllReaderSessionIDsCall(
				edgeserverSD);
		try {
			Set<Long> ids = command.makeCall();
			if (ids.size() > 0) {
				Assert.fail("Edge server already has a reader session in it");
			} else {
				logger.info("getAllReaderSession() call success");
			}
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		} catch (RuntimeException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void getAvailableReaderPlugins() {
		RPGetAvailableReaderPluginsCall command = new RPGetAvailableReaderPluginsCall(
				readerPluginsSD);
		try {
			List<String> plugins = command.makeCall();
			if (!plugins.contains(alienRI)) {
				Assert
						.fail("Edge Server must have the alien reader plugin installed");
			} else {
				logger.info("getAvailableReaderPlugins success");
			}
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		} catch (RuntimeException e) {
			Assert.fail(e.getMessage());
		}
	}

	public void getReaderPluginXML() {
		RPGetReaderPluginXMLCall command = new RPGetReaderPluginXMLCall(
				readerPluginsSD, alienRI);
		try {
			ReaderPluginWrapper wrapper = command.makeCall();
			if (wrapper == null) {
				Assert.fail("readerPluginXML is null");
			} else {
				logger.info("getReaderPluginXMLCall success for alien reader");
			}
		} catch (RifidiReaderPluginXMLNotFoundException e) {
			Assert.fail(e.getMessage());
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		}
	}

	public String getReaderInfoAnnotation() {
		RPGetReaderInfoAnnotationCall command = new RPGetReaderInfoAnnotationCall(
				readerPluginsSD, alienRI);
		try {
			String readerInfoAnnotation = command.makeCall();
			if (readerInfoAnnotation == null) {
				Assert.fail("annotation is null");
			} else {
				return readerInfoAnnotation;
			}

		} catch (RifidiReaderInfoNotFoundException e) {
			Assert.fail(e.getMessage());
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		}
		return null;
	}

	public ReaderInfoWrapper createReaderInfo(String readerInfoAnnotation) {
		try {
			String riString = new AnnotationWrapper(readerInfoAnnotation)
					.buildDefaultReaderInfo();
			return new ReaderInfoWrapper(riString);
		} catch (IOException e) {
			Assert.fail(e.getMessage());
		} catch (JDOMException e) {
			Assert.fail(e.getMessage());
		}
		return null;
	}

	public long createReaderConnection(ReaderInfoWrapper readerInfo) {

		ESCreateReaderSessionCall command = new ESCreateReaderSessionCall(
				edgeserverSD, readerInfo);
		try {
			return command.makeCall();
		} catch (RifidiReaderInfoNotFoundException e) {
			Assert.fail(e.getMessage());
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		}
		return Long.MAX_VALUE;
	}

	/**
	 * @param readerID
	 */
	private void deleteReaderSession(long readerID) {

		ESDeleteReaderSessionCall call = new ESDeleteReaderSessionCall(
				edgeserverSD, readerID);
		try {
			call.makeCall();
		} catch (ServerUnavailable e) {
			logger.error(e.getMessage());
		} catch (RuntimeException e) {
			logger.error(e.getMessage());
		}
	}

}
