package org.rifidi.edge.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Service
public class ALESettings {
	
//	@Value(value = "${ale.standard.version}")
	@Value(value = "1.0")
	private String aleStandardVersion;
	
//	@Value(value = "${lr.standard.version}")
	@Value(value = "1.0")
	private String lrStandardVersion;
	
//	@Value(value = "${vendor.version}")
	@Value(value = "1.0")
	private String vendorVersion;
	
	

	public ALESettings() {
		super();
		System.out.println("ALESettings() call");
	}

	/**
	 * return the current standard version of the ALE.
	 * @return current standard version.
	 */
	public String getAleStandardVersion() {
		return aleStandardVersion;
	}

	public void setAleStandardVersion(String aleStandardVersion) {
		this.aleStandardVersion = aleStandardVersion;
	}

	/**
	 * return the current standard version of the logical reader management.
	 * @return current standard version.
	 */
	public String getLrStandardVersion() {
		return lrStandardVersion;
	}
	
	public void setLrStandardVersion(String lrStandardVersion) {
		this.lrStandardVersion = lrStandardVersion;
	}

	/**
	 * encodes the current vendor version of this filtering and collection - the current build.
	 * @return current vendor version.
	 */
	public String getVendorVersion() {
		return vendorVersion;
	}

	public void setVendorVersion(String vendorVersion) {
		this.vendorVersion = vendorVersion;
	}
}
