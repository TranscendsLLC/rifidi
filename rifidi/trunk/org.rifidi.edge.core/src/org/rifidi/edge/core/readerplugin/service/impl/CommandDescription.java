package org.rifidi.edge.core.readerplugin.service.impl;

import java.util.List;

public class CommandDescription {

	private String name;
	private String classname;
	private String xsd;
	private List<String> groups;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getXsd() {
		return xsd;
	}

	public void setXsd(String xsd) {
		this.xsd = xsd;
	}

	public List<String> getGroups() {
		return groups;
	}

	public void setGroups(List<String> groups) {
		this.groups = groups;
	}

}
