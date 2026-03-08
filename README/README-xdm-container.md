# 在容器中部署数据建模引擎运行SDK

容器是操作系统内核自带能力，是基于Linux内核实现的轻量级高性能资源隔离机制，可以帮助应用程序快速、可靠、一致地部署，不受部署环境的影响。

本文指导您将工业数字模型驱动引擎（Industrial Digital Model Engine，简称iDME）的数据建模引擎运行SDK部署到容器中。您不需要改动任何代码和架构，仅需将整体数据建模引擎运行SDK构建为镜像，即可部署到容器中。

## 准备事项

-   创建一个配置文件，添加对应资源的配置信息。例如“application.properties“。

    如下内容以[数据建模引擎运行SDK通用配置](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0012.html)和部分[数据建模引擎运行SDK自定义配置](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0013.html)为例。

    - PostgreSQL数据库环境变量示例
    
    ```properties
    # 数据库配置
    RDS_IP=127.0.0.1
    RDS_PORT=5432
    RDS_PASSWORD=root
    RDS_TYPE=postgresql
    RDS_NAME=postgres
    RDS_DATABASE=sdk17
    spring.datasource.url=jdbc:postgresql://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}?stringtype=unspecified
    spring.datasource.driverClassName=org.postgresql.Driver
    xdm.runtime.db-type=postgresql
    xdm.runtime.hibernate.dialect=com.huawei.it.rdm.configuration.XdmPostgresqlDialect
    
    # 本地SSF单点登录配置，需要提前在数据库中创建名为${RDS_DATABASE}_ssf的数据库
    SSF_RDS_TYPE=postgresql
    SSF_DRIVER=org.postgresql.Driver
    spring.datasource.ssf.url=jdbc:postgresql://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}_ssf
    
    # Redis配置
    REDIS_HOST=127.0.0.1
    REDIS_PASSWORD=root
    REDIS_PORT=6379
    REDIS_TYPE=SINGLE
    
    # 服务编排配置
    # 服务编排全量功能可用，默认值false
    CUSTOM_SERVICE_GLOBAL_SWITCH=true
    # 服务编排是否可管理，默认值false
    CUSTOM_SERVICE_MANAGEABLE=true
    # 服务编排是否可修改，默认值false
    CUSTOM_SERVICE_MODIFIABLE=true
    # 服务编排是否可执行，默认值false
    CUSTOM_SERVICE_EXECUTABLE=true
    # 是否开启java编排，默认值false
    OPEN_JAVA_CUSTOM_SERVICE=true
    # 是否支持服务编排java类白名单，默认值true
    CUSTOM_CODE_WHITENABLE=true
    # java服务编排编译路径
    xdm.runtime.java-compile.lib-path=/root/deploytest/lib
    
    # 公共信息配置
    # 开启运行态初始化，用于刷新数据
    xdm.runtime.init=true
    # 部署jar包版本号，若版本相同，重复启动不会刷新数据
    DEPLOY_VERSION=2.25.070.01
    # 应用id
    TENANT_ID=da1f9c962b264627840dd50ae9b2e1fe
    # 应用名称
    APP_NAME=XDMLocal
    # 应用唯一标识，默认使用应用id的值
    APP_IDENTIFIER=da1f9c962b264627840dd50ae9b2e1fe
    # 应用名称小写
    PAAS_APP_NAME=xdmlocal
    # scc配置，sdk下不涉及scc，置空
    devspore.security.provider.scc.tools-dir=
    # 运行态全量数据服务配置，用于组装url，填写格式http://ip:port
    APIG_DEFAULT_GROUP_HOST=http://127.0.0.1:8003
    olc.config.path=olc
    xdm.delegate.bean.inject=false
    server.servlet.context-path=/rdm_da1f9c962b264627840dd50ae9b2e1fe_app/services/
    server.port=8003
    # 应用信息
    tenant.info={"id":"${TENANT_ID}","creator":"test1 3c03e719256a427eb9277b64fc83fb40","createTime":"2022-07-19T12:02:45.000+0000","modifier":"test1 3c03e719256a427eb9277b64fc83fb40","lastModifiedTime":"2022-07-19T12:02:45.000+0000","markForDelete":false,"name":"${APP_NAME}","nameEn":"${APP_NAME}","description":"${APP_NAME}","descriptionEn":"","owner":"lundefined","shortName":"${APP_NAME}","codeRepository":"","dbConn":null,"dbReadOnlyConn":null,"hisDeployUnit":null,"databaseType":"postgresql","aliasName":"${APP_NAME}","hisAppId":"","tps":1000,"qps":1000,"operationTime":null,"appNameEn":"${APP_NAME}","certifiedDataSourceName":null,"certifiedDataSourceNumber":null,"integrationMode":"API","domain":null,"appEnvInfo":"dev","metadataSynchronization":false,"codeDownloadPath":"","jarDownloadPath":"","appStatus":1,"domainId":"ac15e3fa2b1d499788d2538044961bd0","domainName":null,"identifier":"${APP_NAME}","deployedStatus":true,"damintegrated":false,"xdmhosted":false,"classname":"com.huawei.it.rdm.tenantmgmt.bean.Application"}
    
    # SDK许可证配置
    XDM_SDK_DEPLOY_ENABLE=true
    xdm.license.licenseFile=license.dat
    xdm.license.deployType=MACHINE
    xdm.license.runtime.valid-type=LICENSE
    xdm.license.init-type=SYSTEM_PATH
    # license公钥，在申请的license邮件中获取
    xdm.license.public-key=iDME SDK:1:5 XXXX
    
    # OBS 配置
    BUCKET_NAME=test
    OBS_END_POINT=https://obs.cn-north-4.huawei.com
    RES_AK=testak
    RES_SK=testsk
    
    # Elasticsearch配置，可选配置
    # ES_USERNAME=admin
    # ES_URL=192.168.0.54:9200
    # ES_PASSWORD=admin
    # ES_SCHEMA=http
    # ES_SYNC_ENABLE=true
    
    # MongoDB配置，可选配置
    # mongodb.init=false
    # MONGODB_URI=mongodb://rwuser:root@127.0.0.1:8635/test?authSource=admin
    # MONGODB_DATABASE=test
    
    # DEW(KMS)配置
    # KMS_END_POINT=https://kms.cn-north-4.myhuaweicloud.com
    # KMS_KEY_ID=keyid
    
    # AOM配置
    # AOM_END_POINT=https://aom.cn-north-4.myhuaweicloud.com
    # AOM_CLUSTER_ID=clusterid
    ```
    
    - MySQL数据库环境变量示例
    
    ```properties
    # 数据库配置
    RDS_IP=127.0.0.1
    RDS_PORT=3306
    RDS_PASSWORD=root
    RDS_TYPE=mysql
    RDS_NAME=root
    RDS_DATABASE=sdk17
    spring.datasource.url=jdbc:mariadb://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}
    spring.datasource.driverClassName=org.mariadb.jdbc.Driver
    xdm.runtime.db-type=mysql
    xdm.runtime.hibernate.dialect=com.huawei.it.rdm.configuration.XdmMySqlDialect
    
    # 本地SSF单点登录配置，需要提前在数据库中创建名为${RDS_DATABASE}_ssf的数据库
    SSF_RDS_TYPE=mysql
    SSF_DRIVER=org.mariadb.jdbc.Driver
    spring.datasource.ssf.url=jdbc:mariadb://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}_ssf
    
    # Redis配置
    REDIS_HOST=127.0.0.1
    REDIS_PASSWORD=root
    REDIS_PORT=6379
    REDIS_TYPE=SINGLE
    
    # 服务编排配置
    # 服务编排全量功能可用，默认值false
    CUSTOM_SERVICE_GLOBAL_SWITCH=true
    # 服务编排是否可管理，默认值false
    CUSTOM_SERVICE_MANAGEABLE=true
    # 服务编排是否可修改，默认值false
    CUSTOM_SERVICE_MODIFIABLE=true
    # 服务编排是否可执行，默认值false
    CUSTOM_SERVICE_EXECUTABLE=true
    # 是否开启java编排，默认值false
    OPEN_JAVA_CUSTOM_SERVICE=true
    # 是否支持服务编排java类白名单，默认值true
    CUSTOM_CODE_WHITENABLE=true
    # java服务编排编译路径
    xdm.runtime.java-compile.lib-path=/root/deploytest/lib
    
    # 公共信息配置
    # 开启运行态初始化，用于刷新数据
    xdm.runtime.init=true
    # 部署jar包版本号，若版本相同，重复启动不会刷新数据
    DEPLOY_VERSION=2.25.070.01
    # 应用id
    TENANT_ID=da1f9c962b264627840dd50ae9b2e1fe
    # 应用名称
    APP_NAME=XDMLocal
    # 应用唯一标识，默认使用应用id的值
    APP_IDENTIFIER=da1f9c962b264627840dd50ae9b2e1fe
    # 应用名称小写
    PAAS_APP_NAME=xdmlocal
    # scc配置，sdk下不涉及scc，置空
    devspore.security.provider.scc.tools-dir=
    # 运行态全量数据服务配置，用于组装url，填写格式http://ip:port
    APIG_DEFAULT_GROUP_HOST=http://127.0.0.1:8003
    olc.config.path=olc
    xdm.delegate.bean.inject=false
    server.servlet.context-path=/rdm_da1f9c962b264627840dd50ae9b2e1fe_app/services/
    server.port=8003
    # 应用信息
    tenant.info={"id":"${TENANT_ID}","creator":"test1 3c03e719256a427eb9277b64fc83fb40","createTime":"2022-07-19T12:02:45.000+0000","modifier":"test1 3c03e719256a427eb9277b64fc83fb40","lastModifiedTime":"2022-07-19T12:02:45.000+0000","markForDelete":false,"name":"${APP_NAME}","nameEn":"${APP_NAME}","description":"${APP_NAME}","descriptionEn":"","owner":"lundefined","shortName":"${APP_NAME}","codeRepository":"","dbConn":null,"dbReadOnlyConn":null,"hisDeployUnit":null,"databaseType":"postgresql","aliasName":"${APP_NAME}","hisAppId":"","tps":1000,"qps":1000,"operationTime":null,"appNameEn":"${APP_NAME}","certifiedDataSourceName":null,"certifiedDataSourceNumber":null,"integrationMode":"API","domain":null,"appEnvInfo":"dev","metadataSynchronization":false,"codeDownloadPath":"","jarDownloadPath":"","appStatus":1,"domainId":"ac15e3fa2b1d499788d2538044961bd0","domainName":null,"identifier":"${APP_NAME}","deployedStatus":true,"damintegrated":false,"xdmhosted":false,"classname":"com.huawei.it.rdm.tenantmgmt.bean.Application"}
    
    # SDK许可证配置
    XDM_SDK_DEPLOY_ENABLE=true
    xdm.license.licenseFile=license.dat
    xdm.license.deployType=MACHINE
    xdm.license.runtime.valid-type=LICENSE
    xdm.license.init-type=SYSTEM_PATH
    # license公钥，在申请的license邮件中获取
    xdm.license.public-key=iDME SDK:1:5 XXXX
    
    # OBS 配置
    BUCKET_NAME=test
    OBS_END_POINT=https://obs.cn-north-4.huawei.com
    RES_AK=testak
    RES_SK=testsk
    
    # Elasticsearch配置，可选配置
    # ES_USERNAME=admin
    # ES_URL=192.168.0.54:9200
    # ES_PASSWORD=admin
    # ES_SCHEMA=http
    # ES_SYNC_ENABLE=true
    
    # MongoDB配置，可选配置
    # mongodb.init=false
    # MONGODB_URI=mongodb://rwuser:root@127.0.0.1:8635/test?authSource=admin
    # MONGODB_DATABASE=test
    
    # DEW(KMS)配置
    # KMS_END_POINT=https://kms.cn-north-4.myhuaweicloud.com
    # KMS_KEY_ID=keyid
    
    # AOM配置
    # AOM_END_POINT=https://aom.cn-north-4.myhuaweicloud.com
    # AOM_CLUSTER_ID=clusterid
    ```

    ```properties
    # 高斯数据库配置
    RDS_IP=127.0.0.1
    RDS_PORT=8000
    RDS_PASSWORD=root
    RDS_TYPE=gaussdb
    RDS_NAME=postgres
    RDS_DATABASE=sdk17
    spring.datasource.url=jdbc:opengauss://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}?stringtype=unspecified&sslmode=require&batchMode=off&reWriteBatchedInserts=true
    spring.datasource.driverClassName=com.huawei.opengauss.jdbc.Driver
    xdm.runtime.db-type=gaussdb
    xdm.runtime.hibernate.dialect=com.huawei.it.rdm.configuration.XdmGaussDbDialect
    # 自动给关键字加"
    hibernate.auto_quote_keyword=true
    
    # 本地SSF单点登录配置，需要提前在数据库中创建名为${RDS_DATABASE}_ssf的数据库
    SSF_RDS_TYPE=opengauss
    SSF_DRIVER=com.huawei.opengauss.jdbc.Driver
    spring.datasource.ssf.url=jdbc:opengauss://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}_ssf?stringtype=unspecified&sslmode=require&batchMode=off&reWriteBatchedInserts=true
    spring.datasource.ssf.mapperLocations=classpath*:ssf/mapper_gauss/**/*.xml

    # Redis配置
    REDIS_HOST=127.0.0.1
    REDIS_PASSWORD=root
    REDIS_PORT=6379
    REDIS_TYPE=SINGLE
    
    # 服务编排配置
    # 服务编排全量功能可用，默认值false
    CUSTOM_SERVICE_GLOBAL_SWITCH=true
    # 服务编排是否可管理，默认值false
    CUSTOM_SERVICE_MANAGEABLE=true
    # 服务编排是否可修改，默认值false
    CUSTOM_SERVICE_MODIFIABLE=true
    # 服务编排是否可执行，默认值false
    CUSTOM_SERVICE_EXECUTABLE=true
    # 是否开启java编排，默认值false
    OPEN_JAVA_CUSTOM_SERVICE=true
    # 是否支持服务编排java类白名单，默认值true
    CUSTOM_CODE_WHITENABLE=true
    # java服务编排编译路径
    xdm.runtime.java-compile.lib-path=/root/deploytest/lib
    
    # 公共信息配置
    # 开启运行态初始化，用于刷新数据
    xdm.runtime.init=true
    # 部署jar包版本号，若版本相同，重复启动不会刷新数据
    DEPLOY_VERSION=2.25.070.01
    # 应用id
    TENANT_ID=da1f9c962b264627840dd50ae9b2e1fe
    # 应用名称
    APP_NAME=XDMLocal
    # 应用唯一标识，默认使用应用id的值
    APP_IDENTIFIER=da1f9c962b264627840dd50ae9b2e1fe
    # 应用名称小写
    PAAS_APP_NAME=xdmlocal
    # scc配置，sdk下不涉及scc，置空
    devspore.security.provider.scc.tools-dir=
    # 运行态全量数据服务配置，用于组装url，填写格式http://ip:port
    APIG_DEFAULT_GROUP_HOST=http://127.0.0.1:8003
    olc.config.path=olc
    xdm.delegate.bean.inject=false
    server.servlet.context-path=/rdm_da1f9c962b264627840dd50ae9b2e1fe_app/services/
    server.port=8003
    # 应用信息
    tenant.info={"id":"${TENANT_ID}","creator":"test1 3c03e719256a427eb9277b64fc83fb40","createTime":"2022-07-19T12:02:45.000+0000","modifier":"test1 3c03e719256a427eb9277b64fc83fb40","lastModifiedTime":"2022-07-19T12:02:45.000+0000","markForDelete":false,"name":"${APP_NAME}","nameEn":"${APP_NAME}","description":"${APP_NAME}","descriptionEn":"","owner":"lundefined","shortName":"${APP_NAME}","codeRepository":"","dbConn":null,"dbReadOnlyConn":null,"hisDeployUnit":null,"databaseType":"postgresql","aliasName":"${APP_NAME}","hisAppId":"","tps":1000,"qps":1000,"operationTime":null,"appNameEn":"${APP_NAME}","certifiedDataSourceName":null,"certifiedDataSourceNumber":null,"integrationMode":"API","domain":null,"appEnvInfo":"dev","metadataSynchronization":false,"codeDownloadPath":"","jarDownloadPath":"","appStatus":1,"domainId":"ac15e3fa2b1d499788d2538044961bd0","domainName":null,"identifier":"${APP_NAME}","deployedStatus":true,"damintegrated":false,"xdmhosted":false,"classname":"com.huawei.it.rdm.tenantmgmt.bean.Application"}
    
    # SDK许可证配置
    XDM_SDK_DEPLOY_ENABLE=true
    xdm.license.licenseFile=license.dat
    xdm.license.deployType=MACHINE
    xdm.license.runtime.valid-type=LICENSE
    xdm.license.init-type=SYSTEM_PATH
    # license公钥，在申请的license邮件中获取
    xdm.license.public-key=iDME SDK:1:5 XXXX
    
    # OBS 配置
    BUCKET_NAME=test
    OBS_END_POINT=https://obs.cn-north-4.huawei.com
    RES_AK=testak
    RES_SK=testsk
    
    # Elasticsearch配置，可选配置
    # ES_USERNAME=admin
    # ES_URL=192.168.0.54:9200
    # ES_PASSWORD=admin
    # ES_SCHEMA=http
    # ES_SYNC_ENABLE=true
    
    # MongoDB配置，可选配置
    # mongodb.init=false
    # MONGODB_URI=mongodb://rwuser:root@127.0.0.1:8635/test?authSource=admin
    # MONGODB_DATABASE=test
    
    # DEW(KMS)配置
    # KMS_END_POINT=https://kms.cn-north-4.myhuaweicloud.com
    # KMS_KEY_ID=keyid
    
    # AOM配置
    # AOM_END_POINT=https://aom.cn-north-4.myhuaweicloud.com
    # AOM_CLUSTER_ID=clusterid
    ```

-   创建一个脚本文件，用于启动应用运行态SDK。例如“startxdm.bash“，脚本中部分文件需要名称需要更新。

    ```shell
    # 指定启动类
    START_CLASS=com.huawei.microservice.rdm.RdmApplication
    
    # 指定启动类所在路径，为lib目录下
    LOAD_CLASSPATH=./lib/*:./
    BIND_ADDRESS=`hostname -I`
    
    # 从部署包中解压出olc配置文件，microserviceTemplate.app的版本号会不同，需要根据下载tar包中的lib里的microserviceTemplate.app的jar包进行确定
    # 如lib下为microserviceTemplate.app-2.25.060.0-shaded-small.jar需要替换下方的microserviceTemplate.app-1.0.0-SNAPSHOT-shaded-small.jar
    jar xvf ./lib/microserviceTemplate.app-1.0.0-SNAPSHOT-shaded-small.jar olc
    
    # 执行java命令，以配置文件application.properties启动服务，并把日志输入到当前目录下的xdm.log中
    sh -c "java -Xms8g -Xmx8g -Dfile.encoding=UTF-8 -Dconfig.file=/root/deploytest/application.properties -classpath $LOAD_CLASSPATH $START_CLASS --server.address=${BIND_ADDRESS} -Diit.test=true >xdm.log 2>&1 &"
    
    # 启用JDK 17应用运行态需添加如下相应JVM参数
    --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/javax.crypto=ALL-UNNAMED --add-opens java.base/sun.security.util=ALL-UNNAMED --add-opens java.base/sun.security.x509=ALL-UNNAMED --add-opens java.base/sun.security.pkcs=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED
    ```

-   获取数据建模引擎运行SDK包，具体操作请参见[获取数据建模引擎SDK](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0009.html)。
-   已获取基础容器镜像，且该镜像已安装JDK 1.8或JDK 17。

## 部署数据建模引擎运行SDK

1.  使用SSH方式登录机器。
2.  执行如下命令，检查是否安装docker。

    ```
    docker -v
    ```

    -   如果已安装docker，请执行下一步。
    -   如果未安装docker，请安装。详细操作请参见[Docker-CE](https://mirrors.huaweicloud.com/mirrorDetail/5ea14d84b58d16ef329c5c13?mirrorName=docker-ce&catalog=docker)。

3.  基于**准备事项**中已获取的基础容器镜像，并根据实际部署环境和业务场景，编写一个Dockerfile文件。

    Dockerfile文件的填写示例如下：

    ```
    FROM 基础容器镜像
    
    USER root
    COPY /构建机器目录/SDK包 /容器路径/xxx-sdk-xdm.tar
    COPY /构建机器目录/startxdm.bash /容器路径/startxdm.bash
    COPY /构建机器目录/许可证文件 /容器路径/许可证文件
    
    USER service:servicegroup
    CMD ["/容器路径/startxdm.bash"]
    ```

    其中：

    -   加粗的字段需要根据实际值填写。
    -   FROM语句：声明基础容器镜像的来源。
    -   USER语句：指定运行容器时的用户名。
    -   COPY语句：将本地机器目录下的文件复制到容器目录下。
    -   CMD语句：指定启动容器时执行的命令。

4.  执行如下命令，构建镜像。

    ```
    docker build -t <镜像名称>:<版本名称> .
    ```

5.  执行如下命令，将**准备事项**中创建的配置文件（application.properties）映射到容器。

    ```
    docker run --env-file=application.properties
    ```

6.  根据实际情况，选择如下方式，验证是否启动成功。
    -   **方式一：查看容器日志**

        执行如下命令，查看容器日志。

        ```
        docker logs
        ```

        显示结果为类似如下信息，则说明启动成功。

        ```
        Started RdmApplication in xxx.xxx seconds (JVM running for xxx.xxx)
        ```

    -   **方式二：调用指定接口**

        约等待2分钟，调用如下健康检查接口，查询服务是否正常启动。

        ```
        http://{容器的IP地址}:{SDK服务的端口号}/rdm_{应用的唯一标识}_app/services/v1/health
        ```

        例如：

        ```
        http://127.0.0.1:8003/rdm_123456_app/services/v1/health
        ```

        显示结果为如下信息，则说明启动成功。

        ```
        {"result":"SUCCESS","data":["success"],"errors":[]}
        ```

## 异常处理

**问题描述**

服务正常启动，但调用任何接口都返回如下错误信息：

```
{
    "result": "FAIL",
    "timestamp": "2024-12-04 10:33:04",
    "error_code": "LIC.60007023",
    "error_msg": "No access rights. Please check license config or file.",
    "trance_id": null
}
```

**可能原因**

此错误信息表示当前SDK未正常运行，无法提供服务。可能存在如下原因：

-   当前SDK服务的免费使用时间已到期。
-   当前SDK服务未配置License。
-   当前SDK服务配置的License已过期。
-   当前SDK服务配置License的信息填写不正确。

**处理步骤**

1.  在容器环境中，调用如下接口，获取容器的ESN。

    ```
    http://{容器的IP地址}:{SDK服务的端口号}/rdm_{应用的唯一标识}_app/services/v1/license/collect-esn
    ```

    记录显示结果中的“data“信息。

    ```
    {
        "result": "SUCCESS",
        "data": [
            "ESN"
        ],
        "errors": []
    }
    ```

2.  将**1**记录的“data“信息提供给运维工程师，获取License文件。
3.  在配置文件（例如“application.properties“）中添加[License配置](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0013.html#ZH-CN_TOPIC_0000002137019365__zh-cn_topic_0000001968767369_section1272355992312)信息。

