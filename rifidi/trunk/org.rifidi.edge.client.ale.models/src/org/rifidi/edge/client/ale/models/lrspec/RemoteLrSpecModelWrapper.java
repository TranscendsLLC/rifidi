/* 
 *  RemoteLrSpecModelWrapper.java
 *  Created:	Apr 20, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.models
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.models.lrspec;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.AddReaders;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.Define;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.DuplicateNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.GetLRSpec;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.GetPropertyValue;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImmutableReaderExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.InUseExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.NoSuchNameExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.NonCompositeReaderExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ReaderLoopExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.RemoveReaders;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SetProperties;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SetPropertiesResult;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SetReaders;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.Undefine;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.Update;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.ValidationExceptionResponse;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.AddReaders.Readers;
import org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SetProperties.Properties;
import org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRProperty;
import org.rifidi.edge.client.ale.api.xsd.alelr.epcglobal.LRSpec;
import org.rifidi.edge.client.ale.models.alelrserviceporttype.AleLrServicePortTypeWrapper;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class RemoteLrSpecModelWrapper {

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	private String name;
	private AleLrServicePortTypeWrapper parent;
	private LRSpec lrSpec = new LRSpec();
	private Log logger = LogFactory.getLog(RemoteLrSpecModelWrapper.class);

	public RemoteLrSpecModelWrapper(String name,
			AleLrServicePortTypeWrapper parent) {
		this.name = name;
		this.parent = parent;
	}

	public LRSpec getExtLrSpec() {
		GetLRSpec parms = new GetLRSpec();
		parms.setName(name);
		try {
			return this.parent.getAleLrServicePortType().getLRSpec(parms);
		} catch (SecurityExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (NoSuchNameExceptionResponse e) {
			logger.debug(e.getMessage());
		}
		return new LRSpec();
	}

	/**
	 * @return the lrSpec
	 */
	public LRSpec getLrSpec() {
		return lrSpec;
	}

	/**
	 * @param lrSpec
	 *            the lrSpec to set
	 */
	public void setLrSpec(LRSpec lrSpec) {
		this.lrSpec = lrSpec;
	}

	public String define() {
		Define parms = new Define();
		parms.setName(name);
		parms.setSpec(this.lrSpec);
		String retVal = "";
		try {
			this.parent.getAleLrServicePortType().define(parms);

		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (DuplicateNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ValidationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;
	}

	public String undefine() {
		Undefine parms = new Undefine();
		parms.setName(name);
		String retVal = "";
		try {
			this.parent.getAleLrServicePortType().undefine(parms);

		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (InUseExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImmutableReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;
	}

	public String update() {
		Update parms = new Update();
		parms.setName(name);
		parms.setSpec(lrSpec);
		String retVal = "";
		try {
			this.parent.getAleLrServicePortType().update(parms);

		} catch (ReaderLoopExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (InUseExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImmutableReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ValidationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;
	}

	public String addReaders(String[] readers) {
		AddReaders parms = new AddReaders();
		parms.setName(name);
		Readers readersToAdd = new Readers();

		readersToAdd.getReader().clear();

		for (int i = 0; i < readers.length; i++) {
			readersToAdd.getReader().add(readers[i]);
		}

		parms.setReaders(readersToAdd);
		String retVal = "";
		try {
			this.parent.getAleLrServicePortType().addReaders(parms);

		} catch (ReaderLoopExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NonCompositeReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (InUseExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImmutableReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ValidationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;
	}

	public String setReaders(String[] readers) {
		SetReaders parms = new SetReaders();
		parms.setName(name);
		org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SetReaders.Readers readersToSet = new org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.SetReaders.Readers();

		readersToSet.getReader().clear();

		for (int i = 0; i < readers.length; i++) {

			readersToSet.getReader().add(readers[i]);
		}
		parms.setReaders(readersToSet);
		String retVal = "";
		try {
			this.parent.getAleLrServicePortType().setReaders(parms);
		} catch (ReaderLoopExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NonCompositeReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (InUseExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImmutableReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ValidationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;
	}

	public String removeReaders(String[] readers) {
		RemoveReaders parms = new RemoveReaders();
		parms.setName(name);
		org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.RemoveReaders.Readers readersToRemove = new org.rifidi.edge.client.ale.api.wsdl.alelr.epcglobal.RemoveReaders.Readers();

		readersToRemove.getReader().clear();

		for (int i = 0; i < readers.length; i++) {
			readersToRemove.getReader().add(readers[i]);
		}
		parms.setReaders(readersToRemove);
		String retVal = "";
		try {
			this.parent.getAleLrServicePortType().removeReaders(parms);
		} catch (NonCompositeReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (SecurityExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (InUseExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImplementationExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (ImmutableReaderExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		} catch (NoSuchNameExceptionResponse e) {
			retVal = e.getMessage();
			logger.debug(retVal);
		}
		return retVal;
	}

	public SetPropertiesResult setProperties(LRProperty[] properties) {
		SetProperties parms = new SetProperties();
		parms.setName(name);
		Properties propertiesToSet = new Properties();
		propertiesToSet.getProperty().clear();
		for (int i = 0; i < properties.length; i++) {
			propertiesToSet.getProperty().add(properties[i]);
		}
		parms.setProperties(propertiesToSet);

		try {
			return this.parent.getAleLrServicePortType().setProperties(parms);
		} catch (SecurityExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (InUseExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (ImmutableReaderExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (NoSuchNameExceptionResponse e) {
			logger.debug(e.getMessage());
		} catch (ValidationExceptionResponse e) {
			logger.debug(e.getMessage());
		}
		return null;

	}

	public String getPropertyValue(String propertyName) {
		GetPropertyValue parms = new GetPropertyValue();
		parms.setName(name);
		parms.setPropertyName(propertyName);
		try {
			return this.parent.getAleLrServicePortType()
					.getPropertyValue(parms);
		} catch (SecurityExceptionResponse e) {
			logger.debug(e.getMessage());
			return e.getMessage();
		} catch (ImplementationExceptionResponse e) {
			logger.debug(e.getMessage());
			return e.getMessage();
		} catch (NoSuchNameExceptionResponse e) {
			logger.debug(e.getMessage());
			return e.getMessage();
		}
	}

}
