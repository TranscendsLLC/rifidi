package org.rifidi.edge.adapter.thinkify50;

import javax.management.MBeanInfo;

import org.rifidi.edge.configuration.AbstractCommandConfigurationFactory;
import org.rifidi.edge.configuration.AnnotationMBeanInfoStrategy;
import org.rifidi.edge.exceptions.InvalidStateException;

public class Thinkify50PushCommandConfigurationFactory extends
		AbstractCommandConfigurationFactory<Thinkify50PushCommandConfiguration> {
	
	/** Name of the command. */
	public static final String ID = "Thinkify50-Push";
	/** Mbeaninfo for this class. */
	public static final MBeanInfo mbeaninfo;
	static {
		AnnotationMBeanInfoStrategy strategy = new AnnotationMBeanInfoStrategy();
		mbeaninfo = strategy
				.getMBeanInfo(Thinkify50PushCommandConfiguration.class);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#createInstance(java.lang.String)
	 */
	@Override
	public void createInstance(String serviceID)
			throws IllegalArgumentException, InvalidStateException {
		Thinkify50PushCommandConfiguration config = new Thinkify50PushCommandConfiguration();
		config.setID(serviceID);
		config.register(getContext(), Thinkify50SensorFactory.FACTORY_ID);
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getServiceDescription(java.lang.String)
	 */
	@Override
	public MBeanInfo getServiceDescription(String factoryID) {
		return (MBeanInfo) mbeaninfo.clone();
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.ServiceFactory#getFactoryID()
	 */
	@Override
	public String getFactoryID() {
		return ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getReaderFactoryID()
	 */
	@Override
	public String getReaderFactoryID() {
		return Thinkify50SensorFactory.FACTORY_ID;
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getDisplayName()
	 */
	@Override
	public String getDisplayName() {
		return "Thinkify50 Push";
	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.configuration.AbstractCommandConfigurationFactory#getCommandDescription()
	 */
	@Override
	public String getCommandDescription() {
		return "Sets the Thinkify to push tags back to the edge server";
	}

}
