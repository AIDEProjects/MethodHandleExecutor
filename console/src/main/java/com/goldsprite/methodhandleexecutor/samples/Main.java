package com.goldsprite.methodhandleexecutor.samples;
import com.goldsprite.methodhandleexecutor.core.*;

public class Main {
	public static void main(String[] args) {
		try {
			MyCommands myCommands = new MyCommands();
			MethodHandleExecutor executor = new MethodHandleExecutor(myCommands);

			executor.executeCommand("sayHello");
			executor.executeCommand("staticHello");
			float ret = executor.executeCommand("add 3.1 4");
			System.out.println("返回值: "+ret);
			executor.executeCommand("sub 3000000000 5");
			executor.executeCommand("sub 6.4 5");
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}
}

