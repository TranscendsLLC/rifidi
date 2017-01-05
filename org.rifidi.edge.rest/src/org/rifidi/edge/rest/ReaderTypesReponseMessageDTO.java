package org.rifidi.edge.rest;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "response")
public class ReaderTypesReponseMessageDTO implements Serializable {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3877574527110318237L;
	
	@XmlElementWrapper(required = true, name="sensors")
	@XmlElement(name = "sensor")
	private List<ReaderTypeDTO> sensors;

	public List<ReaderTypeDTO> getSensors() {
		return sensors;
	}

	public void setSensors(List<ReaderTypeDTO> sensors) {
		this.sensors = sensors;
	}
	
}
