package com.wego.screenscraping;

/**
 * Constants used in forming request headers.
 * Header Names
 * 
 * @author KumarVibhav
 *
 */
public final class ScrapingHeaderConstants {
	public static final String HOST = "Host";
	public static final String USER_AGENT = "User-Agent";
	public static final String ACCEPT = "Accept";
	public static final String ACCEPT_LANGUAGE = "Accept-Language";
	public static final String ACCEPT_ENCODING = "Accept-Encoding";
	public static final String CONTENT_TYPE = "Content-Type";
	public static final String CONTENT_LENGTH = "Content-Length";
	public static final String COOKIE = "Cookie";
	public static final String CONNECTION = "Connection";
	public static final String PRAGMA = "Pragma";
	public static final String CACHE_CONTROL = "Cache-Control";
	public static final String REFERER = "Referer";
	
	private ScrapingHeaderConstants() {
		throw new AssertionError();
	}
}
