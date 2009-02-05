/* 
 *  GetTagReads.java
 *  Created:	Feb 3, 2009
 *  Project:	RiFidi org.rifidi.edge.regression
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.regression.gettagreads;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.JDOMException;
import org.junit.Test;
import org.rifidi.edge.core.api.exceptions.RifidiException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.exceptions.RifidiReaderPluginXMLNotFoundException;
import org.rifidi.edge.core.api.readerplugin.commands.CommandArgument;
import org.rifidi.edge.core.api.readerplugin.commands.CommandConfiguration;
import org.rifidi.edge.core.api.readersession.enums.CommandStatus;
import org.rifidi.edge.core.api.readersession.enums.ReaderSessionStatus;
import org.rifidi.edge.core.rmi.api.readerconnection.returnobjects.CommandInfo;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESCreateReaderSessionCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESDeleteReaderSessionCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESGetAllReaderSessionIDsCall;
import org.rifidi.edge.core.rmi.client.edgeserverstub.ESServerDescription;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetAvailableReaderPluginsCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetReaderInfoAnnotationCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPGetReaderPluginXMLCall;
import org.rifidi.edge.core.rmi.client.pluginstub.RPServerDescription;
import org.rifidi.edge.core.rmi.client.pluginstub.valueobjects.ReaderPluginWrapper;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionCommandStatusCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionEnableCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionExecuteCommandCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionGetStateCall;
import org.rifidi.edge.core.rmi.client.sessionstub.SessionServerDescription;
import org.rifidi.edge.core.rmi.client.sessionstub.valueobjects.ReaderInfoWrapper;
import org.rifidi.edge.regression.createsession.AnnotationWrapper;
import org.rifidi.rmi.utils.exceptions.ServerUnavailable;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class GetTagReads {
	private static final String SERVERIP = "127.0.0.1";
	private static final int SERVERPORT = 1099;
	private static final ESServerDescription edgeserverSD = new ESServerDescription(
			SERVERIP, SERVERPORT);
	private static final RPServerDescription readerPluginsSD = new RPServerDescription(
			SERVERIP, SERVERPORT);
	private static SessionServerDescription sessionSD = null;
	private static Log logger = LogFactory.getLog(GetTagReads.class);
	// private static String alienRI =
	// "org.rifidi.edge.readerplugin.alien.AlienReaderInfo";
	// private static String alienTagStreamCmdName = "AlienTagStreamCommand";

	private static String dummyRI = "org.rifidi.edge.readerplugin.dummy.plugin.DummyReaderInfo";
	private static String dummyTagStreamCmdName = "TagStreamCommand";

	private static HashSet<CommandArgument> comArgs = new HashSet<CommandArgument>();

	@Test
	public void testGetTagReads() {
		// First test the getAllReaderSessions call
		getAllReaderConnections();

		// next make sure alien reader plugin is found
		getAvailableReaderPlugins();

		// get the readerPluginXML for the alien reader
		getReaderPluginXML();

		String readerInfoAnnotation = getReaderInfoAnnotation();

		ReaderInfoWrapper readerInfo = createReaderInfo(readerInfoAnnotation);

		long id= createReaderConnection(readerInfo);

		getReaderState(ReaderSessionStatus.CONFIGURED.toString());

		enable();

		getReaderState(ReaderSessionStatus.OK.toString());

		commandStatus(CommandStatus.NOCOMMAND.toString());

		// CommandArgument comArg= new CommandArgument("TagType","ALL",false);
		// comArgs.add(comArg);
		// comArg=new CommandArgument("AntennaSequence","0",false);
		// comArgs.add(comArg);
		// comArg=new CommandArgument("PollPeriodInMillis","1000",false);
		// comArgs.add(comArg);
		CommandConfiguration commandConfiguration = new CommandConfiguration(
				dummyTagStreamCmdName, comArgs);

		executeCommand(commandConfiguration);

		commandStatus(CommandStatus.EXECUTING.toString());
		
		deleteReaderSession(id);

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
			if (!plugins.contains(dummyRI)) {
				Assert
						.fail("Edge Server must have the correct reader plugin installed");
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
				readerPluginsSD, dummyRI);
		try {
			ReaderPluginWrapper wrapper = command.makeCall();
			if (wrapper == null) {
				Assert.fail("readerPluginXML is null");
			} else {
				logger.info("getReaderPluginXMLCall success for reader");
			}
		} catch (RifidiReaderPluginXMLNotFoundException e) {
			Assert.fail(e.getMessage());
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		}
	}

	public String getReaderInfoAnnotation() {
		RPGetReaderInfoAnnotationCall command = new RPGetReaderInfoAnnotationCall(
				readerPluginsSD, dummyRI);
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
			long id = command.makeCall();
			sessionSD = new SessionServerDescription(SERVERIP, SERVERPORT, id);
			return id;
		} catch (RifidiReaderInfoNotFoundException e) {
			Assert.fail(e.getMessage());
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		}
		return Long.MAX_VALUE;
	}

	/**
	 * @return
	 */
	public String getReaderState(String desiredState) {
		SessionGetStateCall command = new SessionGetStateCall(sessionSD);

		try {
			String readerState = command.makeCall();
			if (!readerState.equals(desiredState)) {
				Assert.fail("reader state is " + readerState);
			} else {
				logger.info("reader is in state " + readerState + " - success");
			}
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		} catch (RuntimeException e) {
			Assert.fail(e.getMessage());
		}

		return null;
	}

	/**
	 * 
	 */
	public void enable() {
		SessionEnableCall call = new SessionEnableCall(sessionSD);
		try {
			call.makeCall();
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		}

	}

	/**
	 * @return
	 */
	public CommandInfo commandStatus(String desiredState) {
		SessionCommandStatusCall call = new SessionCommandStatusCall(sessionSD);
		try {
			CommandInfo info = call.makeCall();
			if (!info.getCommandStatus().toString().equals(desiredState)) {
				Assert.fail("command status is "
						+ info.getCommandStatus().toString());
			} else {
				logger.info("command status is "
						+ info.getCommandStatus().toString() + " - success");
			}

		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		} catch (RuntimeException e) {
			Assert.fail(e.getMessage());
		}
		return null;
	}

	/**
	 * @param commandConfiguration
	 */
	public void executeCommand(CommandConfiguration commandConfiguration) {
		SessionExecuteCommandCall call = new SessionExecuteCommandCall(
				sessionSD, commandConfiguration);

		try {
			call.makeCall();
		} catch (ServerUnavailable e) {
			Assert.fail(e.getMessage());
		} catch (RifidiException e) {
			Assert.fail(e.getMessage());
		}

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
