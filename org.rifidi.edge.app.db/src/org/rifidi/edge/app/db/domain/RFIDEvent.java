/**
 * 
 */
package org.rifidi.edge.app.db.domain;

/**
 * @author kyle
 * 
 */
public class RFIDEvent {

	private String id;
	private String reader;
	private Integer antenna;
	private Long timestamp;

	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the reader
	 */
	public String getReader() {
		return reader;
	}

	/**
	 * @param reader
	 *            the reader to set
	 */
	public void setReader(String reader) {
		this.reader = reader;
	}

	/**
	 * @return the antenna
	 */
	public Integer getAntenna() {
		return antenna;
	}

	/**
	 * @param antenna
	 *            the antenna to set
	 */
	public void setAntenna(Integer antenna) {
		this.antenna = antenna;
	}

	/**
	 * @return the timestamp
	 */
	public Long getTimestamp() {
		return timestamp;
	}

	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Long timestamp) {
		this.timestamp = timestamp;
	}

}
