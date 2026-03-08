# 使用Nacos作为数据建模引擎运行SDK的配置中心

本文档指导您如何将Nacos作为工业数字模型驱动引擎（Industrial Digital Model Engine，简称iDME）-数据建模引擎运行SDK的远程配置中心，实现配置集中化管理与动态更新，支撑应用程序的高效部署与运维。

数据建模引擎运行SDK已内置集成Alibaba开源的Nacos，支持Nacos全部标准配置功能。

## 准备事项
在开始部署前，请确保完成以下准备工作：

- 获取Nacos配置中心的连接地址。

- 已结合实际部署环境，参考对应文档完成数据建模引擎运行SDK部署。

    - 本地服务器部署：请参见[在本地通用服务器中部署数据建模引擎运行SDK](README-xdm-machine.md)
    - 容器化部署：请参见[在容器中部署数据建模引擎运行SDK](README-xdm-container.md)
    - 已有项目集成：请参见[在已有项目中部署数据建模引擎运行SDK](README-xdm-project.md)


## 操作步骤

1. 根据实际部署方式，在“application.properties”配置文件所在目录中，创建一个“bootstrap.properties”配置文件，并添加如下配置信息。
      ```properties
    # 指定配置中心类型为 Nacos
    xdm.runtime.config-center.type=nacos
    # 以下为开源Nacos标准配置项，可参考Nacos官方文档进行调整
    ## Nacos服务器地址，格式为IP:Port，默认端口为8848。
    spring.cloud.nacos.config.server-addr=127.0.0.1:8848
    ## 配置分组，默认使用DEFAULT_GROUP，可根据业务自定义。
    spring.cloud.nacos.config.group=DEFAULT_GROUP
    ## 配置的Data ID名称，用于标识配置文件，需与Nacos中配置的Data ID一致
    spring.cloud.nacos.config.name=testconfig
    # 如启用Nacos权限控制，需配置具有读取权限的账号凭证
    spring.cloud.nacos.config.username=username
    spring.cloud.nacos.config.password=password
    ```

2. 将“application.properties”配置文件中的相关配置，按需配置到Nacos配置中心。

   远程配置中心配置项优先级最高。若远程配置中心配置项与本地“application.properties”配置项重复时，以配置中心为准。

    - 如使用**华为云微服务引擎CSE的Nacos服务**，请参考[微服务引擎 CSE注册配置中心](https://support.huaweicloud.com/usermanual-cse/cse_03_0063.html)进行相关配置。
    - 如使用**自建Nacos集群**，请参考[Nacos 官方文档](https://nacos.io/)完成部署与配置。

3. 根据实际部署方式，重启数据建模引擎运行SDK。

