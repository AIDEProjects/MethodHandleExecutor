# MethodHandleExecutor
自动指令器，输入字符串自动解析并执行目标类方法句柄

# V0.3.0

## 更新条目
- 一个样品类用于提供公开方法
    - 增加3个不同参数类型的sub方法分别使用int long float与int相减
- 公开句柄执行器
    - methodHandles的value改为List形式，以解决重名方法异常的问题
    - 初始化时将同名method存储为methodName->List
    - 重构了命令解析方式，遍历以尝试从方法列表中匹配符合的方法
    - 增加了覆盖基本数据类型的参数类型转换
- 通过字符串调用公开句柄方法
    - 现在可以调用重名方法
- examples.Main 演示入口
    - 增加sub long int和sub float int的示例，但实际皆被解析为float
## 问题记录
1. 由于宽松parse方法命令可能被错误的解析为意料之外的方法
