/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.DuplicateSubscriptionExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.ECSpecValidationExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchNameExceptionResponse;
import org.rifidi.edge.epcglobal.ale.api.read.ws.NoSuchSubscriberExceptionResponse;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiBoundarySpec;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiECSpec;
import org.rifidi.edge.epcglobal.aleread.wrappers.RifidiReport;
import org.rifidi.edge.esper.EsperManagementService;
import org.rifidi.edge.lr.LogicalReader;

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

	/**
	 * Constructor.
	 */
	public ECSPECManagerServiceImpl() {
		nameToSpec = new HashMap<String, RifidiECSpec>();
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
	 * org.rifidi.edge.epcglobal.aleread.service.ECSPECManagerService#createSpec
	 * (java.lang.String, org.rifidi.edge.epcglobal.ale.api.read.data.ECSpec,
	 * org.rifidi.edge.epcglobal.aleread.wrappers.RifidiBoundarySpec,
	 * java.util.Set, java.util.Set, java.util.List)
	 */
	@Override
	public void createSpec(String name, ECSpec spec,
			RifidiBoundarySpec rifidiBoundarySpec, Set<LogicalReader> readers,
			Set<String> primarykeys, List<RifidiReport> reports)
			throws DuplicateNameExceptionResponse,
			ECSpecValidationExceptionResponse {
		synchronized (this) {
			logger.debug("Creating " + name);
			if (!nameToSpec.containsKey(name)) {
				RifidiECSpec ecSpec = new RifidiECSpec(name, spec, esper,
						rifidiBoundarySpec, readers, primarykeys, reports);
				nameToSpec.put(name, ecSpec);
				logger.debug("Created " + name);
				return;
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
			logger.debug("Destroying " + name);
			if (!nameToSpec.containsKey(name)) {
				throw new NoSuchNameExceptionResponse(name + " doesn't exist.");
			}
			RifidiECSpec spec = nameToSpec.remove(name);
			spec.destroy();
			logger.debug("Destroied " + name);
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
	 * @seeorg.rifidi.edge.epcglobal.aleread.service.ECSPECManagerService#
	 * getSubscriptions(java.lang.String)
	 */
	@Override
	public List<String> getSubscriptions(String specName)
			throws NoSuchNameExceptionResponse {
		if (nameToSpec.containsKey(specName)) {
			return nameToSpec.get(specName).getSubscriptions();
		}
		throw new NoSuchNameExceptionResponse("A spec named " + specName
				+ " doesn't exist. ");
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
			DuplicateSubscriptionExceptionResponse {
		synchronized (this) {
			logger.debug("Subscribing " + uri + " to " + specName);
			// check if the spec actually exists
			if (nameToSpec.containsKey(specName)) {
				nameToSpec.get(specName).subscribe(uri);
				logger.debug("Subscribed " + uri + " to " + specName);
				return;
			}
			throw new NoSuchNameExceptionResponse("A spec named " + specName
					+ " doesn't exist. ");
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
			NoSuchSubscriberExceptionResponse {
		synchronized (this) {
			logger.debug("Unubscribing " + uri + " from " + specName);
			// check if the spec actually exists
			if (nameToSpec.containsKey(specName)) {
				nameToSpec.get(specName).unsubscribe(uri);
				logger.debug("Unubscribed " + uri + " from " + specName);
				return;
			}
			throw new NoSuchNameExceptionResponse("A spec named " + specName
					+ " doesn't exist. ");
		}
	}
}
