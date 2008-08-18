package org.rifidi.edge.core.readerplugin.commands;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface StringMetadata {
	String name();
	String displayName();
	String defaultValue();
	boolean editable();
	String regex();

}
