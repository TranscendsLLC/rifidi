package org.rifidi.edge.adapter.llrp;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "encodeMessage")
public class LLRPEncodeMessageDto 
		implements Serializable {
	
	private String status;

	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	@XmlElement
	public void setStatus(String status) {
		this.status = status;
	}
	
	/*
	private String tag;
	private Long timeStamp;
	private String stationId;
	private int antennaId;
	*/
	
	
	
	
	/**
	 * @return the tag
	 */
	/*
	public String getTag() {
		return tag;
	}
	*/
	/**
	 * @param tag the tag to set
	 */
	/*
	@XmlElement
	public void setTag(String tag) {
		this.tag = tag;
	}
	*/
	
	
	
	

}
