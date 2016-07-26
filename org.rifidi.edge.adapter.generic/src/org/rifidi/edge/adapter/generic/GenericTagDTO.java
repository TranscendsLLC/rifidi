package org.rifidi.edge.adapter.generic;
import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * 
 */

/**
 * @author matt
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "tag")
public class GenericTagDTO implements Serializable {
	
	private static final long serialVersionUID = -3035982716629339545L;

	//The ID of the tag
	@XmlElement(name = "id")
	private String id;
	
	//The antenna of the tag
	@XmlElement(name = "antenna")
	private Integer antenna;
	
	@XmlElement(name = "timestamp")
	private Long timestamp;
	
	@XmlElement(name = "reader")
	private String reader;
	
	@XmlElement(name = "rssi")
	private String rssi;

	public Long getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

	public String getReader() {
		return reader;
	}

	public void setReader(String reader) {
		this.reader = reader;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Integer getAntenna() {
		return antenna;
	}

	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}

	public String getRssi() {
		return rssi;
	}

	public void setRssi(String rssi) {
		this.rssi = rssi;
	}
	
}
