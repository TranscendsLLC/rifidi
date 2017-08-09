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
import java.io.StringWriter;
import java.io.Writer;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.MBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.restlet.Application;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.resource.Directory;
import org.restlet.routing.Router;
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
import org.rifidi.edge.sensors.AbstractSensorFactory;
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

	public static final String SUCCESS_MESSAGE = "Success";

	public static final String FAIL_MESSAGE = "Fail";

	public static final String WARNING_STATE = "Warning";

	public static final String ReaderIDPropertyName = "readerID";

	public static final String[] ReadZoneRequiredProperties = new String[] { ReaderIDPropertyName };

	public static final String[] ReadZoneValidProperties = new String[] { ReaderIDPropertyName, "antennas", "tagPattern", "matchPattern" };

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorManagerService;

	/** The command manager service for command commands */
	public CommandManagerService commandManagerService;
	
	private RestletHelper restletHelper; 

	/** The Provisioning Service */
	private volatile ProvisioningService provisioningService;

	/** Configuration Service */
	private volatile ConfigurationService configService;

	/** Esper service */
	@SuppressWarnings("unused")
	private EsperManagementService esperService;

	/**  */
	private AppManager appManager;

	/** */
	private ReaderDAO readerDAO;

	private CommandDAO commandDAO;
	
	private RawTagMonitoringService rawTagMonitoringService;

	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());

	@Override
	public Restlet createInboundRoot() {
		return this.initRestlet();
	}

	public Router initRestlet() {
		final SensorManagerServiceRestletImpl self = this;
		
		this.restletHelper = new RestletHelper(this.sensorManagerService, this.commandManagerService, this.readerDAO, this.commandDAO);
		LLRPRestletHelper llrpHelper = new LLRPRestletHelper(this.restletHelper);

		Restlet readers = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("readers requested");
				restletHelper.setResponseHeaders(request, response);

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
				restletHelper.setResponseHeaders(request, response);

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
				restletHelper.setResponseHeaders(request, response);

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
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet startSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("startSession requested");
					restletHelper.setResponseHeaders(request, response);

					String strReaderId = (String) request.getAttributes().get("readerID");
					String strSessionID = (String) request.getAttributes().get("sessionID");

					SessionStatus checkSessionState = sensorManagerService.getSession(strReaderId, strSessionID).getStatus();

					if (!checkSessionState.equals(SessionStatus.PROCESSING) && !checkSessionState.equals(SessionStatus.CONNECTING)) {
						sensorManagerService.startSession(strReaderId, strSessionID);

						SessionStatus currentSessionState = checkSessionState(strReaderId, strSessionID, SessionStatus.PROCESSING);

						if (currentSessionState.equals(SessionStatus.PROCESSING)) {

							// Generate a success message
							response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);

						} else {

							// Generate a failure message with
							// currentSessionStatus
							response.setEntity(
									self.generateReturnString(self.generateErrorMessage("Session already started, current state is " + currentSessionState
											+ "  - See Rifidi Edge Sever Log for details", currentSessionState.toString())), MediaType.TEXT_XML);

						}
					} else {
						response.setEntity(self.generateReturnString(self.generateErrorMessage("Unable to start session, current state is " + checkSessionState, checkSessionState.toString())),
								MediaType.TEXT_XML);
					}
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}

		};
		Restlet stopSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("stopSession requested");
					restletHelper.setResponseHeaders(request, response);

					String strReaderId = (String) request.getAttributes().get("readerID");
					String strSessionID = (String) request.getAttributes().get("sessionID");

					sensorManagerService.stopSession(strReaderId, strSessionID);

					SessionStatus currentSessionState = checkSessionState(strReaderId, strSessionID, SessionStatus.CLOSED);

					if (currentSessionState.equals(SessionStatus.CLOSED)) {

						// Generate a success message
						response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);

					} else {

						// Generate a failure message with currentSessionStatus
						response.setEntity(self.generateReturnString(self.generateErrorMessage("Unable to stop session, current state is " + currentSessionState
								+ "  - See Rifidi Edge Sever Log for details", currentSessionState.toString())), MediaType.TEXT_XML);

					}
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
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
					restletHelper.setResponseHeaders(request, response);

					sensorManagerService.createSession(readerID);
					CreateSessionResponseMessageDTO sr = new CreateSessionResponseMessageDTO();
					sr.setSessionID(1); // The sessionID for any reader should
										// be 1.
					response.setEntity(self.generateReturnString(sr), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};
		Restlet deleteSession = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("deleteSession requested");
					restletHelper.setResponseHeaders(request, response);
					sensorManagerService.deleteSession((String) request.getAttributes().get("readerID"), (String) request.getAttributes().get("sessionID"));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet deleteReader = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("deleteReader requested");
					restletHelper.setResponseHeaders(request, response);
					sensorManagerService.deleteReader((String) request.getAttributes().get("readerID"));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet deleteCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("deleteCommand requested");
					restletHelper.setResponseHeaders(request, response);
					commandManagerService.deleteCommand((String) request.getAttributes().get("commandID"));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet executeCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					logger.info("executeCommand requested");
					restletHelper.setResponseHeaders(request, response);

					sensorManagerService.submitCommand((String) request.getAttributes().get("readerID"), (String) request.getAttributes().get("sessionID"),
							(String) request.getAttributes().get("commandID"), Long.parseLong((String) request.getAttributes().get("repeatInterval")), TimeUnit.MILLISECONDS);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (NumberFormatException nEx) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(nEx.getMessage(), null)), MediaType.TEXT_XML);
				} catch (CommandSubmissionException cEx) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(cEx.getMessage(), null)), MediaType.TEXT_XML);

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}

			}
		};
		Restlet setProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("setProperties requested");
				try {
					restletHelper.setResponseHeaders(request, response);

					String strObjectId = (String) request.getAttributes().get("readerID");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					boolean readerExists = restletHelper.readerExists(strObjectId);
					boolean commandExists = restletHelper.commandExists(strObjectId);

					// Check if command or reader exists before submit
					// getproperties
					if (!commandExists && !readerExists) {
						throw new Exception("Neither reader nor command with id " + strObjectId + " exist");
					}

					AttributeList attributes = restletHelper.getProcessedAttributes(strPropAttr);

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

					response.setEntity(self.generateReturnString(self.generateWarningMessage(nEx.getMessage())), MediaType.TEXT_XML);

				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);

				}
			}
		};

		Restlet getProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("getProperties requested");
				try {
					restletHelper.setResponseHeaders(request, response);

					String strObjectId = (String) request.getAttributes().get("readerID");

					// Check if reader or command exists before submit
					// getproperties
					if (!self.restletHelper.commandExists(strObjectId) && !self.restletHelper.readerExists(strObjectId)) {
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

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet createReader = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("createReader requested");

				String readerId = null;

				try {
					restletHelper.setResponseHeaders(request, response);

					String strReaderType = (String) request.getAttributes().get("readerType");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					AttributeList attributes = new AttributeList();
					if (strPropAttr != null) {
						strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

						attributes = restletHelper.getProcessedAttributes(strPropAttr);

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

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)), MediaType.TEXT_XML);

				}

				catch (Exception e) {

					e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);

				}
			}
		};

		Restlet createCommand = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("createCommand requested");
				String strCommandId = null;

				try {
					restletHelper.setResponseHeaders(request, response);

					String strPropAttr = (String) request.getAttributes().get("properties");

					AttributeList attributes = new AttributeList();

					if (strPropAttr != null) {
						// Decode url attributes
						strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

						attributes = restletHelper.getProcessedAttributes(strPropAttr);
					}

					// Create the command
					strCommandId = self.commandManagerService.createCommand((String) request.getAttributes().get("commandType"), attributes);

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

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)), MediaType.TEXT_XML);

				}

				catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);

				}
			}
		};

		Restlet startApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("startApp requested");
				try {
					restletHelper.setResponseHeaders(request, response);

					appManager.startApp((Integer.parseInt((String) request.getAttributes().get("appID"))));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet stopApp = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("stopApp requested");
				try {
					restletHelper.setResponseHeaders(request, response);

					appManager.stopApp((Integer.parseInt((String) request.getAttributes().get("appID"))));
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet readerTypes = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("readerTypes requested");
				try {
					restletHelper.setResponseHeaders(request, response);

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

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet readerMetadata = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("readerMetadata requested");
				try {
					restletHelper.setResponseHeaders(request, response);

					ReaderMetadataResponseMessageDTO rmrmd = new ReaderMetadataResponseMessageDTO();

					String cat_str = "org.rifidi.edge.category";
					String order_value = "org.rifidi.edge.ordervalue";
					String display_name = "displayName";

					List<ReaderFactoryMetadataDTO> readerFactoryMetadataList = new ArrayList<ReaderFactoryMetadataDTO>();
					Set<AbstractSensorFactory<?>> readerfactories = self.readerDAO.getReaderFactories();
					for (AbstractSensorFactory<?> factory : readerfactories) {
						List<ReaderMetadataDTO> readerMetadataList = new ArrayList<ReaderMetadataDTO>();
						MBeanAttributeInfo[] attArray = factory.getServiceDescription(factory.getFactoryID()).getAttributes();
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
								readerMetadata.setDisplayName((String) supp.getDescriptor().getFieldValue(display_name));
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
					Set<AbstractCommandConfigurationFactory<?>> commandfactories = self.commandDAO.getCommandFactories();
					for (AbstractCommandConfigurationFactory<?> factory : commandfactories) {
						List<CommandMetadataDTO> commandMetadataList = new ArrayList<CommandMetadataDTO>();
						MBeanAttributeInfo[] attArray = factory.getServiceDescription(factory.getFactoryID()).getAttributes();
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
								commandMetadata.setDisplayName((String) supp.getDescriptor().getFieldValue(display_name));
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
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet commandTypes = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("commandTypes requested");
				try {
					restletHelper.setResponseHeaders(request, response);

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

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet apps = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("apps requested");
				try {

					restletHelper.setResponseHeaders(request, response);

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

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
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
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};
		Restlet save = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("save requested");
				try {

					restletHelper.setResponseHeaders(request, response);

					configService.storeConfiguration();
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet currenttags = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("currenttags requested");
				try {
					restletHelper.setResponseHeaders(request, response);
					Map<String,CurrentTagDTO> currenttags = new HashMap<String,CurrentTagDTO>();
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

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};


		Restlet ping = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				restletHelper.setResponseHeaders(request, response);

				PingDTO ping = new PingDTO();
				ping.setTimestamp(Long.toString(System.currentTimeMillis()));
				response.setEntity(self.generateReturnString(ping), MediaType.TEXT_XML);
			}
		};

		Restlet getAppProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getAppProperties requested");

					restletHelper.setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);
						Properties appProperties = RifidiEdgeHelper.getApplicationProperties(app.getGroup(), app.getName());

						PropertyResponseMessageDTO responseMessageDTO = new PropertyResponseMessageDTO();
						List<PropertyNameDTO> propertyNameList = new LinkedList<PropertyNameDTO>();

						for (Map.Entry<Object, Object> mapEntry : appProperties.entrySet()) {

							PropertyNameDTO pnd = new PropertyNameDTO();
							pnd.setPropertyName((String) mapEntry.getKey());
							pnd.setPropertyValue((String) mapEntry.getValue());
							propertyNameList.add(pnd);
						}

						responseMessageDTO.setProperties(propertyNameList);

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(), MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet getGroupProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getGroupProperties requested");

					restletHelper.setResponseHeaders(request, response);

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

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(), MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet getReadZones = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getReadZones requested");

					restletHelper.setResponseHeaders(request, response);

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

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(), MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet deleteReadZone = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("deleteReadZone requested");

					restletHelper.setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZone = (String) request.getAttributes().get("readZone");

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);
						RifidiEdgeHelper.deleteReadZone(app.getGroup(), readZone);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet getReadZoneProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("getReadZoneProperties requested");

					restletHelper.setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZoneName = (String) request.getAttributes().get("readZoneName");

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						Properties readZoneProperties = RifidiEdgeHelper.getReadZoneProperties(app.getGroup(), app.getName(), readZoneName);

						PropertyResponseMessageDTO responseMessageDTO = new PropertyResponseMessageDTO();
						List<PropertyNameDTO> propertyNameList = new LinkedList<PropertyNameDTO>();

						for (Map.Entry<Object, Object> mapEntry : readZoneProperties.entrySet()) {

							PropertyNameDTO pnd = new PropertyNameDTO();
							pnd.setPropertyName((String) mapEntry.getKey());
							pnd.setPropertyValue((String) mapEntry.getValue());
							propertyNameList.add(pnd);
						}

						responseMessageDTO.setProperties(propertyNameList);

						response.setEntity(self.generateReturnString(responseMessageDTO).toString(), MediaType.TEXT_XML);

					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet setAppProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				// String strCommandId = null;

				try {

					logger.info("setAppProperties requested");

					restletHelper.setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = restletHelper.getProcessedAttributes(strPropAttr);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.setApplicationProperties(app.getGroup(), app.getName(), attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)), MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);

				}
			}
		};

		Restlet setGroupProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				try {

					logger.info("setGroupProperties requested");

					restletHelper.setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = restletHelper.getProcessedAttributes(strPropAttr);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.setGroupProperties(app.getGroup(), attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)), MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);

				}
			}
		};

		Restlet addReadZone = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				try {

					logger.info("addReadZone requested");

					restletHelper.setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZone = (String) request.getAttributes().get("readZone");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = restletHelper.getProcessedAttributes(strPropAttr);

					// Validate that properties are valid for readzone
					validateReadzoneProperties(attributes);

					// Validate that properties contain the minimum required
					// ones
					validateMinumimRequiredReadzoneProperties(attributes);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.addReadZone(app.getGroup(), readZone, attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)), MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);

				}
			}
		};

		Restlet setReadZoneProperties = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				try {

					logger.info("setReadZoneProperties requested");

					restletHelper.setResponseHeaders(request, response);

					Integer intAppId = Integer.parseInt((String) request.getAttributes().get("appID"));

					String readZone = (String) request.getAttributes().get("readZoneName");

					String strPropAttr = (String) request.getAttributes().get("properties");

					// Decode url attributes
					strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");

					AttributeList attributes = restletHelper.getProcessedAttributes(strPropAttr);

					// Validate that properties are valid for readzone
					validateReadzoneProperties(attributes);

					Map<Integer, RifidiApp> appMap = appManager.getApps();

					if (appMap != null && appMap.get(intAppId) != null) {

						RifidiApp app = appMap.get(intAppId);

						RifidiEdgeHelper.setReadZoneProperties(app.getGroup(), readZone, attributes);

						response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
					} else {

						throw new Exception("Application with id " + intAppId + " does not exist.");

					}

				} catch (NotValidPropertyForObjectException nE) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(nE.getMessage(), null)), MediaType.TEXT_XML);

				}

				catch (Exception e) {

					// e.printStackTrace();
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);

				}
			}
		};

		Restlet updateServersFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					restletHelper.setResponseHeaders(request, response);

					String data = (String) request.getAttributes().get("data");
					String decodedData = URLDecoder.decode(data, "UTF-8");
					RifidiEdgeHelper.updateServersFile(decodedData);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet getServersFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					restletHelper.setResponseHeaders(request, response);

					byte[] data = RifidiEdgeHelper.getServersFile();
					String str = new String(data, "UTF-8");
					response.setEntity(str, MediaType.APPLICATION_JSON);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet updateUIPropertiesFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					restletHelper.setResponseHeaders(request, response);

					String data = (String) request.getAttributes().get("data");
					String decodedData = URLDecoder.decode(data, "UTF-8");
					RifidiEdgeHelper.updateUIPropertiesFile(decodedData);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {

					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet getUIPropertiesFile = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					restletHelper.setResponseHeaders(request, response);

					byte[] data = RifidiEdgeHelper.getUIPropertiesFile();
					String str = new String(data, "UTF-8");
					response.setEntity(str, MediaType.APPLICATION_JSON);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};

		Restlet addDefaultApplication = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					restletHelper.setResponseHeaders(request, response);
					String group = (String) request.getAttributes().get("group");
					RifidiEdgeHelper.addDefaultApp(group);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}

			}
		};
		
		Restlet shutdown = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {
					restletHelper.setResponseHeaders(request, response);
					response.setEntity(self.generateReturnString(self.generateSuccessMessage()), MediaType.TEXT_XML);
					Thread thread = new Thread(new RestletShutdown());
					thread.start();					
				} catch (Exception e) {
					response.setEntity(self.generateReturnString(self.generateErrorMessage(e.toString(), null)), MediaType.TEXT_XML);
				}
			}
		};		
		
		Router router = new Router(getContext().createChildContext());
		
		router.attach("/shutdown", shutdown);
		router.attach("/readers", readers);
		router.attach("/commands", commands);
		router.attach("/readerstatus/{readerID}", readerStatus);
		router.attach("/startsession/{readerID}/{sessionID}", startSession);
		router.attach("/stopsession/{readerID}/{sessionID}", stopSession);
		router.attach("/createsession/{readerID}", createSession);
		router.attach("/deletesession/{readerID}/{sessionID}", deleteSession);
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

		// thinkify commands
		// router.attach("/rcs/{readerID}/{sessionID}", rcs);
		
		// single shot commands

		router.attach("/ping", ping);

		// router to update servers file
		router.attach("/updateServersFile/{data}", updateServersFile);

		// router to get servers file
		router.attach("/getServersFile", getServersFile);

		// router to update ui properties file
		router.attach("/updateUIPropertiesFile/{data}", updateUIPropertiesFile);

		// router to get properties file
		router.attach("/getUIPropertiesFile", getUIPropertiesFile);
		
		llrpHelper.initLLRP(router);
		
		// Attach web administration dashboard app
		String appPath = "file:///" + System.getProperty("org.rifidi.home") + File.separator + "admin" + File.separator + "app" + File.separator;

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
	 * Validate if attributes are valid for reader or command id
	 * 
	 * @param strObjectId
	 *            the id of reader or command
	 * @param attributes
	 *            the lsit of attributes to validate
	 * @throws NotValidPropertyForObjectException
	 *             if there is a non valid property for reader or command
	 */
	private void validateAttributesForReaderOrCommand(String strObjectId, AttributeList attributes) throws NotValidPropertyForObjectException {

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
			NotValidPropertyForObjectException notValidPropertyForObjectException = new NotValidPropertyForObjectException("Not valid properties: "
					+ getFormatedListOfNonValidProperties(notValidPropertiesList));

			notValidPropertyForObjectException.setNotValidPropertiesList(notValidPropertiesList);

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
	 * Validate that properties for a readzone are valid and there is at least
	 * the minimum required properties
	 * 
	 * @param attributes
	 *            list of attributes to validate
	 * @throws Exception
	 *             if there is a non valid property or the properties list has
	 *             an invalid property for a readzone
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

				throw new Exception("Invalid property " + attribute.getName() + " for readzone. Valid ones are: " + validProperties);
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

				throw new Exception("Required property " + ReadZoneRequiredProperties[i] + " not found. Required ones are : " + requiredProperties);

			}

		}

	}

	private class CurrentTagsAggregator implements Runnable {

		public CurrentTagsSubscriber subscriber;
		public RawTagMonitoringService service;
		public ReadZone readzone;

		public CurrentTagsAggregator(CurrentTagsSubscriber subscriber, ReadZone readzone, RawTagMonitoringService service) {
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

		public Map<String,CurrentTagDTO> currenttags;

		public CurrentTagsSubscriber(Map<String,CurrentTagDTO> currenttags) {
			this.currenttags = currenttags;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.rifidi.edge.api.service.tagmonitor.RawTagSubscriber#tagArrived
		 * (org.rifidi.edge.notification.TagReadEvent)
		 */
		@Override
		public void tagArrived(TagReadEvent tag) {
			CurrentTagDTO dto = new CurrentTagDTO();
			dto.setId(tag.getTag().getFormattedID());
			dto.setAntenna(tag.getAntennaID());
			dto.setReader(tag.getReaderID());
			dto.setTimestamp(tag.getTimestamp());
			Serializable rssi = tag.getExtraInformation().get(StandardTagReadEventFieldNames.RSSI);
			if(!rssi.equals(null)) {
				dto.setRssi((String)rssi);
			}
			this.currenttags.put(tag.getTag().getFormattedID()+tag.getReaderID(),dto);
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
