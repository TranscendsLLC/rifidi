package org.rifidi.edge.adminclient;

import org.rifidi.edge.adminclient.commands.edgeServer.EdgeServerCommands;
import org.rifidi.edge.adminclient.commands.edgeServer.NewEdgeServerCommands;

public class TestApplication {
	
	private Console console;
	
	public static void main(String[] args) {
		new TestApplication();
	}
	
	public TestApplication() {
		console = new Console();
		//console.startConsole(new EdgeServerCommands());
		console.startConsole(new NewEdgeServerCommands());
	}
}
