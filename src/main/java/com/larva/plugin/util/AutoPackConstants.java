package com.larva.plugin.util;

import java.io.File;
import java.util.regex.Matcher;

public class AutoPackConstants {

	public static final String DEFAULT_CONFIG_FILE = "builder.properties";
	
	public static final String DEFAULT_BRANCH = "master";
	
	public static final String WEBROOT_JAVA_PATH = "WebRoot";
	
	public static final String SECURITY_JAVA_PATH = "WEB-INF";
	
	public static final String CLAZZ_JAVA_PATH = "classes";
	
	public static final String DELIMITER = File.separator;
	
	public static final String MATCHER_DELIMITER = Matcher.quoteReplacement(File.separator);
	
	public static final String LINUX_DELIMITER = "/";
	
	public static final String CLASSES_JAVA_PATH = SECURITY_JAVA_PATH + LINUX_DELIMITER + CLAZZ_JAVA_PATH;
	
}
