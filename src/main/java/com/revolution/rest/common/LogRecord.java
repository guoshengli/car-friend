package com.revolution.rest.common;

import org.apache.log4j.Logger;

public class LogRecord {
	private static Logger info = Logger.getLogger("InfoLogger");
	private static Logger error = Logger.getLogger("ErrorLogger");

	public static void info(String infomation) {
		info.info(infomation);
	}

	public static void error(String infomation) {
		error.error(infomation);
	}
}
