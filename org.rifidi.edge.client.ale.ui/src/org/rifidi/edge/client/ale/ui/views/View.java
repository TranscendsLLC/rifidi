/* 
 *  View.java
 *  Created:	Feb 10, 2009
 *  Project:	RiFidi org.rifidi.edge.client.ale
 *  				http://www.rifidi.org
 *  				http://rifidi.sourceforge.net
 *  Copyright:	Pramari LLC and the Rifidi Project
 *  License:	Lesser GNU Public License (LGPL)
 *  				http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.edge.client.ale.ui.views;

import javax.xml.namespace.QName;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.wsdl.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ImplementationExceptionResponse;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class View extends ViewPart {

	public static final String id = "org.rifidi.edge.client.ale.ui.views.View";
	private static final QName ALE_SERVICE_NAME = new QName(
			"urn:epcglobal:ale:wsdl:1", "ALEService");
	// private static final QName LR_SERVICE_NAME = new QName(
	// "urn:epcglobal:alelr:wsdl:1", "ALELRService");
	private ALEServicePortType aleProxy = null;
	private ALELRServicePortType readerProxy = null;
	private String aleEndPoint = "http://localhost:8080/fc-server-0.4.0/services/ALEService";
	private String readerEndPoint = "http://localhost:8080/fc-server-0.4.0/services/ALELRService";
	private Log logger = LogFactory.getLog(View.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		parent.setLayout(new FillLayout());
		Text text = new Text(parent, SWT.MULTI | SWT.WRAP);
		text.setText("Filtering and Collection API\n");

		// init ALE
		text
				.append("\nJaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();");
		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
		text.append("\naleFactory.setServiceClass(ALEServicePortType.class);");
		aleFactory.setServiceClass(ALEServicePortType.class);
		text.append("\naleEndPoint = " + aleEndPoint);
		aleFactory.setAddress(aleEndPoint);
		text.append("\naleProxy = (ALEServicePortType) aleFactory.create();");
		aleProxy = (ALEServicePortType) aleFactory.create();

		try {
			text.append("\ngetVendorVersion(): "
					+ aleProxy.getVendorVersion(new EmptyParms()) + "\n");
		} catch (ImplementationExceptionResponse e) {
			logger.error(e.getMessage());
		}

		text.append("\nLogicalReader API\n");
		// init logical Reader
		text
				.append("\nJaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();");
		JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
		text.append("\nlrFactory.setServiceClass(ALELRServicePortType.class);");
		lrFactory.setServiceClass(ALELRServicePortType.class);
		text.append("\nlrFactory.setAddress(" + readerEndPoint + ");");
		lrFactory.setAddress(readerEndPoint);
		text
				.append("\nreaderProxy = (ALELRServicePortType) lrFactory.create();");
		readerProxy = (ALELRServicePortType) lrFactory.create();

		try {
			text
					.append("\ngetVendorVersion(): "
							+ readerProxy
									.getVendorVersion(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms()));
		} catch (org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ImplementationExceptionResponse e) {
			logger.error(e.getMessage());
		}
		try {
			text
					.append("\ngetLogicalReaders(): "
							+ readerProxy
									.getLogicalReaderNames(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms()));

		} catch (SecurityExceptionResponse e) {
			logger.error(e.getMessage());
		} catch (org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ImplementationExceptionResponse e) {
			logger.error(e.getMessage());
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.ui.part.WorkbenchPart#setFocus()
	 */
	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
