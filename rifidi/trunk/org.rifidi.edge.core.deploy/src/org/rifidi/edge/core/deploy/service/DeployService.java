package org.rifidi.edge.core.deploy.service;

import java.util.List;

public interface DeployService {
	
	public void add(List<String> directoriesToMonitor);

	public void remove(List<String> directories);

}
