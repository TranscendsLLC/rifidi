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

import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.routing.Router;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;
import org.rifidi.edge.api.CommandConfigFactoryDTO;
import org.rifidi.edge.api.CommandConfigurationDTO;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.ReaderFactoryDTO;
import org.rifidi.edge.api.RifidiApp;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.service.appmanager.AppManager;
import org.rifidi.edge.configuration.Configuration;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.rest.exception.NotValidPropertyForObjectException;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;
import org.rifidi.edge.services.EsperManagementService;

/**
 * This class handles the incoming rest requests.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class SensorManagerServiceRestletImpl extends Application {

	public static final String SUCCESS_MESSAGE = "Success";

	public static final String FAIL_MESSAGE = "Fail";

	public static final String WARNING_STATE = "Warning";

	public static final String[] LlrpReaderTypeArrray = new String[] { "LLRP" };

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorManagerService;

	/** The command manager service for command commands */
	public CommandManagerService commandManagerService;

	/** Configuration Service */
	private volatile ConfigurationService configService;

	/** Esper service */
	private EsperManagementService esperService;

	/**  */
	public AppManager appManager;

	/** */
	public ReaderDAO readerDAO;

	@Override
	public Restlet createInboundRoot() {
		return this.initRestlet();
	}

	public Router initRestlet() {
		final SensorManagerServiceRestletImpl self = this;

		Restlet readers = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
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
				response.setEntity(self.generateReturnString(rrmd),
						MediaType.TEXT_XML);
			}
		};

		Restlet commands = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				Set<CommandConfigurationDTO> dtos = commandManagerService
						.getCommands();
				List<CommandNameDTO> cnd = new LinkedList<CommandNameDTO>();
				for (CommandConfigurationDTO dto : dtos) {
					CommandNameDTO c = new CommandNameDTO();
					c.setCommandID(dto.getCommandConfigID());
					c.setCommandType(dto.getCommandConfigFactoryID());
					cnd.add(c);
				}
				CommandResponseMessageDTO crmd = new CommandResponseMessageDTO();
				crmd.setCommands(cnd);
				response.setEntity(self.generateReturnString(crmd),
						MediaType.TEXT_XML);
			}
		};

		Restlet readerStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				Set<ReaderDTO> dtos = sensorManagerService.getReaders();
				ReaderStatusResponseMessageDTO rsrmd = new ReaderStatusResponseMessageDTO();
				for (ReaderDTO dto : dtos) {
					if (dto.getReaderID().equals(
							request.getAttributes().get("readerID"))) {
						ReaderNameDTO r = new ReaderNameDTO();
						r.setReaderID(dto.getReaderID());
						r.setReaderType(dto.getReaderFactoryID());
						rsrmd.setReader(r);
						// response.getAttributes().put(dto.getReaderID(),
						// dto.getReaderFactoryID());
						List<SessionNameDTO> slist = new LinkedList<SessionNameDTO>();
						for (SessionDTO sdto : dto.getSessions()) {
							SessionNameDTO snd = new SessionNameDTO();
							snd.setSessionId(sdto.getID());
							snd.setSessionStatus(sdto.getStatus().toString());
							// response.getAttributes().put(sdto.getID(),
							// sdto.getStatus());
							slist.add(snd);
						}
						rsrmd.setSessions(slist);
						response.setEntity(self.generateReturnString(rsrmd),
								MediaType.TEXT_XML);
						break;
					}
				}
			}
		};

		Restlet startSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					String strReaderId = (String) request.getAttributes().get(
							"readerID");
					String strSessionID = (String) request.getAttributes().get(
							"sessionID");

					sensorManagerService
							.startSession(strReaderId, strSessionID);

					SessionStatus currentSessionState = checkSessionState(
							strReaderId, strSessionID, SessionStatus.PROCESSING);

					if (currentSessionState.equals(SessionStatus.PROCESSING)) {

						// Generate a success message
						response.setEntity(self.generateReturnString(self
								.generateSuccessMessage()), MediaType.TEXT_XML);

					} else {

						// Generate a failure message with currentSessionStatus
						response.setEntity(
								self.generateReturnString(self
										.generateErrorMessage(
												"Unable to start session, current state is "
														+ currentSessionState
														+ "  - See Rifidi Edge Sever Log for details",
												currentSessionState.toString())),
								MediaType.TEXT_XML);

					}

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}

		};
		Restlet stopSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					String strReaderId = (String) request.getAttributes().get(
							"readerID");
					String strSessionID = (String) request.getAttributes().get(
							"sessionID");

					sensorManagerService.stopSession(strReaderId, strSessionID);

					SessionStatus currentSessionState = checkSessionState(
							strReaderId, strSessionID, SessionStatus.CLOSED);

					if (currentSessionState.equals(SessionStatus.CLOSED)) {

						// Generate a success message
						response.setEntity(self.generateReturnString(self
								.generateSuccessMessage()), MediaType.TEXT_XML);

					} else {

						// Generate a failure message with currentSessionStatus
						response.setEntity(
								self.generateReturnString(self
										.generateErrorMessage(
												"Unable to stop session, current state is "
														+ currentSessionState
														+ "  - See Rifidi Edge Sever Log for details",
												currentSessionState.toString())),
								MediaType.TEXT_XML);

					}
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet createSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					sensorManagerService.createSession((String) request
							.getAttributes().get("readerID"));
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet deleteSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					sensorManagerService.deleteSession((String) request
							.getAttributes().get("readerID"), (String) request
							.getAttributes().get("sessionID"));
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet executeCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					sensorManagerService.submitCommand((String) request
							.getAttributes().get("readerID"), (String) request
							.getAttributes().get("sessionID"), (String) request
							.getAttributes().get("commandID"), Long
							.parseLong((String) request.getAttributes().get(
									"repeatInterval")), TimeUnit.MILLISECONDS);
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (NumberFormatException | CommandSubmissionException e) {
					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}

			}
		};
		Restlet setProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					String strObjectId = (String) request.getAttributes().get(
							"readerID");

					String strPropAttr = (String) request.getAttributes().get(
							"properties");

					boolean readerExists = readerExists(strObjectId);
					boolean commandExists = commandExists(strObjectId);

					// Check if command or reader exists before submit
					// getproperties
					if (!commandExists && !readerExists) {
						throw new Exception(
								"Neither reader nor command with id "
										+ strObjectId + " exist");
					}

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					// Check if reader id exists
					if (readerExists) {

						// Set properties for reader, if parameter is a reader
						// id
						sensorManagerService.setReaderProperties(strObjectId,
								attributes);

					} else if (commandExists) { // Check if command exists

						// Set properties for command, if parameter is a command
						// id
						commandManagerService.setCommandProperties(strObjectId,
								attributes);
					}

					// Validate properties for this reader or command
					validateAttributesForReaderOrCommand(strObjectId,
							attributes);

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);

				} catch (NotValidPropertyForObjectException nEx) {

					response.setEntity(self.generateReturnString(self
							.generateWarningMessage(nEx.getMessage())),
							MediaType.TEXT_XML);

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet getProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					String strObjectId = (String) request.getAttributes().get(
							"readerID");

					// Check if reader or command exists before submit
					// getproperties
					if (!commandExists(strObjectId)
							&& !readerExists(strObjectId)) {
						throw new Exception(
								"Neither reader nor command with id "
										+ strObjectId + " exist");
					}

					Configuration configuration = configService
							.getConfiguration(strObjectId);
					PropertyResponseMessageDTO prmd = new PropertyResponseMessageDTO();
					List<PropertyNameDTO> pndList = new LinkedList<PropertyNameDTO>();
					for (String key : configuration.getAttributes().keySet()) {
						PropertyNameDTO pnd = new PropertyNameDTO();
						pnd.setPropertyName(key);
						if (configuration.getAttributes().get(key) != null) {
							pnd.setPropertyValue(configuration.getAttributes()
									.get(key).toString());
						} else {
							pnd.setPropertyValue("null");
						}
						pndList.add(pnd);
					}
					prmd.setProperties(pndList);
					// System.out.println(self.generateReturnString(prmd)
					// .toString());
					response.setEntity(self.generateReturnString(prmd)
							.toString(), MediaType.TEXT_XML);

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet createReader = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				String readerId = null;

				try {

					String strReaderType = (String) request.getAttributes()
							.get("readerType");

					String strPropAttr = (String) request.getAttributes().get(
							"properties");

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					// Create reader
					readerId = sensorManagerService.createReader(strReaderType,
							attributes);

					// Validate what properties are wrong for this created
					// reader
					validateAttributesForReaderOrCommand(readerId, attributes);

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);

				} catch (NotValidPropertyForObjectException nE) {

					// If there was an invalid property creating the reader,
					// and the reader was created, the reader must be deleted
					if (readerId != null) {
						sensorManagerService.deleteReader(readerId);
					}

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet createCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				String strCommandId = null;

				try {

					String strPropAttr = (String) request.getAttributes().get(
							"properties");

					AttributeList attributes = getProcessedAttributes(strPropAttr);

					// Create the command
					strCommandId = self.commandManagerService
							.createCommand((String) request.getAttributes()
									.get("commandType"), attributes);

					// Validate properties for this command
					validateAttributesForReaderOrCommand(strCommandId,
							attributes);

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);

				} catch (NotValidPropertyForObjectException nE) {

					// If there was an invalid property creating the command,
					// and the command was created, the command must be deleted
					if (strCommandId != null) {
						commandManagerService.deleteCommand(strCommandId);
					}

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(nE.getMessage(), null)),
							MediaType.TEXT_XML);

				}

				catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);

				}
			}
		};

		Restlet startApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					appManager.startApp((Integer.parseInt((String) request
							.getAttributes().get("appID"))));
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet stopApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					appManager.stopApp((Integer.parseInt((String) request
							.getAttributes().get("appID"))));
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet readerTypes = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					ReaderTypesReponseMessageDTO rtr = new ReaderTypesReponseMessageDTO();
					Set<ReaderFactoryDTO> grf = self.sensorManagerService
							.getReaderFactories();
					List<ReaderTypeDTO> ret = new LinkedList<ReaderTypeDTO>();
					for (ReaderFactoryDTO rfd : grf) {
						ReaderTypeDTO rtd = new ReaderTypeDTO();
						rtd.setReaderDesc(rfd.getReaderFactoryDescription());
						rtd.setReaderType(rfd.getReaderFactoryID());
						ret.add(rtd);
					}
					rtr.setSensors(ret);
					response.setEntity(self.generateReturnString(rtr)
							.toString(), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet commandTypes = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					CommandTypesResponseMessageDTO rtr = new CommandTypesResponseMessageDTO();
					Set<CommandConfigFactoryDTO> grf = self.commandManagerService
							.getCommandConfigFactories();
					List<CommandTypeDTO> ret = new LinkedList<CommandTypeDTO>();
					for (CommandConfigFactoryDTO rfd : grf) {
						CommandTypeDTO rtd = new CommandTypeDTO();
						rtd.setCommandDesc(rfd.getDescription());
						rtd.setCommandType(rfd.getCommandFactoryID());
						ret.add(rtd);
					}
					rtr.setCommands(ret);
					response.setEntity(self.generateReturnString(rtr)
							.toString(), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet apps = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					Map<Integer, RifidiApp> apps = appManager.getApps();
					List<AppNameDTO> appNames = new LinkedList<AppNameDTO>();
					for (Integer i : apps.keySet()) {
						AppNameDTO and = new AppNameDTO();
						and.setAppName(apps.get(i).getGroup() + ":"
								+ apps.get(i).getName());
						and.setAppNumber(Integer.toString(i));
						and.setAppStatus(apps.get(i).getState().toString());
						appNames.add(and);
					}
					AppResponseMessageDTO armd = new AppResponseMessageDTO();
					armd.setApps(appNames);
					response.setEntity(self.generateReturnString(armd),
							MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};
		Restlet save = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					configService.storeConfiguration();
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.toString(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet llrpEncode = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					String strReaderId = (String) request.getAttributes().get(
							"readerID");

					Object objSessionId = request.getAttributes().get(
							"sessionID");

					// Check if reader id exists
					if (!readerExists(strReaderId)) {
						throw new Exception("Reader with id " + strReaderId
								+ " does not exist");
					}

					AbstractSensor<?> sensor = readerDAO
							.getReaderByID(strReaderId);

					// look up the associated service configuration object
					Configuration config = configService
							.getConfiguration(strReaderId);

					// Check if reader id is a LLRP reader type
					String strCurrentReaderType = sensor.getDTO(config)
							.getReaderFactoryID();
					boolean isLlrpReaderType = false;

					// Iterate over defined llrp reader types
					for (int i = 0; i < LlrpReaderTypeArrray.length; i++) {

						// If current reader type matches a defined llrp reader
						// type
						if (strCurrentReaderType
								.equals(LlrpReaderTypeArrray[i])) {

							isLlrpReaderType = true;
							break;
						}
					}

					if (!isLlrpReaderType) {

						// It is not a llrp reader type
						throw new Exception("Reader with id " + strReaderId
								+ " of type " + strCurrentReaderType
								+ " is not a LLRP reader type");

					}

					Map<String, SensorSession> sessionMap = sensor
							.getSensorSessions();

					// Check if session id exists
					if (sessionMap.containsKey(objSessionId)) {

						LLRPReaderSession session = (LLRPReaderSession) sessionMap
								.get(objSessionId);

						// Validate no current operations on session are
						// running, and response to user if so
						if (session.isRunningLLRPEncoding()) {

							throw new Exception(
									"Session with id "
											+ objSessionId
											+ " of reader with id "
											+ strReaderId
											+ " is currently in the middle of encoding operations. Try again in a while");

						}

						// TODO Check if there is more than one tag in the scope
						// of this reader, if so then fail
						boolean thereAreMultipleTags = false;

						if (thereAreMultipleTags) {

							// There is more than one tag in the scope of the
							// reader
							throw new Exception(
									"There are more than one tag for reader with id "
											+ strReaderId);

						}

						// There is only one tag in the scope of this reader
						// for session object
						// Get jvm properties
						setLlrpEncodeJvmProperties(session);

						// Get the tag id from url
						String strTag = (String) request.getAttributes().get(
								"tag");

						session.llrpEncode(strTag);

						/*
						 * TODO delete session.addAccessSpec((String)
						 * request.getAttributes() .get("password"), (String)
						 * request .getAttributes().get("tag"));
						 */

					} else {

						// Session id does not exist
						throw new Exception("Session with id " + objSessionId
								+ " does not exist for reader with id "
								+ strReaderId);
					}

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self
							.generateErrorMessage(e.getMessage(), null)),
							MediaType.TEXT_XML);
				}
			}
		};

		Restlet llrpMessage = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					AbstractSensor<?> sensor = readerDAO
							.getReaderByID((String) request.getAttributes()
									.get("readerID"));
					Map<String, SensorSession> sessionMap = sensor
							.getSensorSessions();
					if (sessionMap.containsKey(request.getAttributes().get(
							"sessionID"))) {
						LLRPReaderSession session = (LLRPReaderSession) sessionMap
								.get(request.getAttributes().get("sessionID"));

						SAXBuilder sb = new SAXBuilder();
						Document doc = sb.build((String) request
								.getAttributes().get("llrpmessage"));

						session.sendLLRPMessage(doc);
					}
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

				}
			}
		};

		Router router = new Router(getContext());
		router.attach("/readers", readers);
		router.attach("/commands", commands);
		router.attach("/readerstatus/{readerID}", readerStatus);
		router.attach("/startsession/{readerID}/{sessionID}", startSession);
		router.attach("/stopsession/{readerID}/{sessionID}", stopSession);
		router.attach("/createsession/{readerID}", createSession);
		router.attach("/deletesession/{readerID}/{sessionID}", deleteSession);
		router.attach(
				"/executecommand/{readerID}/{sessionID}/{commandID}/{repeatInterval}",
				executeCommand);
		router.attach("/getproperties/{readerID}", getProperties);
		router.attach("/setproperties/{readerID}/{properties}", setProperties);

		// createreader with properties
		router.attach("/createreader/{readerType}/{properties}", createReader);

		// createreader with no properties
		router.attach("/createreader/{readerType}", createReader);

		// createcommand with properties
		router.attach("/createcommand/{commandType}/{properties}",
				createCommand);

		// createcommand with no properties
		router.attach("/createcommand/{commandType}", createCommand);

		router.attach("/startapp/{appID}", startApp);
		router.attach("/stopapp/{appID}", stopApp);
		router.attach("/commandtypes", commandTypes);
		router.attach("/readertypes", readerTypes);
		router.attach("/apps", apps);
		router.attach("/save", save);
		router.attach("/llrpencode/{readerID}/{sessionID}/{tag}", llrpEncode);
		router.attach(
				"/llrpencode/{readerID}/{sessionID}/{tag}/{operationCode}",
				llrpEncode);
		router.attach("/llrpmessage/{readerID}/{sessionID}/{llrpmessage}",
				llrpMessage);
		return router;
	}

	public RestResponseMessageDTO generateSuccessMessage() {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(SUCCESS_MESSAGE);
		return message;
	}

	public RestResponseMessageDTO generateErrorMessage(String description,
			String currentState) {
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
			JAXBContext jaxbContext = JAXBContext.newInstance(message
					.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(message, writer);
			String content = writer.toString();
			writer.close();
			return content;
		} catch (Exception e) {
			return e.toString();
		}
	}

	// Spring Inject

	public void setSensorManagerService(
			SensorManagerService sensorManagerService) {
		this.sensorManagerService = sensorManagerService;
	}

	public void setCommandManagerService(
			CommandManagerService commandManagerService) {
		this.commandManagerService = commandManagerService;
	}

	public void setAppManager(AppManager appManager) {
		this.appManager = appManager;
	}

	public void setReaderDAO(ReaderDAO readerDAO) {
		this.readerDAO = readerDAO;
	}

	// End Spring Inject

	/**
	 * Sets the configuration service for this class.
	 * 
	 * @param configService
	 *            the configService to set
	 */
	public void setConfigurationService(ConfigurationService configService) {
		this.configService = configService;
	}

	/**
	 * Checks is session of reader id is at desired state before reaching n
	 * attempts every 500ms
	 * 
	 * @param strReaderId
	 *            the reader id
	 * @param strSessionID
	 *            the session id
	 * @param desiredState
	 *            the desired state to check for the session
	 * @return current session state for session
	 */
	private SessionStatus checkSessionState(String strReaderId,
			String strSessionID, SessionStatus desiredState) {

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
			SessionDTO sessionDto = sensorManagerService.getSession(
					strReaderId, strSessionID);

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
	private AttributeList getProcessedAttributes(String propertiesChain)
			throws Exception {

		AttributeList attributes = new AttributeList();

		// Check if propertiesChain has properties to process...
		if (propertiesChain != null && !propertiesChain.isEmpty()) {

			String[] splitProp = propertiesChain.split(";");

			for (String pair : splitProp) {

				String[] prop = pair.split("=");

				// chech if property has property and value
				if (prop.length == 2) {

					// It has property and value
					attributes.add(new Attribute(prop[0], prop[1]));

				} else {

					// Property with no recognizable value, for example
					// Port=123=456, or Port,
					throw new Exception("Property with no recognizable value: "
							+ prop[0]);

				}
			}

		}

		return attributes;
	}

	/**
	 * Checks if reader given by reader id exists
	 * 
	 * @param strReaderIdthe
	 *            reader id to check
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
	 * @param strCommandId
	 *            command id to check
	 * @throws Exception
	 *             if command with command id does not exist
	 */
	private boolean commandExists(String strCommandId) {

		boolean commandExists = false;

		CommandConfigurationDTO commandConfigurationDTO = commandManagerService
				.getCommandConfiguration(strCommandId);
		;

		if (commandConfigurationDTO != null) {

			commandExists = true;
		}

		return commandExists;

	}

	/**
	 * Validate if attributes are valid for reader or command id
	 * 
	 * @param strObjectId
	 *            the id of reader or command
	 * @param attributes
	 *            the lsit of attributes to validate
	 * @throws NotValidPropertyForObjectException
	 *             if there is a non valid property for reader or command
	 */
	private void validateAttributesForReaderOrCommand(String strObjectId,
			AttributeList attributes) throws NotValidPropertyForObjectException {

		// List of non valid property=value pair
		List<String> notValidPropertiesList = new ArrayList<>();

		// Check if properties are valid for this reader or command
		Configuration configuration = configService
				.getConfiguration(strObjectId);

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
				notValidPropertiesList.add(attribute.getName() + "="
						+ attribute.getValue());
			}

		}

		// Check if non valid properties list has elements
		if (!notValidPropertiesList.isEmpty()) {

			// There is at least one non valid property
			NotValidPropertyForObjectException notValidPropertyForObjectException = new NotValidPropertyForObjectException(
					"Not valid properties: "
							+ getFormatedListOfNonValidProperties(notValidPropertiesList));

			notValidPropertyForObjectException
					.setNotValidPropertiesList(notValidPropertiesList);

			throw notValidPropertyForObjectException;
		}

	}

	/**
	 * Returns a string containing the values located inside
	 * notValidPropertiesList and separated by |
	 * 
	 * @param notValidPropertiesList
	 *            the list of properties to process
	 * @return a string containing the values located inside
	 *         notValidPropertiesList and separated by |
	 */
	private String getFormatedListOfNonValidProperties(
			List<String> notValidPropertiesList) {

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
	 * @param session
	 *            the session of reader where the properties are going to be set
	 * @throws Exception
	 *             if there is an invalid lock privilege name from jvm
	 */
	private void setLlrpEncodeJvmProperties(LLRPReaderSession session)
			throws Exception {

		String strTargetEpc = System
				.getProperty("org.rifidi.llrp.encode.targetepc");

		session.setTargetEpc(strTargetEpc != null ? strTargetEpc
				: LLRPReaderSession.DEFAULT_TARGET_EPC);

		String strTagMask = System
				.getProperty("org.rifidi.llrp.encode.tagmask");

		session.setTagMask(strTagMask != null ? strTagMask
				: LLRPReaderSession.DEFAULT_TAG_MASK);

		String strTimeout = System
				.getProperty("org.rifidi.llrp.encode.timeout");

		session.setOperationsTimeout(strTimeout != null ? Integer
				.parseInt(strTimeout)
				: LLRPReaderSession.DEFAULT_OPERATIONS_TIMEOUT);

		String strAccessPwd = System
				.getProperty("org.rifidi.llrp.encode.accesspwd");

		session.setAccessPwd(strAccessPwd != null ? strAccessPwd
				: LLRPReaderSession.DEFAULT_ACCESS_PASSWORD);

		String strKillPwd = System
				.getProperty("org.rifidi.llrp.encode.killpwd");

		session.setKillPwd(strKillPwd != null ? strKillPwd
				: LLRPReaderSession.DEFAULT_KILL_PASSWORD);

		String strKillPwdLock = System
				.getProperty("org.rifidi.llrp.encode.killpwdlock");

		if (strKillPwd != null) {

			int intKillPwdLockPrivilege = LLRPReaderSession
					.getLockPrivilege(strKillPwdLock);
			session.setKillPwdLockPrivilege(intKillPwdLockPrivilege);

		} else {

			session.setKillPwdLockPrivilege(LLRPReaderSession.DEFAULT_KILL_PASSWORD_LOCK_PRIVILEGE);
		}

		String strAccessPwdLock = System
				.getProperty("org.rifidi.llrp.encode.accesspwdlock");

		if (strAccessPwdLock != null) {

			int intAccessPwdLockPrivilege = LLRPReaderSession
					.getLockPrivilege(strAccessPwdLock);
			session.setAccessPwdLockPrivilege(intAccessPwdLockPrivilege);

		} else {

			session.setAccessPwdLockPrivilege(LLRPReaderSession.DEFAULT_ACCESS_PASSWORD_LOCK_PRIVILEGE);
		}

		String strEpcLock = System
				.getProperty("org.rifidi.llrp.encode.epclock");

		if (strEpcLock != null) {

			int intEpcLockPrivilege = LLRPReaderSession
					.getLockPrivilege(strEpcLock);
			session.setEpcLockPrivilege(intEpcLockPrivilege);

		} else {

			session.setEpcLockPrivilege(LLRPReaderSession.DEFAULT_EPC_LOCK_PRIVILEGE);
		}

	}

	/*
	 * private List<TagReadEvent> getCurrentTags(String readerID) {
	 * 
	 * List<TagReadEvent> currentTags = new LinkedList<TagReadEvent>();
	 * EPOnDemandQueryResult result =
	 * executeQuery("select * from curtags where readerID=\"" + readerID +
	 * "\""); if (result.getArray() != null) { for (EventBean event :
	 * result.getArray()) { TagReadEvent tag = (TagReadEvent)
	 * event.getUnderlying(); currentTags.add(tag); } } return currentTags; }
	 */

}
