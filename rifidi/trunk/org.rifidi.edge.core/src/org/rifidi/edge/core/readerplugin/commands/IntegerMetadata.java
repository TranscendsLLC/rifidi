package org.rifidi.edge.core.readerplugin.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface IntegerMetadata{
	String name();
	String displayName();
	int defaultValue();
	boolean editable();
	int maxValue();
	int minValue();
}
