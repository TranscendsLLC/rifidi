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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECBoundarySpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECReportOutputSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECReportSetSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECReportSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECTime;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec.ReportSpecs;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class FncView extends ViewPart {

	public static final String id = "org.rifidi.edge.client.ale.ui.views.FncView";
	private ALEServicePortType aleProxy = null;
	
	private String aleEndPoint = "http://localhost:8080/fc-server-0.4.0/services/ALEService";
	
	private Log logger = LogFactory.getLog(FncView.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		GridLayout grid = new GridLayout();
		grid.numColumns=3;
		grid.makeColumnsEqualWidth=true;
		parent.setLayout(grid);
		
		Label lblEcName = new Label(parent,SWT.SHADOW_NONE);
		lblEcName.setText("Event Cycle Name");
		Text txtEcName = new Text(parent,SWT.SHADOW_NONE);
		
		Label lblInclSpecInRep = new Label(parent,SWT.SHADOW_NONE);
		lblInclSpecInRep.setText("Include Spec in Report?");
		lblInclSpecInRep.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		
		Button oBinclSpecY = new Button(parent,SWT.RADIO);
		oBinclSpecY.setText("yes");
		Button oBinclSpecN = new Button(parent,SWT.RADIO);
		oBinclSpecN.setSelection(true);
		oBinclSpecN.setText("no");
		
		Label lblLogRdrs = new Label(parent,SWT.SHADOW_NONE);
		lblLogRdrs.setText("Logical Readers");
		lblLogRdrs.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Text txtlogRdrs = new Text(parent,SWT.SHADOW_NONE);
		
		Label lblBoundary = new Label(parent,SWT.SHADOW_NONE);
		lblBoundary.setText("Boundary");
		lblBoundary.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Label lblStableSet = new Label(parent,SWT.SHADOW_NONE);
		lblStableSet.setText("Stable Set [ms]");
		lblStableSet.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Text txtStableSet = new Text(parent,SWT.SHADOW_NONE);
		txtStableSet.setText("0");
		Label lblRepeatAfter = new Label(parent,SWT.SHADOW_NONE);
		lblRepeatAfter.setText("Repeat After [ms]");
		lblRepeatAfter.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Text txtRepeatAfter = new Text(parent,SWT.SHADOW_NONE);
		txtRepeatAfter.setText("0");
		Label lblDuration = new Label(parent,SWT.SHADOW_NONE);
		lblDuration.setText("Duration [ms]");
		lblDuration.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING));
		Text txtDuration = new Text(parent,SWT.SHADOW_NONE);
		txtDuration.setText("0");
		
		
		
//		ECSpec spec = new ECSpec();
//		LogicalReaders lrs = new LogicalReaders();
//		lrs.getLogicalReader().add("LogicalReader1");
//		spec.setLogicalReaders(lrs);
//		ECBoundarySpec bspec= new ECBoundarySpec();
//		ECTime time = new ECTime();
//		time.setUnit("MS");
//		time.setValue(5500);
//		bspec.setDuration(time);
//		time.setValue(6000);
//		bspec.setRepeatPeriod(time);
//		time.setValue(0);
//		bspec.setStableSetInterval(time);
//		spec.setBoundarySpec(bspec);
//		ReportSpecs repSpecs = new ReportSpecs();
//		ECReportSpec repSpec = new ECReportSpec();
//		ECReportSetSpec rss = new ECReportSetSpec();
//		rss.setSet("CURRENT");
//		ECReportOutputSpec eros = new ECReportOutputSpec();
//		eros.setIncludeRawHex(true);
//		eros.setIncludeRawDecimal(true);
//		eros.setIncludeEPC(true);
//		repSpec.setOutput(eros);
//		repSpec.setReportSet(rss);
//		repSpecs.getReportSpec().add(repSpec);
//		spec.setReportSpecs(repSpecs);
		
		

		// init ALE
//		logger.debug("\nJaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();");
//		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
//		logger.debug("\naleFactory.setServiceClass(ALEServicePortType.class);");
//		aleFactory.setServiceClass(ALEServicePortType.class);
//		logger.debug("\naleEndPoint = " + aleEndPoint);
//		aleFactory.setAddress(aleEndPoint);
//		logger.debug("\naleProxy = (ALEServicePortType) aleFactory.create();");
//		aleProxy = (ALEServicePortType) aleFactory.create();

//		try {
//			logger.debug("\ngetVendorVersion(): "
//					+ aleProxy.getVendorVersion(new EmptyParms()) + "\n");
//		} catch (ImplementationExceptionResponse e) {
//			logger.error(e.getMessage());
//		}
//
//		logger.debug("\nLogicalReader API\n");
		

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
