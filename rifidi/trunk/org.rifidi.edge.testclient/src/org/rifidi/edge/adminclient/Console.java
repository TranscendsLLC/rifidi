package org.rifidi.edge.adminclient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.HashMap;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.rifidi.edge.adminclient.annotations.Command;
import org.rifidi.edge.adminclient.commands.ICommand;
import org.rifidi.edge.adminclient.commands.MethodHelper;

public class Console {

	private Log logger = LogFactory.getLog(Console.class);

	private HashMap<String, String[]> commandHelp;
	private HashMap<String, MethodHelper> commands;

	public Console() {
		printWelcome();
	}
	
	public void startConsole(ICommand command) {

		initialize(command);

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String input = null;
		try {
			while (true) {
				System.out.print("Console> ");
				input = in.readLine();
				input = input.trim();
				String[] curCommand = input.split(" ");
				MethodHelper m = null;
				
				
				if ((m = commands.get(curCommand[0].toLowerCase())) != null) {
					String[] args = new String[curCommand.length - 1];
					for (int i = 1; i < curCommand.length; i++)
						args[i - 1] = curCommand[i];
					try {
						String out = (String) m.method.invoke(m.instance,
								(Object[]) args);
						System.out.println(out);
					} catch (Exception e) {
						System.out.println("Help: "
								+ helpForCommand(curCommand[0]));
					}
				} else {
					System.out.println("Command not found. \n"
							+ "You can type help for getting help"
							+ " for the available commands. ");
				}
				
				
			}
		} catch (IOException e) {
			logger.error(e);
		}
	}

		
	private void initialize(ICommand instance) {
		commandHelp = new HashMap<String, String[]>();
		commands = new HashMap<String, MethodHelper>();

		// Get help methods from Console
		for (Method m : this.getClass().getMethods()) {
			if (m.isAnnotationPresent(Command.class)) {
				Command command = m.getAnnotation(Command.class);
				commandHelp.put(command.name().toLowerCase(), command.arguments());
				commands.put(command.name().toLowerCase(), new MethodHelper(this, m));
			}
		}

		// Get commands from ICommand
		for (Method m : instance.getClass().getMethods()) {
			if (m.isAnnotationPresent(Command.class)) {
				Command command = m.getAnnotation(Command.class);
				commandHelp.put(command.name().toLowerCase(), command.arguments());
				commands.put(command.name().toLowerCase(), new MethodHelper(instance, m));
			}
		}
	}

	private void printWelcome() {
		System.out.println("*************************************");
		System.out.println("*  == Rifidi EdgeServer Console ==  *");
		System.out.println("*                                   *");
		System.out.println("*  Andreas Huebner (Pramari, LLC)   *");
		System.out.println("*************************************");
	}

	@Command(name = "help")
	public String help() {
		StringBuffer buffer = new StringBuffer();
		System.out.println("==== Help Menu ====");
		for (String commandName : commandHelp.keySet()) {
			buffer.append(helpForCommand(commandName) + "\n");
		}
		return buffer.toString();
	}

	// @Command(name="help",arguments="command")
	private String helpForCommand(String command) {
		String commandArguments = " ";
		for (String argument : commandHelp.get(command)) {
			commandArguments = commandArguments + " [" + argument + "] ";
		}
		String commandDescription = "* " + command + commandArguments;
		return commandDescription;
	}

	@Command(name = "quit")
	public void quit() {
		System.out.println("== Quit Application ==");
		System.exit(-1);
	}

}
