package org.rifidi.edge.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;

import org.apache.log4j.Logger;
import org.rifidi.app.ale.AleApp;
import org.rifidi.edge.api.CommandDTO;
import org.rifidi.edge.api.CommandSubmissionException;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.SessionDTO;
import org.rifidi.edge.api.SessionStatus;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.LRProperty;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.SecurityExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.server.epcglobal.alelr.services.LogicalReaderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("rifidiHelper")
public class RifidiHelper {

	@Autowired
	SensorManagerService sensorManagerService;

	@Autowired
	private LogicalReaderService logicalReaderService;
	
	@Autowired
	ConfigurationService configService;

	@Autowired
	private AleApp aleApp;

	/** logger. */
	private static final Logger LOG = Logger.getLogger(RifidiHelper.class);

	public RifidiHelper() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public ReaderDTO getReader(String readerId){
		Set<ReaderDTO> readerDTOSet = sensorManagerService.getReaders();
		for(ReaderDTO readerDTO : readerDTOSet){
			if ( readerDTO.getReaderID().equals(readerId) ){
				return readerDTO;
			}
		}
		
		return null;
	}

	public String validateRifidiReader(String aleLogicalReader) 
			throws ValidationExceptionResponse {

		String rifidiReader = aleLogicalReader;
		ReadZone readZone = null;
		ReaderDTO readerDTO = getReader(rifidiReader);
		
		if (readerDTO == null) {
			// this is not a rifidi reader id
			// check if it is a readzone name
			readZone = getReadzone(aleLogicalReader);
			if (readZone != null) {
				//Extract the reader id from readzone, and check it is a valid rifidi reader 
				rifidiReader = readZone.getReaderID();
				
				ReaderDTO readerDTOFromReadzone = getReader(rifidiReader);
				if ( readerDTOFromReadzone == null ){
					throw new ValidationExceptionResponse("Reader with id '" + rifidiReader + "' contained in readzone '" + aleLogicalReader + "' is not created in Rifidi server. Please first create the reader in Rifidi server.");
				}
				
				
			} else {
				throw new ValidationExceptionResponse("Id '" + aleLogicalReader
						+ "' does not match with any reader neither readzone. Please make sure in Rifidi server there is a reader with this id or a readzone");
			}

		}

		return rifidiReader;

	}
	
	public ReadZone getReadzone( String aleLogicalReader ){
		
		HashMap<String, ReadZone> readzoneMap = aleApp.getReadZones();
		return readzoneMap.get(aleLogicalReader);
		
	}

	private SessionDTO getSession(String deviceName) {

		SessionDTO sessionDTO = null;
		ReaderDTO readerDTO = getReader(deviceName);
		if (readerDTO != null) {
			List<SessionDTO> sessionList = readerDTO.getSessions();
			if (!sessionList.isEmpty()) {
				sessionDTO = sessionList.get(0);
				return sessionDTO;
			}
		}
		return null;
	}

	public boolean isSessionInProcessingState(String deviceId) {
		boolean isProcessing = false;
		SessionDTO sessionDTO = getSession(deviceId);
		if (sessionDTO != null && sessionDTO.getStatus().equals(SessionStatus.PROCESSING)) {
			isProcessing = true;
		}

		return isProcessing;
	}

	// Validate there is a Rifidi session in PROCESSING state
	public List<String> validateSessionInProcessingState(ECSpec ecSpec)
			throws org.rifidi.edge.epcglobal.alelr.NoSuchNameExceptionResponse, SecurityExceptionResponse,
			ImplementationExceptionResponse {

		List<String> errorMessages = new ArrayList<>();

		List<String> ecLogicalReaders = ecSpec.getLogicalReaders().getLogicalReader();
		for (String ecLogicalReader : ecLogicalReaders) {

			errorMessages.addAll( validateECLogicalReaderVsRifidiReader(ecLogicalReader) );

		}

		return errorMessages;
	}

	private List<String> validateECLogicalReaderVsRifidiReader(String ecLogicalReader)
			throws NoSuchNameExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse {

		List<String> errorMessages = new ArrayList<>();

		LRSpec lrSpec = null;
		try {
			lrSpec = logicalReaderService.getLRSpec(ecLogicalReader);
		} catch (Exception e){
			errorMessages.add(e.getMessage());
			throw new ImplementationExceptionResponse(e.getMessage(), e);
		}

		if (lrSpec.isIsComposite()) {
			List<String> lrLogicalReaders = lrSpec.getReaders().getReader();
			for (String lrLogicalReader : lrLogicalReaders) {
				
				//recursive call
				errorMessages.addAll( validateECLogicalReaderVsRifidiReader(lrLogicalReader) );

			}
		} else {
			// lrSpec is base reader, validate if it is a rifidi reader or
			// readzone
			// Initially assume the deviceId is a physical reader id
			String rifidiReaderId = ecLogicalReader;
			ReadZone readZone = null;
			String readzoneName = null;
			ReaderDTO readerDTO = getReader(rifidiReaderId);
			if (readerDTO == null) {
				// this is not a rifidi reader id
				// check if it is a readzone name
				readZone = getReadzone(rifidiReaderId);
				if (readZone != null) {
					// it is a readzone name, then update reader id
					readzoneName = rifidiReaderId; 
					rifidiReaderId = readZone.getReaderID();
				} else {
					errorMessages.add("Id '" + ecLogicalReader
							+ "' does not match with any reader neither readzone. Please make sure in Rifidi server there is a reader or a readzone with this id");
					LOG.info("Id '" + ecLogicalReader
							+ "' does not match with any reader neither readzone. Please make sure in Rifidi server there is a reader or a readzone with this id");
					System.out.println("Id '" + ecLogicalReader
							+ "' does not match with any reader neither readzone. Please make sure in Rifidi server there is a reader or a readzone with this id");
				}

			}

			// Validate session on reader rifidiReaderId
//			System.out.println("readzoneName: " + readzoneName);
			boolean isSessionProcessing = isSessionInProcessingState(rifidiReaderId);
			if (!isSessionProcessing) {
				errorMessages.add("There is no session running in PROCESSING state in Rifidi server for device id: '"
						+ rifidiReaderId + "'"
						+ ( (readzoneName != null) ? " from readzone '" + readzoneName + "'":"" ));
				LOG.info("There is no session running in PROCESSING state in Rifidi server for device id: '"
						+ rifidiReaderId + "'"
						+ ( (readzoneName != null) ? " from readzone '" + readzoneName + "'":"" ));
					System.out.println("There is no session running in PROCESSING state in Rifidi server for device id: '"
						+ rifidiReaderId + "'"
						+ ( (readzoneName != null) ? " from readzone '" + readzoneName + "'":"" ));
				}

		}

		return errorMessages;

	}
	
	public void setRifidiReaderProperties(String readerName, AttributeList attributes)
			throws ValidationExceptionResponse {

		//Determine if readerName is a rifidi reader or a readzone
		readerName = validateRifidiReader(readerName);
		
		boolean startSession = false;

		SessionDTO sessionDTO = getSession(readerName);

		if (sessionDTO != null && sessionDTO.getStatus() != null
				&& (sessionDTO.getStatus().equals(SessionStatus.PROCESSING)
						|| sessionDTO.getStatus().equals(SessionStatus.CONNECTING)
						|| sessionDTO.getStatus().equals(SessionStatus.LOGGINGIN))) {
			startSession = true;
		}

		// Before delete session, store the commands
		List<CommandDTO> commandList = new ArrayList<>();
		Set<ReaderDTO> dtos = sensorManagerService.getReaders();

		for (ReaderDTO readerDto : dtos) {
			if (readerDto.getReaderID().equals(readerName)) {
				for (SessionDTO sdto : readerDto.getSessions()) {
					for (CommandDTO command : sdto.getCommands()) {
						commandList.add(command);
					}
				}
				break;
			}
		}

		sensorManagerService.deleteSession(readerName, sessionDTO.getID());
		sensorManagerService.setReaderProperties(readerName, attributes);
		sensorManagerService.createSession(readerName);

		// update session object
		sessionDTO = getSession(readerName);

		// execute saved commands
		for (CommandDTO commandDTO : commandList) {
			try {
				sensorManagerService.submitCommand(readerName, sessionDTO.getID(), commandDTO.getCommandID(),
						commandDTO.getInterval(), TimeUnit.MILLISECONDS);
			} catch (CommandSubmissionException ex) {
				ex.printStackTrace();
			}
		}

		if (startSession) {
			sensorManagerService.startSession(readerName, sessionDTO.getID());
		}

		// Save configuration
		storeRifidiConfiguration();

	}
	
	public void storeRifidiConfiguration(){
		configService.storeConfiguration();
	}
	
	public String getErrorMessagesAsSingleText(List<String> errorMessages){
		
		String errorsStr = "";
		for (String error : errorMessages){
			errorsStr += error + "|";
		}
		//Quit last |
		if( !errorsStr.isEmpty() ){
			errorsStr = errorsStr.substring(0, errorsStr.length()-1 );
		}
		return errorsStr;
	}
	
	/**
	 * 
	 * @param ecSpecNameFull the spec name in the form of Cycle1_1
	 * @return
	 */
	public String getECSpecName( String ecSpecNameFull ){
		int index = ecSpecNameFull.lastIndexOf("_");
		if ( index >= 0 ){
			return ecSpecNameFull.substring(0, ecSpecNameFull.lastIndexOf("_") );
		} else {
			return ecSpecNameFull;
		}
		
	}
	

}
