/**
 * 
 */
package org.rifidi.edge.app.db.domain;

/**
 * This is a data container object that contains the data from an RFID event
 * that should be written to the data source.
 * 
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class RFIDEvent {

	/** The ID of the tag */
	private String id;
	/** The ID of the reader that collected the tag */
	private String reader;
	/** The ID of the antenna that saw the tag */
	private Integer antenna;
	/** When the tag was seen */
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
