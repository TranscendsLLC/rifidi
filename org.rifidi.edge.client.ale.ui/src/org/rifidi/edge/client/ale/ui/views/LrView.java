/* 
 *  LrView.java
 *  Created:	Feb 16, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale.ui
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.views;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.SecurityExceptionResponse;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 *
 */
public class LrView extends ViewPart {

	private ALELRServicePortType readerProxy = null;
	private String readerEndPoint = "http://localhost:8080/fc-server-0.4.0/services/ALELRService";
	private Log logger = LogFactory.getLog(LrView.class);
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets.Composite)
	 */
	@Override
	public void createPartControl(Composite arg0) {
		// init logical Reader
		logger.debug("\nJaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();");
		JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
		logger.debug("\nlrFactory.setServiceClass(ALELRServicePortType.class);");
		lrFactory.setServiceClass(ALELRServicePortType.class);
		logger.debug("\nlrFactory.setAddress(" + readerEndPoint + ");");
		lrFactory.setAddress(readerEndPoint);
		logger.debug("\nreaderProxy = (ALELRServicePortType) lrFactory.create();");
		readerProxy = (ALELRServicePortType) lrFactory.create();

		try {
			logger.debug("\ngetVendorVersion(): "
							+ readerProxy
									.getVendorVersion(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms()));
		} catch (org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ImplementationExceptionResponse e) {
			logger.error(e.getMessage());
		}
		try {
			logger.debug("\ngetLogicalReaders(): "
							+ readerProxy
									.getLogicalReaderNames(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms()));

		} catch (SecurityExceptionResponse e) {
			logger.error(e.getMessage());
		} catch (org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ImplementationExceptionResponse e) {
			logger.error(e.getMessage());
		}
		

	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
