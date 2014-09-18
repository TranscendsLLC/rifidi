package org.rifidi.app.rifidiservices.dto;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "tagMessage")
public class TagMessageDto 
		implements Serializable {
	
	private String tag;
	private Long timeStamp;
	private String stationId;
	/**
	 * @return the tag
	 */
	public String getTag() {
		return tag;
	}
	/**
	 * @param tag the tag to set
	 */
	@XmlElement
	public void setTag(String tag) {
		this.tag = tag;
	}
	
	
	
	
	/**
	 * @return the stationId
	 */
	public String getStationId() {
		return stationId;
	}
	/**
	 * @param stationId the stationId to set
	 */
	@XmlElement
	public void setStationId(String stationId) {
		this.stationId = stationId;
	}
	/**
	 * @return the timeStamp
	 */
	public Long getTimeStamp() {
		return timeStamp;
	}
	/**
	 * @param timeStamp the timeStamp to set
	 */
	@XmlElement
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	

}
