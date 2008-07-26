package org.rifidi.edge.core.readerplugin;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

import org.rifidi.edge.core.readerplugin.connectionmanager.ConnectionManager;
import org.rifidi.edge.core.readerplugin.xml.CommandDescription;
import org.rifidi.edge.core.readerplugin.xml.ReaderPluginXML;

public class ReaderPlugin {
	
	ReaderPluginXML readerPluginXML;
	
	public ReaderPlugin(ReaderPluginXML readerPluginXML){
		this.readerPluginXML = readerPluginXML;
	}
	
	public CommandDescription getProperty(String propertyName){
		return getCommand(propertyName, readerPluginXML.getPropertyList());
	}
	
	public CommandDescription getCommand(String commandName){
		return getCommand(commandName, readerPluginXML.getCommandList());
	}
	
	private CommandDescription getCommand(String name, List<CommandDescription> commandList){
		for(CommandDescription desc : commandList){
			if(desc.getName().equals(name)){
				return desc;
			}
		}
		return null;
	}

	public ConnectionManager newConnectionManager(ReaderInfo readerInfo) {
		try {
			//TODO: make a constructor with class and superclass
			return (ConnectionManager) Class.forName(readerPluginXML.getConnectionManager())
					.getConstructor(readerInfo.getClass().getSuperclass()).newInstance(
							readerInfo);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayList<String> getCommands(){
		return getCommands(readerPluginXML.getCommandList());
	}
	
	public ArrayList<String> getProperties(){
		return getCommands(readerPluginXML.getPropertyList());
	}
	
	private ArrayList<String> getCommands(ArrayList<CommandDescription> commands){
		ArrayList<String> names = new ArrayList<String>();
		for(CommandDescription d : commands){
			names.add(d.getName());
		}
		return names;
	}

}
