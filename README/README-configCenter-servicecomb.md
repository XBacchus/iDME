# 使用ServiceComb作为数据建模引擎运行SDK的配置中心

本文档指导您如何将ServiceComb作为工业数字模型驱动引擎（Industrial Digital Model Engine，简称iDME）-数据建模引擎运行SDK的远程配置中心，实现配置集中化管理与动态更新，支撑应用程序的高效部署与运维。

数据建模引擎运行SDK已内置集成HuaWei开源的ServiceComb，支持ServiceComb全部标准配置功能。

## 准备事项

在开始部署前，请确保完成以下准备工作：

-   获取ServiceComb配置中心的连接地址。

- 已结合实际部署环境，参考对应文档完成数据建模引擎运行SDK部署。

    - 本地服务器部署：请参见[在本地通用服务器中部署数据建模引擎运行SDK](README-xdm-machine.md)
    - 容器化部署：请参见[在容器中部署数据建模引擎运行SDK](README-xdm-container.md)
    - 已有项目集成：请参见[在已有项目中部署数据建模引擎运行SDK](README-xdm-project.md)


## 操作步骤
1.  根据实际部署方式，在“application.properties”配置文件所在目录中，创建一个“bootstrap.properties”配置文件，并添加如下配置信息。
    ```properties
    # 指定配置中心类型为ServiceComb
    xdm.runtime.config-center.type=servicecomb
    # 以下为开源ServiceComb标准配置项，可参考HuaWei ServiceComb官方文档进行调整
    spring.cloud.servicecomb.config.server-type=kie
    spring.cloud.servicecomb.config.server-addr=http://127.0.0.1:30110
    spring.cloud.servicecomb.config.kie.custom-label=testcustomlabel
    spring.cloud.servicecomb.config.kie.custom-label-value=testcustomlabelvalue
    ```

2. 将“application.properties”配置文件中的相关配置，按需配置到ServiceComb配置中心。

   远程配置中心配置项优先级最高。若远程配置中心配置项与本地“application.properties”配置项重复时，以配置中心为准。

    - 如使用**华为云微服务引擎CSE的ServiceComb服务**，请参考[微服务引擎 CSE ServiceComb引擎专享版](https://support.huaweicloud.com/usermanual-cse/cse_03_0049.html)进行相关配置。
    - 如使用**自建ServiceComb集群**，请参考ServiceComb官方文档完成部署与配置。

3. 根据实际部署方式，重启数据建模引擎运行SDK。

