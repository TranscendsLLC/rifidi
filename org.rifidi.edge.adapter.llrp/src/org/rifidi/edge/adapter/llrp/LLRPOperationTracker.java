package org.rifidi.edge.adapter.llrp;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
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
import org.llrp.ltk.exceptions.InvalidLLRPMessageException;
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

public class LLRPOperationTracker extends TimerTask implements Serializable {

	/** Map to keep track of all operations and responses received **/
	private Map<UnsignedShort, LLRPOperationDto> operationMap;

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
		operationMap = new HashMap<UnsignedShort, LLRPOperationDto>();
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
		operationMap.put(llrpOperationDto.getOpSpecId(), llrpOperationDto);
	}

	/**
	 * @return the operationMap
	 */
	public Map<UnsignedShort, LLRPOperationDto> getOperationMap() {
		return operationMap;
	}

	/**
	 * @param operationMap
	 *            the operationMap to set
	 */
	public void setOperationMap(
			Map<UnsignedShort, LLRPOperationDto> operationMap) {
		this.operationMap = operationMap;
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
			System.out.println("\n1.res.getResult(): " + res.getResult());
			System.out.println("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);
			operationMap.get(res.getOpSpecID()).setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2BlockWriteOpSpecResult) {

			C1G2BlockWriteOpSpecResult res = (C1G2BlockWriteOpSpecResult) result;
			System.out.println("\n2.res.getResult(): " + res.getResult());
			System.out.println("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);
			operationMap.get(res.getOpSpecID()).setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2KillOpSpecResult) {

			C1G2KillOpSpecResult res = (C1G2KillOpSpecResult) result;
			System.out.println("\n3.res.getResult(): " + res.getResult());
			System.out.println("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);
			operationMap.get(res.getOpSpecID()).setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2LockOpSpecResult) {

			C1G2LockOpSpecResult res = (C1G2LockOpSpecResult) result;
			System.out.println("\n4.res.getResult(): " + res.getResult());
			System.out.println("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);
			operationMap.get(res.getOpSpecID()).setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2ReadOpSpecResult) {

			C1G2ReadOpSpecResult res = (C1G2ReadOpSpecResult) result;
			System.out.println("\n5.res.getResult(): " + res.getResult());
			System.out.println("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);
			operationMap.get(res.getOpSpecID()).setResponseCode(res.getResult().toString());

		} else if (result instanceof C1G2WriteOpSpecResult) {

			C1G2WriteOpSpecResult res = (C1G2WriteOpSpecResult) result;
			System.out.println("\n6.res.getResult(): " + res.getResult());
			System.out.println("res.getOpSpecID(): " + res.getOpSpecID());

			// Assign the result to appropriate operation in tracker
			operationMap.get(res.getOpSpecID()).setResponseResult(res);
			operationMap.get(res.getOpSpecID()).setResponseCode(res.getResult().toString());

		}

	}

	/**
	 * Checks if all results are received
	 * 
	 * @return true if all results are received, otherwise returns false
	 */
	public boolean areAllResultsReceived() {

		for (LLRPOperationDto llrpOperationDto : getOperationMap().values()) {

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

		for (LLRPOperationDto llrpOperationDto : getOperationMap().values()) {

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

		System.out.println("Start time:" + new Date());
		checkOperationStatus();
		System.out.println("End time:" + new Date());

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

					// Sleep the time out
					Thread.sleep(millisToSleep);

				} catch (InterruptedException e) {

					// No matters
				}
			}

		}
		
		LLRPEncodeMessageDto llrpEncodeMessageDto = new LLRPEncodeMessageDto();

		if (areAllResultsReceived()) {

			System.out.println("allResultsReceived()");

		} else {

			System.out.println("NOT allResultsReceived()");

		}

		if (areAllResultsSuccessful()) {

			// Post to queue a single success message is asynchronous
			System.out.println("allResultsSuccessful()");
			String topicName = llrpReaderSession.getSensor().getID()
					+ "/encode";
			

			// TODO change this hard coded success message
			llrpEncodeMessageDto.setStatus("Success");
			//postMqttMesssage(topicName, llrpEncodeMessageDto);

		} else {

			// TODO change this hard coded fail message
			llrpEncodeMessageDto.setStatus("Fail");
			
			System.out.println("NOT allResultsSuccessful()");

		
			//Set the operation list with it's returned operation code
			for (LLRPOperationDto llrpOperationDto : getOperationMap().values()) {
				
				try{
					llrpEncodeMessageDto.addOperation(llrpOperationDto.getOperationName() + ":" + llrpOperationDto.getResponseCode() + "accessspecres: " + llrpOperationDto.getAccessSpecResponse().toXMLString());
				} catch (InvalidLLRPMessageException ex){
					ex.printStackTrace();
				}
				
			}
			
			// TODO post to queue a fail message if asynchronous
		}

		// Cancel this timer
		cancel();
		System.out.println("TimerTask cancelled! :" + new Date());

		llrpReaderSession.setRunningLLRPEncoding(false);
		
		return llrpEncodeMessageDto;

	}

	public void postMqttMesssage(String mqttTopic, Object messageContent) {

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
