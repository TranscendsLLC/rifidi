package org.rifidi.edge.server.epcglobal.ale.services;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;
import org.rifidi.app.ale.AleApp;
import org.rifidi.edge.api.ReaderDTO;
import org.rifidi.edge.api.SensorManagerService;
import org.rifidi.edge.api.service.tagmonitor.ReadZone;
import org.rifidi.edge.epcglobal.ale.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ECReportExtension;
import org.rifidi.edge.epcglobal.ale.ECReports;
import org.rifidi.edge.epcglobal.ale.ECReportsExtension;
import org.rifidi.edge.epcglobal.ale.ECSpec;
import org.rifidi.edge.epcglobal.ale.ECSpec.LogicalReaders;
import org.rifidi.edge.epcglobal.ale.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.AddReaders;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.server.ale.infrastructure.MqttNotifier;
import org.rifidi.edge.server.ale.infrastructure.Notifier;
import org.rifidi.edge.server.epcglobal.ale.Cycle;
import org.rifidi.edge.server.epcglobal.ale.EventCycle;
import org.rifidi.edge.server.epcglobal.alelr.Reader;
import org.rifidi.edge.server.epcglobal.alelr.services.CycleService;
import org.rifidi.edge.server.epcglobal.alelr.services.LogicalReaderService;
import org.rifidi.edge.utils.DeserializerUtil;
import org.rifidi.edge.utils.ECSpecValidator;
import org.rifidi.edge.utils.FileUtils;
import org.rifidi.edge.utils.PersistenceConfig;
import org.rifidi.edge.utils.RemoveConfig;
import org.rifidi.edge.utils.RifidiHelper;
import org.rifidi.edge.utils.SerializerUtil;
import org.rifidi.edge.utils.WriteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ALEService implements ApplicationLevelEventService {

	/**
	 * prefix for name of report generators which are created by immediate
	 * command.
	 */
	private static final String REPORT_GENERATOR_NAME_PREFIX = "ReportGenerator_";

	/**
	 * index for name of report generator which are created by immediate
	 * command.
	 */
	private long nameCounter = 0;

	// private ReportsGeneratorsProvider reportGeneratorsProvider = new
	// ReportsGeneratorsProvider();

	@Autowired
	private WriteConfig persistenceWriteAPI;

	@Autowired
	private RemoveConfig persistenceRemoveAPI;

	@Autowired
	private CycleService cycleService;

	@Autowired
	private PersistenceConfig config;

	@Autowired
	private FileUtils fileUtils;
	
	@Autowired
	private RifidiHelper rifidiHelper;
	
	@Autowired
	private LogicalReaderService logicalReaderService;
	
	@Autowired
	private SensorManagerService sensorManagerService;
	
	@Autowired
	private AleApp aleApp;
	
	@Autowired
	private ECSpecValidator ecSpecValidator; 

	/** logger. */
	private static final Logger LOG = Logger.getLogger(ALEService.class);

	public ALEService() {
		super();
		System.out.println("ALEService() call");
	}

	@Override
	public boolean isReady() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void define(String specName, ECSpec spec)
			throws DuplicateNameExceptionResponse, ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		// llrpControllerManager.define(lrSpecName, addRoSpec);
//		System.out.println("ALEService.Define, specName: " + specName + "; spec: " + spec);
		ecSpecValidator.validateSpec(spec);

		if (cycleService.containsKey(specName)) {
			LOG.debug("spec already defined: " + specName);
			throw new DuplicateNameExceptionResponse("ECSpec already defined with name: " + specName);
		}
		cycleService.define(specName, spec);

		// cycleService.put(specName,
		// cycleService.createNewReportGenerator(specName, spec));

		if (cycleService.containsKey(specName)) {
			System.out.println("ALEService.define: reportGeneratorsProvider contains " + specName);
		} else {
			System.out.println("ALEService.define: reportGeneratorsProvider NOT contains " + specName);
		}

		persistenceWriteAPI.writeECSpec(specName, spec);

	}

	@Override
	public void undefine(String specName) throws NoSuchNameExceptionResponse {
		// TODO Auto-generated method stub
		System.out.println("ALEService.Undefine, specName: " + specName);

		throwNoSuchNameExceptionIfNoSuchSpec(specName);
		
		//Before undefine, validate there are not subscribers
		if( !getSubscribers(specName).isEmpty() ){
			throw new NoSuchNameExceptionResponse("There are still subscribers to '" + specName
					+ "'. Please first unsubscribe them.");
		}

		// cycleService.remove(specName);
		cycleService.undefine(specName);
		persistenceRemoveAPI.removeECSpec(specName);
	}

	@Override
	public ECSpec getECSpec(String specName) throws NoSuchNameExceptionResponse {
		// TODO Auto-generated method stub
		throwNoSuchNameExceptionIfNoSuchSpec(specName);

		final String path = config.getRealPathECSpecDir();
		final String fileName = specName + ".xml";

		if (!fileUtils.fileExist(fileName, path)) {
			throw new NoSuchNameExceptionResponse();

		} else {

			try {

				ECSpec ecSpec = DeserializerUtil.deserializeECSpec(path + fileName);
				return ecSpec;
			} catch (Exception e) {
				e.printStackTrace();
				throw new NoSuchNameExceptionResponse(e.getMessage());
			}
		}

		/*
		 * Cycle cycle = cycleService.get(specName); ECSpec ecSpec =
		 * cycle.getECSpec();
		 */
		// return ecSpec;

	}

	@Override
	public String[] getECSpecNames() {
		// TODO Auto-generated method stub
		// return reportGeneratorsProvider.keySet().toArray(new String[0]);
		List<String> specNames = cycleService.getECSpecNames();
		return specNames.toArray(new String[0]);
	}

	@Override
	public void subscribe(String specName, String notificationURI)
			throws NoSuchNameExceptionResponse, InvalidURIExceptionResponse, DuplicateSubscriptionExceptionResponse {
		// TODO Auto-generated method stub

		throwNoSuchNameExceptionIfNoSuchSpec(specName);
		cycleService.subscribe(specName, notificationURI);

		// reportGeneratorsProvider.get(specName).subscribe(notificationURI);
		persistenceWriteAPI.writeECSpecSubscriber(specName, notificationURI);

	}

	@Override
	public void unsubscribe(String specName, String notificationURI)
			throws NoSuchNameExceptionResponse, NoSuchSubscriberExceptionResponse, InvalidURIExceptionResponse {
		// TODO Auto-generated method stub

		throwNoSuchNameExceptionIfNoSuchSpec(specName);

		cycleService.unsubscribe(specName, notificationURI);
		persistenceRemoveAPI.removeECSpecSubscriber(specName, notificationURI);

	}

	@Override
	public ECReports poll(String specName) throws NoSuchNameExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		throwNoSuchNameExceptionIfNoSuchSpec(specName);
		
		List<String> errorMessages = new ArrayList<>();
		
		ECSpec ecSpec = getECSpec(specName);
		
		
		try {
						
			errorMessages = rifidiHelper.validateSessionInProcessingState(ecSpec);
			
			if ( !errorMessages.isEmpty() ){
				String strError = rifidiHelper.getErrorMessagesAsSingleText(errorMessages);
				throw new ImplementationExceptionResponse(strError);
			}
			
		} catch (Exception e){
			throw new ImplementationExceptionResponse(e.getMessage(), e);
		}
				
		ECReports reports = poll(cycleService.get(specName));
		

		
		// INIT test
//		try {
//			Notifier notifier = new MqttNotifier();
//			String xmlReport = notifier.getXml(reports);
//			System.out.println("xmlReport: " + xmlReport);
//		} catch (ImplementationExceptionResponse e) {
//			e.printStackTrace();
//		}
		// End test

		Cycle cycle = cycleService.get(specName);
		cycle.limpiarTags();
		return reports;

	}

	@Override
	public ECReports immediate(ECSpec spec) throws ECSpecValidationExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		try {
			
			ecSpecValidator.validateSpec(spec);
			
			for (String logicalReaderName : spec.getLogicalReaders().getLogicalReader()) {
				Reader reader = logicalReaderService.getLogicalReader(logicalReaderName);
				if ( reader == null ){
					throw new ECSpecValidationExceptionResponse("Not found a logical reader with name: '" + logicalReaderName + "'. Make sure you have defined this logical reader by using ALELR define method");
				}
			}

			String reportName = getNextReportGeneratorName();
			
			List<String> errorMessages = new ArrayList<>();
			
			try {
				errorMessages = rifidiHelper.validateSessionInProcessingState(spec);
				
				if ( !errorMessages.isEmpty() ){
					String strError = rifidiHelper.getErrorMessagesAsSingleText(errorMessages);
					throw new ImplementationExceptionResponse(strError);
				}
				
			} catch (Exception e){
				throw new ImplementationExceptionResponse(e.getMessage(), e);
			}
			
			Cycle cycle = cycleService.define(reportName, spec);

			ECReports reports = poll(cycle);
			
			cycleService.undefine(reportName);

			return reports;
		} catch (NoSuchNameExceptionResponse e) {
			throw new ImplementationExceptionResponse("immediate failed");
		}
		// return null;

	}

	@Override
	public Set<String> getSubscribers(String specName) throws NoSuchNameExceptionResponse {
		// TODO Auto-generated method stub
		throwNoSuchNameExceptionIfNoSuchSpec(specName);
		return cycleService.getSubscribers(specName);

	}

	@Override
	public String getStandardVersion() {
		// TODO Auto-generated method stub
		LOG.debug("getStandardVersion");
		return "1.0";// aleSettings.getAleStandardVersion();

	}

	@Override
	public String getVendorVersion() {
		// TODO Auto-generated method stub
		LOG.debug("getVendorVersion");
		return "1.0";// aleSettings.getVendorVersion();

	}

	/**
	 * throws an exception if the given specification name is not existing.
	 * 
	 * @param specName
	 *            the name of the specification to verify.
	 * @throws NoSuchNameException
	 *             when name not existing.
	 */
	protected void throwNoSuchNameExceptionIfNoSuchSpec(String specName) throws NoSuchNameExceptionResponse {
		if (!cycleService.containsKey(specName)) {
			throw new NoSuchNameExceptionResponse("No ECSpec with such name defined: " + specName);
		}
	}

	// private ECReports poll(ReportsGenerator reportGenerator) throws
	// NoSuchNameExceptionResponse {
	private ECReports poll(Cycle cycle) throws NoSuchNameExceptionResponse {

		ECReports reports = null;
		cycle.poll();
		try {
			synchronized (cycle) {
				// reports = cService.getPollReports();
				reports = cycle.getPollReports();
				while (reports == null) {
					cycle.wait();
					reports = cycle.getPollReports();
				}
			}
		} catch (InterruptedException e) {
			LOG.debug("got interrupted.");
		} finally {
			 cycle.onCompleted();
		}

		return reports;
	}

	/**
	 * This method returns a name for a report generator which is created by a
	 * immediate command.
	 * 
	 * @return name for input generator
	 */
	private String getNextReportGeneratorName() {
		return REPORT_GENERATOR_NAME_PREFIX + (nameCounter++);
	}

}
