package com.goldsprite.methodhandleexecutor.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;

public class MethodHandleExecutor {
	private final Map<String, MethodHandle> methodHandles = new HashMap<>();
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
				methodHandles.put(method.getName(), handle);
				methodIsStatic.put(method.getName(), Modifier.isStatic(method.getModifiers()));  // 记录静态性
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
			throw new IllegalArgumentException("命令行不能为空");
		}

		String command = parts[0];
		MethodHandle handle = methodHandles.get(command);
		Boolean isStatic = methodIsStatic.get(command);  // 获取静态性
		if (handle == null) {
			throw new NoSuchMethodException("未找到匹配的命令: " + command);
		}
		if (isStatic && target == null){
			throw new NullPointerException("试图从null调用一个实例方法");
		}

		// 获取方法的参数类型
		Class<?>[] parameterTypes = handle.type().parameterArray();

		// 检查参数数量是否匹配
		int paramOffset = !isStatic ?1 : 0;
		int expectedArgsCount = parameterTypes.length - paramOffset;
		if (parts.length - 1 != expectedArgsCount) {
			throw new IllegalArgumentException("参数数量不匹配");
		}

		// 解析参数
		Object[] args = new Object[expectedArgsCount];
		for (int i = 0; i < expectedArgsCount; i++) {
			int argIndex = i + 1;  // 第一个命令部分是方法名，args 从第二部分开始
			args[i] = parseArgument(parts[argIndex], parameterTypes[i + paramOffset]);
		}

		// 调用方法
		return isStatic ? handle.invokeWithArguments(args) : handle.bindTo(target).invokeWithArguments(args);
	}

	// 辅助方法：解析参数
	private Object parseArgument(String arg, Class<?> targetType) {
		if (targetType == String.class) {
			return arg;
		}
		try {
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
		} catch (NumberFormatException ignored) {
			// 如果解析失败，返回原始字符串
		}
		return arg;
	}
}

