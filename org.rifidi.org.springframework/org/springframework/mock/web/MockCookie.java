/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.mock.web;

import javax.servlet.http.Cookie;

import org.springframework.lang.Nullable;
import org.springframework.util.Assert;

/**
 * Extension of {@code Cookie} with extra attributes, as defined in
 * <a href="https://tools.ietf.org/html/rfc6265">RFC 6265</a>.
 *
 * @author Vedran Pavic
 * @since 5.1
 */
public class MockCookie extends Cookie {

	private static final long serialVersionUID = 4312531139502726325L;


	@Nullable
	private String sameSite;


	/**
	 * Constructor with the cookie name and value.
	 * @param name  the name
	 * @param value the value
	 * @see Cookie#Cookie(String, String)
	 */
	public MockCookie(String name, String value) {
		super(name, value);
	}


	/**
	 * Add the "SameSite" attribute to the cookie.
	 * <p>This limits the scope of the cookie such that it will only be attached
	 * to same site requests if {@code "Strict"} or cross-site requests if
	 * {@code "Lax"}.
	 * @see <a href="https://tools.ietf.org/html/draft-ietf-httpbis-rfc6265bis#section-4.1.2.7">RFC6265 bis</a>
	 */
	public void setSameSite(@Nullable String sameSite) {
		this.sameSite = sameSite;
	}

	/**
	 * Return the "SameSite" attribute, or {@code null} if not set.
	 */
	@Nullable
	public String getSameSite() {
		return this.sameSite;
	}


	/**
	 * Factory method that parses the value of a "Set-Cookie" header.
	 * @param setCookieHeader the "Set-Cookie" value; never {@code null} or empty
	 * @return the created cookie
	 */
	public static MockCookie parse(String setCookieHeader) {
		Assert.notNull(setCookieHeader, "Set-Cookie header must not be null");
		String[] cookieParts = setCookieHeader.split("\\s*=\\s*", 2);
		Assert.isTrue(cookieParts.length == 2, () -> "Invalid Set-Cookie header '" + setCookieHeader + "'");

		String name = cookieParts[0];
		String[] valueAndAttributes = cookieParts[1].split("\\s*;\\s*", 2);
		String value = valueAndAttributes[0];
		String[] attributes =
				(valueAndAttributes.length > 1 ? valueAndAttributes[1].split("\\s*;\\s*") : new String[0]);

		MockCookie cookie = new MockCookie(name, value);
		for (String attribute : attributes) {
			if (attribute.startsWith("Domain")) {
				cookie.setDomain(extractAttributeValue(attribute, setCookieHeader));
			}
			else if (attribute.startsWith("Max-Age")) {
				cookie.setMaxAge(Integer.parseInt(extractAttributeValue(attribute, setCookieHeader)));
			}
			else if (attribute.startsWith("Path")) {
				cookie.setPath(extractAttributeValue(attribute, setCookieHeader));
			}
			else if (attribute.startsWith("Secure")) {
				cookie.setSecure(true);
			}
			else if (attribute.startsWith("HttpOnly")) {
				cookie.setHttpOnly(true);
			}
			else if (attribute.startsWith("SameSite")) {
				cookie.setSameSite(extractAttributeValue(attribute, setCookieHeader));
			}
		}
		return cookie;
	}

	private static String extractAttributeValue(String attribute, String header) {
		String[] nameAndValue = attribute.split("=");
		Assert.isTrue(nameAndValue.length == 2,
				() -> "No value in attribute '" + nameAndValue[0] + "' for Set-Cookie header '" + header + "'");
		return nameAndValue[1];
	}

}
