package com.goldsprite.methodhandleexecutor.samples;
import com.goldsprite.methodhandleexecutor.core.*;

public class MyCommands {
	public void sayHello() { // 实例方法
		Debug.log("Hello!");
	}

	public static void staticHello() { // 静态方法
		Debug.log("Static Hello!");
	}
	
	public static float add(Float num1, float num2){
		Debug.logf("float: %f+%f=%f", num1, num2, num1+num2);
		return num1+num2;
	}
	
	public void sub(int num1, int num2){
		Debug.logf("int: %d-%d=%d", num1, num2, num1-num2);
	}
	public void sub(long num1, int num2){
		Debug.logf("long: %d-%d=%d", num1, num2, num1-num2);
	}
	public void sub(float num1, int num2){
		Debug.logf("Float: %f-%d=%f", num1, num2, num1-num2);
	}
}
