package com.creants.creants_2x.core.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author LamHa
 *
 */
public class QAntTracer {
	private static final Logger TRACE_LOG = LogManager.getLogger("TraceLogger");
	private static final Logger ERROR_LOG = LogManager.getLogger("ErrorLogger");

	public static void debug(Class<?> clazz, Object... msgs) {
		if (TRACE_LOG.isDebugEnabled()) {
			TRACE_LOG.debug(getTraceMessage(clazz, msgs));
		}

	}

	public static void info(Class<?> clazz, Object... msgs) {
		TRACE_LOG.info(getTraceMessage(clazz, msgs));
	}

	/**
	 * Log thông tin lỗi
	 * 
	 * @param clazz
	 *            class nào xảy ra lỗi
	 * @param msgs
	 *            thông tin kèm theo lỗi - nên kèm theo tên hàm
	 */
	public static void error(Class<?> clazz, Object... msgs) {
		ERROR_LOG.error(getTraceMessage(clazz, msgs));
	}

	/**
	 * Log thông tin cảnh báo
	 * 
	 * @param clazz
	 *            class nào xảy ra lỗi
	 * @param msgs
	 *            thông tin kèm theo lỗi - nên kèm theo tên hàm
	 */
	public static void warn(Class<?> clazz, Object... msgs) {
		TRACE_LOG.warn(getTraceMessage(clazz, msgs));
	}

	private static String getTraceMessage(Class<?> clazz, Object[] msgs) {
		StringBuilder traceMsg = new StringBuilder().append("{").append(clazz.getSimpleName()).append("}: ");
		Object[] arrayOfObject;
		int j = (arrayOfObject = msgs).length;
		for (int i = 0; i < j; i++) {
			traceMsg.append(arrayOfObject[i].toString()).append(" ");
		}

		return traceMsg.toString();
	}

	public static String getTraceMessage(Exception throwable) {
		StringWriter stringWriter = new StringWriter();
		throwable.printStackTrace(new PrintWriter(stringWriter));
		return "\n" + stringWriter.toString();
	}
}
