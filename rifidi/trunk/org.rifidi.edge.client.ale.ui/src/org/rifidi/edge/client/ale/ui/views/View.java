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

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.wsdl.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ImplementationExceptionResponse;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class View extends ViewPart {

	public static final String id = "org.rifidi.edge.client.ale.ui.views.View";
	private ALEServicePortType aleProxy = null;
	private ALELRServicePortType readerProxy = null;
	private String aleEndPoint = "";
	private String readerEndPoint = "";

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {
		// init ALE
		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
		aleFactory.setServiceClass(ALEServicePortType.class);
		aleEndPoint = "http://localhost:8080/fc-server-0.4.0/services/ALEService";
		aleFactory.setAddress(aleEndPoint);
		aleProxy = (ALEServicePortType) aleFactory.create();
		
		// init logical Reader
		JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
		lrFactory.setServiceClass(ALELRServicePortType.class);
		readerEndPoint="http://localhost:8080/fc-server-0.4.0/services/ALELRService";
		lrFactory.setAddress(readerEndPoint);
		readerProxy = (ALELRServicePortType) lrFactory.create();
		
		try {
			System.out.println(aleProxy.getVendorVersion(new EmptyParms()));
		} catch (ImplementationExceptionResponse e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
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
