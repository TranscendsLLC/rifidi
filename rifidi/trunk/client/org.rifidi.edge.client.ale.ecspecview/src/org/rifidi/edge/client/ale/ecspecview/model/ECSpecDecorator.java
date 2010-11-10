
package org.rifidi.edge.client.ale.ecspecview.model;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.namespace.QName;

import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECBoundarySpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpecExtension;

/**
 * A decorator around an ECSpec
 * @author Kyle Neumeier - kyle@pramari.com
 */
public class ECSpecDecorator extends ECSpec {
	
	/** The name of the spec */
	private String name;
	/** The Spec */
	private ECSpec spec;
	/**
	 * @param name
	 * @param spec
	 */
	public ECSpecDecorator(String name, ECSpec spec) {
		super();
		this.name = name;
		this.spec = spec;
	}
	
	/**
	 * Returns the name of the ECSpecDecorator.  
	 * 
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the ECSpec.  
	 * 
	 * @return the spec
	 */
	public ECSpec getSpec() {
		return spec;
	}

	/**
	 * @param arg0
	 * @return
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		return spec.equals(arg0);
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#getAny()
	 */
	public List<Object> getAny() {
		return spec.getAny();
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#getBoundarySpec()
	 */
	public ECBoundarySpec getBoundarySpec() {
		return spec.getBoundarySpec();
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.epcglobal.Document#getCreationDate()
	 */
	public XMLGregorianCalendar getCreationDate() {
		return spec.getCreationDate();
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#getExtension()
	 */
	public ECSpecExtension getExtension() {
		return spec.getExtension();
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#getLogicalReaders()
	 */
	public LogicalReaders getLogicalReaders() {
		return spec.getLogicalReaders();
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#getOtherAttributes()
	 */
	public Map<QName, String> getOtherAttributes() {
		return spec.getOtherAttributes();
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#getReportSpecs()
	 */
	public ReportSpecs getReportSpecs() {
		return spec.getReportSpecs();
	}
	
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.epcglobal.Document#getSchemaVersion()
	 */
	public BigDecimal getSchemaVersion() {
		return spec.getSchemaVersion();
	}
	/**
	 * @return
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		return spec.hashCode();
	}
	/**
	 * @return
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#isIncludeSpecInReports()
	 */
	public boolean isIncludeSpecInReports() {
		return spec.isIncludeSpecInReports();
	}
	
	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#setBoundarySpec(org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECBoundarySpec)
	 */
	public void setBoundarySpec(ECBoundarySpec value) {
		spec.setBoundarySpec(value);
	}
	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.epcglobal.Document#setCreationDate(javax.xml.datatype.XMLGregorianCalendar)
	 */
	public void setCreationDate(XMLGregorianCalendar value) {
		spec.setCreationDate(value);
	}
	
	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#setExtension(org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpecExtension)
	 */
	public void setExtension(ECSpecExtension value) {
		spec.setExtension(value);
	}
	
	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#setIncludeSpecInReports(java.lang.Boolean)
	 */
	public void setIncludeSpecInReports(Boolean value) {
		spec.setIncludeSpecInReports(value);
	}
	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#setLogicalReaders(org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.LogicalReaders)
	 */
	public void setLogicalReaders(LogicalReaders value) {
		spec.setLogicalReaders(value);
	}
	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec#setReportSpecs(org.rifidi.edge.client.ale.api.xsd.ale.epcglobal.ECSpec.ReportSpecs)
	 */
	public void setReportSpecs(ReportSpecs value) {
		spec.setReportSpecs(value);
	}
	/**
	 * @param value
	 * @see org.rifidi.edge.client.ale.api.xsd.epcglobal.Document#setSchemaVersion(java.math.BigDecimal)
	 */
	public void setSchemaVersion(BigDecimal value) {
		spec.setSchemaVersion(value);
	}
	/**
	 * @return
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return spec.toString();
	}
	
	

}
