package org.rifidi.edge.core.readerplugin.service.impl;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.Bundle;
import org.rifidi.edge.core.api.exceptions.RifidiReaderInfoNotFoundException;
import org.rifidi.edge.core.api.readerplugin.ReaderInfo;
import org.rifidi.edge.core.api.readerplugin.ReaderPlugin;
import org.rifidi.edge.core.api.readerplugin.service.ReaderPluginListener;
import org.rifidi.edge.core.api.readerplugin.service.ReaderPluginService;
import org.rifidi.edge.core.api.readerplugin.xml.ReaderPluginXML;
import org.w3c.dom.Document;

/**
 * This is the implementation of the ReaderPluginService. It keeps track of
 * currently available ReaderPlugins and provides information helping to create
 * instances of the different types of ReaderPlugins. It also allows to monitor
 * the creation and the removal of ReaderPlugins.
 * 
 * @author Andreas Huebner - andreas@pramari.com
 * 
 */
public class ReaderPluginServiceImpl implements ReaderPluginService {

	private Log logger = LogFactory.getLog(ReaderPluginServiceImpl.class);

	// Key: ReaderInfo Class Name ; Value: ReaderPlugin instance
	private HashMap<String, ReaderPlugin> registry = new HashMap<String, ReaderPlugin>();

	// Key: bundleID ; Value: ReaderInfoClass Name
	private HashMap<Long, String> loadedBundles = new HashMap<Long, String>();

	private ArrayList<ReaderPluginListener> listeners = new ArrayList<ReaderPluginListener>();

	private JAXBContext jaxbContext;
	private Unmarshaller unmarshaller;

	public ReaderPluginServiceImpl() {
		try {
			jaxbContext = JAXBContext.newInstance(ReaderPluginXML.class);
			unmarshaller = jaxbContext.createUnmarshaller();
		} catch (JAXBException e) {
			logger.error("Fatal Exception in ReaderPluginService");
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginService#
	 * registerReaderPlugin(java.lang.Class,
	 * org.rifidi.edge.core.readerplugin.ReaderPlugin)
	 */
	@SuppressWarnings("unchecked")
	@Override
	public void registerReaderPlugin(Bundle readerPluginBundle) {

		ReaderPlugin readerPlugin = null;
		ReaderPluginXML readerPluginXML = null;

		URL xml = null;
		Enumeration enumerations = readerPluginBundle.findEntries("READER-INF",
				"ReaderPlugin.xml", false);
		if (enumerations!=null && enumerations.hasMoreElements()) {
			xml = (URL) enumerations.nextElement();
		}

		// TODO Strange...this used to work, but doesn't seem to always work...
		// URL = readerPluginBundle.getResource("ReaderPlugin.xml");
		if (xml != null) {
			try {
				readerPluginXML = parseXML(xml.openStream());
				readerPlugin = new ReaderPlugin(readerPluginXML);
				String readerInfo = readerPluginXML.getInfo();
				registry.put(readerInfo, readerPlugin);
				loadedBundles.put(readerPluginBundle.getBundleId(), readerInfo);
				fireRegisterEvent(readerInfo);
			} catch (JAXBException e) {
				logger.error("ERROR", e);
			} catch (IOException e) {
				logger.error("ERROR", e);
			}
		} else {
			logger.error("Cannot find ReaderPlugin.XML for the bundle: "
					+ readerPluginBundle.getSymbolicName());
		}
	}

	/**
	 * Parse the XML to a ReaderPlugin
	 * 
	 * @param in
	 *            InputStream of the readerplugin.xml
	 * @return a instance of the ReaderPlugin appropriate to the given xml
	 * @throws JAXBException
	 *             if there is a error parsing the input to the readerinfo
	 *             object
	 */
	private ReaderPluginXML parseXML(InputStream in) throws JAXBException {
		return (ReaderPluginXML) unmarshaller.unmarshal(in);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginService#
	 * unregisterReaderPlugin(java.lang.Class)
	 */
	@Override
	public void unregisterReaderPlugin(Bundle readerPluginBundle) {
		// ReaderPlugin plugin = registry.remove(readerInfo.getName());
		// logger.debug("ReaderPlugin unregistered "
		// + plugin.getClass().getSimpleName() + " : "
		// + readerInfo.getName());
		// for (ReaderPluginListener l : listeners) {
		// l.readerPluginUnregisteredEvent(readerInfo);
		// }
		String readerInfo = loadedBundles.remove(readerPluginBundle
				.getBundleId());
		registry.remove(readerInfo);
		fireUnregisterEvent(readerInfo);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginService#
	 * getAllReaderInfos()
	 */
	@Override
	public List<String> getAllReaderInfos() {
		return new ArrayList<String>(registry.keySet());
	}

	@Override
	public Document getReaderInfoAnnotation(String readerInfoClassName)
			throws RifidiReaderInfoNotFoundException {
		ReaderPlugin plugin = registry.get(readerInfoClassName);
		if (plugin != null) {
			return plugin.getReaderInfoAnnotation();
		} else {
			throw new RifidiReaderInfoNotFoundException(
					"Cannot find plugin with reader info "
							+ readerInfoClassName);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.rifidi.edge.core.readerplugin.service.ReaderPluginService#getReaderPlugin
	 * (java.lang.Class)
	 */
	@Override
	public ReaderPlugin getReaderPlugin(String readerInfo) {
		return registry.get(readerInfo);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginService#
	 * addReaderPluginListener
	 * (org.rifidi.edge.core.readerplugin.service.ReaderPluginListener)
	 */
	@Override
	public void addReaderPluginListener(ReaderPluginListener listener) {
		listeners.add(listener);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.core.readerplugin.service.ReaderPluginService#
	 * removeReaderPluginListener
	 * (org.rifidi.edge.core.readerplugin.service.ReaderPluginListener)
	 */
	@Override
	public void removeReaderPluginListener(ReaderPluginListener listener) {
		listeners.remove(listener);
	}

	/**
	 * Fire new ReaderPlugin registered event
	 * 
	 * @param readerInfo
	 *            class name the ReaderInfo of the ReaderPlugin registered
	 */
	@SuppressWarnings("unchecked")
	private void fireUnregisterEvent(String readerInfo) {
		// TODO Remove the Class.forName later on
		try {
			for (ReaderPluginListener listener : listeners) {

				listener
						.readerPluginUnregisteredEvent((Class<? extends ReaderInfo>) Class
								.forName(readerInfo));

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Fire ReaderPlugin unregistered event
	 * 
	 * @param readerInfo
	 *            class name the ReaderInfo of the ReaderPlugin unregistered
	 */
	@SuppressWarnings("unchecked")
	private void fireRegisterEvent(String readerInfo) {
		// TODO Remove the Class.forName later on
		try {
			for (ReaderPluginListener listener : listeners) {

				listener
						.readerPluginRegisteredEvent((Class<? extends ReaderInfo>) Class
								.forName(readerInfo));

			}
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
