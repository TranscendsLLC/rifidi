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
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.routing.Router;
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
import org.rifidi.edge.sensors.AbstractSensor;

/**
 * This class handles the incoming rest requests.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class SensorManagerServiceRestletImpl extends Application {

	public static final String SUCCESS_MESSAGE = "Success";

	public static final String ERROR_MESSAGE = "Error";

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorManagerService;

	/** The command manager service for command commands */
	public CommandManagerService commandManagerService;

	/** Configuration Service */
	private volatile ConfigurationService configService;

	/**  */
	public AppManager appManager;

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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
				} catch (Exception e) {
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
				}

			}
		};
		Restlet setProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					
					String strObjectId = (String) request
							.getAttributes().get("readerID");
					
					AttributeList attributes = new AttributeList();
					String propList = (String) request.getAttributes().get(
							"properties");
					String[] splitProp = propList.split(";");
					for (String pair : splitProp) {
						String[] prop = pair.split("=");
						attributes.add(new Attribute(prop[0], prop[1]));
					}

					//Set properties for reader, if parameter is a reader id
					sensorManagerService.setReaderProperties(strObjectId, attributes);
					
					//Set properties for command, if parameter is a command id
					commandManagerService.setCommandProperties(strObjectId, attributes);

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		};

		Restlet getProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					Configuration configuration = configService
							.getConfiguration((String) request.getAttributes()
									.get("readerID"));
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
					System.out.println(self.generateReturnString(prmd)
							.toString());
					response.setEntity(self.generateReturnString(prmd)
							.toString(), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		};

		Restlet createReader = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					AttributeList attributes = new AttributeList();
					String propList = (String) request.getAttributes().get(
							"properties");

					// Check if request has properties to process...
					if (propList != null && !propList.isEmpty()) {
						
						String[] splitProp = propList.split(";");
						for (String pair : splitProp) {
							String[] prop = pair.split("=");
							attributes.add(new Attribute(prop[0], prop[1]));
						}
						
					}

					sensorManagerService.createReader((String) request
							.getAttributes().get("readerType"), attributes);

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		};

		Restlet createCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					AttributeList attributes = new AttributeList();
					String propList = (String) request.getAttributes().get(
							"properties");

					// Check if request has properties to process...
					if (propList != null && !propList.isEmpty()) {

						String[] splitProp = propList.split(";");

						for (String pair : splitProp) {
							String[] prop = pair.split("=");
							attributes.add(new Attribute(prop[0], prop[1]));
						}
					}

					self.commandManagerService.createCommand((String) request
							.getAttributes().get("commandType"), attributes);

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);

				} catch (Exception e) {
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
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
		message.setMessage(ERROR_MESSAGE);
		message.setDescription(description);
		message.setState(currentState);
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
			return e.getMessage();
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

}
