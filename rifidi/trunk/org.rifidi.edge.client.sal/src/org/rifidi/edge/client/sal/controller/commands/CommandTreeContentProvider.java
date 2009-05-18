
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
import org.rifidi.edge.client.model.sal.RemoteCommandConfigType;
import org.rifidi.edge.client.model.sal.RemoteCommandConfiguration;
import org.rifidi.edge.client.model.sal.RemoteEdgeServer;
import org.rifidi.edge.client.model.sal.properties.RemoteObjectDirtyEvent;

/**
 * TODO: Class level comment.  
 * 
 * @author kyle
 */
public class CommandTreeContentProvider implements ITreeContentProvider,
		CommandController, PropertyChangeListener, IMapChangeListener {

	/** The model Element */
	private List<RemoteEdgeServer> edgeServerList;
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
		if (parentElement instanceof List) {
			return ((List) parentElement).toArray();
		} else if (parentElement instanceof RemoteEdgeServer) {
			RemoteEdgeServer server = (RemoteEdgeServer) parentElement;
			Collection<RemoteCommandConfigFactory> commandPlugins = server
					.getRemoteCommandConfigFactories().values();

			Object[] retVal = new Object[commandPlugins.size()];
			return commandPlugins.toArray(retVal);
		} else if (parentElement instanceof RemoteCommandConfigFactory) {
			RemoteCommandConfigFactory factory = (RemoteCommandConfigFactory) parentElement;
			Set<RemoteCommandConfigType> types = factory.getCommandTypes();
			Object[] retVal = new Object[types.size()];
			return types.toArray(retVal);
		} else if (parentElement instanceof RemoteCommandConfigType) {
			RemoteCommandConfigType type = (RemoteCommandConfigType) parentElement;
			Set<RemoteCommandConfiguration> set = this.configTypeToCommandConfigSet
					.get(type.getCommandConfigType());
			if (set != null) {
				Object[] retVal = new Object[set.size()];
				return set.toArray(retVal);
			}
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
		// TODO Auto-generated method stub
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
		if (element instanceof List) {
			return !((List) element).isEmpty();
		} else if (element instanceof RemoteCommandConfigFactory) {
			return !((RemoteCommandConfigFactory) element).getCommandTypes()
					.isEmpty();
		} else if (element instanceof RemoteCommandConfigType) {
			RemoteCommandConfigType type = (RemoteCommandConfigType) element;
			Set<RemoteCommandConfiguration> set = this.configTypeToCommandConfigSet
					.get(type.getCommandConfigType());
			if (set != null) {
				return !set.isEmpty();
			}
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
		// TODO Auto-generated method stub

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
			this.edgeServerList = (List<RemoteEdgeServer>) newInput;
			List<RemoteEdgeServer> oldEdgeServerList = (List<RemoteEdgeServer>) oldInput;
			if (oldEdgeServerList != null) {
				for (RemoteEdgeServer edgeServer : oldEdgeServerList) {
					edgeServer.removePropertyChangeListener(this);
					edgeServer.getCommandConfigurations()
							.removeMapChangeListener(this);
					edgeServer.getRemoteCommandConfigFactories()
							.removeMapChangeListener(this);
				}
			}
			if (edgeServerList != null) {
				for (RemoteEdgeServer edgeServer : edgeServerList) {
					this.configTypeToCommandConfigSet = new HashMap<String, Set<RemoteCommandConfiguration>>();
					edgeServer.addPropertyChangeListener(this);
					edgeServer.getRemoteCommandConfigFactories()
							.addMapChangeListener(this);
					edgeServer.getCommandConfigurations().addMapChangeListener(
							this);
				}
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
			viewer.refresh(edgeServerList.get(0));
		} else if (evt.getPropertyName().equals(
				RemoteObjectDirtyEvent.DIRTY_EVENT_PROPERTY)) {
			RemoteObjectDirtyEvent bean = (RemoteObjectDirtyEvent) evt
					.getNewValue();
			RemoteCommandConfiguration commandConfig = (RemoteCommandConfiguration) edgeServerList
					.get(0).getCommandConfigurations().get(bean.getModelID());

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
				removeRemoteCommandPlugin((RemoteCommandConfigFactory) oldVal);
				addRemoteCommandPlugin((RemoteCommandConfigFactory) newVal);
			}
		}

		// handle event when obj is added
		for (Object key : event.diff.getAddedKeys()) {
			Object val = event.diff.getNewValue(key);
			if (val instanceof RemoteCommandConfigFactory) {
				addRemoteCommandPlugin((RemoteCommandConfigFactory) val);
			} else if (val instanceof RemoteCommandConfiguration) {
				addRemoteCommandConfiguration((RemoteCommandConfiguration) val);
			}
		}

		// handle event when obj is removed
		for (Object key : event.diff.getRemovedKeys()) {
			Object val = event.diff.getOldValue(key);
			if (val instanceof RemoteCommandConfigFactory) {
				removeRemoteCommandPlugin((RemoteCommandConfigFactory) val);
			} else if (val instanceof RemoteCommandConfiguration) {
				removeRemoteCommandConfiguration((RemoteCommandConfiguration) val);
			}
		}

	}

	/**
	 * Remove the remote command plugin specified.  
	 * 
	 * @param plugin
	 */
	private void removeRemoteCommandPlugin(RemoteCommandConfigFactory plugin) {
		viewer.remove(plugin);
	}

	/**
	 * TODO: Method level comment.  
	 * 
	 * @param plugin
	 */
	private void addRemoteCommandPlugin(RemoteCommandConfigFactory plugin) {
		viewer.add(edgeServerList.get(0), plugin);
		viewer.setExpandedState(edgeServerList.get(0), true);
	}

	/**
	 * Helper method to process a RemoteCommandConfig being added
	 * 
	 * @param config
	 */
	private void addRemoteCommandConfiguration(RemoteCommandConfiguration config) {
		Collection<RemoteCommandConfigFactory> factories = edgeServerList
				.get(0).getRemoteCommandConfigFactories().values();
		for (RemoteCommandConfigFactory factory : factories) {
			RemoteCommandConfigType type = factory.getCommandType(config
					.getCommandType());
			if (type != null) {
				Set<RemoteCommandConfiguration> commandSet = this.configTypeToCommandConfigSet
						.get(type.getCommandConfigType());
				if (commandSet == null) {
					commandSet = new HashSet<RemoteCommandConfiguration>();
					configTypeToCommandConfigSet.put(type
							.getCommandConfigType(), commandSet);
				}
				commandSet.add(config);
				config.addPropertyChangeListener(this);
				viewer.add(type, config);
				viewer.setExpandedState(type, true);
				break;
			}
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
	 * TODO: Method level comment.  
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
		edgeServerList.get(0).createCommandConfiguration(commandConfigType);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @seeorg.rifidi.edge.client.sal.controller.commands.CommandController#
	 * deleteCommand(java.lang.String)
	 */
	@Override
	public void deleteCommand(String commandConfigID) {
		edgeServerList.get(0).deleteCommandConfiguration(commandConfigID);

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
	 * @see org.rifidi.edge.client.sal.controller.commands.CommandController#clearPropertyChanges(java.lang.String)
	 */
	@Override
	public void clearPropertyChanges(String commandID) {
		RemoteCommandConfiguration config = (RemoteCommandConfiguration) edgeServerList
				.get(0).getCommandConfigurations().get(commandID);
		if (config != null) {
			config.clearUpdatedProperties();
		}

	}

	/*
	 * (non-Javadoc)
	 * @see org.rifidi.edge.client.sal.controller.commands.CommandController#synchPropertyChanges(java.lang.String)
	 */
	@Override
	public void synchPropertyChanges(String commandID) {
		RemoteCommandConfiguration config = (RemoteCommandConfiguration) edgeServerList
				.get(0).getCommandConfigurations().get(commandID);
		if (config != null) {
			config.synchUpdatedProperties(edgeServerList.get(0));
		}

	}
}
