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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.Server;
import org.restlet.data.MediaType;
import org.restlet.data.Protocol;
import org.restlet.resource.Get;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Router;
import org.rifidi.edge.api.CommandConfigurationDTO;
import org.rifidi.edge.api.CommandDTO;
import org.rifidi.edge.api.CommandManagerService;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.RifidiApp;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.api.service.appmanager.AppManager;
import org.rifidi.edge.configuration.ConfigurationService;

/**
 * This class handles the incoming restlet requests.
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class SensorManagerServiceRestletImpl extends Application {

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorManagerService;

	/** The command manager service for command commands */
	public CommandManagerService commandManagerService;

	/**  */
	public AppManager appManager;

	/**
	 * 
	 */
	public SensorManagerServiceRestletImpl() {

		System.out.println("CALLED RESTLET CONSTRUCTOR *********");
		// Create the HTTP server and listen on port 8182
		// try {
		// new Server(Protocol.HTTP, 8182, RestletServer.class).start();
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		// this.initRestlet();
		// this.initTest();
	}

	@Get
	public String toString() {
		return "hello, world";
	}

	@Override
	public Restlet createInboundRoot() {
		System.out.println("Hit createInboundRoot!");
		// Router router = new Router(getContext());
		// Restlet hello = new Restlet() {
		// @Override
		// public void handle(Request request, Response response) {
		// System.out.println("hit hello");
		// response.setEntity("hello", MediaType.TEXT_PLAIN);
		// }
		// };
		// Restlet world = new Restlet() {
		// @Override
		// public void handle(Request request, Response response) {
		// System.out.println("hit world");
		// response.setEntity("world", MediaType.TEXT_PLAIN);
		// }
		// };
		// router.attach("/hello/{name}", HelloWorldResource.class);
		// router.attach("/world", world);
		//
		// return router;
		return this.initRestlet();
	}

	public Router initRestlet() {
		final SensorManagerServiceRestletImpl self = this;

		Restlet hello = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				System.out.println("hit hello");
				response.setEntity("hello", MediaType.TEXT_PLAIN);
			}
		};
		Restlet world = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				System.out.println("hit world");
				response.setEntity("world", MediaType.TEXT_PLAIN);
			}
		};
		Restlet readers = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				System.out.println("hit readers");
				Set<ReaderDTO> dtos = sensorManagerService.getReaders();
				for (ReaderDTO dto : dtos) {
					response.getAttributes().put(dto.getReaderID(),
							dto.getReaderFactoryID());
				}
			}
		};

		Restlet commands = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				Set<CommandConfigurationDTO> dtos = commandManagerService
						.getCommands();
				for (CommandConfigurationDTO dto : dtos) {
					response.getAttributes().put(dto.getCommandConfigID(),
							dto.getCommandConfigFactoryID());
				}
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
				System.out.println("Startsession called");
				sensorManagerService.startSession((String) request
						.getAttributes().get("readerID"), (String) request
						.getAttributes().get("sessionID"));
			}
		};
		Restlet stopSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				System.out.println("Stopsession called");
				sensorManagerService.stopSession((String) request
						.getAttributes().get("readerID"), (String) request
						.getAttributes().get("sessionID"));
			}
		};
		Restlet createSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				sensorManagerService.createSession((String) request
						.getAttributes().get("readerID"));
			}
		};
		Restlet deleteSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				sensorManagerService.deleteSession((String) request
						.getAttributes().get("readerID"), (String) request
						.getAttributes().get("sessionID"));
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
				} catch (NumberFormatException | CommandSubmissionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
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
				} catch (Exception e) {
					// TODO Handle exception
					e.printStackTrace();
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
				} catch (Exception e) {
					// TODO Handle exception
					e.printStackTrace();
				}
			}
		};

		Restlet startApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				appManager.startApp((Integer) request.getAttributes().get(
						"appID"));
			}
		};

		Restlet stopApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				appManager.stopApp((Integer) request.getAttributes().get(
						"appID"));
			}
		};
		Restlet apps = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				Map<Integer, RifidiApp> apps = appManager.getApps();
				for (Integer i : apps.keySet()) {
					response.getAttributes().put(apps.get(i).getName(), i);
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
		router.attach("/executeCommand/{readerID}/{sessionID}/{commandID}/{repeatInterval}",
				executeCommand);
		router.attach("/setProperties/{readerID}/{properties}", setProperties);
		router.attach("/createReader/{readerType}/{properties}", createReader);
		router.attach("/startApp/{appID}", startApp);
		router.attach("/stopApp/{appID}", stopApp);
		router.attach("/apps", apps);
		router.attach("/hello/{name}", HelloWorldResource.class);
		router.attach("/world", world);
		System.out.println("Finished attaching routers");
		return router;
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
