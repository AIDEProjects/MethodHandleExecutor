# MethodHandleExecutor
自动指令器，输入字符串自动解析并执行目标类方法句柄

# V0.4.0

## 更新条目
- 增加Debug类用于统一log输出方式
    - boolean debug用于控制是否输出调试信息，默认false
- 增加Project类
    - 存放一个版本号string VERSION为"0.4.0"
---
- 一个样品类用于提供公开方法
    - 将print统一为为Debug.log
- 公开句柄执行器
    - 取消了多个方法多余的throws声明，不需要try-catch也可使用
    - methodHandles修改为方法签名(name与paramList)->handle形式
    - 初始化时通过method构建methodKey并存储
- execCommand
    - 重写解析参数类型方法为Class parseParamType(Object[] outArg)
        - 使用数组传入引用型参数，使用正则匹配分别解析字符串参数为所有基本数据类型，输出到outArg并返回解析出的类型Class
    - 使用parseParamType遍历解析指令参数后获取methodKey从而获取handle
    - 最后使用得到的handle与args调用目标方法句柄
- examples.Main 演示入口
    - 测试所有参数类型并通过
    - 方法重载通过
## 问题记录
1. 取消throws也许不是好的，因为该类可能抛出异常
