package org.rifidi.edge.adapter.llrp;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.llrp.ltk.types.UnsignedShort;

public class LLRPOperationDto 
		implements Serializable {
	
	private Integer opSpecId;
	private String operationName;
	private String responseResult;
	
	/**
	 * @param operationOrder
	 * @param operationName
	 */
	public LLRPOperationDto(String operationName, Integer opSpecId) {
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
	public String getResponseResult() {
		return responseResult;
	}

	/**
	 * @param responseResult the responseResult to set
	 */
	public void setResponseResult(String responseResult) {
		this.responseResult = responseResult;
	}

	/**
	 * @return the opSpecId
	 */
	public Integer getOpSpecId() {
		return opSpecId;
	}

	/**
	 * @param opSpecId the opSpecId to set
	 */
	public void setOpSpecId(Integer opSpecId) {
		this.opSpecId = opSpecId;
	}

	
}
