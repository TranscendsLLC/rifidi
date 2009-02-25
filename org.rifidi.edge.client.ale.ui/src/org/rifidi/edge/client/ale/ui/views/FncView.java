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

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ALELRServicePortType;
import org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ValidationExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ArrayOfString;
import org.rifidi.edge.client.ale.wsdl.epcglobal.Define;
import org.rifidi.edge.client.ale.wsdl.epcglobal.EmptyParms;
import org.rifidi.edge.client.ale.wsdl.epcglobal.ImplementationExceptionResponse;
import org.rifidi.edge.client.ale.wsdl.epcglobal.SecurityExceptionResponse;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECBoundarySpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECReportOutputSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECReportSetSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECReportSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECTime;
import org.rifidi.edge.client.ale.xsd.epcglobal.LRProperty;
import org.rifidi.edge.client.ale.xsd.epcglobal.LRSpec;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec.LogicalReaders;
import org.rifidi.edge.client.ale.xsd.epcglobal.ECSpec.ReportSpecs;
import org.rifidi.edge.client.ale.xsd.epcglobal.LRSpec.Properties;

/**
 * @author Tobias Hoppenthaler - tobias@pramari.com
 * 
 */
public class FncView extends ViewPart {

	public static final String id = "org.rifidi.edge.client.ale.ui.views.FncView";

	private Log logger = LogFactory.getLog(FncView.class);

	private ALEServicePortType aleProxy = null;
	private String aleEndPoint = "http://localhost:8080/fc-server-0.4.0/services/ALEService";

	private ALELRServicePortType readerProxy = null;
	private String readerEndPoint = "http://localhost:8080/fc-server-0.4.0/services/ALELRService";

	private String LOGICAL_READER_NAME = "LogicalReader1";

	private Group group = null;
	private Label label1 = null;
	private Text txtECspecName = null;
	private Label label = null;
	private Button rbInclInSpecY = null;
	private Button rbInclInSpecN = null;
	private Label label2 = null;
	private Text txtRd2Incl = null;
	private Group group1 = null;
	private Label label3 = null;
	private Text txtStableSet = null;
	private Label label4 = null;
	private Text txtRepeatAfter = null;
	private Label label5 = null;
	private Text txtDuration = null;
	private Group group2 = null;
	private Label label6 = null;
	private Text txtRepSpecName = null;
	private Group group3 = null;
	private Group group4 = null;
	private Button rbRepIfEmptyY = null;
	private Button rbRepIfEmptyN = null;
	private Button rbRepOoChangeY = null;
	private Button rbRepOoChangeN = null;
	private Group group5 = null;
	private Group group6 = null;
	private Group group7 = null;
	private Button rbIncludeTagY = null;
	private Button rbIncludeTagN = null;
	private Button rbIncludeEPCy = null;
	private Button rbIncludeEPCn = null;
	private Group group8 = null;
	private Group group9 = null;
	private Button rbIncludeRawHexY = null;
	private Button rbIncludeRawHexN = null;
	private Button rbIncludeRawDecY = null;
	private Button rbIncludeRawDecN = null;
	private Group group10 = null;
	private Button rbIncludeTagCountY = null;
	private Button rbIncludeTagCountN = null;
	private Button pbExecute = null;
	private Composite parent = null;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.part.WorkbenchPart#createPartControl(org.eclipse.swt.widgets
	 * .Composite)
	 */
	@Override
	public void createPartControl(Composite parent) {

		this.parent = parent;
		initALE();
		initALELR();
		initialize();

	}

	/**
	 * 
	 */
	private void initALELR() {
		// init ALELR
		logger
				.debug("\nJaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();");
		JaxWsProxyFactoryBean lrFactory = new JaxWsProxyFactoryBean();
		logger
				.debug("\nlrFactory.setServiceClass(ALELRServicePortType.class);");
		lrFactory.setServiceClass(ALELRServicePortType.class);
		logger.debug("\nlrFactory.setAddress(" + readerEndPoint + ");");
		lrFactory.setAddress(readerEndPoint);
		logger
				.debug("\nreaderProxy = (ALELRServicePortType) lrFactory.create();");
		readerProxy = (ALELRServicePortType) lrFactory.create();

	}

	/**
	 * 
	 */
	private void initALE() {
		// init ALE
		logger
				.debug("\nJaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();");
		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
		logger.debug("\naleFactory.setServiceClass(ALEServicePortType.class);");
		aleFactory.setServiceClass(ALEServicePortType.class);
		logger.debug("\naleEndPoint = " + aleEndPoint);
		aleFactory.setAddress(aleEndPoint);
		logger.debug("\naleProxy = (ALEServicePortType) aleFactory.create();");
		aleProxy = (ALEServicePortType) aleFactory.create();

	}

	private ECSpec buildEcSpec() {
		ECSpec spec = new ECSpec();
		LogicalReaders lrs = new LogicalReaders();
		// lrs.getLogicalReader().add(txtRd2Incl.getText());
		lrs.getLogicalReader().add(LOGICAL_READER_NAME);
		spec.setLogicalReaders(lrs);
		ECBoundarySpec bspec = new ECBoundarySpec();
		ECTime durationTime = new ECTime();
		durationTime.setUnit("MS");
		// Long.parseLong(txtDuration.getText())
		durationTime.setValue(5500);
		bspec.setDuration(durationTime);
		ECTime repeatTime = new ECTime();
		repeatTime.setUnit("MS");
		// Long.parseLong(txtRepeatAfter.getText())
		repeatTime.setValue(6000);
		bspec.setRepeatPeriod(repeatTime);
		ECTime stableSetTime = new ECTime();
		stableSetTime.setUnit("MS");
		// Long.parseLong(txtStableSet.getText())
		stableSetTime.setValue(0);
		bspec.setStableSetInterval(stableSetTime);
		spec.setBoundarySpec(bspec);
		ReportSpecs repSpecs = new ReportSpecs();
		ECReportSpec repSpec = new ECReportSpec();
		ECReportSetSpec rss = new ECReportSetSpec();
		// txtRepSpecName.getText()
		rss.setSet("CURRENT");
		ECReportOutputSpec eros = new ECReportOutputSpec();
		// rbIncludeRawDecY.getSelection()
		eros.setIncludeRawHex(true);
		// rbIncludeRawDecY.getSelection()
		eros.setIncludeRawDecimal(true);
		// rbIncludeEPCy.getSelection()
		eros.setIncludeEPC(true);
		repSpec.setOutput(eros);
		repSpec.setReportSet(rss);
		repSpecs.getReportSpec().add(repSpec);
		spec.setReportSpecs(repSpecs);
		return spec;
	}

	private void initialize() {
		createGroup();
		parent.setSize(new Point(736, 1015));
		parent.setLayout(new GridLayout(3, true));
		label2 = new Label(parent, SWT.NONE);
		label2.setText("Logical Readers:");
		txtRd2Incl = new Text(parent, SWT.BORDER);
		txtRd2Incl.setText(LOGICAL_READER_NAME);
		createGroup1();
		createGroup2();
		createGroup5();
		pbExecute = new Button(parent, SWT.NONE);
		pbExecute.setText("Define");
		pbExecute
				.addSelectionListener(new org.eclipse.swt.events.SelectionListener() {
					public void widgetSelected(
							org.eclipse.swt.events.SelectionEvent e) {
						logger.debug("widgetSelected()");
						execute();
					}

					public void widgetDefaultSelected(
							org.eclipse.swt.events.SelectionEvent e) {
					}
				});
	}

	private void execute() {
		ECSpec spec = buildEcSpec();
		LRSpec lrSpec = buildLrSpec();
		// specify logical reader

		org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.Define defineR = new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.Define();
		defineR.setName(LOGICAL_READER_NAME);

		defineR.setSpec(lrSpec);

		try {
			logger.debug("\ngetVendorVersion(): "
					+ aleProxy.getVendorVersion(new EmptyParms()) + "\n");
			System.out.println("\ngetStandardVersion(): "
					+ aleProxy.getStandardVersion(new EmptyParms()) + "\n");
			Object out = null;
			out = readerProxy
					.getVendorVersion(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms());
			logger.debug(out);
			out = readerProxy
					.getStandardVersion(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms());
			logger.debug(out);
			org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.ArrayOfString readers = readerProxy
					.getLogicalReaderNames(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms());
			ArrayList<String> rDstrings = (ArrayList<String>) readers
					.getString();
			if (rDstrings.size() < 1)
				logger.debug("NO READER IS DEFINED ON SERVER");
			for (String string : rDstrings) {
				logger.debug("\n" + string);
			}
			out = readerProxy.define(defineR);
			logger.debug(out);

			readers = readerProxy
					.getLogicalReaderNames(new org.rifidi.edge.client.ale.logicalreader.wsdl.epcglobal.EmptyParms());
			rDstrings = (ArrayList<String>) readers.getString();
			if (rDstrings.size() < 1)
				logger.debug("NO READER IS DEFINED ON SERVER");
			for (String string : rDstrings) {
				logger.debug("\n" + string);
			}
			ArrayOfString names = aleProxy.getECSpecNames(new EmptyParms());
			ArrayList<String> strings = (ArrayList<String>) names.getString();
			if (strings.size() < 1)
				logger.debug("NO ECSPEC IS DEFINED ON SERVER");
			for (String string : strings) {
				logger.debug("\n" + string);
			}
			Define define = new Define();
			define.setSpec(spec);
			define.setSpecName(txtECspecName.getText());
			aleProxy.define(define);
			names = aleProxy.getECSpecNames(new EmptyParms());
			strings = (ArrayList<String>) names.getString();
			if (strings.size() < 1)
				logger.debug("NO ECSPEC IS DEFINED ON SERVER");
			for (String string : strings) {
				logger.debug("\n" + string);
			}

		} catch (Exception e) {
			logger.error(e.getMessage());
		}

	}

	private LRSpec buildLrSpec() {
		LRSpec lrSpec = new LRSpec();
		lrSpec.setIsComposite(false);

		Properties props = new Properties();

		LRProperty lrpReaderType = new LRProperty();
		lrpReaderType.setName("ReaderType");
		lrpReaderType.setValue("org.fosstrak.ale.server.readers.rp.RPAdaptor");
		props.getProperty().add(lrpReaderType);

		LRProperty lrpDesc = new LRProperty();
		lrpDesc.setName("Description");
		lrpDesc
				.setValue("This Logical Reader consists of shelf 1 and shelf 2,3,4 of the physical reader named MyReader");
		props.getProperty().add(lrpDesc);

		LRProperty lrpPhysName = new LRProperty();
		lrpPhysName.setName("PhysicalReaderName");
		lrpPhysName.setValue("MyReader");
		props.getProperty().add(lrpPhysName);

		LRProperty lrpReadInterval = new LRProperty();
		lrpReadInterval.setName("ReadTimeInterval");
		lrpReadInterval.setValue("1000");
		props.getProperty().add(lrpReadInterval);

		LRProperty lrpPhysicalSource = new LRProperty();
		lrpPhysicalSource.setName("PhysicalReaderSource");
		lrpPhysicalSource.setValue("Shelf1,Shelf2,Shelf3,Shelf4");
		props.getProperty().add(lrpPhysicalSource);

		LRProperty lrpNotificationPoint = new LRProperty();
		lrpNotificationPoint.setName("NotificationPoint");
		lrpNotificationPoint.setValue("http://localhost:9090");
		props.getProperty().add(lrpNotificationPoint);

		LRProperty lrpConnectionPoint = new LRProperty();
		lrpConnectionPoint.setName("ConnectionPoint");
		lrpConnectionPoint.setValue("http://localhost:8000");
		props.getProperty().add(lrpConnectionPoint);

		lrSpec.setProperties(props);
		return lrSpec;
	}

	/**
	 * This method initializes group
	 * 
	 */
	private void createGroup() {
		group = new Group(parent, SWT.NONE);
		group.setLayout(new GridLayout());
		group.setText("Create new Event Cycle");
		label1 = new Label(group, SWT.NONE);
		label1.setText("Event Cycle Name:");
		txtECspecName = new Text(group, SWT.BORDER);
		txtECspecName.setText("specCurrent");
		label = new Label(group, SWT.NONE);
		label.setText("Include Spec in Reports:");
		rbInclInSpecY = new Button(group, SWT.RADIO);
		rbInclInSpecY.setText("Yes");
		rbInclInSpecN = new Button(group, SWT.RADIO);
		rbInclInSpecN.setText("No");
		rbInclInSpecN.setSelection(true);
	}

	/**
	 * This method initializes group1
	 * 
	 */
	private void createGroup1() {
		group1 = new Group(parent, SWT.NONE);
		group1.setLayout(new GridLayout());
		group1.setText("Boundary");
		label3 = new Label(group1, SWT.NONE);
		label3.setText("Stable Set [ms] :");
		txtStableSet = new Text(group1, SWT.BORDER);
		txtStableSet.setText("0");
		label4 = new Label(group1, SWT.NONE);
		label4.setText("Repeat After [ms] :");
		txtRepeatAfter = new Text(group1, SWT.BORDER);
		txtRepeatAfter.setText("6000");
		label5 = new Label(group1, SWT.NONE);
		label5.setText("Duration [ms] :");
		txtDuration = new Text(group1, SWT.BORDER);
		txtDuration.setText("5500");
	}

	/**
	 * This method initializes group2
	 * 
	 */
	private void createGroup2() {
		group2 = new Group(parent, SWT.NONE);
		group2.setLayout(new GridLayout());
		group2.setText("Report Spec");
		label6 = new Label(group2, SWT.NONE);
		label6.setText("Name:");
		txtRepSpecName = new Text(group2, SWT.BORDER);
		txtRepSpecName.setText("CURRENT");
		createGroup3();
		createGroup4();
	}

	/**
	 * This method initializes group3
	 * 
	 */
	private void createGroup3() {
		group3 = new Group(group2, SWT.NONE);
		group3.setLayout(new GridLayout());
		group3.setText("Report if Empty");
		rbRepIfEmptyY = new Button(group3, SWT.RADIO);
		rbRepIfEmptyY.setText("Yes");
		rbRepIfEmptyN = new Button(group3, SWT.RADIO);
		rbRepIfEmptyN.setText("No");
		rbRepIfEmptyN.setSelection(true);
	}

	/**
	 * This method initializes group4
	 * 
	 */
	private void createGroup4() {
		group4 = new Group(group2, SWT.NONE);
		group4.setLayout(new GridLayout());
		group4.setText("Report only on Change");
		rbRepOoChangeY = new Button(group4, SWT.RADIO);
		rbRepOoChangeY.setText("Yes");
		rbRepOoChangeN = new Button(group4, SWT.RADIO);
		rbRepOoChangeN.setText("No");
		rbRepOoChangeN.setSelection(true);
	}

	/**
	 * This method initializes group5
	 * 
	 */
	private void createGroup5() {
		group5 = new Group(parent, SWT.NONE);
		group5.setLayout(new GridLayout());
		createGroup6();
		group5.setText("Output Options");
		createGroup7();
		createGroup8();
		createGroup9();
		createGroup10();
	}

	/**
	 * This method initializes group6
	 * 
	 */
	private void createGroup6() {
		group6 = new Group(group5, SWT.NONE);
		group6.setLayout(new GridLayout());
		group6.setText("Include Tag");
		rbIncludeTagY = new Button(group6, SWT.RADIO);
		rbIncludeTagY.setText("Yes");
		rbIncludeTagN = new Button(group6, SWT.RADIO);
		rbIncludeTagN.setText("No");
		rbIncludeTagN.setSelection(true);
	}

	/**
	 * This method initializes group7
	 * 
	 */
	private void createGroup7() {
		group7 = new Group(group5, SWT.NONE);
		group7.setLayout(new GridLayout());
		group7.setText("Include EPC");
		rbIncludeEPCy = new Button(group7, SWT.RADIO);
		rbIncludeEPCy.setText("Yes");
		rbIncludeEPCn = new Button(group7, SWT.RADIO);
		rbIncludeEPCn.setText("No");
		rbIncludeEPCn.setSelection(true);
	}

	/**
	 * This method initializes group8
	 * 
	 */
	private void createGroup8() {
		group8 = new Group(group5, SWT.NONE);
		group8.setLayout(new GridLayout());
		group8.setText("Include RAW Hex");
		rbIncludeRawHexY = new Button(group8, SWT.RADIO);
		rbIncludeRawHexY.setText("Yes");
		rbIncludeRawHexN = new Button(group8, SWT.RADIO);
		rbIncludeRawHexN.setText("No");
		rbIncludeRawHexN.setSelection(true);
	}

	/**
	 * This method initializes group9
	 * 
	 */
	private void createGroup9() {
		group9 = new Group(group5, SWT.NONE);
		group9.setLayout(new GridLayout());
		group9.setText("Include RAW Decimal");
		rbIncludeRawDecY = new Button(group9, SWT.RADIO);
		rbIncludeRawDecY.setText("Yes");
		rbIncludeRawDecN = new Button(group9, SWT.RADIO);
		rbIncludeRawDecN.setText("No");
		rbIncludeRawDecN.setSelection(true);
	}

	/**
	 * This method initializes group10
	 * 
	 */
	private void createGroup10() {
		group10 = new Group(group5, SWT.NONE);
		group10.setLayout(new GridLayout());
		group10.setText("Include Tag Count");
		rbIncludeTagCountY = new Button(group10, SWT.RADIO);
		rbIncludeTagCountY.setText("Yes");
		rbIncludeTagCountN = new Button(group10, SWT.RADIO);
		rbIncludeTagCountN.setText("No");
		rbIncludeTagCountN.setSelection(true);
	}

	// @jve:decl-index=0:visual-constraint="324,21"

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
