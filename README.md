# MethodHandleExecutor
自动指令器，输入字符串自动解析并执行目标类方法句柄

# V0.1.0

## 更新条目
- 一个样品类用于提供公开方法
    - sayHello 实例方法
    - staticHello 静态方法
    - add(int 数字1, int 数字2) 有参有返回方法
- 创建目标类型的公开句柄执行器
    - getMethodHandle 使用lookup从目标类型与方法获取方法句柄
    - executeCommand 获取对应方法句柄并转换参数以调用
    - parseArgument 将String参数转换为给定的paramType类型
- 通过字符串调用公开句柄方法

## 现有问题
1. isStatic的判断根据参方法参数长度以及target实例equals来判定，在target为null时调用实例方法将错误判断为静态导致异常
