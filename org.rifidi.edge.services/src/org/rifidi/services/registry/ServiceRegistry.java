/*
 *  ServiceRegistry.java
 *
 *  Project:		RiFidi Designer - A Virtualization tool for 3D RFID environments
 *  http://www.rifidi.org
 *  http://rifidi.sourceforge.net
 *  Copyright:	    Pramari LLC and the Rifidi Project
 *  License:		Lesser GNU Public License (LGPL)
 *  http://www.opensource.org/licenses/lgpl-license.html
 */
package org.rifidi.services.registry;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.osgi.framework.ServiceEvent;
import org.osgi.framework.ServiceListener;
import org.osgi.framework.ServiceReference;
import org.rifidi.edge.services.Activator;
import org.rifidi.services.annotations.Inject;

/**
 * This registry is used to access the different services. It is ensured that
 * all services only exist once to avoid singletons.
 * 
 * @author Jochen Mader Jan 24, 2008
 * @tags
 * 
 */
public class ServiceRegistry {
	/**
	 * Singleton pattern.
	 */
	private static ServiceRegistry instance;
	/**
	 * Map of wanted services. Key: service wanted Value: list of instances that
	 * needs the service
	 */
	private Map<String, List<DeferredInit>> wanted;

	/**
	 * Private constructor singleton pattern.
	 */
	@SuppressWarnings("unchecked")
	private ServiceRegistry() {
		Activator.getDefaultContext().addServiceListener(new ServiceListener() {

			/*
			 * (non-Javadoc)
			 * 
			 * @see org.osgi.framework.ServiceListener#serviceChanged(org.osgi.framework.ServiceEvent)
			 */
			@Override
			public void serviceChanged(ServiceEvent event) {
				if (ServiceEvent.REGISTERED == event.getType()) {
					for (String serviceName : (String[]) event
							.getServiceReference().getProperty("objectClass")) {
						if (wanted.containsKey(serviceName)) {
							for (DeferredInit defInit : wanted.get(serviceName)) {
								System.out.println(defInit.object + " got "
										+ wanted);
								serviceMethod(defInit.method, defInit.object);
							}
							wanted.remove(serviceName);
						}
					}
				}
			}
		});
		wanted = new HashMap<String, List<DeferredInit>>();
	}

	/**
	 * @return the singleton instance
	 */
	public static ServiceRegistry getInstance() {
		if (instance == null) {
			instance = new ServiceRegistry();
		}
		return instance;
	}

	/**
	 * Service the given instance
	 * 
	 * @param object instance to be serviced
	 */
	public void service(Object object) {
		for (Method method : object.getClass().getDeclaredMethods()) {
			serviceMethod(method, object);
		}
		Class<?> clazz = object.getClass();
		while (!Object.class.equals(object.getClass().getSuperclass())) {
			clazz = clazz.getSuperclass();
			if (clazz == null) {
				break;
			}
			for (Method method : clazz.getDeclaredMethods()) {
				serviceMethod(method, object);
			}
		}
	}

	private void serviceMethod(Method method, Object object) {
		if (method.isAnnotationPresent(Inject.class)) {

			Class<?> wantedService = method.getParameterTypes()[0];
			ServiceReference ref = Activator.getDefaultContext()
					.getServiceReference(wantedService.getName());
			if (ref != null) {
				try {
					method.invoke(object, Activator.getDefaultContext()
							.getService(ref));
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				}
				return;
			}
			if (wanted.get(wantedService.getName()) == null) {
				ArrayList<DeferredInit> list = new ArrayList<DeferredInit>();
				list.add(new DeferredInit(object, method));
				wanted.put(wantedService.getName(), list);
				return;
			}
			wanted.get(wantedService.getName()).add(
					new DeferredInit(object, method));
			System.out.println(object + " is waiting for " + wanted);
		}
	}

	private class DeferredInit {
		public Object object;
		public Method method;

		/**
		 * @param object
		 * @param method
		 */
		public DeferredInit(Object object, Method method) {
			super();
			this.object = object;
			this.method = method;
		}
	}
}
