/**
 * 
 */
package org.rifidi.edge.client.ale.ecspecview.modelmanager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.rifidi.edge.client.ale.api.wsdl.ale.epcglobal.ALEServicePortType;
import org.rifidi.edge.client.ale.ecspecview.Activator;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class ALEModelManager implements IPropertyChangeListener {

	/** Static instance for this singleton */
	private static ALEModelManager instance;
	/** Listeners to be notified when the model changes */
	private Set<ModelChangedListener> listeners;
	/** The ServicePortType */
	private List<ALEServicePortType> servicePortType;

	/**
	 * Private constructor for this Singleton
	 */
	private ALEModelManager() {
		Activator.getDefault().getPreferenceStore().addPropertyChangeListener(
				this);
		this.listeners = new HashSet<ModelChangedListener>();
		servicePortType = new ArrayList<ALEServicePortType>();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
		Activator.getDefault().getPreferenceStore()
				.removePropertyChangeListener(this);
	}

	/**
	 * Static accessor method for this Singleton
	 * 
	 * @return
	 */
	public static ALEModelManager getInstance() {
		if (instance == null) {
			instance = new ALEModelManager();
		}
		return instance;
	}

	/**
	 * Get the servicePortType
	 * 
	 * @return
	 */
	public List<ALEServicePortType> getModel() {
		if (this.servicePortType.isEmpty()) {
			updateModel();
		}
		return this.servicePortType;
	}

	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(Activator.ALE_PORT_URL_PREF_NAME)) {
			updateModel();
		}
	}

	/**
	 * helper method to create a new ALEServicePortType
	 */
	private void updateModel() {
		String url = Activator.getDefault().getPreferenceStore().getString(
				Activator.ALE_PORT_URL_PREF_NAME);
		JaxWsProxyFactoryBean aleFactory = new JaxWsProxyFactoryBean();
		aleFactory.setServiceClass(ALEServicePortType.class);
		aleFactory.setAddress(url);
		servicePortType.clear();
		servicePortType.add( (ALEServicePortType) aleFactory.create());
		notifyListeners();
	}

	/**
	 * Add a listener to this manager
	 * 
	 * @param listener
	 */
	public void addModelChangedListener(ModelChangedListener listener) {
		this.listeners.add(listener);
	}

	/**
	 * Remove a listener from this model
	 * 
	 * @param listener
	 */
	public void removeModelChangedListener(ModelChangedListener listener) {
		this.listeners.remove(listeners);
	}

	/**
	 * Private helper method to notify listeners of a model change
	 * 
	 * @param model
	 *            The new model
	 */
	private void notifyListeners() {
		for (ModelChangedListener listener : this.listeners) {
			listener.modelUpdated(this.servicePortType);
		}
	}

}
