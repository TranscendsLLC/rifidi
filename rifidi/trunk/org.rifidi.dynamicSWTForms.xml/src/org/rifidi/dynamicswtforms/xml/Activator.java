package org.rifidi.dynamicswtforms.xml;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.rifidi.dynamicswtforms.xml.processor.DynamicSWTFormXMLProcessor;
import org.rifidi.dynamicswtforms.xml.processor.impl.DynamicSWTFormXMLProcessorImpl;

public class Activator implements BundleActivator {

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		
		System.out.println("Registering Serivce: WidgetAnnotaionProcessorService");
		DynamicSWTFormXMLProcessor widgetService = new DynamicSWTFormXMLProcessorImpl();
		context.registerService(DynamicSWTFormXMLProcessor.class.getName(), widgetService, null);
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
	}

}
