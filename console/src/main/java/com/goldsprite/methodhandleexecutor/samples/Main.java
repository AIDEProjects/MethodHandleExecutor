package com.goldsprite.methodhandleexecutor.samples;
import com.goldsprite.methodhandleexecutor.core.*;

public class Main {
    public static void main(String[] args) {
        try {
            MyCommands myCommands = new MyCommands();
            MethodHandleExecutor executor = new MethodHandleExecutor(MyCommands.class);

            executor.executeCommand(myCommands, "sayHello");
            executor.executeCommand(myCommands, "staticHello");
			Object ret = executor.executeCommand(myCommands, "add 3 4");
			float retNum = (float)ret;
			System.out.println("返回值: "+retNum);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}

