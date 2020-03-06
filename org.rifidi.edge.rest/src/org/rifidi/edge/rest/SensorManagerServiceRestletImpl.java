/*******************************************************************************
 * Copyright (c) 2014 Transcends, LLC.
 * This program is free software; you can redistribute it and/or modify it under the terms of the GNU 
 * General Public License as published by the Free Software Foundation; either version 2 of the 
 * License, or (at your option) any later version. This program is distributed in the hope that it will 
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY 
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details. You 
 * should have received a copy of the GNU General Public License along with this program; if not, 
 * write to the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA 02110-1301, 
 * USA. 
 * http://www.gnu.org/licenses/gpl-2.0.html
 *******************************************************************************/
package org.rifidi.edge.rest;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.restlet.Application;
import org.restlet.Message;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.Header;
import org.restlet.data.MediaType;
import org.restlet.data.Reference;
import org.restlet.resource.Directory;
import org.restlet.resource.Get;
import org.restlet.resource.Post;
import org.restlet.routing.Router;
import org.restlet.util.Series;
import org.rifidi.edge.adapter.llrp.LLRPEncodeMessageDto;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;
import org.rifidi.edge.api.CommandConfigFactoryDTO;
import org.rifidi.edge.api.CommandConfigurationDTO;
import org.rifidi.edge.api.CommandDTO;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.ReaderFactoryDTO;
import org.rifidi.edge.api.RifidiApp;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.service.appmanager.AppManager;
import org.rifidi.edge.api.service.tagmonitor.RawTagMonitoringService;
import org.rifidi.edge.api.service.tagmonitor.RawTagSubscriber;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.Configuration;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.daos.CommandDAO;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.notification.StandardTagReadEventFieldNames;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.rest.exception.NotValidPropertyForObjectException;
import org.rifidi.edge.sensors.AbstractGPIOService;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.AbstractSensorFactory;
import org.rifidi.edge.sensors.CannotExecuteException;
import org.rifidi.edge.sensors.SensorSession;
import org.rifidi.edge.services.EsperManagementService;
import org.rifidi.edge.services.ProvisioningService;
import org.rifidi.edge.util.RifidiEdgeHelper;

//import org.rifidi.edge.adapter.llrp.LLRPEncodeMessageDto;

/**
 * This class handles the incoming rest requests.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class SensorManagerServiceRestletImpl extends Application {

	public static String HEADERS_KEY = "org.restlet.http.headers";

	public static final String SUCCESS_MESSAGE = "Success";

	public static final String FAIL_MESSAGE = "Fail";

	public static final String WARNING_STATE = "Warning";

	public static final String[] LlrpReaderTypeArrray = new String[] { "LLRP" };

	public static final String ReaderIDPropertyName = "readerID";

	public static final String[] ReadZoneRequiredProperties = new String[] { ReaderIDPropertyName };

	public static final String[] ReadZoneValidProperties = new String[] { ReaderIDPropertyName, "antennas",
			"tagPattern", "matchPattern" };

	public enum LLRPGetOperations {
		GET_ROSPECS, GET_READER_CONFIG, GET_READER_CAPABILITIES
	}

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorManagerService;

	/** The command manager service for command commands */
	public CommandManagerService commandManagerService;

	/** The Provisioning Service */
	private volatile ProvisioningService provisioningService;

	/** Configuration Service */
	private volatile ConfigurationService configService;

	/** Esper service */
	private EsperManagementService esperService;

	/** GPIO Service */
	private final Set<AbstractGPIOService<?>> gpioServiceList = new CopyOnWriteArraySet<AbstractGPIOService<?>>();

	/**  */
	public AppManager appManager;

	/** */
	public ReaderDAO readerDAO;

	public CommandDAO commandDAO;

	private RawTagMonitoringService rawTagMonitoringService;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public Restlet createInboundRoot() {
		return this.initRestlet();
	}

	private void llrpGetOperation(Request request, Response response, LLRPGetOperations operation) {
		LLRPReaderSession session = null;

		try {
			String strReaderId = (String) request.getAttributes().get("readerID");

			String strSessionID = (String) request.getAttributes().get("sessionID");

			// Check if reader id exists
			if (!readerExists(strReaderId)) {
				throw new Exception("Reader with id " + strReaderId + " does not exist");
			}

			SessionStatus checkSessionState = sensorManagerService.getSession(strReaderId, strSessionID).getStatus();

			AbstractSensor<?> sensor = readerDAO.getReaderByID(strReaderId);

			// look up the associated service configuration object
			Configuration config = configService.getConfiguration(strReaderId);

			// Check if reader id is a LLRP reader type
			String strCurrentReaderType = sensor.getDTO(config).getReaderFactoryID();
			boolean isLlrpReaderType = false;

			// Iterate over defined llrp reader types
			for (int i = 0; i < LlrpReaderTypeArrray.length; i++) {

				// If current reader type matches a defined llrp reader
				// type
				if (strCurrentReaderType.equals(LlrpReaderTypeArrray[i])) {
					isLlrpReaderType = true;
					break;
				}
			}

			if (!isLlrpReaderType) {
				// It is not an llrp reader type
				throw new Exception("Reader with id " + strReaderId + " of type " + strCurrentReaderType
						+ " is not an LLRP reader type");
			}

			Map<String, SensorSession> sessionMap = sensor.getSensorSessions();

			// Check if session id exists
			if (sessionMap.containsKey(strSessionID)) {
				if (checkSessionState.equals(SessionStatus.PROCESSING)) {
					session = (LLRPReaderSession) sessionMap.get(strSessionID);

					String returnXML = "";

					if (operation.equals(LLRPGetOperations.GET_ROSPECS)) {
						returnXML = session.getRospecs();
					} else if (operation.equals(LLRPGetOperations.GET_READER_CONFIG)) {
						returnXML = session.getReaderConfig();
					} else if (operation.equals(LLRPGetOperations.GET_READER_CAPABILITIES)) {
						returnXML = session.getReaderCapabilities();
					} else {
						throw new Exception("Operation with code " + operation + " is invalid. ");
					}

					response.setEntity(returnXML, MediaType.TEXT_XML);
				} else {
					throw new Exception("Session with id " + strSessionID + " is not in the PROCESSING state. ");
				}
			} else {
				// Session id does not exist
				throw new Exception(
						"Session with id " + strSessionID + " does not exist for reader with id " + strReaderId);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			response.setEntity(this.generateReturnString(this.generateErrorMessage(e.getMessage(), null)),
					MediaType.TEXT_XML);
		} finally {
			// cleanup session
			if (session != null) {
				session.cleanupSession();
			}
		}
	}

	private void executeLlrpOperation(Request request, Response response,
			LLRPReaderSession.LLRP_OPERATION_CODE operationCode) {

		LLRPReaderSession session = null;

		try {

			// Variable to receive synchronous response if set
			LLRPEncodeMessageDto llrpEncodeMessageDto = new LLRPEncodeMessageDto();
			// llrpEncodeMessageDto.setStatus("Success !!!");

			String strReaderId = (String) request.getAttributes().get("readerID");

			Object objSessionId = request.getAttributes().get("sessionID");

			// Check if reader id exists
			if (!readerExists(strReaderId)) {
				throw new Exception("Reader with id " + strReaderId + " does not exist");
			}

			AbstractSensor<?> sensor = readerDAO.getReaderByID(strReaderId);

			// look up the associated service configuration object
			Configuration config = configService.getConfiguration(strReaderId);

			// Check if reader id is a LLRP reader type
			String strCurrentReaderType = sensor.getDTO(config).getReaderFactoryID();
			boolean isLlrpReaderType = false;

			// Iterate over defined llrp reader types
			for (int i = 0; i < LlrpReaderTypeArrray.length; i++) {

				// If current reader type matches a defined llrp reader
				// type
				if (strCurrentReaderType.equals(LlrpReaderTypeArrray[i])) {

					isLlrpReaderType = true;
					break;
				}
			}

			if (!isLlrpReaderType) {

				// It is not an llrp reader type
				throw new Exception("Reader with id " + strReaderId + " of type " + strCurrentReaderType
						+ " is not an LLRP reader type");

			}

			Map<String, SensorSession> sessionMap = sensor.getSensorSessions();

			// Check if session id exists
			if (sessionMap.containsKey(objSessionId)) {

				session = (LLRPReaderSession) sessionMap.get(objSessionId);

				// Check if session is processing or not
				if (!session.getStatus().equals(SessionStatus.PROCESSING)) {
					throw new Exception("Session with id " + objSessionId + " of reader with id " + strReaderId
							+ " is not in the processing state.");
				}

				// Validate no current operations on session are
				// running, and response to user if so
				if (session.isRunningLLRPEncoding()) {

					throw new Exception("Session with id " + objSessionId + " of reader with id " + strReaderId
							+ " is currently in the middle of encoding operations. Try again in a while");

				}

				// Check if there is more than one tag in the scope
				// of this reader, if so then fail
				int numberOfTags = session.numTagsOnLLRP().intValue();
				boolean thereIsOneTag = (numberOfTags == 1);

				if (!thereIsOneTag) {

					if (numberOfTags < 1) {

						// There is no tag in the scope of the reader
						throw new Exception("There is no tag in the scope of reader with id " + strReaderId);
					} else {

						throw new Exception("There are " + numberOfTags + " tags in the scope of the reader with id "
								+ strReaderId);

					}

				}

				// Set the block length of data to be written on this reader'
				// session
				session.setWriteDataBlockLength(4);

				// There is only one tag in the scope of this reader
				// for session object
				// Get jvm properties
				setLlrpEncodeJvmProperties(session);

				if (operationCode == null) {

					// Try an access password read to see if tag is not yet
					// encoded
					// Hold a reference to boolean value indicating if session
					// is executing in asynchronous mode
					boolean operationExecuteMode = session.isExecuteOperationsInAsynchronousMode();

					// Force session to execute in synchronous mode
					session.setExecuteOperationsInAsynchronousMode(false);

					// Check access password read before the encode operation
					llrpEncodeMessageDto = session.llrpReadAccessPasswordOperation();

					if (!llrpEncodeMessageDto.getStatus().equals("Success")) {
						throw new Exception(
								"Given access password does not match expected access password - possibly old password is wrong or tag password has been changed via a previous encoding operation");
					}

					// Set the initial operation execute mode
					session.setExecuteOperationsInAsynchronousMode(operationExecuteMode);

					// There is no operation code, so we submit the complete
					// encode operation

					// Get the tag id from url
					String strTag = (String) request.getAttributes().get("tag");

					checkBlockLengthReminder(strTag, session.getWriteDataBlockLength(), "tag");

					llrpEncodeMessageDto = session.llrpEncode(strTag);

				} else {

					// Single shot operation, according to operation code

					// Get the properties
					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					// Validate that if there are parameters, they are well pair
					// formed values
					AttributeList attributes = getProcessedAttributes(strPropAttr);

					if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCWrite)) {

						// check the required properties for epc write
						// operation, and overwrite the properties got from jvm

						// check for accesspwd and tag
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						String strTag = (String) getAttributeValue(attributes, "tag");
						checkBlockLengthReminder(strTag, session.getWriteDataBlockLength(), "tag");

						llrpEncodeMessageDto = session.llrpWriteEpcOperation(strTag);

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordWrite)) {

						// check for oldaccesspwd and accesspwd
						String oldaccesspwd = (String) getAttributeValue(attributes, "oldaccesspwd");
						validatePassword(oldaccesspwd, "Old access");

						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");

						session.setOldAccessPwd(oldaccesspwd);
						session.setAccessPwd(accesspwd);

						llrpEncodeMessageDto = session.llrpWriteAccessPasswordOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordWrite)) {

						// check for accesspwd and kill password
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						String killpwd = (String) getAttributeValue(attributes, "killpwd");
						validatePassword(killpwd, "Kill");
						session.setKillPwd(killpwd);

						llrpEncodeMessageDto = session.llrpWriteKillPasswordOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCLock)) {

						// check for accesspwd
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						// check for privilege type
						String strPrivilege = (String) getAttributeValue(attributes, "privilege");

						int intEpcLockPrivilege = LLRPReaderSession.getLockPrivilege(strPrivilege);
						session.setEpcLockPrivilege(intEpcLockPrivilege);

						llrpEncodeMessageDto = session.llrpLockEpcOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordLock)) {

						// check for accesspwd
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						// check for privilege type
						String strPrivilege = (String) getAttributeValue(attributes, "privilege");

						int intAccessPwdLockPrivilege = LLRPReaderSession.getLockPrivilege(strPrivilege);
						session.setAccessPwdLockPrivilege(intAccessPwdLockPrivilege);

						llrpEncodeMessageDto = session.llrpLockAccessPasswordOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordLock)) {

						// check for accesspwd
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						// check for privilege type
						String strPrivilege = (String) getAttributeValue(attributes, "privilege");

						int intKillPwdLockPrivilege = LLRPReaderSession.getLockPrivilege(strPrivilege);
						session.setKillPwdLockPrivilege(intKillPwdLockPrivilege);

						llrpEncodeMessageDto = session.llrpLockKillPasswordOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryLock)) {

						// check for accesspwd
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						// check for privilege type
						String strPrivilege = (String) getAttributeValue(attributes, "privilege");

						int intUserMemoryLockPrivilege = LLRPReaderSession.getLockPrivilege(strPrivilege);
						session.setUserMemoryLockPrivilege(intUserMemoryLockPrivilege);

						llrpEncodeMessageDto = session.llrpLockUserMemoryOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCRead)) {

						// check the required properties for epc read
						// operation, and overwrite the properties got from jvm

						// check for accesspwd and tag
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						// check for wordCount
						String strWordCount = (String) getNonRequiredAttributeValue(attributes, "wordCount");
						if (strWordCount != null && !strWordCount.isEmpty()) {

							session.setWordCount(Integer.parseInt(strWordCount));

						} else {

							// Set default word count value
							session.setWordCount(LLRPReaderSession.DEFAULT_EPC_WORD_COUNT);
						}

						llrpEncodeMessageDto = session.llrpReadEpcOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordValidate)) {

						// check the required properties for access password
						// read
						// operation, and overwrite the properties got from jvm

						// check for accesspwd and tag
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setOldAccessPwd(accesspwd);

						llrpEncodeMessageDto = session.llrpReadAccessPasswordOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordRead)) {

						// check the required properties for kill password read
						// operation, and overwrite the properties got from jvm

						// check for accesspwd and tag
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setOldAccessPwd(accesspwd);

						llrpEncodeMessageDto = session.llrpReadKillPasswordOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryRead)) {

						// check the required properties for user memory read
						// operation, and overwrite the properties got from jvm

						// check for accesspwd
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						// check for wordCount
						String strWordCount = (String) getNonRequiredAttributeValue(attributes, "wordCount");
						if (strWordCount != null && !strWordCount.isEmpty()) {

							session.setWordCount(Integer.parseInt(strWordCount));

						} else {

							// Set default user memory word count value
							session.setWordCount(LLRPReaderSession.DEFAULT_USER_MEMORY_WORD_COUNT);
						}

						llrpEncodeMessageDto = session.llrpReadUserMemoryOperation();

					} else if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryWrite)) {

						// check the required properties for user memory read
						// operation, and overwrite the properties got from jvm

						// check for accesspwd
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						validatePassword(accesspwd, "Access");
						session.setAccessPwd(accesspwd);

						String strData = (String) getAttributeValue(attributes, "data");
						checkBlockLengthReminder(strData, session.getWriteDataBlockLength(), "data");

						session.setUserMemoryData(strData);

						llrpEncodeMessageDto = session.llrpWriteUserMemoryOperation();

					} else {

						throw new Exception("Operation with code " + operationCode + " is invalid. ");

					}

				}

				/*
				 * TODO delete session.addAccessSpec((String) request.getAttributes()
				 * .get("password"), (String) request .getAttributes().get("tag"));
				 */

			} else {

				// Session id does not exist
				throw new Exception(
						"Session with id " + objSessionId + " does not exist for reader with id " + strReaderId);
			}

			// response.setEntity(self.generateReturnString(self
			// .generateSuccessMessage()), MediaType.TEXT_XML);

			response.setEntity(this.generateReturnString(llrpEncodeMessageDto), MediaType.TEXT_XML);

		} catch (Exception e) {
			// test ini
			// LLRPEncodeMessageDto llrpEncodeMessageDto = new
			// LLRPEncodeMessageDto();
			// llrpEncodeMessageDto.setStatus(FAIL_MESSAGE);
			// response.setEntity(self.generateReturnString(llrpEncodeMessageDto),
			// MediaType.TEXT_XML);
			// test end

			e.printStackTrace();

			response.setEntity(this.generateReturnString(this.generateErrorMessage(e.getMessage(), null)),
					MediaType.TEXT_XML);
		} finally {
			// cleanup session
			if (session != null) {
				session.cleanupSession();
			}
		}

	}

	public Router initRestlet() {

		final SensorManagerServiceRestletImpl self = this;

		Restlet readers = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("readers requested");
				setResponseHeaders(request, response);

				Set<ReaderDTO> dtos = sensorManagerService.getReaders();
				List<ReaderNameDTO> rnd = new LinkedList<ReaderNameDTO>();
				for (ReaderDTO dto : dtos) {
					ReaderNameDTO r = new ReaderNameDTO();
					r.setReaderID(dto.getReaderID());
					r.setReaderType(dto.getReaderFactoryID());
					rnd.add(r);
				}
				ReaderResponseMessageDTO rrmd = new ReaderResponseMessageDTO();
				rrmd.setReaders(rnd);
				response.setEntity(self.generateReturnString(rrmd), MediaType.TEXT_XML);
			}
		};

		Restlet commands = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("commands requested");
				setResponseHeaders(request, response);

				Set<CommandConfigurationDTO> dtos = commandManagerService.getCommands();
				List<CommandNameDTO> cnd = new LinkedList<CommandNameDTO>();
				for (CommandConfigurationDTO dto : dtos) {
					CommandNameDTO c = new CommandNameDTO();
					c.setCommandID(dto.getCommandConfigID());
					c.setCommandType(dto.getCommandConfigFactoryID());
					cnd.add(c);
				}
				CommandResponseMessageDTO crmd = new CommandResponseMessageDTO();
				crmd.setCommands(cnd);
				response.setEntity(self.generateReturnString(crmd), MediaType.TEXT_XML);
			}
		};

		Restlet readerStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("readerStatus requested");
				setResponseHeaders(request, response);

				Set<ReaderDTO> dtos = sensorManagerService.getReaders();
				ReaderStatusResponseMessageDTO rsrmd = new ReaderStatusResponseMessageDTO();
				boolean readerFound = false;
				try {
					for (ReaderDTO dto : dtos) {
						if (dto.getReaderID().equals(request.getAttributes().get("readerID"))) {
							readerFound = true;
							ReaderNameDTO r = new ReaderNameDTO();
							r.setReaderID(dto.getReaderID());
							r.setReaderType(dto.getReaderFactoryID());
							rsrmd.setReader(r);
							List<SessionNameDTO> slist = new LinkedList<SessionNameDTO>();
							for (SessionDTO sdto : dto.getSessions()) {
								SessionNameDTO snd = new SessionNameDTO();
								snd.setSessionId(sdto.getID());
								snd.setSessionStatus(sdto.getStatus().toString());
								List<ExecutingCommandDTO> exec = new ArrayList<ExecutingCommandDTO>();
								for (CommandDTO command : sdto.getCommands()) {
									ExecutingCommandDTO ecdto = new ExecutingCommandDTO();
									ecdto.setCommandID(command.getCommandID());
									Set<CommandConfigurationDTO> configdtos = commandManagerService.getCommands();
									for (CommandConfigurationDTO configdto : configdtos) {
										if (configdto.getCommandConfigID().equals(command.getCommandID())) {
											ecdto.setFactoryID(configdto.getCommandConfigFactoryID());
										}
									}
									ecdto.setInterval(command.getInterval());
									exec.add(ecdto);
								}
								snd.setExecutingCommands(exec);
								slist.add(snd);
							}
							rsrmd.setSessions(slist);
							response.setEntity(self.generateReturnString(rsrmd), MediaType.TEXT_XML);
							break;
						}
					}
					if (!readerFound) {
						throw new Exception("No reader with ID " + request.getAttributes().get("readerID") + " found.");
					}
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet startSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("startSession requested");
					setResponseHeaders(request, response);

					String strReaderId = (String) request.getAttributes().get("readerID");
					String strSessionID = (String) request.getAttributes().get("sessionID");

					SessionStatus checkSessionState = sensorManagerService.getSession(strReaderId, strSessionID)
							.getStatus();

					if (!checkSessionState.equals(SessionStatus.PROCESSING)
							&& !checkSessionState.equals(SessionStatus.CONNECTING)) {
						sensorManagerService.startSession(strReaderId, strSessionID);

						SessionStatus currentSessionState = checkSessionState(strReaderId, strSessionID,
								SessionStatus.PROCESSING);

						if (currentSessionState.equals(SessionStatus.PROCESSING)) {

							// Generate a success message
							response.setEntity(self.generateReturnString(self.generateSuccessMessage()),
									MediaType.TEXT_XML);

						} else {

							// Generate a failure message with
							// currentSessionStatus
							response.setEntity(self.generateReturnString(self.generateErrorMessage(
									"Session already started, current state is " + currentSessionState
											+ "  - See Rifidi Edge Sever Log for details",
									currentSessionState.toString())), MediaType.TEXT_XML);

						}
					} else {
						response.setEntity(self.generateReturnString(self.generateErrorMessage(
								"Unable to start session, current state is " + checkSessionState,
								checkSessionState.toString())), MediaType.TEXT_XML);
					}
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}

		};
		Restlet stopSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("stopSession requested");
					setResponseHeaders(request, response);

					String strReaderId = (String) request.getAttributes().get("readerID");
					String strSessionID = (String) request.getAttributes().get("sessionID");

					sensorManagerService.stopSession(strReaderId, strSessionID);

					SessionStatus currentSessionState = checkSessionState(strReaderId, strSessionID,
							SessionStatus.CLOSED);

					if (currentSessionState.equals(SessionStatus.CLOSED)) {

						// Generate a success message
						response.setEntity(self.generateReturnString(self.generateSuccessMessage()),
								MediaType.TEXT_XML);

					} else {

						// Generate a failure message with currentSessionStatus
						response.setEntity(
								self.generateReturnString(self.generateErrorMessage(
										"Unable to stop session, current state is " + currentSessionState
												+ "  - See Rifidi Edge Sever Log for details",
										currentSessionState.toString())),
								MediaType.TEXT_XML);

					}
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet createSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("createSession requested");
					String readerID = (String) request.getAttributes().get("readerID");
					List<SessionDTO> sessions = sensorManagerService.getReader(readerID).getSessions();
					// only one session allowed.
					if (!sessions.isEmpty()) {
						throw new Exception("Reader " + readerID + " already has a session.");
					}
					setResponseHeaders(request, response);

					sensorManagerService.createSession(readerID);
					CreateSessionResponseMessageDTO sr = new CreateSessionResponseMessageDTO();
					sr.setSessionID(1); // The sessionID for any reader should
										// be 1.
					response.setEntity(self.generateReturnString(sr), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet deleteSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("deleteSession requested");
					setResponseHeaders(request, response);
					sensorManagerService.deleteSession((String) request.getAttributes().get("readerID"),
							(String) request.getAttributes().get("sessionID"));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet resetSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("resetSession requested");
					String readerID = (String) request.getAttributes().get("readerID");
					String sessionID = (String) request.getAttributes().get("sessionID");

					setResponseHeaders(request, response);

					// Get the current commands
					List<CommandDTO> commands = sensorManagerService.getSession(readerID, sessionID).getCommands();

					// Delete the session
					sensorManagerService.deleteSession(readerID, sessionID);

					// Recreate the session
					sensorManagerService.createSession(readerID);

					// Re-execute commands
					for (CommandDTO command : commands) {
						sensorManagerService.submitCommand(readerID, sessionID, command.getCommandID(),
								command.getInterval(), command.getTimeUnit());
					}

					// Start the session
					sensorManagerService.startSession(readerID, sessionID);

					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet deleteReader = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("deleteReader requested");
					setResponseHeaders(request, response);
					sensorManagerService.deleteReader((String) request.getAttributes().get("readerID"));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet deleteCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("deleteCommand requested");
					setResponseHeaders(request, response);
					commandManagerService.deleteCommand((String) request.getAttributes().get("commandID"));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet executeCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("executeCommand requested");
					setResponseHeaders(request, response);

					sensorManagerService.submitCommand((String) request.getAttributes().get("readerID"),
							(String) request.getAttributes().get("sessionID"),
							(String) request.getAttributes().get("commandID"),
							Long.parseLong((String) request.getAttributes().get("repeatInterval")),
							TimeUnit.MILLISECONDS);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (NumberFormatException nEx) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(nEx.getMessage(), null)),
							MediaType.TEXT_XML);
				} catch (CommandSubmissionException cEx) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(cEx.getMessage(), null)),
							MediaType.TEXT_XML);

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}

			}
		};
		Restlet setProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("setProperties requested");
				try {

					setResponseHeaders(request, response);

					String strObjectId = (String) request.getAttributes().get("readerID");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					boolean readerExists = readerExists(strObjectId);
					boolean commandExists = commandExists(strObjectId);

					// Check if command or reader exists before submit
					// getproperties
					if (!commandExists && !readerExists) {
						throw new Exception("Neither reader nor command with id " + strObjectId + " exist");
					}

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					// Check if reader id exists
					if (readerExists) {

						// Set properties for reader, if parameter is a reader
						// id
						sensorManagerService.setReaderProperties(strObjectId, attributes);

					} else if (commandExists) { // Check if command exists

						// Set properties for command, if parameter is a command
						// id
						commandManagerService.setCommandProperties(strObjectId, attributes);
					}

					// Validate properties for this reader or command
					validateAttributesForReaderOrCommand(strObjectId, attributes);

					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);

				} catch (NotValidPropertyForObjectException nEx) {

					response.setEntity(self.generateReturnString(self.generateWarningMessage(nEx.getMessage())),
							MediaType.TEXT_XML);

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet getProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("getProperties requested");
				try {

					setResponseHeaders(request, response);

					String strObjectId = (String) request.getAttributes().get("readerID");

					// Check if reader or command exists before submit
					// getproperties
					if (!commandExists(strObjectId) && !readerExists(strObjectId)) {
						throw new Exception("Neither reader nor command with id " + strObjectId + " exist");
					}

					Configuration configuration = configService.getConfiguration(strObjectId);
					PropertyResponseMessageDTO prmd = new PropertyResponseMessageDTO();
					List<PropertyNameDTO> pndList = new LinkedList<PropertyNameDTO>();
					for (String key : configuration.getAttributes().keySet()) {
						PropertyNameDTO pnd = new PropertyNameDTO();
						pnd.setPropertyName(key);
						if (configuration.getAttributes().get(key) != null) {
							pnd.setPropertyValue(configuration.getAttributes().get(key).toString());
						} else {
							pnd.setPropertyValue("null");
						}
						pndList.add(pnd);
					}
					prmd.setProperties(pndList);
					// System.out.println(self.generateReturnString(prmd)
					// .toString());
					response.setEntity(self.generateReturnString(prmd).toString(), MediaType.TEXT_XML);

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet createReader = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("createReader requested");

				String readerId = null;

				try {

					setResponseHeaders(request, response);

					String strReaderType = (String) request.getAttributes().get("readerType");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					AttributeList attributes = new AttributeList();
					if (strPropAttr != null) {
						strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

						attributes = getProcessedAttributes(strPropAttr);

						// From attributes, extract the readerID property if is
						// there
						Attribute attributeToDelete = null;
						for (Attribute attribute : attributes.asList()) {
							if (attribute.getName().equals(ReaderIDPropertyName)) {
								readerId = (String) attribute.getValue();

								// remove readerID from attributes, so when the
								// time
								// to validate the properties for reader comes,
								// it does
								// not throw error because of this readerID
								// property name
								// Hold a reference of attribute to be deleted,
								// to delete
								// outside this loop, because if try to delete
								// inside,
								// we get an ConcurrentModificationException
								attributeToDelete = attribute;
							}
						}

						if (attributeToDelete != null) {
							attributes.remove(attributeToDelete);
						}
					}

					// Create reader
					if (readerId == null) {
						// no reader id is provided, so call the default create
						// method
						readerId = sensorManagerService.createReader(strReaderType, attributes);
					} else {
						// reader id is provided
						readerId = sensorManagerService.createReader(strReaderType, attributes, readerId);
					}

					// Validate what properties are wrong for this created
					// reader
					validateAttributesForReaderOrCommand(readerId, attributes);

					CreateReaderResponseMessageDTO crmd = new CreateReaderResponseMessageDTO();
					crmd.setReaderID(readerId);

					response.setEntity(self.generateReturnString(crmd), MediaType.TEXT_XML);

				} catch (NotValidPropertyForObjectException nE) {

					// If there was an invalid property creating the reader,
					// and the reader was created, the reader must be deleted
					if (readerId != null) {
						sensorManagerService.deleteReader(readerId);
					}

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet createCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("createCommand requested");
				String strCommandId = null;

				try {

					setResponseHeaders(request, response);

					String strPropAttr = (String) request.getAttributes().get("properties");

					AttributeList attributes = new AttributeList();

					if (strPropAttr != null) {
						// Decode url attributes
						strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

						attributes = getProcessedAttributes(strPropAttr);
					}

					// Create the command
					strCommandId = self.commandManagerService
							.createCommand((String) request.getAttributes().get("commandType"), attributes);

					// Validate properties for this command
					validateAttributesForReaderOrCommand(strCommandId, attributes);

					CreateCommandResponseMessageDTO ccrmd = new CreateCommandResponseMessageDTO();
					ccrmd.setCommandID(strCommandId);

					response.setEntity(self.generateReturnString(ccrmd), MediaType.TEXT_XML);

				} catch (NotValidPropertyForObjectException nE) {

					// If there was an invalid property creating the command,
					// and the command was created, the command must be deleted
					if (strCommandId != null) {
						commandManagerService.deleteCommand(strCommandId);
					}

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet startApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("startApp requested");
				try {

					setResponseHeaders(request, response);

					appManager.startApp((Integer.parseInt((String) request.getAttributes().get("appID"))));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet stopApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("stopApp requested");
				try {

					setResponseHeaders(request, response);

					appManager.stopApp((Integer.parseInt((String) request.getAttributes().get("appID"))));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet readerTypes = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("readerTypes requested");
				try {

					setResponseHeaders(request, response);

					ReaderTypesReponseMessageDTO rtr = new ReaderTypesReponseMessageDTO();
					Set<ReaderFactoryDTO> grf = self.sensorManagerService.getReaderFactories();
					List<ReaderTypeDTO> ret = new LinkedList<ReaderTypeDTO>();
					for (ReaderFactoryDTO rfd : grf) {
						ReaderTypeDTO rtd = new ReaderTypeDTO();
						rtd.setReaderDesc(rfd.getReaderFactoryDescription());
						rtd.setReaderType(rfd.getReaderFactoryID());
						ret.add(rtd);
					}
					rtr.setSensors(ret);
					response.setEntity(self.generateReturnString(rtr).toString(), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet readerMetadata = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("readerMetadata requested");
				try {

					setResponseHeaders(request, response);

					ReaderMetadataResponseMessageDTO rmrmd = new ReaderMetadataResponseMessageDTO();

					String cat_str = "org.rifidi.edge.category";
					String order_value = "org.rifidi.edge.ordervalue";
					String display_name = "displayName";

					List<ReaderFactoryMetadataDTO> readerFactoryMetadataList = new ArrayList<ReaderFactoryMetadataDTO>();
					Set<AbstractSensorFactory<?>> readerfactories = self.readerDAO.getReaderFactories();
					for (AbstractSensorFactory<?> factory : readerfactories) {
						List<ReaderMetadataDTO> readerMetadataList = new ArrayList<ReaderMetadataDTO>();
						MBeanAttributeInfo[] attArray = factory.getServiceDescription(factory.getFactoryID())
								.getAttributes();
						for (MBeanAttributeInfo att : attArray) {
							ReaderMetadataDTO readerMetadata = new ReaderMetadataDTO();
							OpenMBeanAttributeInfoSupport supp = (OpenMBeanAttributeInfoSupport) att;
							if (supp.getDefaultValue() != null) {
								readerMetadata.setDefaultValue(supp.getDefaultValue().toString());
							}
							if (supp.getDescription() != null) {
								readerMetadata.setDescription(supp.getDescription());
							}
							if (supp.getMaxValue() != null) {
								readerMetadata.setMaxValue(supp.getMaxValue().toString());
							}
							if (supp.getMinValue() != null) {
								readerMetadata.setMinValue(supp.getMinValue().toString());
							}
							if (supp.getType() != null) {
								readerMetadata.setType(supp.getType());
							}
							if (supp.getName() != null) {
								readerMetadata.setName(supp.getName());
							}
							if (supp.getDescriptor().getFieldValue(cat_str) != null) {
								readerMetadata.setCategory((String) supp.getDescriptor().getFieldValue(cat_str));
							}
							if (supp.getDescriptor().getFieldValue(order_value) != null) {
								readerMetadata.setOrderValue((Float) supp.getDescriptor().getFieldValue(order_value));
							}
							if (supp.getDescriptor().getFieldValue(display_name) != null) {
								readerMetadata
										.setDisplayName((String) supp.getDescriptor().getFieldValue(display_name));
							}
							readerMetadata.setWritable(supp.isWritable());

							readerMetadataList.add(readerMetadata);
						}

						ReaderFactoryMetadataDTO readerMetaFactory = new ReaderFactoryMetadataDTO();
						readerMetaFactory.setReadermetadata(readerMetadataList);
						readerMetaFactory.setId(factory.getFactoryID());
						readerFactoryMetadataList.add(readerMetaFactory);
					}

					List<CommandFactoryMetadataDTO> commandFactoryMetadataList = new ArrayList<CommandFactoryMetadataDTO>();
					Set<AbstractCommandConfigurationFactory<?>> commandfactories = self.commandDAO
							.getCommandFactories();
					for (AbstractCommandConfigurationFactory<?> factory : commandfactories) {
						List<CommandMetadataDTO> commandMetadataList = new ArrayList<CommandMetadataDTO>();
						MBeanAttributeInfo[] attArray = factory.getServiceDescription(factory.getFactoryID())
								.getAttributes();
						for (MBeanAttributeInfo att : attArray) {
							CommandMetadataDTO commandMetadata = new CommandMetadataDTO();
							OpenMBeanAttributeInfoSupport supp = (OpenMBeanAttributeInfoSupport) att;
							if (supp.getDefaultValue() != null) {
								commandMetadata.setDefaultValue(supp.getDefaultValue().toString());
							}
							if (supp.getDescription() != null) {
								commandMetadata.setDescription(supp.getDescription());
							}
							if (supp.getMaxValue() != null) {
								commandMetadata.setMaxValue(supp.getMaxValue().toString());
							}
							if (supp.getMinValue() != null) {
								commandMetadata.setMinValue(supp.getMinValue().toString());
							}
							if (supp.getType() != null) {
								commandMetadata.setType(supp.getType());
							}
							if (supp.getName() != null) {
								commandMetadata.setName(supp.getName());
							}
							if (supp.getDescriptor().getFieldValue(cat_str) != null) {
								commandMetadata.setCategory((String) supp.getDescriptor().getFieldValue(cat_str));
							}
							if (supp.getDescriptor().getFieldValue(order_value) != null) {
								commandMetadata.setOrderValue((Float) supp.getDescriptor().getFieldValue(order_value));
							}
							if (supp.getDescriptor().getFieldValue(display_name) != null) {
								commandMetadata
										.setDisplayName((String) supp.getDescriptor().getFieldValue(display_name));
							}
							commandMetadata.setWritable(supp.isWritable());

							commandMetadataList.add(commandMetadata);
						}
						CommandFactoryMetadataDTO commandMetaFactory = new CommandFactoryMetadataDTO();
						commandMetaFactory.setCommandmetadata(commandMetadataList);
						commandMetaFactory.setId(factory.getFactoryID());
						commandMetaFactory.setReaderID(factory.getReaderFactoryID());
						commandFactoryMetadataList.add(commandMetaFactory);
					}

					rmrmd.setCommandMetaList(commandFactoryMetadataList);
					rmrmd.setReaderMetaList(readerFactoryMetadataList);

					response.setEntity(self.generateReturnString(rmrmd).toString(), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet commandTypes = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("commandTypes requested");
				try {

					setResponseHeaders(request, response);

					CommandTypesResponseMessageDTO rtr = new CommandTypesResponseMessageDTO();
					Set<CommandConfigFactoryDTO> grf = self.commandManagerService.getCommandConfigFactories();
					List<CommandTypeDTO> ret = new LinkedList<CommandTypeDTO>();
					for (CommandConfigFactoryDTO rfd : grf) {
						CommandTypeDTO rtd = new CommandTypeDTO();
						rtd.setCommandDesc(rfd.getDescription());
						rtd.setCommandType(rfd.getCommandFactoryID());
						rtd.setReaderFactory(rfd.getReaderFactoryID());
						ret.add(rtd);
					}
					rtr.setCommands(ret);
					response.setEntity(self.generateReturnString(rtr).toString(), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet apps = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("apps requested");
				try {

					setResponseHeaders(request, response);

					Map<Integer, RifidiApp> apps = appManager.getApps();
					List<AppNameDTO> appNames = new LinkedList<AppNameDTO>();
					for (Integer i : apps.keySet()) {
						AppNameDTO and = new AppNameDTO();
						and.setAppName(apps.get(i).getGroup() + ":" + apps.get(i).getName());
						and.setAppNumber(Integer.toString(i));
						and.setAppStatus(apps.get(i).getState().toString());
						appNames.add(and);
					}
					AppResponseMessageDTO armd = new AppResponseMessageDTO();
					armd.setApps(appNames);
					response.setEntity(self.generateReturnString(armd), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet loadApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("loadApp requested");
				try {
					provisioningService.provision((String) request.getAttributes().get("path"));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet save = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("save requested");
				try {

					setResponseHeaders(request, response);

					configService.storeConfiguration();
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet currenttags = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("currenttags requested");
				try {
					setResponseHeaders(request, response);
					Map<String, CurrentTagDTO> currenttags = new HashMap<String, CurrentTagDTO>();
					CurrentTagsSubscriber sub = new CurrentTagsSubscriber(currenttags);
					ReadZone zone = new ReadZone((String) request.getAttributes().get("readerID"));

					CurrentTagsAggregator agg = new CurrentTagsAggregator(sub, zone, rawTagMonitoringService);
					agg.initstart();

					CurrentTagsReponseMessageDTO tagresponse = new CurrentTagsReponseMessageDTO();
					List<CurrentTagDTO> tempList = new ArrayList<CurrentTagDTO>();
					tempList.addAll(currenttags.values());
					tagresponse.setTags(tempList);
					response.setEntity(self.generateReturnString(tagresponse), MediaType.TEXT_XML);

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet llrpGetReaderConfig = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("llrpGetReaderConfig requested");
				setResponseHeaders(request, response);
				llrpGetOperation(request, response, LLRPGetOperations.GET_READER_CONFIG);
			}
		};

		Restlet llrpGetRospecs = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("llrpGetRospecs requested");
				setResponseHeaders(request, response);
				llrpGetOperation(request, response, LLRPGetOperations.GET_ROSPECS);
			}
		};

		Restlet llrpGetReaderCapabilities = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("llrpGetReaderCapabilities requested");
				setResponseHeaders(request, response);
				llrpGetOperation(request, response, LLRPGetOperations.GET_READER_CAPABILITIES);
			}
		};

		Restlet llrpEncode = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEncode requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, null);

			}
		};

		Restlet llrpEpcWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEpcWrite requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCWrite);

			}
		};

		Restlet llrpAccessPasswordWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpAccessPasswordWrite requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordWrite);

			}
		};

		Restlet llrpKillPasswordWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpKillPasswordWrite requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordWrite);

			}
		};

		Restlet llrpEPCLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEPCLock requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCLock);

			}
		};

		Restlet llrpAccessPasswordLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpAccessPasswordLock requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordLock);

			}
		};

		Restlet llrpKillPasswordLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpKillPasswordLock requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordLock);

			}
		};

		Restlet llrpUserMemoryLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpUserMemoryLock requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryLock);

			}
		};

		Restlet llrpEpcRead = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEpcRead requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCRead);

			}
		};

		Restlet llrpAccessPwdValidate = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpAccessPwdValidate requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response,
						LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordValidate);

			}
		};

		Restlet llrpKillPwdRead = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpKillPwdRead requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordRead);

			}
		};

		Restlet llrpUserMemoryRead = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpUserMemoryRead requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryRead);

			}
		};

		Restlet llrpUserMemoryWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpUserMemoryWrite requested");

				setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryWrite);

			}
		};

		Restlet llrpMessage = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("llrpMessage requested");

					setResponseHeaders(request, response);

					AbstractSensor<?> sensor = readerDAO
							.getReaderByID((String) request.getAttributes().get("readerID"));

					if (sensor == null) {
						throw new Exception("ReaderID is missing or invalid");
					}

					Map<String, SensorSession> sessionMap = sensor.getSensorSessions();
					String llrpResponse = "";
					if (sessionMap != null && sessionMap.containsKey(request.getAttributes().get("sessionID"))) {
						LLRPReaderSession session = (LLRPReaderSession) sessionMap
								.get(request.getAttributes().get("sessionID"));
						Boolean sendonly = false;
						try {
							sendonly = Boolean.parseBoolean((String) request.getAttributes().get("sendonly"));
						} catch (Exception e) {
							// Do nothing
						}

						SAXBuilder sb = new SAXBuilder();

						String strEntityAsText = request.getEntityAsText();
						Document doc = sb.build(new StringReader(strEntityAsText));
						llrpResponse = session.sendLLRPMessage(doc, sendonly);
						if (llrpResponse == null) {
							llrpResponse = self.generateReturnString(self.generateSuccessMessage());
						}
						response.setEntity(llrpResponse, MediaType.TEXT_XML);
					} else {
						throw new Exception("SessionID is missing or invalid");
					}
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet ping = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				setResponseHeaders(request, response);

				PingDTO ping = new PingDTO();
				ping.setTimestamp(Long.toString(System.currentTimeMillis()));
				response.setEntity(self.generateReturnString(ping), MediaType.TEXT_XML);
			}
		};

		Restlet setGPO = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				setResponseHeaders(request, response);
				String readerID = null;
				String sessionID = null;
				String portstr = null;
				try {
					readerID = (String) request.getAttributes().get("readerID");
					sessionID = (String) request.getAttributes().get("sessionID");
					portstr = (String) request.getAttributes().get("ports");
					SessionStatus checkSessionState = null;
					try {
						checkSessionState = sensorManagerService.getSession(readerID, sessionID).getStatus();
					} catch (Exception e) {
						response.setEntity(self.generateReturnString(self.generateErrorMessage(
								"Could not find session id " + sessionID + " of reader with id " + readerID, null)),
								MediaType.TEXT_XML);
						return;
					}
					if (!checkSessionState.equals(SessionStatus.PROCESSING)) {
						response.setEntity(
								self.generateReturnString(self.generateErrorMessage("Session with id " + sessionID
										+ " of reader with id " + readerID + " is not in the processing state.", null)),
								MediaType.TEXT_XML);
						return;
					}
					Set<Integer> ports = new HashSet<Integer>();
					logger.info("Setting GPO for reader " + readerID + " ports: " + portstr);
					for (String port : portstr.split(",")) {
						ports.add(Integer.parseInt(port));
					}
					for (AbstractGPIOService<?> service : gpioServiceList) {
						if (service.isReaderAvailable(readerID)) {
							try {
								service.setGPO(readerID, ports);
							} catch (CannotExecuteException e) {
								e.printStackTrace();
								response.setEntity(self.generateReturnString(self.generateErrorMessage(
										"Error when setting GPO for reader " + readerID + " ports " + ports, null)),
										MediaType.TEXT_XML);
								return;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setEntity(
							self.generateReturnString(self.generateErrorMessage(
									"Error when setting GPO for reader " + readerID + " ports " + portstr, null)),
							MediaType.TEXT_XML);
					return;
				}
				response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
			}
		};
		
		Restlet flashGPO = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				setResponseHeaders(request, response);
				String readerID = null;
				String sessionID = null;
				String portstr = null;
				Integer flashtime = null;
				try {
					readerID = (String) request.getAttributes().get("readerID");
					sessionID = (String) request.getAttributes().get("sessionID");
					portstr = (String) request.getAttributes().get("ports");
					flashtime = Integer.parseInt((String) request.getAttributes().get("seconds"));
					SessionStatus checkSessionState = null;
					try {
						checkSessionState = sensorManagerService.getSession(readerID, sessionID).getStatus();
					} catch (Exception e) {
						response.setEntity(self.generateReturnString(self.generateErrorMessage(
								"Could not find session id " + sessionID + " of reader with id " + readerID, null)),
								MediaType.TEXT_XML);
						return;
					}
					if (!checkSessionState.equals(SessionStatus.PROCESSING)) {
						response.setEntity(
								self.generateReturnString(self.generateErrorMessage("Session with id " + sessionID
										+ " of reader with id " + readerID + " is not in the processing state.", null)),
								MediaType.TEXT_XML);
						return;
					}
					logger.info("Flashing GPO for reader " + readerID + " ports " + portstr + " for " + flashtime
							+ " seconds");
					Set<Integer> ports = new HashSet<Integer>();
					for (String port : portstr.split(",")) {
						ports.add(Integer.parseInt(port));
					}
					for (AbstractGPIOService<?> service : gpioServiceList) {
						if (service.isReaderAvailable(readerID)) {
							try {
								service.flashGPO(readerID, flashtime, ports);
							} catch (CannotExecuteException e) {
								e.printStackTrace();
								response.setEntity(self.generateReturnString(self.generateErrorMessage(
										"Error when setting GPO for reader " + readerID + " ports " + ports, null)),
										MediaType.TEXT_XML);
								return;
							}
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
					response.setEntity(
							self.generateReturnString(self.generateErrorMessage(
									"Error when setting GPO for reader " + readerID + " ports " + portstr, null)),
							MediaType.TEXT_XML);
					return;
				}
				response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
			}
		};

		Restlet getAppProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getAppProperties requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);
						Properties appProperties = RifidiEdgeHelper.getApplicationProperties(app.getGroup(),
								app.getName());

						PropertyResponseMessageDTO responseMessageDTO = new PropertyResponseMessageDTO();
						List<PropertyNameDTO> propertyNameList = new LinkedList<PropertyNameDTO>();

						for (Map.Entry<Object, Object> mapEntry : appProperties.entrySet()) {

							PropertyNameDTO pnd = new PropertyNameDTO();
							pnd.setPropertyName((String) mapEntry.getKey());
							pnd.setPropertyValue((String) mapEntry.getValue());
							propertyNameList.add(pnd);
						}

						responseMessageDTO.setProperties(propertyNameList);

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(),
								MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet getGroupProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getGroupProperties requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);
						Properties groupProperties = RifidiEdgeHelper.getGroupProperties(app.getGroup());

						PropertyResponseMessageDTO responseMessageDTO = new PropertyResponseMessageDTO();
						List<PropertyNameDTO> propertyNameList = new LinkedList<PropertyNameDTO>();

						for (Map.Entry<Object, Object> mapEntry : groupProperties.entrySet()) {

							PropertyNameDTO pnd = new PropertyNameDTO();
							pnd.setPropertyName((String) mapEntry.getKey());
							pnd.setPropertyValue((String) mapEntry.getValue());
							propertyNameList.add(pnd);
						}

						responseMessageDTO.setProperties(propertyNameList);

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(),
								MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet getReadZones = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getReadZones requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);
						HashMap<String, ReadZone> readZones = RifidiEdgeHelper.getReadZones(app.getGroup());

						PropertyResponseMessageDTO responseMessageDTO = new PropertyResponseMessageDTO();
						List<PropertyNameDTO> propertyNameList = new LinkedList<PropertyNameDTO>();

						for (String strReadZone : readZones.keySet()) {

							PropertyNameDTO pnd = new PropertyNameDTO();
							pnd.setPropertyName(null);
							pnd.setPropertyValue(strReadZone);
							propertyNameList.add(pnd);
						}

						responseMessageDTO.setProperties(propertyNameList);

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(),
								MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet deleteReadZone = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("deleteReadZone requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZone = (String) request.getAttributes().get("readZone");

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);
						RifidiEdgeHelper.deleteReadZone(app.getGroup(), readZone);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()),
								MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet getReadZoneProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getReadZoneProperties requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZoneName = (String) request.getAttributes().get("readZoneName");

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						Properties readZoneProperties = RifidiEdgeHelper.getReadZoneProperties(app.getGroup(),
								app.getName(), readZoneName);

						PropertyResponseMessageDTO responseMessageDTO = new PropertyResponseMessageDTO();
						List<PropertyNameDTO> propertyNameList = new LinkedList<PropertyNameDTO>();

						for (Map.Entry<Object, Object> mapEntry : readZoneProperties.entrySet()) {

							PropertyNameDTO pnd = new PropertyNameDTO();
							pnd.setPropertyName((String) mapEntry.getKey());
							pnd.setPropertyValue((String) mapEntry.getValue());
							propertyNameList.add(pnd);
						}

						responseMessageDTO.setProperties(propertyNameList);

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(),
								MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet setAppProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				// String strCommandId = null;

				try {

					logger.info("setAppProperties requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.setApplicationProperties(app.getGroup(), app.getName(), attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()),
								MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet setGroupProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				try {

					logger.info("setGroupProperties requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.setGroupProperties(app.getGroup(), attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()),
								MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet addReadZone = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				try {

					logger.info("addReadZone requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZone = (String) request.getAttributes().get("readZone");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					// Validate that properties are valid for readzone
					validateReadzoneProperties(attributes);

					// Validate that properties contain the minimum required
					// ones
					validateMinumimRequiredReadzoneProperties(attributes);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.addReadZone(app.getGroup(), readZone, attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()),
								MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet setReadZoneProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				try {

					logger.info("setReadZoneProperties requested");

					setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZone = (String) request.getAttributes().get("readZoneName");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					// Validate that properties are valid for readzone
					validateReadzoneProperties(attributes);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.setReadZoneProperties(app.getGroup(), readZone, attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()),
								MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet updateServersFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					setResponseHeaders(request, response);

					String data = (String) request.getAttributes().get("data");
					String decodedData = URLDecoder.decode(data, "UTF-8");
					RifidiEdgeHelper.updateServersFile(decodedData);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet getServersFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					setResponseHeaders(request, response);

					byte[] data = RifidiEdgeHelper.getServersFile();
					String str = new String(data, "UTF-8");
					response.setEntity(str, MediaType.APPLICATION_JSON);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet updateUIPropertiesFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					setResponseHeaders(request, response);

					String data = (String) request.getAttributes().get("data");
					String decodedData = URLDecoder.decode(data, "UTF-8");
					RifidiEdgeHelper.updateUIPropertiesFile(decodedData);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet getUIPropertiesFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					setResponseHeaders(request, response);

					byte[] data = RifidiEdgeHelper.getUIPropertiesFile();
					String str = new String(data, "UTF-8");
					response.setEntity(str, MediaType.APPLICATION_JSON);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet addDefaultApplication = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					setResponseHeaders(request, response);
					String group = (String) request.getAttributes().get("group");
					RifidiEdgeHelper.addDefaultApp(group);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}

			}
		};

		Restlet shutdown = new Restlet() {
			@Override
			@Post
			public void handle(Request request, Response response) {
				try {
					setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
					Thread thread = new Thread(new RestletShutdown());
					thread.start();
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet restart = new Restlet() {
			@Override
			@Post
			public void handle(Request request, Response response) {
				try {
					setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
					if (SystemUtils.IS_OS_LINUX) {
						Thread thread = new Thread(new RestletRestart());
						thread.start();
					} else {
						throw new Exception("Restart will only work on Linux");
					}
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet bundles = new Restlet() {
			@Override
			@Post
			@Get
			public void handle(Request request, Response response) {
				try {
					Map<Integer, String> states = new HashMap<Integer, String>();
					states.put(1, "UNINSTALLED");
					states.put(2, "INSTALLED");
					states.put(4, "RESOLVED");
					states.put(8, "STARTING");
					states.put(16, "STOPPING");
					states.put(32, "ACTIVE");

					final BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
					BundleResponseMessageDTO bundleResponse = new BundleResponseMessageDTO();
					List<BundleDTO> bundleDTOs = new LinkedList<BundleDTO>();
					for (Bundle bundle : bundleContext.getBundles()) {
						BundleDTO bundleDTO = new BundleDTO();
						bundleDTO.setName(bundle.getSymbolicName());
						bundleDTO.setId(bundle.getBundleId());
						bundleDTO.setState(states.get(bundle.getState()));
						bundleDTOs.add(bundleDTO);
					}
					bundleResponse.setBundles(bundleDTOs);

					setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(bundleResponse), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet startBundle = new Restlet() {
			@Override
			@Post
			public void handle(Request request, Response response) {
				try {
					String bundleID = (String) request.getAttributes().get("bundleID");

					final BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
					logger.info("Starting a bundle: "
							+ bundleContext.getBundle(Long.parseLong(bundleID)).getSymbolicName());
					bundleContext.getBundle(Long.parseLong(bundleID)).start();
					;

					setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet stopBundle = new Restlet() {
			@Override
			@Post
			public void handle(Request request, Response response) {
				try {
					String bundleID = (String) request.getAttributes().get("bundleID");

					final BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
					logger.info("Stopping a bundle: "
							+ bundleContext.getBundle(Long.parseLong(bundleID)).getSymbolicName());
					bundleContext.getBundle(Long.parseLong(bundleID)).stop();

					setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet installBundle = new Restlet() {
			@Override
			@Post
			public void handle(Request request, Response response) {
				try {
					String bundlePath = (String) request.getEntityAsText();
					logger.info("Installing a bundle: " + bundlePath);
					final BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
					bundleContext.installBundle(bundlePath);

					setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet uninstallBundle = new Restlet() {
			@Override
			@Post
			public void handle(Request request, Response response) {
				try {
					String bundleID = (String) request.getAttributes().get("bundleID");

					final BundleContext bundleContext = FrameworkUtil.getBundle(this.getClass()).getBundleContext();
					logger.info("Uninstalling a bundle: "
							+ bundleContext.getBundle(Long.parseLong(bundleID)).getSymbolicName());
					bundleContext.getBundle(Long.parseLong(bundleID)).uninstall();

					setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Router router = new Router(getContext().createChildContext());

		router.attach("/shutdown", shutdown);
		router.attach("/restart", restart);
		router.attach("/ss", bundles);
		router.attach("/startbundle/{bundleID}", startBundle);
		router.attach("/stopbundle/{bundleID}", stopBundle);
		router.attach("/installbundle", installBundle);
		router.attach("/uninstallbundle/{bundleID}", uninstallBundle);
		router.attach("/readers", readers);
		router.attach("/commands", commands);
		router.attach("/readerstatus/{readerID}", readerStatus);
		router.attach("/startsession/{readerID}/{sessionID}", startSession);
		router.attach("/stopsession/{readerID}/{sessionID}", stopSession);
		router.attach("/createsession/{readerID}", createSession);
		router.attach("/deletesession/{readerID}/{sessionID}", deleteSession);
		router.attach("/resetsession/{readerID}/{sessionID}", resetSession);
		router.attach("/deletereader/{readerID}", deleteReader);
		router.attach("/deletecommand/{commandID}", deleteCommand);
		router.attach("/executecommand/{readerID}/{sessionID}/{commandID}/{repeatInterval}", executeCommand);

		// get and set properties for a reader
		router.attach("/getproperties/{readerID}", getProperties);
		router.attach("/setproperties/{readerID}/{properties}", setProperties);

		// get properties for an application
		router.attach("/getAppProperties/{appID}", getAppProperties);

		// set properties for an application
		router.attach("/setAppProperties/{appID}/{properties}", setAppProperties);

		// get properties for a group
		router.attach("/getGroupProperties/{appID}", getGroupProperties);

		// set properties for a group
		router.attach("/setGroupProperties/{appID}/{properties}", setGroupProperties);

		// get readzones
		router.attach("/getReadZones/{appID}", getReadZones);

		// delete readzone
		router.attach("/deleteReadZone/{appID}/{readZone}", deleteReadZone);

		// add readzone
		router.attach("/addReadZone/{appID}/{readZone}/{properties}", addReadZone);

		// get readzone properties
		router.attach("/getReadZoneProperties/{appID}/{readZoneName}", getReadZoneProperties);

		// set readzone properties
		router.attach("/setReadZoneProperties/{appID}/{readZoneName}/{properties}", setReadZoneProperties);

		// createreader with properties
		router.attach("/createreader/{readerType}/{properties}", createReader);

		// createreader with no properties
		router.attach("/createreader/{readerType}", createReader);

		// createcommand with properties
		router.attach("/createcommand/{commandType}/{properties}", createCommand);

		// createcommand with no properties
		router.attach("/createcommand/{commandType}", createCommand);

		router.attach("/startapp/{appID}", startApp);
		router.attach("/stopapp/{appID}", stopApp);
		router.attach("/loadapp/{path}", loadApp);
		router.attach("/adddefaultapp/{group}", addDefaultApplication);
		router.attach("/commandtypes", commandTypes);
		router.attach("/readertypes", readerTypes);
		router.attach("/readermetadata", readerMetadata);
		router.attach("/apps", apps);
		router.attach("/save", save);
		router.attach("/currenttags/{readerID}", currenttags);

		// gpio commands
		router.attach("/setgpo/{readerID}/{sessionID}/{ports}", setGPO);
		router.attach("/flashgpo/{readerID}/{sessionID}/{ports}/{seconds}", flashGPO);

		// thinkify commands
		// router.attach("/rcs/{readerID}/{sessionID}", rcs);

		// single shot commands

		router.attach("/llrpgetrospecs/{readerID}/{sessionID}", llrpGetRospecs);
		router.attach("/llrpgetreaderconfig/{readerID}/{sessionID}", llrpGetReaderConfig);
		router.attach("/llrpgetreadercapabilities/{readerID}/{sessionID}", llrpGetReaderCapabilities);

		// LLRPEPCWrite single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPEPCWrite", llrpEpcWrite);

		// LLRPEPCWrite single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPEPCWrite/{properties}", llrpEpcWrite);

		// llrpAccessPasswordWrite single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordWrite", llrpAccessPasswordWrite);

		// llrpAccessPasswordWrite single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordWrite/{properties}",
				llrpAccessPasswordWrite);

		// llrpKillPasswordWrite single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPKillPasswordWrite", llrpKillPasswordWrite);

		// llrpKillPasswordWrite single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPKillPasswordWrite/{properties}", llrpKillPasswordWrite);

		// llrpEPCLock single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPEPCLock", llrpEPCLock);

		// llrpEPCLock single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPEPCLock/{properties}", llrpEPCLock);

		// llrpAccessPasswordLock single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordLock", llrpAccessPasswordLock);

		// llrpAccessPasswordLock single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordLock/{properties}", llrpAccessPasswordLock);

		// llrpKillPasswordLock single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPKillPasswordLock", llrpKillPasswordLock);

		// llrpKillPasswordLock single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPKillPasswordLock/{properties}", llrpKillPasswordLock);

		// llrpUserMemoryLock single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPUserMemoryLock", llrpUserMemoryLock);

		// llrpUserMemoryLock single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPUserMemoryLock/{properties}", llrpUserMemoryLock);

		// LLRPEPCRead single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPEPCRead", llrpEpcRead);

		// LLRPEPCRead single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPEPCRead/{properties}", llrpEpcRead);

		// LLRPAccessPasswordValidate single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordValidate", llrpAccessPwdValidate);

		// LLRPAccessPasswordValidate single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordValidate/{properties}",
				llrpAccessPwdValidate);

		// LLRPKillPasswordRead single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPKillPasswordRead", llrpKillPwdRead);

		// LLRPKillPasswordRead single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPKillPasswordRead/{properties}", llrpKillPwdRead);

		// LLRPUserMemoryRead single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPUserMemoryRead", llrpUserMemoryRead);

		// LLRPUserMemoryRead single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPUserMemoryRead/{properties}", llrpUserMemoryRead);

		// LLRPUserMemoryWrite single shot command with no properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPUserMemoryWrite", llrpUserMemoryWrite);

		// LLRPUserMemoryWrite single shot command with properties
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPUserMemoryWrite/{properties}", llrpUserMemoryWrite);

		// llrp encode
		router.attach("/llrpencode/{readerID}/{sessionID}/{tag}", llrpEncode);

		router.attach("/llrpmessage/{readerID}/{sessionID}", llrpMessage);
		router.attach("/llrpmessage/{readerID}/{sessionID}/{sendonly}", llrpMessage);

		router.attach("/ping", ping);

		// router to update servers file
		router.attach("/updateServersFile/{data}", updateServersFile);

		// router to get servers file
		router.attach("/getServersFile", getServersFile);

		// router to update ui properties file
		router.attach("/updateUIPropertiesFile/{data}", updateUIPropertiesFile);

		// router to get properties file
		router.attach("/getUIPropertiesFile", getUIPropertiesFile);

		// Attach web administration dashboard app
		String appPath = "file:///" + System.getProperty("org.rifidi.home") + File.separator + "admin" + File.separator
				+ "app" + File.separator;

		Directory directory = new Directory(getContext(), appPath);
		router.attach("/dashboard", directory);

		return router;
	}

	public RestResponseMessageDTO generateSuccessMessage() {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(SUCCESS_MESSAGE);
		return message;
	}

	public RestResponseMessageDTO generateErrorMessage(String description, String currentState) {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(FAIL_MESSAGE);
		message.setDescription(description);
		message.setState(currentState);
		return message;
	}

	public RestResponseMessageDTO generateWarningMessage(String description) {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(SUCCESS_MESSAGE);
		message.setDescription(description);
		message.setState(WARNING_STATE);
		return message;
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

	// Spring Inject

	public void setSensorManagerService(SensorManagerService sensorManagerService) {
		this.sensorManagerService = sensorManagerService;
	}

	public void setCommandManagerService(CommandManagerService commandManagerService) {
		this.commandManagerService = commandManagerService;
	}

	public void setProvisioningService(ProvisioningService provisioningService) {
		this.provisioningService = provisioningService;
	}

	public void setAppManager(AppManager appManager) {
		this.appManager = appManager;
	}

	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	public void setCommandDAO(CommandDAO commandDAO) {
		this.commandDAO = commandDAO;
	}

	public void setRawTagMonitoringService(RawTagMonitoringService rzms) {
		this.rawTagMonitoringService = rzms;
	}

	public void setGPIOService(Set<AbstractGPIOService<?>> gpioSet) {
		// Should we clear the local list before we iterate through the loop?
		for (AbstractGPIOService<?> service : gpioSet) {
			logger.debug("Adding a GPIO service in the setter: " + service);
			this.gpioServiceList.add(service);
		}
	}

	public Set<AbstractGPIOService<?>> getServiceSet() {
		return this.gpioServiceList;
	}

	/**
	 * @param gpioService the gpioService to set
	 */
	public void onBind(AbstractGPIOService<?> gpioService, Dictionary<String, String> parameters) {
		logger.debug("Binding: " + gpioService);
		this.gpioServiceList.add(gpioService);
	}

	/**
	 * @param gpioService the gpioService to set
	 */
	public void onUnbind(AbstractGPIOService<?> gpioService, Dictionary<String, String> parameters) {
		this.gpioServiceList.remove(gpioService);
	}

	// End Spring Inject

	/**
	 * Sets the configuration service for this class.
	 * 
	 * @param configService the configService to set
	 */
	public void setConfigurationService(ConfigurationService configService) {
		this.configService = configService;
	}

	/**
	 * Checks is session of reader id is at desired state before reaching n attempts
	 * every 500ms
	 * 
	 * @param strReaderId  the reader id
	 * @param strSessionID the session id
	 * @param desiredState the desired state to check for the session
	 * @return current session state for session
	 */
	private SessionStatus checkSessionState(String strReaderId, String strSessionID, SessionStatus desiredState) {

		// Define a session state to track the actual status of session
		SessionStatus currentSessionState = null;

		// the count to loop to check every 500ms this amount and if
		// status processing is not seen then return false
		int attemptCount = 20;

		for (int i = 0; i < attemptCount; i++) {

			// Wait for 500ms
			try {

				Thread.sleep(500l);

			} catch (InterruptedException intEx) {

				// No matters
				intEx.printStackTrace();

			}

			// Check if session is already in desired state
			// and return successful message
			SessionDTO sessionDto = sensorManagerService.getSession(strReaderId, strSessionID);

			// Ask if its state is in desired state
			currentSessionState = sessionDto.getStatus();

			if (currentSessionState.equals(desiredState)) {

				// Session is already in desired state
				break;
			}

		}

		return currentSessionState;

	}

	/**
	 * Processes a chain of semicolon separated properties and checks whether it is
	 * a well formed pair
	 * 
	 * @param propertiesChain separated values of properties, for example:
	 *                        (prop1=val2;prop2=val2;prop3=val3)
	 * @return AttributeList containing the attributes
	 * @throws Exception if any property has no recognizable value
	 */
	private AttributeList getProcessedAttributes(String propertiesChain) throws Exception {

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

	/**
	 * Checks if reader given by reader id exists
	 * 
	 * @param strReaderIdthe reader id to check
	 */
	private boolean readerExists(String strReaderId) {

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
	 * @param strCommandId command id to check
	 * @throws Exception if command with command id does not exist
	 */
	private boolean commandExists(String strCommandId) {

		boolean commandExists = false;

		CommandConfigurationDTO commandConfigurationDTO = commandManagerService.getCommandConfiguration(strCommandId);
		;

		if (commandConfigurationDTO != null) {

			commandExists = true;
		}

		return commandExists;

	}

	/**
	 * Validate if attributes are valid for reader or command id
	 * 
	 * @param strObjectId the id of reader or command
	 * @param attributes  the lsit of attributes to validate
	 * @throws NotValidPropertyForObjectException if there is a non valid property
	 *                                            for reader or command
	 */
	private void validateAttributesForReaderOrCommand(String strObjectId, AttributeList attributes)
			throws NotValidPropertyForObjectException {

		// List of non valid property=value pair
		List<String> notValidPropertiesList = new ArrayList<String>();

		// Check if properties are valid for this reader or command
		Configuration configuration = configService.getConfiguration(strObjectId);

		// Get the possible attribute list for this reader or command
		String[] attributeNameVector = configuration.getAttributeNames();

		// Iterate over posted attributes
		for (Attribute attribute : attributes.asList()) {

			// Current posted attribute is not valid until it is confirmed
			boolean isValidAttribute = false;

			// Iterate over possible attribute list for this reader or command
			// and check
			// if posted attribute matches any valid attibute
			for (int i = 0; i < attributeNameVector.length; i++) {

				if (attribute.getName().equals(attributeNameVector[i])) {

					isValidAttribute = true;
					break;
				}
			}

			if (!isValidAttribute) {
				notValidPropertiesList.add(attribute.getName() + "=" + attribute.getValue());
			}

		}

		// Check if non valid properties list has elements
		if (!notValidPropertiesList.isEmpty()) {

			// There is at least one non valid property
			NotValidPropertyForObjectException notValidPropertyForObjectException = new NotValidPropertyForObjectException(
					"Not valid properties: " + getFormatedListOfNonValidProperties(notValidPropertiesList));

			notValidPropertyForObjectException.setNotValidPropertiesList(notValidPropertiesList);

			throw notValidPropertyForObjectException;
		}

	}

	/**
	 * Returns a string containing the values located inside notValidPropertiesList
	 * and separated by |
	 * 
	 * @param notValidPropertiesList the list of properties to process
	 * @return a string containing the values located inside notValidPropertiesList
	 *         and separated by |
	 */
	private String getFormatedListOfNonValidProperties(List<String> notValidPropertiesList) {

		if (!notValidPropertiesList.isEmpty()) {

			StringBuilder stringBuilder = new StringBuilder();

			for (String prop : notValidPropertiesList) {

				stringBuilder.append(prop + "|");

			}

			// Delete last pipe and return string
			return stringBuilder.substring(0, stringBuilder.length() - 1);

		}

		return "";
	}

	/**
	 * Set the jvm properties into session object
	 * 
	 * @param session the session of reader where the properties are going to be set
	 * @throws Exception if there is a validation error on a property
	 */
	private void setLlrpEncodeJvmProperties(LLRPReaderSession session) throws Exception {

		String strTargetEpc = System.getProperty("org.rifidi.llrp.encode.targetepc");

		checkBlockLengthReminder(strTargetEpc, session.getWriteDataBlockLength(), "targetEpc");

		session.setTargetEpc(strTargetEpc != null ? strTargetEpc : LLRPReaderSession.DEFAULT_TARGET_EPC);

		String strTagMask = System.getProperty("org.rifidi.llrp.encode.tagmask");

		checkBlockLengthReminder(strTagMask, session.getWriteDataBlockLength(), "tagMask");

		session.setTagMask(strTagMask != null ? strTagMask : LLRPReaderSession.DEFAULT_TAG_MASK);

		String strTimeout = System.getProperty("org.rifidi.llrp.encode.timeout");

		session.setOperationsTimeout(
				strTimeout != null ? Integer.parseInt(strTimeout) : LLRPReaderSession.DEFAULT_OPERATIONS_TIMEOUT);

		String strAccessPwd = System.getProperty("org.rifidi.llrp.encode.accesspwd");

		validatePassword(strAccessPwd, "Access");

		session.setAccessPwd(strAccessPwd != null ? strAccessPwd : LLRPReaderSession.DEFAULT_ACCESS_PASSWORD);

		String strOldAccessPwd = System.getProperty("org.rifidi.llrp.encode.oldaccesspwd");

		validatePassword(strOldAccessPwd, "Old access");

		session.setOldAccessPwd(
				strOldAccessPwd != null ? strOldAccessPwd : LLRPReaderSession.DEFAULT_OLD_ACCESS_PASSWORD);

		String strKillPwd = System.getProperty("org.rifidi.llrp.encode.killpwd");

		validatePassword(strKillPwd, "Kill");

		session.setKillPwd(strKillPwd != null ? strKillPwd : LLRPReaderSession.DEFAULT_KILL_PASSWORD);

		// Set the lock privileges

		String strKillPwdLockPrivilege = System.getProperty("org.rifidi.llrp.encode.killpwdlockprivilege");

		if (strKillPwdLockPrivilege != null) {

			int intKillPwdLockPrivilege = LLRPReaderSession.getLockPrivilege(strKillPwdLockPrivilege);
			session.setKillPwdLockPrivilege(intKillPwdLockPrivilege);

		} else {

			session.setKillPwdLockPrivilege(LLRPReaderSession.DEFAULT_KILL_PASSWORD_LOCK_PRIVILEGE);
		}

		String strAccessPwdLockPrivilege = System.getProperty("org.rifidi.llrp.encode.accesspwdlockprivilege");

		if (strAccessPwdLockPrivilege != null) {

			int intAccessPwdLockPrivilege = LLRPReaderSession.getLockPrivilege(strAccessPwdLockPrivilege);
			session.setAccessPwdLockPrivilege(intAccessPwdLockPrivilege);

		} else {

			session.setAccessPwdLockPrivilege(LLRPReaderSession.DEFAULT_ACCESS_PASSWORD_LOCK_PRIVILEGE);
		}

		String strEpcLockPrivilege = System.getProperty("org.rifidi.llrp.encode.epclockprivilege");

		if (strEpcLockPrivilege != null) {

			int intEpcLockPrivilege = LLRPReaderSession.getLockPrivilege(strEpcLockPrivilege);
			session.setEpcLockPrivilege(intEpcLockPrivilege);

		} else {

			session.setEpcLockPrivilege(LLRPReaderSession.DEFAULT_EPC_LOCK_PRIVILEGE);
		}

		String strUserMemoryLockPrivilege = System.getProperty("org.rifidi.llrp.encode.usermemorylockprivilege");

		if (strUserMemoryLockPrivilege != null) {

			int intUserMemoryLockPrivilege = LLRPReaderSession.getLockPrivilege(strUserMemoryLockPrivilege);
			session.setUserMemoryLockPrivilege(intUserMemoryLockPrivilege);

		} else {

			session.setUserMemoryLockPrivilege(LLRPReaderSession.DEFAULT_USER_MEMORY_LOCK_PRIVILEGE);
		}

		// Check if properties for mqtt are set, if so then submit operations
		// response
		// in asynchronous mode to this mqtt,
		// otherwise, submit operations ressonse to web browser in synchronous
		// mode
		boolean asynchronousMode = true;

		// Check mqttbroker
		String mqttBroker = System.getProperty("org.rifidi.llrp.encode.mqttbroker");

		if (mqttBroker != null && !mqttBroker.isEmpty()) {

			session.setMqttBroker(mqttBroker);

		} else {
			asynchronousMode = false;
		}

		// Check mqttqos
		String mqttQos = System.getProperty("org.rifidi.llrp.encode.mqttqos");

		if (mqttQos != null && !mqttQos.isEmpty()) {

			session.setMqttQos(Integer.valueOf(mqttQos));

		} else {
			asynchronousMode = false;
		}

		// Check mqttclientid
		String mqttClientId = System.getProperty("org.rifidi.llrp.encode.mqttclientid");

		if (mqttClientId != null && !mqttClientId.isEmpty()) {

			session.setMqttClientId(mqttClientId);

		} else {
			asynchronousMode = false;
		}

		session.setExecuteOperationsInAsynchronousMode(asynchronousMode);

	}

	/**
	 * Validate password is not empty and if it's length is one, the value must be
	 * zero. Otherwise the password length must be 8
	 * 
	 * @param strPassword   the value of the password to be checked
	 * @param whichPassword the name of the password to be checked, for exception
	 *                      throwing purposes
	 * @throws Exception if strPassword is not null and strPassword length is empty
	 *                   or if password length is one and value is different to '0'
	 *                   or if password length is different to eight
	 */
	private void validatePassword(String strPassword, String whichPassword) throws Exception {

		if (strPassword != null && strPassword.isEmpty()) {
			throw new Exception(whichPassword + " password is empty");
		}

		if ((strPassword.length() == 1 && !strPassword.equals("0")) && (strPassword.length() != 8)) {
			throw new Exception(whichPassword + " password must be 8 characters or value of 0");
		}
	}

	private Object getAttributeValue(AttributeList attributes, String attributeName) throws Exception {

		for (Attribute attr : attributes.asList()) {

			if (attr.getName().equals(attributeName)) {
				return attr.getValue();
			}

		}

		throw new Exception("The property " + attributeName + " is required and is not present");

	}

	private Object getNonRequiredAttributeValue(AttributeList attributes, String attributeName) throws Exception {

		for (Attribute attr : attributes.asList()) {

			if (attr.getName().equals(attributeName)) {
				return attr.getValue();
			}

		}

		return null;

	}

	/**
	 * Validate the remainder of value / blockLength is zero.
	 * 
	 * @param value       the value to be checked
	 * @param blockLength the length of block the value has to satisfy
	 * @param valueName   the name of the value to be checked, to be put in the
	 *                    exception message if it fails
	 * @throws Exception if the remainder of value / blockLength is different to
	 *                   zero
	 */
	private void checkBlockLengthReminder(String value, int blockLength, String valueName) throws Exception {

		int reminder = value.length() % blockLength;
		if (reminder != 0) {
			throw new Exception("The value for " + valueName + " has a wrong length of " + value.length()
					+ ". It is expected this length to be a multiple of " + blockLength);
		}

	}

	/**
	 * Validate that properties for a readzone are valid and there is at least the
	 * minimum required properties
	 * 
	 * @param attributes list of attributes to validate
	 * @throws Exception if there is a non valid property or the properties list has
	 *                   an invalid property for a readzone
	 */
	private void validateReadzoneProperties(AttributeList attributes) throws Exception {

		for (Attribute attribute : attributes.asList()) {

			// Validate if this property is valid for readzone
			boolean isValidProperty = false;
			for (int i = 0; i < ReadZoneValidProperties.length; i++) {

				if (attribute.getName().trim().equals(ReadZoneValidProperties[i])) {
					isValidProperty = true;
					break;
				}

			}

			if (!isValidProperty) {

				String validProperties = "";
				for (int i = 0; i < ReadZoneValidProperties.length; i++) {
					validProperties += ReadZoneValidProperties[i] + "\n";
				}

				throw new Exception("Invalid property " + attribute.getName() + " for readzone. Valid ones are: "
						+ validProperties);
			}

		}
	}

	private void validateMinumimRequiredReadzoneProperties(AttributeList attributes) throws Exception {

		// Validate all minimum required properties are provided
		for (int i = 0; i < ReadZoneRequiredProperties.length; i++) {

			boolean requiredPropertyFound = false;
			for (Attribute attribute : attributes.asList()) {

				if (ReadZoneRequiredProperties[i].equals(attribute.getName().trim())) {
					requiredPropertyFound = true;
					break;
				}

			}

			if (!requiredPropertyFound) {

				String requiredProperties = "";
				for (int j = 0; j < ReadZoneRequiredProperties.length; j++) {
					requiredProperties += ReadZoneRequiredProperties[j] + "\n";
				}

				throw new Exception("Required property " + ReadZoneRequiredProperties[i]
						+ " not found. Required ones are : " + requiredProperties);

			}

		}

	}

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
	 * @param response the response to allow CORS
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

	private class CurrentTagsAggregator implements Runnable {

		public CurrentTagsSubscriber subscriber;
		public RawTagMonitoringService service;
		public ReadZone readzone;

		public CurrentTagsAggregator(CurrentTagsSubscriber subscriber, ReadZone readzone,
				RawTagMonitoringService service) {
			this.subscriber = subscriber;
			this.service = service;
			this.readzone = readzone;
		}

		public void initstart() {
			this.service.subscribe(subscriber, readzone);
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// Don't care
			} finally {
				this.service.unsubscribe(subscriber);
			}
		}

		@Override
		public void run() {
		}
	}

	private class CurrentTagsSubscriber implements RawTagSubscriber {

		public Map<String, CurrentTagDTO> currenttags;

		public CurrentTagsSubscriber(Map<String, CurrentTagDTO> currenttags) {
			this.currenttags = currenttags;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see org.rifidi.edge.api.service.tagmonitor.RawTagSubscriber#tagArrived
		 * (org.rifidi.edge.notification.TagReadEvent)
		 */
		@Override
		public void tagArrived(TagReadEvent tag) {
			CurrentTagDTO dto = new CurrentTagDTO();
			dto.setId(tag.getTag().getFormattedID());
			dto.setAntenna(tag.getAntennaID());
			dto.setReader(tag.getReaderID());
			dto.setTimestamp(tag.getTimestamp());
			try {
				Object rssi = tag.getExtraInformation().get(StandardTagReadEventFieldNames.RSSI);
				if (!(rssi == null)) {
					String signalStrength = null;
					if (rssi instanceof Float || rssi instanceof Integer) {
						signalStrength = String.valueOf(rssi);
					}
					dto.setRssi(signalStrength);
				}
			} catch (Exception e) {
				dto.setRssi("0");
			}
			this.currenttags.put(tag.getTag().getFormattedID() + tag.getReaderID(), dto);
		}

	}

	/*
	 * private List<TagReadEvent> getCurrentTags(String readerID) {
	 * 
	 * List<TagReadEvent> currentTags = new LinkedList<TagReadEvent>();
	 * EPOnDemandQueryResult result =
	 * executeQuery("select * from curtags where readerID=\"" + readerID + "\""); if
	 * (result.getArray() != null) { for (EventBean event : result.getArray()) {
	 * TagReadEvent tag = (TagReadEvent) event.getUnderlying();
	 * currentTags.add(tag); } } return currentTags; }
	 */

}
