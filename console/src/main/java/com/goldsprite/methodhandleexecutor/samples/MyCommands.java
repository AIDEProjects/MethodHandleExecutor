package com.goldsprite.methodhandleexecutor.samples;

public class MyCommands {
	public void sayHello() { // 实例方法
		System.out.println("Hello!");
	}

	public static void staticHello() { // 静态方法
		System.out.println("Static Hello!");
	}
	
	public static float add(float num1, float num2){
		System.out.printf("%f+%f=%f\n", num1, num2, num1+num2);
		return num1+num2;
	}
	
	public void sub(int num1, int num2){
		System.out.printf("int: %d-%d=%d\n", num1, num2, num1-num2);
	}
	public void sub(long num1, int num2){
		System.out.printf("long: %d-%d=%d\n", num1, num2, num1-num2);
	}
	public void sub(float num1, int num2){
		System.out.printf("float: %f-%d=%f\n", num1, num2, num1-num2);
	}
}
