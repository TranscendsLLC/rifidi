package org.rifidi.edge.adapter.llrp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "encodeMessage")
public class LLRPEncodeMessageDto implements Serializable {

	/**
	 * 
	 */
	//private static final long serialVersionUID = 618168723573019895L;
	
	@XmlElement(name="status")
	private String status;//Success / Fail
	
	@XmlElement(name="data")
	private String data;//data returned from read operation
	
	@XmlElementWrapper(required = true)
	@XmlElement(name = "operation")
	/** List of operations **/
	private List<String> operationList;


	/**
	 * 
	 */
	public LLRPEncodeMessageDto() {
		super();
	}


	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}
	
	
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}


	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}


	/**
	 * @return the operationList
	 */
	public List<String> getOperationList() {
		return operationList;
	}


	/**
	 * @param operationList the operationList to set
	 */
	public void setOperationList(List<String> operationList) {
		this.operationList = operationList;
	}
	
	public void addOperation(String operation){
		
		if(operationList == null){
			//Initialize operation list
			operationList = new ArrayList<String>();
		}
		operationList.add(operation);
	}	
	
}
