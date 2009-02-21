/**
 * 
 */
package org.rifidi.edge.core.rmi.internal;

import java.rmi.RemoteException;
import java.util.Map;

import javax.management.AttributeList;
import javax.management.MBeanInfo;

import org.rifidi.edge.core.rmi.CommandConfigurationStub;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 *
 */
public class CommandConfigurationStubImpl implements CommandConfigurationStub {

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.CommandConfigurationStub#createCommandConfiguration(java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public String createCommandConfiguration(String commandConfigurationType,
			AttributeList properties) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.CommandConfigurationStub#deleteCommandConfiguration(java.lang.String)
	 */
	@Override
	public void deleteCommandConfiguration(String commandConfigurationID)
			throws RemoteException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.CommandConfigurationStub#getCommandConfigurationDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getCommandConfigurationDescription(
			String commandConfigurationType) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.CommandConfigurationStub#getCommandConfigurationProperties(java.lang.String)
	 */
	@Override
	public AttributeList getCommandConfigurationProperties(
			String commandConfigurationID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.CommandConfigurationStub#getCommandConfigurationTypes()
	 */
	@Override
	public Map<String, String> getCommandConfigurationTypes()
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.CommandConfigurationStub#getCommandConfigurations(java.lang.String)
	 */
	@Override
	public Map<String, String> getCommandConfigurations(
			String readerConfigurationFactoryID) throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.rifidi.edge.core.rmi.CommandConfigurationStub#setCommandConfigurationProperties(java.lang.String, javax.management.AttributeList)
	 */
	@Override
	public AttributeList setCommandConfigurationProperties(
			String commandConfigurationID, AttributeList properties)
			throws RemoteException {
		// TODO Auto-generated method stub
		return null;
	}

}
