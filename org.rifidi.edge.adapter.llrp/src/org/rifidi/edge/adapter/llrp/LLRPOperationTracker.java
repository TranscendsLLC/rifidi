package org.rifidi.edge.adapter.llrp;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.TimerTask;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.llrp.ltk.generated.enumerations.C1G2BlockEraseResultType;
import org.llrp.ltk.generated.enumerations.C1G2BlockWriteResultType;
import org.llrp.ltk.generated.enumerations.C1G2KillResultType;
import org.llrp.ltk.generated.enumerations.C1G2LockResultType;
import org.llrp.ltk.generated.enumerations.C1G2ReadResultType;
import org.llrp.ltk.generated.enumerations.C1G2WriteResultType;
import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2BlockEraseOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2BlockWriteOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2KillOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2LockOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2ReadOpSpecResult;
import org.llrp.ltk.generated.parameters.C1G2WriteOpSpecResult;
import org.llrp.ltk.types.UnsignedShort;
import org.rifidi.edge.adapter.llrp.LLRPReaderSession.LLRP_OPERATION_CODE;

public class LLRPOperationTracker extends TimerTask implements Serializable {

	/** List to keep track of all operations and responses received **/
	private List<LLRPOperationDto> operationList;

	/** milliseconds to sleep this thread and wait for timeout **/
	long millisToSleep = 500;

	/** Reference to llrpReaderSession **/
	LLRPReaderSession llrpReaderSession;

	/** mqttClient to be used in sending tag data to mqttServer **/
	private MqttClient mqttClient;

	/** Logger for this class */
	final Log logger = LogFactory.getLog(getClass());

	// int numberOfLoops = (int) (getTimeout() / millisToSleep);

	/**
	 *
	 */
	public LLRPOperationTracker(LLRPReaderSession llrpReaderSession) {
		super();
		this.llrpReaderSession = llrpReaderSession;
	}

	
	/**
	 * @return the mqttClient
	 */
	public MqttClient getMqttClient() {
		return mqttClient;
	}

	/**
	 * @param mqttClient
	 *            the mqttClient to set
	 */
	public void setMqttClient(MqttClient mqttClient) {
		this.mqttClient = mqttClient;
	}

	public void addOperationDto(LLRPOperationDto llrpOperationDto) {
		
		if (operationList == null){
			
			//Initialize operation list
			operationList = new LinkedList<LLRPOperationDto>();
			
		}
		operationList.add(llrpOperationDto);
	}

	

	/**
	 * @return the operationList
	 */
	public List<LLRPOperationDto> getOperationList() {
		return operationList;
	}


	/**
	 * @param operationList the operationList to set
	 */
	public void setOperationList(List<LLRPOperationDto> operationList) {
		this.operationList = operationList;
	}
	
	public LLRPOperationDto getOperationByOpSpecId(UnsignedShort opSpecId){
		
		for(LLRPOperationDto llrpOperationDto : operationList){
			
			if (llrpOperationDto.getOpSpecId().equals(opSpecId)){
				
				return llrpOperationDto;
			}
		}
		
		return null;
	}


	/**
	 * Set the result received
	 * 
	 * @param result
	 *            the received result of operation
	 */
	public void setResult(AccessCommandOpSpecResult result) {

		if (result instanceof C1G2BlockEraseOpSpecResult) {

			C1G2BlockEraseOpSpecResult res = (C1G2BlockEraseOpSpecResult) result;
			logger.info("\n1.res.getResult(): " + res.getResult());
			logger.info("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			LLRPOperationDto llrpOperationDto = getOperationByOpSpecId(res.getOpSpecID());
			llrpOperationDto.setResponseResult(res);
			llrpOperationDto.setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2BlockWriteOpSpecResult) {

			C1G2BlockWriteOpSpecResult res = (C1G2BlockWriteOpSpecResult) result;
			logger.info("\n2.res.getResult(): " + res.getResult());
			logger.info("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			LLRPOperationDto llrpOperationDto = getOperationByOpSpecId(res.getOpSpecID());
			llrpOperationDto.setResponseResult(res);
			llrpOperationDto.setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2KillOpSpecResult) {

			C1G2KillOpSpecResult res = (C1G2KillOpSpecResult) result;
			logger.info("\n3.res.getResult(): " + res.getResult());
			logger.info("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			LLRPOperationDto llrpOperationDto = getOperationByOpSpecId(res.getOpSpecID());
			llrpOperationDto.setResponseResult(res);
			llrpOperationDto.setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2LockOpSpecResult) {

			C1G2LockOpSpecResult res = (C1G2LockOpSpecResult) result;
			logger.info("\n4.res.getResult(): " + res.getResult());
			logger.info("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			LLRPOperationDto llrpOperationDto = getOperationByOpSpecId(res.getOpSpecID());
			llrpOperationDto.setResponseResult(res);
			llrpOperationDto.setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2ReadOpSpecResult) {

			C1G2ReadOpSpecResult res = (C1G2ReadOpSpecResult) result;
			logger.info("\n5.res.getResult(): " + res.getResult());
			logger.info("res.getOpSpecID(): " + res.getOpSpecID());
			logger.info("res.getName(): " + res.getName());
			logger.info("res.getReadData(): " + res.getReadData());

			// Assign the result to appropriate operation in tracker
			LLRPOperationDto llrpOperationDto = getOperationByOpSpecId(res.getOpSpecID());
			llrpOperationDto.setResponseResult(res);
			llrpOperationDto.setResponseCode(res.getResult().toString());
			llrpOperationDto.setReadData(res.getReadData());

		} else if (result instanceof C1G2WriteOpSpecResult) {

			C1G2WriteOpSpecResult res = (C1G2WriteOpSpecResult) result;
			logger.info("\n6.res.getResult(): " + res.getResult());
			logger.info("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			LLRPOperationDto llrpOperationDto = getOperationByOpSpecId(res.getOpSpecID());
			llrpOperationDto.setResponseResult(res);
			llrpOperationDto.setResponseCode(res.getResult().toString());

		}

	}

	/**
	 * Checks if all results are received
	 * 
	 * @return true if all results are received, otherwise returns false
	 */
	public boolean areAllResultsReceived() {

		for (LLRPOperationDto llrpOperationDto : getOperationList()) {

			if (llrpOperationDto.getResponseResult() == null) {
				return false;
			}
		}

		return true;

	}

	/**
	 * Checks if all results are received and are success
	 * 
	 * @return true if all results are received and are success
	 */
	public boolean areAllResultsSuccessful() {

		if (!areAllResultsReceived()) {
			return false;
		}

		for (LLRPOperationDto llrpOperationDto : getOperationList()) {

			if (llrpOperationDto.getResponseResult() instanceof C1G2BlockEraseOpSpecResult) {

				C1G2BlockEraseOpSpecResult res = (C1G2BlockEraseOpSpecResult) llrpOperationDto
						.getResponseResult();

				if (res.getResult().intValue() != C1G2BlockEraseResultType.Success) {

					return false;

				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2BlockWriteOpSpecResult) {

				C1G2BlockWriteOpSpecResult res = (C1G2BlockWriteOpSpecResult) llrpOperationDto
						.getResponseResult();

				if (res.getResult().intValue() != C1G2BlockWriteResultType.Success) {

					return false;

				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2KillOpSpecResult) {

				C1G2KillOpSpecResult res = (C1G2KillOpSpecResult) llrpOperationDto
						.getResponseResult();

				if (res.getResult().intValue() != C1G2KillResultType.Success) {

					return false;

				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2LockOpSpecResult) {

				C1G2LockOpSpecResult res = (C1G2LockOpSpecResult) llrpOperationDto
						.getResponseResult();

				if (res.getResult().intValue() != C1G2LockResultType.Success) {

					return false;

				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2ReadOpSpecResult) {

				C1G2ReadOpSpecResult res = (C1G2ReadOpSpecResult) llrpOperationDto
						.getResponseResult();

				if (res.getResult().intValue() != C1G2ReadResultType.Success) {

					return false;

				}

			} else if (llrpOperationDto.getResponseResult() instanceof C1G2WriteOpSpecResult) {

				C1G2WriteOpSpecResult res = (C1G2WriteOpSpecResult) llrpOperationDto
						.getResponseResult();

				if (res.getResult().intValue() != C1G2WriteResultType.Success) {

					return false;

				}

			}

		}

		return true;

	}

	@Override
	public void run() {

		logger.info("Start time:" + new Date());
		
		
		//Test
		/*
		try{
			Thread.sleep(15000);
		} catch(InterruptedException ex){
			
		}
		*/
		
		checkOperationStatus();
		logger.info("End time:" + new Date());

	}

	/**
	 * 
	 * @return
	 */
	public LLRPEncodeMessageDto checkOperationStatus() {

		long millisToSleep = 500;
		int numberOfLoops = (int) (llrpReaderSession.getTimeout() / millisToSleep);

		for (int i = 0; i < numberOfLoops; i++) {

			// Check if all messages received, if so and all succeed return
			// success
			if (areAllResultsReceived()) {

				break;

			} else {

				try {

					// Sleep
					Thread.sleep(millisToSleep);

				} catch (InterruptedException e) {

					// No matters
				}
			}

		}
		
		LLRPEncodeMessageDto llrpEncodeMessageDto = new LLRPEncodeMessageDto();

		if (areAllResultsReceived()) {

			logger.info("allResultsReceived()");

		} else {

			logger.info("NOT allResultsReceived()");

		}
		
		if (areAllResultsSuccessful()) {

			logger.info("allResultsSuccessful()");

			// TODO change this hard coded success message
			llrpEncodeMessageDto.setStatus("Success");

		} else {

			// TODO change this hard coded fail message
			llrpEncodeMessageDto.setStatus("Fail");
			
			logger.info("NOT allResultsSuccessful()");
		
			//Set the operation list with it's returned operation code
			for (LLRPOperationDto llrpOperationDto : getOperationList()) {
				
				llrpEncodeMessageDto.addOperation(llrpOperationDto.getOperationName() + ":" + llrpOperationDto.getResponseCode());
				
			}
			
		}
		
		//Set the received data for single shot operation, so there should be one operation
		//Exclude the access password read from setting the access password retrieved value
		LLRPOperationDto readOperationDto = getOperationList().get(0);
		
		if (!readOperationDto.getOperationName().equals(LLRP_OPERATION_CODE.LLRPAccessPasswordValidate.toString())){
			
			if (readOperationDto.getReadData() != null){
				//trim white spaces and retrieve data
				llrpEncodeMessageDto.setData(readOperationDto.getReadData().toString().replaceAll("\\s", ""));
			}
		}
				
		// post to queue and cleanup if asynchronous
		if (llrpReaderSession.isExecuteOperationsInAsynchronousMode()){
			
			//topic to post message if asynchronous
			String topicName = llrpReaderSession.getSensor().getID()
					+ "/encode";
			
			postMqttMessage(topicName, llrpEncodeMessageDto);
			
			llrpReaderSession.cleanupSession();
			
			// Cancel this timer
			cancel();
			
			logger.info("TimerTask cancelled! :" + new Date());
			
		}

		return llrpEncodeMessageDto;

	}

	public void postMqttMessage(String mqttTopic, Object messageContent) {

		try {

			JAXBContext jaxbContext = JAXBContext.newInstance(messageContent
					.getClass());
			Marshaller jaxbMarshaller = jaxbContext.createMarshaller();

			// output pretty printed
			jaxbMarshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT,
					Boolean.TRUE);

			// Create xml String and send to server using mqttClient
			Writer writer = new StringWriter();
			jaxbMarshaller.marshal(messageContent, writer);
			String content = writer.toString();
			writer.close();

			logger.info("Publishing message: " + content);
			MqttMessage message = new MqttMessage(content.getBytes());
			message.setQos(llrpReaderSession.getMqttQos());

			try {

				mqttClient.connect();
				logger.info("Connected to broker: " + mqttClient.getServerURI());

			} catch (MqttException mEx) {

				logger.error("Error connecting to broker", mEx);
				throw new RuntimeException(mEx);

			}

			try {

				mqttClient.publish(mqttTopic, message);
				logger.info("Message published");

			} catch (MqttException mEx) {

				logger.error("Error publishing to queue", mEx);
				throw new RuntimeException(mEx);

			}

			try {

				mqttClient.disconnect();
				logger.info("mqttClient disconnected.");

			} catch (MqttException mEx) {

				logger.error("Error trying to disconnect mqttClient", mEx);
				throw new RuntimeException(mEx);

			}

		} catch (JAXBException jEx) {

			logger.error("Error publishing to queue", jEx);
			throw new RuntimeException(jEx);

		} catch (IOException ioEx) {

			logger.error("Error publishing to queue", ioEx);
			throw new RuntimeException(ioEx);

		}

	}

	public void initializeMqttParameters() {

		MemoryPersistence persistence = new MemoryPersistence();

		try {

			setMqttClient(new MqttClient(llrpReaderSession.getMqttBroker(), 
					llrpReaderSession.getMqttClientId(), persistence));
			MqttConnectOptions connOpts = new MqttConnectOptions();
			connOpts.setCleanSession(true);

		} catch (MqttException mEx) {

			logger.error("Error creating mqttClient instance to broker", mEx);
			throw new RuntimeException(mEx);

		}

	}


}
