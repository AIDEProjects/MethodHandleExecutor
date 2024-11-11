package com.goldsprite.methodhandleexecutor.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;
import java.util.*;

public class MethodHandleExecutor {
	private final Map<String, List<MethodHandle>> methodHandles = new HashMap<>();
	private final Map<String, Boolean> methodIsStatic = new HashMap<>();  // 存储方法的静态性
	private Object target;

	public MethodHandleExecutor(Object target) throws Exception {
		this.target = target;
		initializeMethodHandles(target.getClass());
	}
	public MethodHandleExecutor(Class<?> targetClass) throws Exception {
		initializeMethodHandles(targetClass);
	}
	public void initializeMethodHandles(Class<?> targetClass) throws Exception {
		MethodHandles.Lookup lookup = MethodHandles.publicLookup();

		for (Method method : targetClass.getMethods()) {
			MethodHandle handle = getMethodHandle(lookup, targetClass, method);
			if (handle != null) {
				String key = method.getName();
				if (!methodHandles.containsKey(key)){
					methodHandles.put(key, new ArrayList<MethodHandle>());
				}
				methodHandles.get(key).add(handle);
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

	public Object executeCommand(String commandLine) throws Throwable {
		String[] parts = commandLine.split(" ");
		if (parts.length == 0) {
			System.out.println("命令行不能为空");
			return null;
		}
		// 第一个部分是方法名
		String command = parts[0];

		List<MethodHandle> handles = methodHandles.get(command);
		if (handles == null || handles.isEmpty()) {
			System.out.println("没有这样的方法名.");
			return null;
		}

		MethodHandle handle = null;
		boolean isStatic = false;
		Object[] args = null;
		for (MethodHandle mh : methodHandles.get(command)){
			Class<?>[] paramTypes = mh.type().parameterArray();
			isStatic = methodIsStatic.get(command);  // 获取静态性
			int paramOffset = !isStatic ?1 : 0;
			int expectedArgsCount = paramTypes.length - paramOffset;
			if (parts.length - 1 != expectedArgsCount) {
				System.out.println("参数数量不匹配, 下一个");
				continue;
			}
			try{
				args = new Object[expectedArgsCount];
				for (int i = 0; i < expectedArgsCount; i++) {
					int argIndex = i + 1;  // 第一个命令部分是方法名，args 从第二部分开始
					args[i] = parseArgument(parts[argIndex], paramTypes[i + paramOffset]);
				}
				handle = mh;
				break;
			}catch (Exception ignore){
				System.out.println("参数类型不匹配，下一个");
			}
		}
		if (handle == null) {
			System.out.println("给定方法的参数类型列表没有一个匹配.");
			return null;
		}
		if (isStatic && target == null){
			System.out.println("试图从null调用一个实例方法");
			return null;
		}

		// 调用方法
		return isStatic ? handle.invokeWithArguments(args) : handle.bindTo(target).invokeWithArguments(args);
	}

	// 辅助方法：解析参数
	private Object parseArgument(String arg, Class targetType) {
		if (targetType == String.class) {
			return arg;
		}
		if (targetType == Integer.class || targetType == int.class) {
			return Integer.parseInt(arg);
		} else if (targetType == Long.class || targetType == long.class) {
			return Long.parseLong(arg);
		} else if (targetType == Double.class || targetType == double.class) {
			return Double.parseDouble(arg);
		} else if (targetType == Float.class || targetType == float.class) {
			return Float.parseFloat(arg);
		} else if (targetType == Boolean.class || targetType == boolean.class) {
			return Boolean.parseBoolean(arg);
		}
		
		return arg;
	}

	public MethodHandle findMatchingHandle(String commandLine){
		String[] parts = commandLine.split(" ");
		if (parts.length == 0){
			throw new IllegalArgumentException("命令行不能为空");
		}

		// 方法名是第一个部分
		String command = parts[0];
		List<MethodHandle> handles = methodHandles.get(command);
		if (handles == null || handles.isEmpty()) {
			System.out.println("没有这样的方法名.");
			return null;
		}

		for (MethodHandle mh : methodHandles.get(command)){
			Class<?>[] paramTypes = mh.type().parameterArray();
			try{
				for (int i=0;i < paramTypes.length;i++){
					Class paramType = paramTypes[i];
					String arg = parts[i + 1];
					parseArgument(arg, paramType);
				}
				return mh;
			}catch (Exception ignore){}
		}
		System.out.println("该方法的参数类型列表没有一个匹配.");
		return null;
	}

}

