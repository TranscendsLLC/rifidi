/*
 *  TestService.java
 *
 *  Project: Rifidi - A developer tool for RFID
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright: Pramari LLC and the Rifidi Project
 *  License: Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */

package org.rifidi.edge.webservice.testservice;

import javax.jws.WebMethod;
import javax.jws.WebService;

/**
 * @author kyle
 *
 */
@WebService(endpointInterface="org.rifidi.edge.webservice.testservice.ITestService")
public class TestService implements ITestService{

	@WebMethod()
	public void sayHi(){
		System.out.println("hi");
	}
}
