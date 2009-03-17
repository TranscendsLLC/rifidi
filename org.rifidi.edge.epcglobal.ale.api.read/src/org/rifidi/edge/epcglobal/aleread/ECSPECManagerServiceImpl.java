/**
 * 
 */
package org.rifidi.edge.epcglobal.aleread;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

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

import com.espertech.esper.client.EPServiceProvider;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class ECSPECManagerServiceImpl implements ECSPECManagerService {
	/** Logger for this class. */
	static final Log logger = LogFactory.getLog(ECSPECManagerService.class);
	/** Map containing the spec name as key and the spec as value. */
	private Map<String, RifidiECSpec> nameToSpec;
	/** Map containing the spec name as key and a set of target uris as value. */
	private Map<String, Set<URI>> nameToURIs;
	/** Esper engine isntance. */
	private EPServiceProvider esper;

	/**
	 * Constructor.
	 */
	public ECSPECManagerServiceImpl() {
		nameToSpec = new ConcurrentHashMap<String, RifidiECSpec>();
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
		logger.debug("Defining " + name);
		if (!nameToSpec.containsKey(name)) {
			try {
				RifidiECSpec ecSpec = new RifidiECSpec(name, spec, esper);
				nameToSpec.put(name, ecSpec);
				return;
			} catch (InvalidURIExceptionResponse e) {
				throw new ECSpecValidationExceptionResponse(e.toString());
			}
		}
		throw new DuplicateNameExceptionResponse("A spec named " + name
				+ " already exists.");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.rifidi.edge.epcglobal.aleread.ECSPECManagerService#getNames()
	 */
	@Override
	public Set<String> getNames() {
		return new HashSet<String>(nameToSpec.keySet());
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
		RifidiECSpec ret = nameToSpec.get(name);
		if (ret == null) {
			throw new NoSuchNameExceptionResponse("No spec named " + name
					+ " exists.");
		}
		return ret.getSpec();
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
		synchronized (nameToURIs) {
			try {
				URI target = new URI(uri);
				// check if the spec actually exists
				if (nameToSpec.containsKey(specName)) {
					// create the list for holding the uri if not yet done
					if (!nameToURIs.containsKey(specName)) {
						nameToURIs.put(specName,
								new ConcurrentSkipListSet<URI>());
						startSpec(specName);
					}
					// check if the uri is already registered
					if (!nameToURIs.get(specName).contains(target)) {
						nameToURIs.get(specName).add(target);
					}
					throw new DuplicateSubscriptionExceptionResponse(uri
							+ " is already registered to " + uri);
				}
				throw new NoSuchNameExceptionResponse("A spec named "
						+ specName + " doesn't exist. ");
			} catch (URISyntaxException e) {
				throw new InvalidURIExceptionResponse(e.toString());
			}
		}
	}

	private void startSpec(String specName) {
		RifidiECSpec spec = nameToSpec.get(specName);
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
		synchronized (nameToURIs) {
			try {
				URI target = new URI(uri);
				// check if the spec actually exists
				if (nameToSpec.containsKey(specName)) {
					// get the list holding the uris
					if (nameToURIs.containsKey(specName)) {
						// try to remove the uri
						boolean rem = nameToURIs.get(specName).remove(target);
						if (rem) {
							if (nameToURIs.get(specName).isEmpty()) {
								// clean up if the set is now empty
								nameToURIs.remove(specName);
								// TODO: take down executor
							}
							return;
						}
					}
					throw new NoSuchSubscriberExceptionResponse(
							"No subscription from " + uri + " to " + specName);
				}
				throw new NoSuchNameExceptionResponse("A spec named "
						+ specName + " doesn't exist. ");
			} catch (URISyntaxException e) {
				throw new InvalidURIExceptionResponse(e.toString());
			}
		}
	}
}
