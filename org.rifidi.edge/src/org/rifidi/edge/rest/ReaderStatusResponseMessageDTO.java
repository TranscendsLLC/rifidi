/**
 * 
 */
package org.rifidi.edge.rest;

import java.io.Serializable;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Matthew Dean - matt@transcends.co
 *
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "Response")
public class ReaderStatusResponseMessageDTO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 725905841777041513L;
	
	@XmlElement(name = "Reader")
	private ReaderNameDTO reader;
	
	@XmlElementWrapper(required = true, name="Sessions")
	@XmlElement(name = "Session")
	private List<SessionNameDTO> sessions;

	public ReaderNameDTO getReader() {
		return reader;
	}

	public void setReader(ReaderNameDTO reader) {
		this.reader = reader;
	}

	public List<SessionNameDTO> getSessions() {
		return sessions;
	}

	public void setSessions(List<SessionNameDTO> sessions) {
		this.sessions = sessions;
	}
	
}
