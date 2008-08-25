package org.rifidi.edge.core.readerplugin.commands.annotations;

public @interface DoubleMetadata {
	String name();
	String displayName();
	double defaultValue();
	boolean editable();
	double maxValue();
	double minValue();
}
