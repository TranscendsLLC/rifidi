package org.rifidi.edge.readerplugin.thingmagic.command;

import org.rifidi.edge.core.commands.AbstractCommandConfiguration;

public class ThingMagicGetTagListCommandConfiguration extends
	AbstractCommandConfiguration<ThingMagicGetTagListCommand>{

	private static final String name = "ThingMagic-GetTagList";
	
	private static final String description = "Get Tags";
	
	@Override
	public ThingMagicGetTagListCommand getCommand(String readerID) {
		// TODO Auto-generated method stub
		return new ThingMagicGetTagListCommand(super.getID(), readerID);
	}

	@Override
	public String getCommandDescription() {
		// TODO Auto-generated method stub
		return description;
	}

	@Override
	public String getCommandName() {
		// TODO Auto-generated method stub
		return name;
	}

}
