package com.letv.framework.util;

import android.util.Log;

import java.util.Locale;

public class LogUtil {
	
	private static String TAG_ORDER = "com.letv";
	
	/**
	 * print log
	 */
	private static boolean isPrintLogV = true;
	private static boolean isPrintLogD = true;
	private static boolean isPrintLogI = true;
	private static boolean isPrintLogW = true;
	private static boolean isPrintLogE = true;

	private String tag;

	/**
	 * 设置log等级输出
	 * 
	 * @param v
	 * @param d
	 * @param i
	 * @param w
	 * @param e
	 */
	public static void initLogLevel(boolean v, boolean d, boolean i, boolean w, boolean e) {
		isPrintLogV = v;
		isPrintLogD = d;
		isPrintLogI = i;
		isPrintLogW = w;
		isPrintLogE = e;
	}

	public LogUtil(Class<?> clazz) {
		if (clazz == null) {
			throw new NullPointerException("[clazz can't null]");
		}
		tag = clazz.getSimpleName();
	}

	public void i(String msg) {
		if (isPrintLogI) {
			Log.i(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public void w(String msg) {
		if (isPrintLogW) {
			Log.w(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public void v(String msg) {
		if (isPrintLogV) {
			Log.v(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public void d(String msg) {
		if (isPrintLogD) {
			Log.d(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}
	
	public static void v(String format, Object... args) {
		if (isPrintLogV) {
			Log.v(TAG_ORDER, buildMessage(format, args));
		}
	}

	// --- static --- //
	
	public static void l(String msg) {
		if (isPrintLogD) {
			e("tagg", msg);
		}
	}

	public static void i(String tag, String msg) {
		if (isPrintLogI) {
			Log.i(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public static void w(String tag, String msg) {
		if (isPrintLogW) {
			Log.w(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public static void v(String tag, String msg) {
		if (isPrintLogV) {
			Log.v(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public static void d(String tag, String msg) {
		if (isPrintLogD) {
			Log.d(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public static void e(String tag, String msg) {
		if (isPrintLogE) {
			Log.e(TAG_ORDER, "[" + tag + "] " + msg);
		}
	}

	public static void e(String msg) {
		if (isPrintLogE) {
			Log.e(TAG_ORDER, "[" + null + "] " + msg);
		}
	}

	// 保存错误日记
	/*
	 * public static void f(boolean isAddTime, String message){ if(isPrintLogE){
	 * DangdangFileManager.saveErrorMessage(message+"\r\n", isAddTime); } }
	 */

	private static String buildMessage(String format, Object... args) {
		String msg = (args == null) ? format : String.format(Locale.US, format, args);
		StackTraceElement[] trace = new Throwable().fillInStackTrace().getStackTrace();

		String caller = "<unknown>";
		// Walk up the stack looking for the first caller outside of VolleyLog.
		// It will be at least two frames up, so start there.
		for (int i = 2; i < trace.length; i++) {
			Class<?> clazz = trace[i].getClass();
			if (!clazz.equals(LogUtil.class)) {
				String callingClass = trace[i].getClassName();
				callingClass = callingClass.substring(callingClass.lastIndexOf('.') + 1);
				callingClass = callingClass.substring(callingClass.lastIndexOf('$') + 1);

				caller = callingClass + "." + trace[i].getMethodName();
				break;
			}
		}
		return String.format(Locale.US, "[%d] %s: %s", Thread.currentThread().getId(), caller, msg);
	}
}