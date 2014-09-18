package org.rifidi.app.rifidiservices.dto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlList;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Data transfer object to be used in posting to broker tags information as xml structure
 * @author Manuel Tobon - manuel@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "stableSetMessage")
public class StableSetMessageDto 
		implements Serializable {
	
	@XmlElement
	/** JVM time when this object is created (machine time) **/
	private Long timeStamp;
	
	@XmlElementWrapper(required = true)
	@XmlElement(name = "tag")
	/** List of tags associated with this station id **/
	private List<String> tagList;
	
	
	public StableSetMessageDto() {
		super();
		//tagList = new ArrayList<TagMessageDto>();
		tagList = new ArrayList<String>();
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
	public void setTimeStamp(Long timeStamp) {
		this.timeStamp = timeStamp;
	}
	
	/**
	 * @return the tagList
	 */
	public List<String> getTagList() {
		return tagList;
	}
	
	/**
	 * @param tagList the tagList to set
	 */
	public void setTagList(List<String> tagList) {
		this.tagList = tagList;
	}
	
	/**
	 * Add a TagMessageDto instance to the tag list
	 * @param tagId the id of the tag to add to tag list
	 */
	public void addTag(String tagId){
		tagList.add(tagId);
	}

	
	

}
