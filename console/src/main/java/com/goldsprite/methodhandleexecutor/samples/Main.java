package com.goldsprite.methodhandleexecutor.samples;
import com.goldsprite.methodhandleexecutor.core.*;

public class Main {
	public static void main(String[] args) {
		MyCommands myCommands = new MyCommands();
		MethodHandleExecutor executor = new MethodHandleExecutor(myCommands);
		Debug.debug = true;
		executor.executeCommand(
			"add 1 2I 3l 4L 5f 6.1F 7d 8.2D true FALSE str"
		//预期类型为 int Integer long Long float Float double Double boolean Boolean String
		);
		Debug.log();
		float ret = executor.executeCommand("add 3F 4.1f");
		Debug.log("返回值: " + ret);
		Debug.log();
		executor.executeCommand("sub 3000000000L 5");
		Debug.log();
		executor.executeCommand("sayHello");
		Debug.log();
		executor.executeCommand("staticHello");
	}
}

