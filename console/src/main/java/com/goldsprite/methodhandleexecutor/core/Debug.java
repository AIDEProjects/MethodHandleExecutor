package com.goldsprite.methodhandleexecutor.core;
import java.io.*;

public class Debug
{
	public static String TAG = "MethodHandle";
	public static boolean debug = false;
	
	public static void debugLog(String log)
	{
		if (Debug.debug) 
			log(log);
	}

	public static void log(){
		log("");
	}
	public static void log(String log){
		System.out.println(log);
	}
	public static void logErr(String log, Throwable e){
		logf("%s\n%s", log, getStackTraceStr(e));
	}
	public static void logf(String log, Object... objs){
		System.out.printf(log+"\n", objs);
	}

	public static String getStackTraceStr(Throwable e){
		StringWriter sw = new StringWriter();
		e.printStackTrace(new PrintWriter(sw));
		return sw.toString();
	}
}
