/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.InvalidURIExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.esper.EsperManagementService;
import org.rifidi.edge.lr.LogicalReaderManagementService;

import com.espertech.esper.client.EPServiceProvider;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ECSPECManagerServiceImpl implements ECSPECManagerService {
	/** Logger for this class. */
	private static final Log logger = LogFactory
			.getLog(ECSPECManagerService.class);
	/** Map containing the spec name as key and the spec as value. */
	private Map<String, RifidiECSpec> nameToSpec;
	/** Esper engine isntance. */
	private EPServiceProvider esper;
	/** Threadpool for executing the scheduled triggers. */
	private ScheduledExecutorService triggerpool;
	/** Service for managing logical readers. */
	private LogicalReaderManagementService lrService;

	/**
	 * Constructor.
	 */
	public ECSPECManagerServiceImpl() {
		nameToSpec = new HashMap<String, RifidiECSpec>();
		// TODO: we might have to manage the size of the pool size but that
		// requires profiling
		triggerpool = new ScheduledThreadPoolExecutor(10);
	}

	/**
	 * @param esperManagement
	 *            the esperManagement to set
	 */
	public void setEsperManagement(EsperManagementService esperManagement) {
		esper = esperManagement.getProvider();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.epcglobal.aleread.ECSPECManagerService#createSpec(java
	 * .lang.String, org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec)
	 */
	@Override
	public synchronized void createSpec(String name, ECSpec spec)
			throws DuplicateNameExceptionResponse,
			ECSpecValidationExceptionResponse {
		synchronized (this) {
			logger.debug("Defining " + name);
			if (!nameToSpec.containsKey(name)) {
				try {
					// check if we got logical readers line 2135 of spec
					if (spec.getLogicalReaders() == null
							|| spec.getLogicalReaders().getLogicalReader() == null
							|| spec.getLogicalReaders().getLogicalReader()
									.size() == 0) {
						throw new ECSpecValidationExceptionResponse(
								"No logical readers were provided.");
					}
					RifidiECSpec ecSpec = new RifidiECSpec(name, spec,
							lrService, esper, triggerpool);
					nameToSpec.put(name, ecSpec);
					return;
				} catch (InvalidURIExceptionResponse e) {
					throw new ECSpecValidationExceptionResponse(e.toString());
				}
			}
			throw new DuplicateNameExceptionResponse("A spec named " + name
					+ " already exists.");
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.epcglobal.aleread.ECSPECManagerService#destroySpec(java
	 * .lang.String)
	 */
	@Override
	public void destroySpec(String name) throws NoSuchNameExceptionResponse {
		synchronized (this) {
			if (!nameToSpec.containsKey(name)) {
				throw new NoSuchNameExceptionResponse(name + " doesn't exist.");
			}
			RifidiECSpec spec = nameToSpec.remove(name);
			spec.stop();
			spec.destroy();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.epcglobal.aleread.ECSPECManagerService#getNames()
	 */
	@Override
	public Set<String> getNames() {
		synchronized (this) {
			return new HashSet<String>(nameToSpec.keySet());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.epcglobal.aleread.ECSPECManagerService#getSpecByName(
	 * java.lang.String)
	 */
	@Override
	public ECSpec getSpecByName(String name) throws NoSuchNameExceptionResponse {
		synchronized (this) {
			RifidiECSpec ret = nameToSpec.get(name);
			if (ret == null) {
				throw new NoSuchNameExceptionResponse("No spec named " + name
						+ " exists.");
			}
			return ret.getSpec();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.epcglobal.aleread.ECSPECManagerService#subscribe(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void subscribe(String specName, String uri)
			throws NoSuchNameExceptionResponse,
			DuplicateSubscriptionExceptionResponse, InvalidURIExceptionResponse {
		synchronized (this) {
			try {
				URI target = new URI(uri);
				// check if the spec actually exists
				if (nameToSpec.containsKey(specName)) {
					nameToSpec.get(specName).subscribe(target);
					return;
				}
				throw new NoSuchNameExceptionResponse("A spec named "
						+ specName + " doesn't exist. ");
			} catch (URISyntaxException e) {
				throw new InvalidURIExceptionResponse(e.toString());
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.epcglobal.aleread.ECSPECManagerService#unsubscribe(java
	 * .lang.String, java.lang.String)
	 */
	@Override
	public void unsubscribe(String specName, String uri)
			throws NoSuchNameExceptionResponse,
			NoSuchSubscriberExceptionResponse, InvalidURIExceptionResponse {
		synchronized (this) {
			try {
				URI target = new URI(uri);
				// check if the spec actually exists
				if (nameToSpec.containsKey(specName)) {
					nameToSpec.get(specName).unsubscribe(target);
					return;
				}
				throw new NoSuchNameExceptionResponse("A spec named "
						+ specName + " doesn't exist. ");
			} catch (URISyntaxException e) {
				throw new InvalidURIExceptionResponse(e.toString());
			}
		}
	}

	/**
	 * @param lrService
	 *            the lrService to set
	 */
	public void setLrService(LogicalReaderManagementService lrService) {
		this.lrService = lrService;
	}
}
