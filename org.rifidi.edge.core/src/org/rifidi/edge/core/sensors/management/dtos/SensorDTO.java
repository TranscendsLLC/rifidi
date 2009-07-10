/**
 * 
 */
package org.rifidi.edge.core.sensors.management.dtos;

import java.util.Set;

/**
 * @author Jochen Mader - jochen@pramari.com
 * 
 */
public class SensorDTO {
	private final Set<String> children;
	private final String name;
	private final boolean composite;

	/**
	 * Constructor.
	 * 
	 * @param name
	 * @param children
	 * @param composite
	 */
	public SensorDTO(final String name, final Set<String> children,
			final boolean composite) {
		this.name = name;
		this.children = children;
		this.composite = composite;
	}

	/**
	 * @return the children
	 */
	public Set<String> getChildren() {
		return children;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the composite
	 */
	public boolean isComposite() {
		return composite;
	}

}
