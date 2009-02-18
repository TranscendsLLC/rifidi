/* 
 *  ConvenienceLogicalReaderApi.java
 *  Created:	Feb 12, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.lra;

import java.util.ArrayList;

import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.AddReadersResult;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.DefineResult;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ImmutableReaderExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.InUseExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.NonCompositeReaderExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ReaderLoopExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.RemoveReadersResult;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.SetPropertiesResult;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.SetReadersResult;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.UndefineResult;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.UpdateResult;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ValidationExceptionResponse;
import org.rifidi.edge.client.ale.xsd.epcglobal.LRProperty;
import org.rifidi.edge.client.ale.xsd.epcglobal.LRSpec;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class ConvenienceLogicalReaderApi {
	
	public String getEndpoint(){
		return null;
	}
	
	public void setEndpoint(String endPointName){
		
	}

	public DefineResult define(String readerName, LRSpec spec)
			throws SecurityExceptionResponse, ImplementationExceptionResponse,
			DuplicateNameExceptionResponse, ValidationExceptionResponse {
	
		return null;
	}

	public UndefineResult undefine(String readerName)
			throws SecurityExceptionResponse, InUseExceptionResponse,
			ImplementationExceptionResponse, ImmutableReaderExceptionResponse,
			NoSuchNameExceptionResponse {
		return null;
	}

	public UpdateResult update(String readerName, LRSpec spec)
			throws ReaderLoopExceptionResponse, SecurityExceptionResponse,
			InUseExceptionResponse, ImplementationExceptionResponse,
			ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse,
			ValidationExceptionResponse {
		return null;
	}

	public ArrayList<String> getLogicalReaderNames()
			throws SecurityExceptionResponse, ImplementationExceptionResponse {
		return null;
	}

	public LRSpec getLRSpec(String readerName)
			throws SecurityExceptionResponse, ImplementationExceptionResponse,
			NoSuchNameExceptionResponse {
		return null;
	}

	public AddReadersResult addReaders(String readerName, String[] readers)
			throws ReaderLoopExceptionResponse,
			NonCompositeReaderExceptionResponse, SecurityExceptionResponse,
			InUseExceptionResponse, ImplementationExceptionResponse,
			ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse,
			ValidationExceptionResponse {
		return null;
	}

	public SetReadersResult setReaders(String readerName, String[] readers)
			throws ReaderLoopExceptionResponse,
			NonCompositeReaderExceptionResponse, SecurityExceptionResponse,
			InUseExceptionResponse, ImplementationExceptionResponse,
			ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse,
			ValidationExceptionResponse {
		return null;
	}

	public RemoveReadersResult removeReaders(String readerName, String[] readers)
			throws NonCompositeReaderExceptionResponse,
			SecurityExceptionResponse, InUseExceptionResponse,
			ImplementationExceptionResponse, ImmutableReaderExceptionResponse,
			NoSuchNameExceptionResponse {
		return null;
	}

	public SetPropertiesResult setProperties(String readerName,
			LRProperty[] properties) throws SecurityExceptionResponse,
			InUseExceptionResponse, ImplementationExceptionResponse,
			ImmutableReaderExceptionResponse, NoSuchNameExceptionResponse,
			ValidationExceptionResponse {
		return null;
	}

	public String getPropertyValue(String readerName, String propertyName)
			throws SecurityExceptionResponse, ImplementationExceptionResponse,
			NoSuchNameExceptionResponse {
		return null;
	}

	public String getStandardVersion() throws ImplementationExceptionResponse {
		return null;
	}

	public String getVendorVersion() throws ImplementationExceptionResponse {
		return null;
	}

}
