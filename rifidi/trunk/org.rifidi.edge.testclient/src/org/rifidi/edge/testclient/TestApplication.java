package org.rifidi.edge.testclient;

import org.rifidi.edge.testclient.commands.edgeServer.EdgeServerCommands;

public class TestApplication {
	
	private Console console;
	
	public static void main(String[] args) {
		new TestApplication();
	}
	
	public TestApplication() {
		console = new Console();
		console.startConsole(new EdgeServerCommands());
	}
}
