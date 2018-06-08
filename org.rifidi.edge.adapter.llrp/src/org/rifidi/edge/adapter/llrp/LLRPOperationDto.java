package org.rifidi.edge.adapter.llrp;

import java.io.Serializable;

import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.generated.messages.ADD_ACCESSSPEC_RESPONSE;
import org.llrp.ltk.types.UnsignedShort;
import org.llrp.ltk.types.UnsignedShortArray_HEX;

public class LLRPOperationDto implements Serializable {
	
	private static final long serialVersionUID = 1770072110407411261L;
	
	private int accessSpecId;
	private UnsignedShort opSpecId;
	private String operationName;
	private AccessCommandOpSpecResult responseResult;
	private String responseCode;
	private String epc;
	private ADD_ACCESSSPEC_RESPONSE accessSpecResponse;
	private UnsignedShortArray_HEX readData;
	

	/**
	 * 
	 * @param operationName
	 * @param opSpecId
	 * @param epc
	 */
	public LLRPOperationDto(String operationName, int accessSpecId, UnsignedShort opSpecId, String epc) {
		super();
		this.operationName = operationName;
		this.accessSpecId = accessSpecId;
		this.opSpecId = opSpecId;
		this.epc = epc;
	}
	
		
	/**
	 * @return the readData
	 */
	public UnsignedShortArray_HEX getReadData() {
		return readData;
	}




	/**
	 * @param readData the readData to set
	 */
	public void setReadData(UnsignedShortArray_HEX readData) {
		this.readData = readData;
	}




	/**
	 * @return the responseCode
	 */
	public String getResponseCode() {
		return responseCode;
	}



	/**
	 * @param responseCode the responseCode to set
	 */
	public void setResponseCode(String responseCode) {
		this.responseCode = responseCode;
	}



	/**
	 * @return the operationName
	 */
	public String getOperationName() {
		return operationName;
	}
	/**
	 * @param operationName the operationName to set
	 */
	public void setOperationName(String operationName) {
		this.operationName = operationName;
	}

	/**
	 * @return the responseResult
	 */
	public AccessCommandOpSpecResult getResponseResult() {
		return responseResult;
	}

	/**
	 * @param responseResult the responseResult to set
	 */
	public void setResponseResult(AccessCommandOpSpecResult responseResult) {
		this.responseResult = responseResult;
	}

	/**
	 * @return the opSpecId
	 */
	public UnsignedShort getOpSpecId() {
		return opSpecId;
	}

	/**
	 * @param opSpecId the opSpecId to set
	 */
	public void setOpSpecId(UnsignedShort opSpecId) {
		this.opSpecId = opSpecId;
	}

	/**
	 * @return the accessSpecId
	 */
	public int getAccessSpecId() {
		return accessSpecId;
	}

	/**
	 * @param accessSpecId the accessSpecId to set
	 */
	public void setAccessSpecId(int accessSpecId) {
		this.accessSpecId = accessSpecId;
	}

	/**
	 * @return the epc
	 */
	public String getEpc() {
		return epc;
	}

	/**
	 * @param epc the epc to set
	 */
	public void setEpc(String epc) {
		this.epc = epc;
	}

	/**
	 * @return the accessSpecResponse
	 */
	public ADD_ACCESSSPEC_RESPONSE getAccessSpecResponse() {
		return accessSpecResponse;
	}

	/**
	 * @param accessSpecResponse the accessSpecResponse to set
	 */
	public void setAccessSpecResponse(ADD_ACCESSSPEC_RESPONSE accessSpecResponse) {
		this.accessSpecResponse = accessSpecResponse;
	}
	
		
	
		
}
