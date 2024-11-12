package com.goldsprite.methodhandleexecutor.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;
import java.util.*;

public class MethodHandleExecutor {
	private final Map<String, MethodHandle> methodHandles = new HashMap<>();
	private final Map<String, Boolean> methodIsStatic = new HashMap<>();  // 存储方法的静态性
	private Object target;

	public MethodHandleExecutor(Object target) {
		this.target = target;
		initializeMethodHandles(target.getClass());
	}
	public MethodHandleExecutor(Class<?> targetClass) {
		initializeMethodHandles(targetClass);
	}
	public void initializeMethodHandles(Class<?> targetClass) {
		MethodHandles.Lookup lookup = MethodHandles.publicLookup();

		for (Method method : targetClass.getMethods()) {
			MethodHandle handle = getMethodHandle(lookup, targetClass, method);
			if (handle != null) {
				String key = buildMethodKey(method.getName(), method.getParameterTypes());
				methodHandles.put(key, handle);
				methodIsStatic.put(key, Modifier.isStatic(method.getModifiers()));
			}
		}
	}

	private MethodHandle getMethodHandle(MethodHandles.Lookup lookup, Class<?> targetClass, Method method) {
		try {
			MethodType methodType = MethodType.methodType(method.getReturnType(), method.getParameterTypes());
			if (Modifier.isStatic(method.getModifiers())) {
				return lookup.findStatic(targetClass, method.getName(), methodType);
			} else {
				return lookup.findVirtual(targetClass, method.getName(), methodType);
			}
		} catch (NoSuchMethodException | IllegalAccessException e) {
			System.err.println("Method handle creation failed for: " + method.getName() + " - " + e.getMessage());
			return null;
		}
	}

	public Object executeCommand(String commandLine) {
		String[] parts = commandLine.split(" ");
		if (parts.length == 0) {
			Debug.log("命令行不能为空");
			return null;
		}
		// 第一个部分是方法名
		String command = parts[0];
		Debug.debugLog("调用方法句柄: "+commandLine);
		
		
		Object[] args = new Object[parts.length-1];
		Class[] paramTypes = new Class[parts.length-1];
		for (int i = 0; i < args.length; i++) {
			int argIndex = i + 1;
			Object[] outArg = new Object[]{ parts[argIndex] };
			paramTypes[i] = parseParamType(outArg);
			args[i] = outArg[0];
		}
		String methodKey = buildMethodKey(command, paramTypes);
		Debug.debugLog("解析方法签名为: "+methodKey);
		MethodHandle handle = methodHandles.get(methodKey);
		if (handle == null) {
			Debug.log("找不到对应的方法.");
			return null;
		}
		boolean isStatic = methodIsStatic.get(methodKey);  // 获取静态性
		if (isStatic && target == null){
			Debug.log("试图从null调用一个实例方法");
			return null;
		}

		// 调用方法
		Object ret = null;
		try{
			return execMethod(target, handle, isStatic, args);
		}catch(Throwable e){}
		return ret;
	}

	private Object execMethod(Object target, MethodHandle handle, boolean isStatic, Object[] args) throws Throwable
	{
		return isStatic ? handle.invokeWithArguments(args) : handle.bindTo(target).invokeWithArguments(args);
	}
	
	private Class<?> parseParamType(Object[] outArg) {
		String strArg = (String)outArg[0];
		boolean isFullType;
		//匹配并处理int/Integer
		if (strArg.matches("^-?\\d+I?$")) {
			if(strArg.endsWith("I")){
				strArg = strArg.substring(0, strArg.length()-1);
				outArg[0] = Integer.valueOf(strArg);
				return Integer.class;
			}
			outArg[0] = Integer.parseInt(strArg);
			return Integer.TYPE;
		}
		//匹配并处理long/Long
		if (strArg.matches("^-?\\d+[lL]$")) {
			isFullType = strArg.endsWith("L");
			strArg = strArg.substring(0, strArg.length()-1);
			outArg[0] = isFullType ?Long.valueOf(strArg) :Long.parseLong(strArg);
			return isFullType ?Long.class :Long.TYPE;
		}
		//匹配并处理float/Float
		if (strArg.matches("^-?\\d+(\\.\\d+)?[fF]$")) {
			isFullType = strArg.endsWith("F");
			strArg = strArg.substring(0, strArg.length()-1);
			outArg[0] = isFullType ?Float.valueOf(strArg) :Float.parseFloat(strArg);
			return isFullType ?Float.class :Float.TYPE;
		}
		//匹配并处理double/Double
		if (strArg.matches("^-?\\d+(\\.\\d+)?[dD]$")) {
			isFullType = strArg.endsWith("D");
			strArg = strArg.substring(0, strArg.length()-1);
			outArg[0] = isFullType ?Double.valueOf(strArg) :Double.parseDouble(strArg);
			return isFullType ?Double.class :Double.TYPE;
		}
		//匹配并处理boolean/Boolean
		if (strArg.matches("^(?i)(true|false)$")) {
			isFullType = strArg.matches("^(TRUE|FALSE)$");
			outArg[0] = isFullType ?Boolean.valueOf(strArg) :Boolean.parseBoolean(strArg);
			return isFullType ?Boolean.class :Boolean.TYPE;
		}
		//其他为String
		return String.class;
	}
	
	// 辅助方法：根据方法名和参数类型构建方法键
	private String buildMethodKey(String methodName, Class<?>[] paramTypes) {
		StringBuilder signature = new StringBuilder(methodName);
		for (Class<?> paramType : paramTypes) {
			signature.append("_").append(paramType.getName());
		}
		return signature.toString();
	}

}

