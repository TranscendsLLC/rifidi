/**
 * 
 */
package org.rifidi.edge.core.messages;

import java.math.BigInteger;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public class EPCGeneration1Event extends DatacontainerEvent {

	/***/
	private static final long serialVersionUID = 1L;
	
	protected Integer epcLength;
	
	private BigInteger epcBank;
	
	public EPCGeneration1Event() {
		epcBank=super.createMemoryBank();
	}
	
	public void setEPCMemory(BigInteger memBank){
		this.epcBank=memBank;
	}
	
	public String getEPC_HEX() {
		return epcBank.toString(16);
	}
	
	public String getEPC_BIN() {
		return epcBank.toString(2);
	}
	
	public BigInteger getEPC(){
		return epcBank;
	}

	/**
	 * @return the epcLength
	 */
	public Integer getEpcLength() {
		return epcLength;
	}

	/**
	 * @param epcLength the epcLength to set
	 */
	public void setEpcLength(Integer epcLength) {
		this.epcLength = epcLength;
	}

}
