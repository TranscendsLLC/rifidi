package org.rifidi.edge.adapter.llrp;

import java.io.Serializable;

import org.llrp.ltk.generated.interfaces.AccessCommandOpSpecResult;
import org.llrp.ltk.types.UnsignedShort;

public class LLRPOperationDto 
		implements Serializable {
	
	private UnsignedShort opSpecId;
	private String operationName;
	private AccessCommandOpSpecResult responseResult;
	

	/**
	 * 
	 * @param operationName
	 * @param opSpecId
	 */
	public LLRPOperationDto(String operationName, UnsignedShort opSpecId) {
		super();
		this.operationName = operationName;
		this.opSpecId = opSpecId;
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

	
}
