package org.rifidi.edge.testclient.commands;

import java.lang.reflect.Method;

public class MethodHelper {

	public Object instance;

	public Method method;

	public MethodHelper(Object instance, Method method) {
		this.instance = instance;
		this.method = method;
	}

}
