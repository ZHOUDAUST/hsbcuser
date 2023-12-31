# A simple authentication and authorization service
## 主要实现的功能
项目实现了对用户、角色、用户角色绑定、token生成以及验证功能，细分功能点如下：
- 用户创建、删除
- 角色创建、删除
- 给用户赋予角色
- 加密token生成
- 根据token验证角色
- 根据token查询用户所有角色
- token定期失效管理功能  

值得注意的是，除了实现基础的题目要求之外，**本项目还在规范上做了优化，详情见本文最后一栏"加分功能点"**。

## 用到的技术栈
- springcloud 2021.0.1 version + springboot 2.7.5 version
- maven
- java8
- spring定时任务

## 用到的jdk之外的依赖
可以在pom文件中找到对应依赖，分别是：
- hutool-all hutool工具，本项目用作处理token的json格式化
- spring-security-crypto 用作处理密码的hash加密与验证
- 其他springboot框架自带依赖

## 项目结构
│ ├── hsbcuser  
│ │ ├── annotation api版本号管理  
│ │ ├── UserRoleFeignApi.java 提供外部调用的restful api  
│ │ ├── UserRoleController.java mvc controller用来逻辑控制  
│ │ ├── dto 数据传输层对象 mvc view  
│ │ ├── pojo 数据存储层对象 mvc model  
│ │ ├── TokenExpireTask.java token过期检验定时任务  
│ │ ├── UserRoleService.java 提供具体用户服务  
│ │ ├── PasswordEncryptUtil.java 提供密码加密与验证  
│ │ ├── TokenGenerator.java 提供加密的token生成与解密

## token过期策略
- 通过接口触发验证失效token
- 手动Invalidate
- 定时任务定期扫描过期token并删除

![img_1.png](img_1.png)

## 单元测试
覆盖率已达100%，用例请参照：
- UserRoleServiceTest：负责用户角色权限service的方法测试
- TokenExpireTaskTest：负责token定时过期删除的方法测试

## 加分功能点
然而，作者认为除实现基本功能之外，还有加分功能点：
- api版本号管理
- spring-logback日志管理