# MYTHS.GAEA
> 盖亚，一套基于借鉴DDD技术栈思想使用kotlin语言开发的后台服务快速开发框架。
> 框架业务采用了CQRS的设计思想，但是没有使用Event Sourcing等复杂技术（等待后续扩展）。

> 当前版本 0.5.0

## 项目结构

|-- doc
    |--
|-- src
    |-- gaea #核心库
    |-- gaea.app #应用服务库，依赖SpringBoot声明了Controller等web服务接口
    |-- gaea.mongo #使用mongo数据库实现了Repository和Query查询
    
## Package结构

|-- app #应用服务
    |-- cmd #命令服务接口
    |-- component #应用服务常用组件
    |-- query #查询服务接口
|-- data #数据结构
    |-- cache #缓存相关
    |-- code #编号相关
    |-- date #日期相关
    |-- message #消息相关
    |-- serialization #序列化相关
|-- domain  #DDD领域层定义
    |-- model #领域模型
    |-- repository #仓储定义
    |-- service #领域服务
|-- extension #一些类型常用扩展方法
|-- io  #io相关服务
    |-- comm #通信相关
    |-- scan #类扫描库
|-- log #日志相关接口
|-- mongo #Mongo相关服务
|-- query #定义了查询相关接口
