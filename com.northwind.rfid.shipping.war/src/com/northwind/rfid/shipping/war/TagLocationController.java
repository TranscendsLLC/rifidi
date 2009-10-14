/**
 * 
 */
package com.northwind.rfid.shipping.war;

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.Controller;

import com.northwind.rfid.shipping.war.service.TagLocationService;

/**
 * @author Kyle Neumeier - kyle@pramari.com
 * 
 */
public class TagLocationController implements Controller {

	/** The Tag Location Service */
	private volatile TagLocationService tagLocationService;

	/**
	 * Called by Spring
	 * 
	 * @param tagLocationService
	 *            the tagLocationService to set
	 */
	public void setTagLocationService(TagLocationService tagLocationService) {
		this.tagLocationService = tagLocationService;
	}

	@Override
	public ModelAndView handleRequest(HttpServletRequest arg0,
			HttpServletResponse arg1) throws Exception {
		HashMap<String, Object> model = new HashMap<String, Object>();
		model.put("dockdoor", tagLocationService.getDockDoorItems());
		model.put("weighstation", tagLocationService.getWeighStationItems());
		model.put("alerts", tagLocationService.getAlerts());

		return new ModelAndView("/WEB-INF/jsp/taglocation.jsp", "model", model);
	}
}