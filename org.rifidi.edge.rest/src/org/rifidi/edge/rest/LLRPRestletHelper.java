/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.StringReader;
import java.net.URLDecoder;
import java.util.Map;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jdom.Document;
import org.jdom.input.SAXBuilder;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.Restlet;
import org.restlet.data.MediaType;
import org.restlet.routing.Router;
import org.rifidi.edge.adapter.llrp.LLRPEncodeMessageDto;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.configuration.Configuration;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.daos.CommandDAO;
import org.rifidi.edge.daos.ReaderDAO;
import org.rifidi.edge.sensors.AbstractSensor;
import org.rifidi.edge.sensors.SensorSession;

/**
 * 
 * 
 * @author Matthew Dean - matt@transcends.co
 */
public class LLRPRestletHelper {
	
	/** Logger for this class */
	private final Log logger = LogFactory.getLog(getClass());
	
	public LLRPRestletHelper(RestletHelper restletHelper, ReaderDAO readerDAO, CommandDAO commandDAO, SensorManagerService sensorManagerService, ConfigurationService configService) {
		this.restletHelper = restletHelper;
		this.readerDAO = readerDAO;
		this.commandDAO = commandDAO;
		this.sensorManagerService = sensorManagerService;
		this.configService = configService;
	}	

	/** The sensor manager service for sensor commands */
	public SensorManagerService sensorManagerService;
	public ReaderDAO readerDAO;
	public CommandDAO commandDAO;
	public RestletHelper restletHelper;
	/** Configuration Service */
	private volatile ConfigurationService configService;

	public static final String[] LlrpReaderTypeArrray = new String[] { "LLRP" };
	
	public enum LLRPGetOperations {
		GET_ROSPECS, GET_READER_CONFIG, GET_READER_CAPABILITIES
	}
	
	/**
	 * Validate the remainder of value / blockLength is zero.
	 * 
	 * @param value
	 *            the value to be checked
	 * @param blockLength
	 *            the length of block the value has to satisfy
	 * @param valueName
	 *            the name of the value to be checked, to be put in the
	 *            exception message if it fails
	 * @throws Exception
	 *             if the remainder of value / blockLength is different to zero
	 */
	private void checkBlockLengthReminder(String value, int blockLength, String valueName) throws Exception {

		int reminder = value.length() % blockLength;
		if (reminder != 0) {
			throw new Exception("The value for " + valueName + " has a wrong length of " + value.length() + ". It is expected this length to be a multiple of " + blockLength);
		}

	}
	
	/**
	 * Validate password is not empty and if it's length is one, the value must
	 * be zero. Otherwise the password length must be 8
	 * 
	 * @param strPassword
	 *            the value of the password to be checked
	 * @param whichPassword
	 *            the name of the password to be checked, for exception throwing
	 *            purposes
	 * @throws Exception
	 *             if strPassword is not null and strPassword length is empty or
	 *             if password length is one and value is different to '0' or if
	 *             password length is different to eight
	 */
	private void validatePassword(String strPassword, String whichPassword) throws Exception {

		if (strPassword != null && strPassword.isEmpty()) {
			throw new Exception(whichPassword + " password is empty");
		}

		if ((strPassword.length() == 1 && !strPassword.equals("0")) && (strPassword.length() != 8)) {
			throw new Exception(whichPassword + " password must be 8 characters or value of 0");
		}
	}

	/**
	 * Set the jvm properties into session object
	 * 
	 * @param session
	 *            the session of reader where the properties are going to be set
	 * @throws Exception
	 *             if there is a validation error on a property
	 */
	private void setLlrpEncodeJvmProperties(LLRPReaderSession session) throws Exception {

		String strTargetEpc = System.getProperty("org.rifidi.llrp.encode.targetepc");

		checkBlockLengthReminder(strTargetEpc, session.getWriteDataBlockLength(), "targetEpc");

		session.setTargetEpc(strTargetEpc != null ? strTargetEpc : LLRPReaderSession.DEFAULT_TARGET_EPC);

		String strTagMask = System.getProperty("org.rifidi.llrp.encode.tagmask");

		checkBlockLengthReminder(strTagMask, session.getWriteDataBlockLength(), "tagMask");

		session.setTagMask(strTagMask != null ? strTagMask : LLRPReaderSession.DEFAULT_TAG_MASK);

		String strTimeout = System.getProperty("org.rifidi.llrp.encode.timeout");

		session.setOperationsTimeout(strTimeout != null ? Integer.parseInt(strTimeout) : LLRPReaderSession.DEFAULT_OPERATIONS_TIMEOUT);

		String strAccessPwd = System.getProperty("org.rifidi.llrp.encode.accesspwd");

		validatePassword(strAccessPwd, "Access");

		session.setAccessPwd(strAccessPwd != null ? strAccessPwd : LLRPReaderSession.DEFAULT_ACCESS_PASSWORD);

		String strOldAccessPwd = System.getProperty("org.rifidi.llrp.encode.oldaccesspwd");

		validatePassword(strOldAccessPwd, "Old access");

		session.setOldAccessPwd(strOldAccessPwd != null ? strOldAccessPwd : LLRPReaderSession.DEFAULT_OLD_ACCESS_PASSWORD);

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
	
	private void llrpGetOperation(Request request, Response response, LLRPGetOperations operation) {
		LLRPReaderSession session = null;

		try {
			String strReaderId = (String) request.getAttributes().get("readerID");

			String strSessionID = (String) request.getAttributes().get("sessionID");

			// Check if reader id exists
			if (!restletHelper.readerExists(strReaderId)) {
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
				throw new Exception("Reader with id " + strReaderId + " of type " + strCurrentReaderType + " is not an LLRP reader type");
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
				throw new Exception("Session with id " + strSessionID + " does not exist for reader with id " + strReaderId);
			}

		} catch (Exception e) {
			// e.printStackTrace();
			response.setEntity(restletHelper.generateReturnString(restletHelper.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
		} finally {
			// cleanup session
			if (session != null) {
				session.cleanupSession();
			}
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

	private void executeLlrpOperation(Request request, Response response, LLRPReaderSession.LLRP_OPERATION_CODE operationCode) {

		LLRPReaderSession session = null;

		try {

			// Variable to receive synchronous response if set
			LLRPEncodeMessageDto llrpEncodeMessageDto = new LLRPEncodeMessageDto();
			// llrpEncodeMessageDto.setStatus("Success !!!");

			String strReaderId = (String) request.getAttributes().get("readerID");

			Object objSessionId = request.getAttributes().get("sessionID");

			// Check if reader id exists
			if (!restletHelper.readerExists(strReaderId)) {
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
				throw new Exception("Reader with id " + strReaderId + " of type " + strCurrentReaderType + " is not an LLRP reader type");

			}

			Map<String, SensorSession> sessionMap = sensor.getSensorSessions();

			// Check if session id exists
			if (sessionMap.containsKey(objSessionId)) {

				session = (LLRPReaderSession) sessionMap.get(objSessionId);

				// Validate no current operations on session are
				// running, and response to user if so
				if (session.isRunningLLRPEncoding()) {

					throw new Exception("Session with id " + objSessionId + " of reader with id " + strReaderId + " is currently in the middle of encoding operations. Try again in a while");

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

						throw new Exception("There are " + numberOfTags + " tags in the scope of the reader with id " + strReaderId);

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
					if(strPropAttr!=null) {
						strPropAttr = URLDecoder.decode(strPropAttr, "UTF-8");
					}

					// Validate that if there are parameters, they are well pair
					// formed values
					AttributeList attributes = restletHelper.getProcessedAttributes(strPropAttr);

					if (operationCode.equals(LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCWrite)) {

						// check the required properties for epc write
						// operation, and overwrite the properties got from jvm

						// check for accesspwd and tag
						String accesspwd = (String) getAttributeValue(attributes, "accesspwd");
						if(accesspwd==null) {
							accesspwd=System.getProperty("org.rifidi.llrp.encode.accesspwd");
						}
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
				 * TODO delete session.addAccessSpec((String)
				 * request.getAttributes() .get("password"), (String) request
				 * .getAttributes().get("tag"));
				 */

			} else {

				// Session id does not exist
				throw new Exception("Session with id " + objSessionId + " does not exist for reader with id " + strReaderId);
			}

			// response.setEntity(self.generateReturnString(self
			// .generateSuccessMessage()), MediaType.TEXT_XML);

			response.setEntity(restletHelper.generateReturnString(llrpEncodeMessageDto), MediaType.TEXT_XML);

		} catch (Exception e) {

			// test ini
			// LLRPEncodeMessageDto llrpEncodeMessageDto = new
			// LLRPEncodeMessageDto();
			// llrpEncodeMessageDto.setStatus(FAIL_MESSAGE);
			// response.setEntity(self.generateReturnString(llrpEncodeMessageDto),
			// MediaType.TEXT_XML);
			// test end

			e.printStackTrace();

			response.setEntity(restletHelper.generateReturnString(restletHelper.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);

		} finally {

			// cleanup session
			if (session != null) {

				session.cleanupSession();

			}

		}

	}

	
	public void initLLRP(Router router) {
		final LLRPRestletHelper self = this;
		
		Restlet llrpGetReaderConfig = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("llrpGetReaderConfig requested");
				restletHelper.setResponseHeaders(request, response);
				llrpGetOperation(request, response, LLRPGetOperations.GET_READER_CONFIG);
			}
		};

		Restlet llrpGetRospecs = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("llrpGetRospecs requested");
				restletHelper.setResponseHeaders(request, response);
				llrpGetOperation(request, response, LLRPGetOperations.GET_ROSPECS);
			}
		};
		
		Restlet llrpGetReaderCapabilities = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				logger.info("llrpGetReaderCapabilities requested");
				restletHelper.setResponseHeaders(request, response);
				llrpGetOperation(request, response, LLRPGetOperations.GET_READER_CAPABILITIES);
			}
		};

		Restlet llrpEncode = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEncode requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, null);

			}
		};

		Restlet llrpEpcWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEpcWrite requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCWrite);

			}
		};

		Restlet llrpAccessPasswordWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpAccessPasswordWrite requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordWrite);

			}
		};

		Restlet llrpKillPasswordWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpKillPasswordWrite requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordWrite);

			}
		};

		Restlet llrpEPCLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEPCLock requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCLock);

			}
		};

		Restlet llrpAccessPasswordLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpAccessPasswordLock requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordLock);

			}
		};

		Restlet llrpKillPasswordLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpKillPasswordLock requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordLock);

			}
		};

		Restlet llrpUserMemoryLock = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpUserMemoryLock requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryLock);

			}
		};

		Restlet llrpEpcRead = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpEpcRead requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPEPCRead);

			}
		};

		Restlet llrpAccessPwdValidate = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpAccessPwdValidate requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPAccessPasswordValidate);

			}
		};

		Restlet llrpKillPwdRead = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpKillPwdRead requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPKillPasswordRead);

			}
		};

		Restlet llrpUserMemoryRead = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpUserMemoryRead requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryRead);

			}
		};

		Restlet llrpUserMemoryWrite = new Restlet() {
			@Override
			public void handle(Request request, Response response) {

				logger.info("llrpUserMemoryWrite requested");

				restletHelper.setResponseHeaders(request, response);

				executeLlrpOperation(request, response, LLRPReaderSession.LLRP_OPERATION_CODE.LLRPUserMemoryWrite);

			}
		};

		Restlet llrpMessage = new Restlet() {
			@Override
			public void handle(Request request, Response response) {
				try {

					logger.info("llrpMessage requested");

					restletHelper.setResponseHeaders(request, response);

					AbstractSensor<?> sensor = readerDAO.getReaderByID((String) request.getAttributes().get("readerID"));
					

					if (sensor == null) {
						throw new Exception("ReaderID is missing or invalid");
					}

					Map<String, SensorSession> sessionMap = sensor.getSensorSessions();
					String llrpResponse = "";
					if (sessionMap != null && sessionMap.containsKey(request.getAttributes().get("sessionID"))) {
						LLRPReaderSession session = (LLRPReaderSession) sessionMap.get(request.getAttributes().get("sessionID"));
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
							llrpResponse = self.restletHelper.generateReturnString(self.restletHelper.generateSuccessMessage());
						}
						response.setEntity(llrpResponse, MediaType.TEXT_XML);
					} else {
						throw new Exception("SessionID is missing or invalid");
					}
				} catch (Exception e) {
					response.setEntity(self.restletHelper.generateReturnString(self.restletHelper.generateErrorMessage(e.getMessage(), null)), MediaType.TEXT_XML);
				}
			}
		};
		
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
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordWrite/{properties}", llrpAccessPasswordWrite);

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
		router.attach("/llrpencode/{readerID}/{sessionID}/LLRPAccessPasswordValidate/{properties}", llrpAccessPwdValidate);

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
	}
	
	
}
