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
import org.rifidi.edge.api.CommandConfigurationDTO;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.RifidiApp;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.api.service.appmanager.AppManager;

/**
 * This class handles the incoming rest requests.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class SensorManagerServiceRestletImpl extends Application {

	public static final String SUCCESS_MESSAGE = "Success";

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorManagerService;

	/** The command manager service for command commands */
	public CommandManagerService commandManagerService;

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
//					response.getAttributes().put(dto.getReaderID(),
//							dto.getReaderFactoryID());
				}
				ReaderResponseMessageDTO rrmd = new ReaderResponseMessageDTO();
				rrmd.setReaders(rnd);
				response.setEntity(self.generateReturnString(rrmd), MediaType.TEXT_XML);
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
				response.setEntity(self.generateReturnString(crmd), MediaType.TEXT_XML);
			}
		};

		Restlet readerStatus = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				Set<ReaderDTO> dtos = sensorManagerService.getReaders();
				for (ReaderDTO dto : dtos) {
					if (dto.getReaderID().equals(
							request.getAttributes().get("readerID"))) {
						response.getAttributes().put(dto.getReaderID(),
								dto.getReaderFactoryID());
						for (SessionDTO sdto : dto.getSessions()) {
							response.getAttributes().put(sdto.getID(),
									sdto.getStatus());
						}
					}
				}
			}
		};

		Restlet startSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					sensorManagerService.startSession((String) request
							.getAttributes().get("readerID"), (String) request
							.getAttributes().get("sessionID"));
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		};
		Restlet stopSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					sensorManagerService.stopSession((String) request
							.getAttributes().get("readerID"), (String) request
							.getAttributes().get("sessionID"));
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
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
					AttributeList attributes = new AttributeList();
					String propList = (String) request.getAttributes().get(
							"properties");
					String[] splitProp = propList.split(";");
					for (String pair : splitProp) {
						String[] prop = pair.split("=");
						attributes.add(new Attribute(prop[0], prop[1]));
					}

					sensorManagerService.setReaderProperties((String) request
							.getAttributes().get("readerID"), attributes);

					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
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
					String[] splitProp = propList.split(";");
					for (String pair : splitProp) {
						String[] prop = pair.split("=");
						attributes.add(new Attribute(prop[0], prop[1]));
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

		Restlet startApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					appManager.startApp((Integer.parseInt((String) request.getAttributes().get(
							"appID"))));
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
					appManager.stopApp((Integer.parseInt((String) request.getAttributes().get(
							"appID"))));
					response.setEntity(self.generateReturnString(self
							.generateSuccessMessage()), MediaType.TEXT_XML);
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
						and.setAppName(apps.get(i).getGroup()+":"+apps.get(i).getName());
						and.setAppNumber(Integer.toString(i));
						and.setAppStatus(apps.get(i).getState().toString());
						appNames.add(and);
					}
					AppResponseMessageDTO armd = new AppResponseMessageDTO();
					armd.setApps(appNames);
					response.setEntity(self.generateReturnString(armd), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(e.getMessage(), MediaType.TEXT_PLAIN);
				}
			}
		};

		Router router = new Router(getContext());
		router.attach("/readers", readers);
		router.attach("/commands", commands);
		router.attach("/readerStatus/{readerID}", readerStatus);
		router.attach("/startSession/{readerID}/{sessionID}", startSession);
		router.attach("/stopSession/{readerID}/{sessionID}", stopSession);
		router.attach("/createSession/{readerID}", createSession);
		router.attach("/deleteSession/{readerID}/{sessionID}", deleteSession);
		router.attach(
				"/executeCommand/{readerID}/{sessionID}/{commandID}/{repeatInterval}",
				executeCommand);
		router.attach("/setProperties/{readerID}/{properties}", setProperties);
		router.attach("/createReader/{readerType}/{properties}", createReader);
		router.attach("/startApp/{appID}", startApp);
		router.attach("/stopApp/{appID}", stopApp);
		router.attach("/apps", apps);
		return router;
	}

	public String generateSuccessMessage() {
		RestResponseMessageDTO message = new RestResponseMessageDTO();
		message.setMessage(SUCCESS_MESSAGE);
		return message.toString();
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

}
