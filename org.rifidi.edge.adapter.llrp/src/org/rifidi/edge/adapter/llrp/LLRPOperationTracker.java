package org.rifidi.edge.adapter.llrp;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

public class LLRPOperationTracker 
		implements Serializable {
	
	private Map<Integer, LLRPOperationDto> operationMap;
	
	/**
	 *
	 */
	public LLRPOperationTracker() {
		super();

		operationMap = new HashMap<Integer, LLRPOperationDto>();
	}
	
	public void addOperationDto(LLRPOperationDto llrpOperationDto){
		operationMap.put(llrpOperationDto.getOpSpecId(), llrpOperationDto);
	}

	

	/**
	 * @return the operationMap
	 */
	public Map<Integer, LLRPOperationDto> getOperationMap() {
		return operationMap;
	}

	/**
	 * @param operationMap the operationMap to set
	 */
	public void setOperationMap(Map<Integer, LLRPOperationDto> operationMap) {
		this.operationMap = operationMap;
	}
	
	public void setResponse(Integer opSpecId, String responseResult){
		operationMap.get(opSpecId).setResponseResult(responseResult);
	}
	
	
	

}
