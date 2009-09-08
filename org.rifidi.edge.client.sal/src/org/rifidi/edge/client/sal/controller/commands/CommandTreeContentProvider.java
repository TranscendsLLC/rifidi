/*
 * CommandTreeContentProvider.java
 * 
 * Created:     July 8th, 2009
 * Project:       Rifidi Edge Server - A middleware platform for RFID applications
 *                    http://www.rifidi.org
 *                    http://rifidi.sourceforge.net
 * Copyright:   Pramari LLC and the Rifidi Project
 * License:      The software in this package is published under the terms of the EPL License
 *                    A copy of the license is included in this distribution under Rifidi-License.txt 
 */
package org.rifidi.edge.client.sal.controller.commands;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.databinding.observable.map.IMapChangeListener;
import org.eclipse.core.databinding.observable.map.MapChangeEvent;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.rifidi.edge.client.model.sal.RemoteCommandConfigFactory;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.RemoteReaderFactory;
import org.rifidi.edge.client.model.sal.properties.RemoteObjectDirtyEvent;

/**
 * Content Provider for the Commands. Also serves as the controller in the MVC
 * pattern
 * 
 * @author Kyle Neumeier -kyle@pramari.com
 */
public class CommandTreeContentProvider implements ITreeContentProvider,
		CommandController, PropertyChangeListener, IMapChangeListener {

	/** The model Element */
	private RemoteEdgeServer remoteEdgeServer;
	/**
	 * A hashmap that stores the RemoteCommandConfigurations mapped to their
	 * type
	 */
	private HashMap<String, Set<RemoteCommandConfiguration>> configTypeToCommandConfigSet;
	/** The tree viewer associated with this content provider */
	private AbstractTreeViewer viewer = null;
	/** The singleton instance */
	private static CommandTreeContentProvider instance;

	/**
	 * Constructor.
	 */
	public CommandTreeContentProvider() {
		super();
		instance = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getChildren(java.lang.
	 * Object)
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		try {
			if (parentElement instanceof List) {
				return ((List) parentElement).toArray();
			} else if (parentElement instanceof RemoteEdgeServer) {
				RemoteEdgeServer server = (RemoteEdgeServer) parentElement;
				Collection<RemoteReaderFactory> readerFactories = server
						.getReaderFactories().values();
				Object[] retVal = new Object[readerFactories.size()];
				return readerFactories.toArray(retVal);

			} else if (parentElement instanceof RemoteReaderFactory) {
				String readerFactoryID = ((RemoteReaderFactory) parentElement)
						.getID();
				Collection<RemoteCommandConfigFactory> commandFactories = (Collection<RemoteCommandConfigFactory>) remoteEdgeServer
						.getRemoteCommandConfigFactories().values();
				Set<RemoteCommandConfigFactory> retVal = new HashSet<RemoteCommandConfigFactory>();
				for (RemoteCommandConfigFactory factory : commandFactories) {
					if (factory.getReaderFactoryID().equals(readerFactoryID)) {
						retVal.add(factory);
					}
				}
				return retVal.toArray();

			} else if (parentElement instanceof RemoteCommandConfigFactory) {
				RemoteCommandConfigFactory factory = (RemoteCommandConfigFactory) parentElement;
				Set<RemoteCommandConfiguration> commandConfigs = this.configTypeToCommandConfigSet
						.get(factory.getCommandConfigFactoryID());
				if (commandConfigs != null) {
					return commandConfigs.toArray();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new Object[] {};
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#getParent(java.lang.Object
	 * )
	 */
	@Override
	public Object getParent(Object element) {
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.ITreeContentProvider#hasChildren(java.lang.
	 * Object)
	 */
	@Override
	public boolean hasChildren(Object element) {
		try {
			if (element instanceof List) {
				return !((List) element).isEmpty();
			} else if (element instanceof RemoteEdgeServer) {
				return !((RemoteEdgeServer) element).getReaderFactories()
						.values().isEmpty();

			} else if (element instanceof RemoteReaderFactory) {
				String readerFactoryID = ((RemoteReaderFactory) element)
						.getID();
				Collection<RemoteCommandConfigFactory> commandFactories = (Collection<RemoteCommandConfigFactory>) remoteEdgeServer
						.getRemoteCommandConfigFactories().values();
				Set<RemoteCommandConfigFactory> retVal = new HashSet<RemoteCommandConfigFactory>();
				for (RemoteCommandConfigFactory factory : commandFactories) {
					if (factory.getReaderFactoryID().equals(readerFactoryID)) {
						return true;
					}
				}

			} else if (element instanceof RemoteCommandConfigFactory) {
				String factoryID = ((RemoteCommandConfigFactory) element)
						.getCommandConfigFactoryID();
				if (this.configTypeToCommandConfigSet.containsKey(factoryID)) {
					return !configTypeToCommandConfigSet.get(factoryID)
							.isEmpty();
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IStructuredContentProvider#getElements(java
	 * .lang.Object)
	 */
	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.eclipse.jface.viewers.IContentProvider#dispose()
	 */
	@Override
	public void dispose() {
		// TODO Handle dispose!

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.jface.viewers.IContentProvider#inputChanged(org.eclipse.jface
	 * .viewers.Viewer, java.lang.Object, java.lang.Object)
	 */
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (this.viewer != viewer) {
			if (viewer instanceof AbstractTreeViewer) {
				this.viewer = (AbstractTreeViewer) viewer;
			} else {
				throw new RuntimeException(
						"ObservableTreeContentProvider supports AbstractTreeViewer but got "
								+ viewer.getClass());
			}
		}
		if (oldInput != newInput) {
			List<RemoteEdgeServer> newInputList = (List<RemoteEdgeServer>) newInput;
			List<RemoteEdgeServer> oldInputList = (List<RemoteEdgeServer>) oldInput;
			if (oldInputList != null) {
				for (RemoteEdgeServer edgeServer : oldInputList) {
					edgeServer.removePropertyChangeListener(this);
					edgeServer.getCommandConfigurations()
							.removeMapChangeListener(this);
					edgeServer.getRemoteCommandConfigFactories()
							.removeMapChangeListener(this);
				}
			}
			if (newInputList != null && newInputList.size() == 1) {
				this.remoteEdgeServer = newInputList.get(0);
				this.configTypeToCommandConfigSet = new HashMap<String, Set<RemoteCommandConfiguration>>();
				remoteEdgeServer.addPropertyChangeListener(this);
				remoteEdgeServer.getRemoteCommandConfigFactories()
						.addMapChangeListener(this);
				remoteEdgeServer.getCommandConfigurations()
						.addMapChangeListener(this);

			} else {
				remoteEdgeServer = null;
			}
			viewer.refresh();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seejava.beans.PropertyChangeListener#propertyChange(java.beans.
	 * PropertyChangeEvent)
	 */
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(RemoteEdgeServer.STATE_PROPERTY)) {
			viewer.refresh(remoteEdgeServer);
		} else if (evt.getPropertyName().equals(
				RemoteObjectDirtyEvent.DIRTY_EVENT_PROPERTY)) {
			RemoteObjectDirtyEvent bean = (RemoteObjectDirtyEvent) evt
					.getNewValue();
			RemoteCommandConfiguration commandConfig = (RemoteCommandConfiguration) remoteEdgeServer
					.getCommandConfigurations().get(bean.getModelID());

			viewer.update(commandConfig, null);

		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.eclipse.core.databinding.observable.map.IMapChangeListener#
	 * handleMapChange
	 * (org.eclipse.core.databinding.observable.map.MapChangeEvent)
	 */
	@Override
	public void handleMapChange(MapChangeEvent event) {
		// handle event when commandConfigPlugin is swapped out
		for (Object key : event.diff.getChangedKeys()) {
			Object newVal = event.diff.getNewValue(key);
			Object oldVal = event.diff.getOldValue(key);
			if ((newVal instanceof RemoteCommandConfigFactory)
					&& (oldVal instanceof RemoteCommandConfigFactory)) {
				removeRemoteCommandConfigFactory((RemoteCommandConfigFactory) oldVal);
				addRemoteCommandConfigFactory((RemoteCommandConfigFactory) newVal);
			}
		}

		// handle event when obj is added
		for (Object key : event.diff.getAddedKeys()) {
			Object val = event.diff.getNewValue(key);
			if (val instanceof RemoteCommandConfigFactory) {
				addRemoteCommandConfigFactory((RemoteCommandConfigFactory) val);
			} else if (val instanceof RemoteCommandConfiguration) {
				addRemoteCommandConfiguration((RemoteCommandConfiguration) val);
			}
		}

		// handle event when obj is removed
		for (Object key : event.diff.getRemovedKeys()) {
			Object val = event.diff.getOldValue(key);
			if (val instanceof RemoteCommandConfigFactory) {
				removeRemoteCommandConfigFactory((RemoteCommandConfigFactory) val);
			} else if (val instanceof RemoteCommandConfiguration) {
				removeRemoteCommandConfiguration((RemoteCommandConfiguration) val);
			}
		}

	}

	/**
	 * Helper method to remove RemoteCommandConfigFactory from viewer
	 * 
	 * @param factory
	 *            the factory to add
	 */
	private void removeRemoteCommandConfigFactory(
			RemoteCommandConfigFactory factory) {
		viewer.remove(factory);
		this.configTypeToCommandConfigSet.remove(factory
				.getCommandConfigFactoryID());
	}

	/**
	 * Helper method to add a RemoteCommandConfigFactory to viewer
	 * 
	 * @param factory
	 *            the factory to add
	 */
	private void addRemoteCommandConfigFactory(
			RemoteCommandConfigFactory factory) {
		RemoteReaderFactory readerFac = (RemoteReaderFactory) remoteEdgeServer
				.getReaderFactories().get(factory.getReaderFactoryID());
		viewer.add(readerFac, factory);
		viewer.setExpandedState(factory, true);
		viewer.refresh();
	}

	/**
	 * Helper method to process a RemoteCommandConfig being added
	 * 
	 * @param config
	 */
	private void addRemoteCommandConfiguration(RemoteCommandConfiguration config) {

		// Look up the RemoteCommandConfigType for the config
		String factoryID = config.getCommandType();
		if (factoryID != null) {
			Set<RemoteCommandConfiguration> commandSet = this.configTypeToCommandConfigSet
					.get(factoryID);
			if (commandSet == null) {
				commandSet = new HashSet<RemoteCommandConfiguration>();
				configTypeToCommandConfigSet.put(factoryID, commandSet);
			}
			commandSet.add(config);
			config.addPropertyChangeListener(this);
			viewer.add(config.getFactory(), config);
			viewer.setExpandedState(config.getFactory(), true);
			viewer.refresh();
		}
	}

	/**
	 * Helper method to process a commandconfig being removed
	 * 
	 * @param config
	 */
	private void removeRemoteCommandConfiguration(
			RemoteCommandConfiguration config) {
		Set<RemoteCommandConfiguration> foundSet = null;
		RemoteCommandConfiguration foundConfig = null;
		for (Set<RemoteCommandConfiguration> configSet : this.configTypeToCommandConfigSet
				.values()) {
			for (RemoteCommandConfiguration thisConfig : configSet) {
				if (thisConfig.getID().equals(config.getID())) {
					foundSet = configSet;
					foundConfig = thisConfig;
				}
			}
		}
		if (foundSet != null && foundConfig != null) {
			foundSet.remove(foundConfig);
		}
		config.removePropertyChangeListener(this);
		viewer.remove(config);
	}

	/**
	 * Return the Command MVC controller
	 */
	public static CommandController getController() {
		return (CommandController) instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sal.controller.commands.CommandController#
	 * createCommand(java.lang.String)
	 */
	@Override
	public void createCommand(String commandConfigType) {
		remoteEdgeServer.createCommandConfiguration(commandConfigType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sal.controller.commands.CommandController#
	 * deleteCommand(java.lang.String)
	 */
	@Override
	public void deleteCommand(String commandConfigID) {
		remoteEdgeServer.deleteCommandConfiguration(commandConfigID);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sal.controller.commands.CommandController#
	 * getCommandConfigurations()
	 */
	@Override
	public Set<RemoteCommandConfiguration> getCommandConfigurations() {
		Set<RemoteCommandConfiguration> configurations = new HashSet<RemoteCommandConfiguration>();
		for (Set<RemoteCommandConfiguration> configSets : this.configTypeToCommandConfigSet
				.values()) {
			configurations.addAll(configSets);
		}
		return configurations;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sal.controller.commands.CommandController#
	 * clearPropertyChanges(java.lang.String)
	 */
	@Override
	public void clearPropertyChanges(String commandID) {
		RemoteCommandConfiguration config = (RemoteCommandConfiguration) remoteEdgeServer
				.getCommandConfigurations().get(commandID);
		if (config != null) {
			config.clearUpdatedProperties();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sal.controller.commands.CommandController#
	 * synchPropertyChanges(java.lang.String)
	 */
	@Override
	public void synchPropertyChanges(String commandID) {
		RemoteCommandConfiguration config = (RemoteCommandConfiguration) remoteEdgeServer
				.getCommandConfigurations().get(commandID);
		if (config != null) {
			config.synchUpdatedProperties(remoteEdgeServer);
		}

	}
}
