package com.goldsprite.methodhandleexecutor.core;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;

public class MethodHandleExecutor {
    private final Map<String, MethodHandle> methodHandles = new HashMap<>();

    public MethodHandleExecutor(Class<?> targetClass) throws Exception {
        MethodHandles.Lookup lookup = MethodHandles.publicLookup();

        for (Method method : targetClass.getMethods()) {
            MethodHandle handle = getMethodHandle(lookup, targetClass, method);
            if (handle != null) {
                methodHandles.put(method.getName(), handle);
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
            // 处理无法找到的方法句柄时的异常
            System.err.println("Method handle creation failed for: " + method.getName() + " - " + e.getMessage());
            return null;
        }
    }

    public MethodHandle getMethodHandle(String methodName) {
        return methodHandles.get(methodName);
    }
	
	public Object executeCommand(Object target, String commandLine) throws Throwable {
		String[] parts = commandLine.split(" ");
		if (parts.length == 0) {
			throw new IllegalArgumentException("命令行不能为空");
		}

		String command = parts[0];
		MethodHandle handle = getMethodHandle(command);
		if (handle == null) {
			throw new NoSuchMethodException("未找到匹配的命令: " + command);
		}

		// 获取方法签名的参数类型
		Class<?>[] parameterTypes = handle.type().parameterArray();

		// 判断是否为静态方法（静态方法的第一个参数不会是目标对象的类型）
		boolean isStatic = parameterTypes.length == 0 || !parameterTypes[0].isInstance(target);

		// 检查参数数量是否匹配
		int expectedArgsCount = isStatic ? parameterTypes.length : parameterTypes.length - 1;
		if (parts.length - 1 != expectedArgsCount) {
			throw new IllegalArgumentException("参数数量不匹配");
		}

		// 解析参数
		Object[] args = new Object[expectedArgsCount];
		for (int i = 0; i < expectedArgsCount; i++) {
			int argIndex = isStatic ? i + 1 : i + 1;  // 第一个命令部分是方法名，args 从第二部分开始
			args[i] = parseArgument(parts[argIndex], parameterTypes[i + (isStatic ? 0 : 1)]);
		}

		// 调用方法
		return isStatic ? handle.invokeWithArguments(args) : handle.bindTo(target).invokeWithArguments(args);
	}
	
	
    public Object executeCommand(Object target, String command, Object... args) throws Throwable {
		MethodHandle handle = getMethodHandle(command);
		if (handle != null) {
			if (handle.type().parameterCount() > 0 && handle.type().parameterType(0).isInstance(target)) {
				// 如果是非静态方法，绑定目标对象
				return handle.bindTo(target).invokeWithArguments(args);
			} else {
				// 静态方法直接调用
				return handle.invokeWithArguments(args);
			}
		} else {
			throw new NoSuchMethodException("未找到匹配的命令: " + command);
		}
	}
	
	private Object parseArgument(String arg, Class<?> targetType) {
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

