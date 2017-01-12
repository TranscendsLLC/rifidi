package org.rifidi.edge.server.epcglobal.alelr.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.xml.bind.JAXBException;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.restlet.data.MediaType;
import org.rifidi.app.ale.AleApp;
import org.rifidi.edge.configuration.ConfigurationService;
import org.rifidi.edge.epcglobal.alelr.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ImmutableReaderExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ImplementationExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.InUseExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.LRProperty;
import org.rifidi.edge.epcglobal.alelr.LRSpec;
import org.rifidi.edge.epcglobal.alelr.LogicalReader;
import org.rifidi.edge.epcglobal.alelr.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.NonCompositeReaderExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ReaderLoopExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.SecurityExceptionResponse;
import org.rifidi.edge.epcglobal.alelr.ValidationExceptionResponse;
import org.rifidi.edge.notification.TagReadEvent;
import org.rifidi.edge.server.epcglobal.ale.services.ALEService;
import org.rifidi.edge.server.epcglobal.alelr.BaseReader;
import org.rifidi.edge.server.epcglobal.alelr.CompositeReader;
import org.rifidi.edge.server.epcglobal.alelr.Reader;
import org.rifidi.edge.utils.ALESettings;
import org.rifidi.edge.utils.RemoveConfig;
import org.rifidi.edge.utils.RifidiHelper;
import org.rifidi.edge.utils.WriteConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import rx.Observer;
import rx.Subscriber;
import rx.Subscription;

@Service
public class LogicalReaderService implements ReaderService {

	@Autowired
	private RemoveConfig persistenceRemoveAPI;

	@Autowired
	private WriteConfig persistenceWriteAPI;

	@Autowired
	private ALESettings aleSettings;

	@Autowired
	private DeviceService deviceService;

	@Autowired
	ConfigurationService configService;

	@Autowired
	private AutowireCapableBeanFactory beanFactory;

	@Autowired
	RifidiHelper rifidiHelper;

	@Autowired
	private ALEService aleService;

	/** logger. */
	private static final Logger LOG = Logger.getLogger(LogicalReaderService.class);

	/**
	 * a map of all LogicalReaders. the readers are mapped against their name.
	 */
	private ConcurrentMap<String, Reader> logicalReaders = new ConcurrentHashMap<String, Reader>();

	@Override
	public Iterable<LogicalReader> getAleLogicalReaders() {
		// TODO Get from the persistance
		return null;
	}

	@Override
	public LogicalReader getAleLogicalReader(String logicalReaderName) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void loadLogicalReaders() {
		// TODO Auto-generated method stub

	}

	@Override
	public Reader addAleLogicalReaderToCompositeReader(String compositeReaderName, LogicalReader logicalReader)
			throws ImplementationExceptionResponse, ValidationExceptionResponse {
		Reader compositeReader = null;
		if (logicalReaders.containsKey(compositeReaderName)) {
			compositeReader = logicalReaders.get(compositeReaderName);
			if (compositeReader instanceof CompositeReader) {
				// If the Reader exists.
				if (logicalReaders.containsKey(logicalReader.getName())) {
					((CompositeReader) compositeReader).addReader(logicalReaders.get(logicalReader.getName()));
				}
				// If the Reader doesn't exist create one.
				else {
					if (!logicalReader.getLRSpec().isIsComposite()) {
						BaseReader baseReader = new BaseReader();
						beanFactory.autowireBean(baseReader);
						baseReader.initialize(logicalReader.getName(), logicalReader.getLRSpec());
						baseReader.setSubscription(deviceService.subscribeObserver(logicalReader, baseReader));
						logicalReaders.putIfAbsent(logicalReader.getName(), baseReader);
						((CompositeReader) compositeReader).addReader(baseReader);
					} else {
						CompositeReader newCompositeReader = new CompositeReader();
						beanFactory.autowireBean(newCompositeReader);
						newCompositeReader.initialize(logicalReader.getName(), logicalReader.getLRSpec());
						logicalReaders.putIfAbsent(logicalReader.getName(), newCompositeReader);
						((CompositeReader) compositeReader).addReader(newCompositeReader);
					}

				}

				// TODO If everything goes ok save.

			}
		}

		return compositeReader;
	}

	@Override
	public Reader createAleLogicalReader(LogicalReader logicalReader)
			throws ImplementationExceptionResponse, ValidationExceptionResponse, IOException, JAXBException {
		Reader reader = null;
		if (!logicalReaders.containsKey(logicalReader.getName())) {
			// TODO persistance
			Boolean isComposite = logicalReader.getLRSpec().isIsComposite();
			if (isComposite == null) {
				throw new ValidationExceptionResponse(
						"Can not determine the boolean value for property 'isComposite'. Make sure the property has a valid boolean value in xml file.");
			}

			if (!isComposite) {
				reader = deviceService.createDeviceFromPlugin("LLRP", logicalReader.getName(), logicalReader);
				reader.setName(logicalReader.getName());
				reader.initialize(logicalReader.getName(), logicalReader.getLRSpec());
				reader.start();
				logicalReaders.putIfAbsent(logicalReader.getName(), reader);
			} else {
				reader = new CompositeReader();
				beanFactory.autowireBean(reader);
				reader.initialize(logicalReader.getName(), logicalReader.getLRSpec());
				reader.start();
				logicalReaders.putIfAbsent(logicalReader.getName(), reader);
			}
		} else {
			throw new ValidationExceptionResponse("The reader '" + logicalReader.getName() + "' already exists.");
		}

		return reader;
	}

	@Override
	public Reader getLogicalReader(String logicalReaderName) {
		if (logicalReaders.containsKey(logicalReaderName)) {
			return logicalReaders.get(logicalReaderName);
		}
		return null;

	}

	@Override
	public Collection<Reader> getLogicalReaders() {
		return logicalReaders.values();
	}

	@Override
	public Iterable<TagReadEvent> getEvents(int lastNSeconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Iterable<TagReadEvent> getEvents(String logicalReaderName, int lastNSeconds) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean start(String compositeReaderName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean stop(String compositeReaderName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Subscription subscribe(String logicalReaderName, Observer<TagReadEvent> observer) {

		if (logicalReaders.containsKey(logicalReaderName)) {
			return logicalReaders.get(logicalReaderName).subscribeObserver(observer);
		}

		return null;
	}

	@Override
	public String getVendorVersion() throws ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		return aleSettings.getVendorVersion();
	}

	@Override
	public String getStandardVersion() throws ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		return aleSettings.getLrStandardVersion();

	}

	@Override
	public String getPropertyValue(String name, String propertyName)
			throws NoSuchNameExceptionResponse, SecurityException, ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		Reader logRd = logicalReaders.get(name);
		throwNoSuchNameExceptionIfReaderNull(logRd, name);

		Collection<LRProperty> propList = logRd.getProperties();
		for (LRProperty prop : propList) {
			if (prop.getName().equalsIgnoreCase(propertyName)) {
				return prop.getValue();
			}
		}
		return null;

	}

	@Override
	public void setProperties(String name, List<LRProperty> properties)
			throws NoSuchNameExceptionResponse, ValidationExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub

		throwValidationExceptionOnNullInput(properties);

		Reader logRd = logicalReaders.get(name);

		throwNoSuchNameExceptionIfReaderNull(logRd, name);

		LRSpec spec = logRd.getLRSpec();
		if (spec.getProperties() == null) {
			spec.setProperties(new LRSpec.Properties());
		}
		// we need to replace the properties, not just add to the old ones.
		spec.getProperties().getProperty().clear();
		spec.getProperties().getProperty().addAll(properties);
		LOG.debug("set the properties");
		try {
			update(name, spec);

			// set rifidi properties
			AttributeList attributes = new AttributeList();
			for (LRProperty lrProperty : properties) {
				attributes.add(new Attribute(lrProperty.getName(), lrProperty.getValue()));
			}
			rifidiHelper.setRifidiReaderProperties(name, attributes);

		} catch (ReaderLoopExceptionResponse e) {
			String errMsg = "ReaderLoopException during update.";
			LOG.debug(errMsg, e);
			throw new ImplementationExceptionResponse(errMsg, e);
		}

	}

	@Override
	public void removeReaders(String name, List<String> readers)
			throws NoSuchNameExceptionResponse, InUseExceptionResponse, ImmutableReaderExceptionResponse,
			NonCompositeReaderExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub

		Reader lgRd = logicalReaders.get(name);

		throwNoSuchNameExceptionIfReaderNull(lgRd, name);
		throwNonCompositeReaderExceptionIfReaderNotComposite(lgRd, name);

		// get the readers that are still in the spec
		LRSpec spec = lgRd.getLRSpec();
		List<String> res = new ArrayList<String>();
		if ((spec.getReaders() != null) && (spec.getReaders().getReader().size() > 0)) {
			for (String reader : spec.getReaders().getReader()) {
				if (!readers.contains(reader)) {
					res.add(reader);
				}
			}
		}

		// add the resulting readers
		spec.setReaders(new LRSpec.Readers());
		spec.getReaders().getReader().addAll(res);
		try {
			update(name, spec);
		} catch (ValidationExceptionResponse e) {
			throw new ImplementationExceptionResponse(e.getMessage());
		} catch (ReaderLoopExceptionResponse e) {
			throw new ImplementationExceptionResponse(e.getMessage());
		}

	}

	@Override
	public void setReaders(String name, List<String> readers)
			throws NoSuchNameExceptionResponse, ValidationExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse, NonCompositeReaderExceptionResponse, ReaderLoopExceptionResponse,
			SecurityExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub

		Reader logRd = logicalReaders.get(name);

		throwNoSuchNameExceptionIfReaderNull(logRd, name);
		throwNonCompositeReaderExceptionIfReaderNotComposite(logRd, name);
		throwValidationExceptionIfNotAllReadersAvailable(readers);

		LRSpec spec = logRd.getLRSpec();
		spec.setReaders(new LRSpec.Readers());
		spec.getReaders().getReader().addAll(readers);
		update(name, spec);

	}

	@Override
	public void addReaders(String name, List<String> readers)
			throws NoSuchNameExceptionResponse, ValidationExceptionResponse, InUseExceptionResponse,
			ImmutableReaderExceptionResponse, ReaderLoopExceptionResponse, SecurityExceptionResponse,
			ImplementationExceptionResponse, NonCompositeReaderExceptionResponse {
		// TODO Auto-generated method stub

		Reader logRd = logicalReaders.get(name);

		throwNoSuchNameExceptionIfReaderNull(logRd, name);
		throwNonCompositeReaderExceptionIfReaderNotComposite(logRd, name);
		throwValidationExceptionIfNotAllReadersAvailable(readers);

		LRSpec spec = logRd.getLRSpec();
		if (spec.getReaders() == null) {
			spec.setReaders(new LRSpec.Readers());
		}
		for (String reader : readers) {
			if (!spec.getReaders().getReader().contains(reader)) {
				spec.getReaders().getReader().add(reader);
			}
		}
		update(name, spec);

	}

	@Override
	public LRSpec getLRSpec(String name)
			throws NoSuchNameExceptionResponse, SecurityExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		Reader logRd = logicalReaders.get(name);

		throwNoSuchNameExceptionIfReaderNull(logRd, name);

		return logRd.getLRSpec();
	}

	@Override
	public List<String> getLogicalReaderNames() throws SecurityExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		List<String> rdNames = new ArrayList<String>();
		Iterable<String> it = logicalReaders.keySet();
		for (String reader : it) {
			rdNames.add(reader);
		}
		return rdNames;
	}

	@Override
	public void undefine(String name) throws NoSuchNameExceptionResponse, InUseExceptionResponse,
			SecurityExceptionResponse, ImmutableReaderExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub

		// the logicalReader must delete himself from its observables
		LOG.debug("undefining reader " + name);

		// Validate there are not ecspec defined
		String[] specNames = aleService.getECSpecNames();
		for (String specName : specNames) {
			try {
				List<String> logicalReaders = aleService.getECSpec(specName).getLogicalReaders().getLogicalReader();
				for (String logicalReader : logicalReaders) {
					if (logicalReader.equals(name)) {
						throw new ImplementationExceptionResponse("The ECSpec '" + specName
								+ "' is using this logical reader. Please first undefine the ECSpec.");
					}
				}
			} catch (org.rifidi.edge.epcglobal.ale.NoSuchNameExceptionResponse ex) {

			}
		}

		Reader reader = getLogicalReader(name);

		throwNoSuchNameExceptionIfReaderNull(reader, name);

		// according to the EPC standard a reader cannot be undefined when there
		// is
		// an active CC or EC pointing to the reader
		// this raises an InUseException
		if (reader.hasSubscription()) {
			throw new InUseExceptionResponse(name + "is still in use.");
		}

		if (reader instanceof CompositeReader) {
			CompositeReader composite = (CompositeReader) reader;
			composite.unsubscribeObserver();
		} else if (reader instanceof BaseReader) {
			BaseReader basereader = (BaseReader) reader;
			// basereader.disconnectReader();
			basereader.setDisconnected();
//			basereader.getSubscription().unsubscribe();
			// basereader.cleanup();

			// FIXME ALE
			// basereader.cleanup();
			basereader.unsubscribeObserver();
			
			//Added by Alejandro 2016-04-27
			deviceService.removeDevice(name);
			
		} else {
			throw new ImplementationExceptionResponse(
					"try to undefine unknown reader type - ALE knows BaseReader and CompositeReader - atomic readers must subclass BaseReader, composite readers (collections of readers) must subclass CompositeReader - this is a serious problem!!! reader-name: "
							+ name);
		}

		persistenceRemoveAPI.removeLRSpec(name);

		Reader removedReader = logicalReaders.remove(name);
		removedReader.setDisconnected();
		removedReader.onCompleted();
		
	}

	@Override
	public void update(String name, LRSpec spec) throws NoSuchNameExceptionResponse, ValidationExceptionResponse,
			InUseExceptionResponse, ImmutableReaderExceptionResponse, ReaderLoopExceptionResponse,
			SecurityExceptionResponse, ImplementationExceptionResponse {
		// TODO Auto-generated method stub
		Reader logRd = logicalReaders.get(name);
		throwNoSuchNameExceptionIfReaderNull(logRd, name);

		logRd.update(spec);

		persistenceRemoveAPI.removeLRSpec(name);
		persistenceWriteAPI.writeLRSpec(name, spec);

	}

	@Override
	public void define(String name, LRSpec spec) throws DuplicateNameExceptionResponse, ValidationExceptionResponse,
			SecurityExceptionResponse, ImplementationExceptionResponse {

		LOG.debug("define");

		throwValidationExceptionOnNullInput(name, "parameter name is null");
		throwValidationExceptionOnNullInput(spec, "parameter spec is null");

		Boolean isComposite = spec.isIsComposite();
		if (isComposite == null) {
			throw new ValidationExceptionResponse(
					"Can not determine the boolean value for property 'isComposite'. Make sure the property has a valid boolean value in xml file.");
		}

		LogicalReader logicalReader = new LogicalReader();
		logicalReader.setLRSpec(spec);
		logicalReader.setName(name);

		try {
			Reader logRead = null;

			// Validate if Rifidi reader exists
			if (!spec.isIsComposite()) {
				rifidiHelper.validateRifidiReader(name);
			} else {

				List<String> lrLogicalReaders = spec.getReaders().getReader();
				for (String lrLogicalReader : lrLogicalReaders) {
					rifidiHelper.validateRifidiReader(lrLogicalReader);
				}

			}

			//
			// for ( LRProperty lrProperty : spec.getProperties().getProperty()
			// ){
			//
			// if (lrProperty.getName().equalsIgnoreCase("PhysicalReaderName")){
			//
			// String readerId = lrProperty.getValue();
			//
			// String realReaderId = validateAndReturnRifidiReaderId(readerId);
			//
			// }
			// }

			logRead = createAleLogicalReader(logicalReader);

			persistenceWriteAPI.writeLRSpec(name, spec);

			// FIXME ALE check is this is correct
			logRead.setLRSpec(spec);

			LOG.debug("saving reader: " + name + " " + logRead.getClass().getCanonicalName());
			logicalReaders.put(name, logRead);
			if (logRead instanceof BaseReader) {
				// ((BaseReader)logRead).connectReader();
				((BaseReader) logRead).setConnected();
			}

			// Call Rifidi save configuration
			// Do not call
			// rifidiHelper.storeRifidiConfiguration();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw new ValidationExceptionResponse(e.getMessage());
		} // getReaderProvider().createReader(name, spec);
			// establish connection when basereader

		LOG.debug("successfully executed define");

	}

	@Override
	public void setLogicalReader(Reader reader) throws ImplementationExceptionResponse {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean contains(String logicalReaderName) {
		// TODO Auto-generated method stub
		// return false;
		return logicalReaders.containsKey(logicalReaderName);
	}

	/**
	 * assert that the given input parameter is not null. if so, throws
	 * exception.
	 * 
	 * @param inputParameter
	 *            the input parameter to test on null.
	 * @throws ValidationException
	 *             if the given input parameter is null.
	 */
	protected void throwValidationExceptionOnNullInput(Object inputParameter, String... parameters)
			throws ValidationExceptionResponse {
		if (null == inputParameter) {
			String errMsg = "given input parameter is null. " + StringUtils.join(parameters);
			LOG.debug(errMsg);
			throw new ValidationExceptionResponse(errMsg);
		}
	}

	/**
	 * assert that the given reader is not null. if so, throws Exception.
	 * 
	 * @param logRd
	 *            the logical reader to test.
	 * @param name
	 *            the name of the reader.
	 * @throws NoSuchNameException
	 *             if the given reader is null.
	 */
	protected void throwNoSuchNameExceptionIfReaderNull(Reader logRd, String name) throws NoSuchNameExceptionResponse {
		if (null == logRd) {
			String errMsg = String.format("There is no such reader %s", name);
			LOG.debug(errMsg);
			throw new NoSuchNameExceptionResponse(errMsg);
		}
	}

	/**
	 * assert that the given reader is a composite reader. if not, throws
	 * exception.
	 * 
	 * @param logRd
	 *            the logical reader that is to be tested.
	 * @param name
	 *            the name of the reader.
	 * @throws NonCompositeReaderException
	 *             if the given reader is not composite.
	 */
	protected void throwNonCompositeReaderExceptionIfReaderNotComposite(Reader logRd, String name)
			throws NonCompositeReaderExceptionResponse {
		if (!(logRd instanceof CompositeReader)) {
			String errMsg = "reader " + name + " is not composite";
			LOG.debug(errMsg);
			throw new NonCompositeReaderExceptionResponse(errMsg);
		}
	}

	/**
	 * assert that the given list of readers are all present/defined.
	 * 
	 * @param readers
	 *            the readers to test.
	 * @throws ValidationException
	 *             if at least one of the requested readers is not existing.
	 */
	protected void throwValidationExceptionIfNotAllReadersAvailable(List<String> readers)
			throws ValidationExceptionResponse {
		for (String readerName : readers) {
			if (!contains(readerName)) {
				throw new ValidationExceptionResponse("the requested reader is not defined: " + readerName);
			}
		}
	}

	/**
	 * a handle to the currently used reader provider.
	 * 
	 * @return the currently used reader provider.
	 */
	// FIXME ALE
	//
	// public ReaderProvider getReaderProvider() {
	// return readerProvider;
	// }

	/**
	 * allow to inject a new reader provider.
	 * 
	 * @param readerProvider
	 *            the new reader provider to be used.
	 */
	// FIXME ALE
	// @Autowired
	// public void setReaderProvider(ReaderProvider readerProvider) {
	// this.readerProvider = readerProvider;
	// }

}
