# 在本地通用服务器中部署数据建模引擎运行SDK

当您希望将iDME应用以微服务的形式部署到本地通用服务器，可以更好地控制和管理自己的应用数据、减少数据传输的延迟时，可以选择此实施方式进行数据建模引擎运行SDK独立部署。后续，您还可以基于应用运行态灵活扩展，为上层应用提供使用HTTP协议进行通信的API调用能力。

## 准备事项

请在部署数据建模引擎运行SDK之前，提前获取SDK包和License文件。

如何获取SDK请参见[获取数据建模引擎SDK](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0009.html)。

## 检查SDK包（可选）

为防止数据建模引擎运行SDK包在存储、下载、传输过程中被篡改，建议在部署前检查SDK包是否完整。

您可以使用如下校验函数进行校验，当生成的文件签名与您获取的SDK包文件签名一致时，表示SDK包完整。

```
 public static String getSHA256(File file) {
        FileInputStream fileInputStream = null;
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            fileInputStream = new FileInputStream(file);
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                messageDigest.update(buffer, 0, length);
            }
            return new String(HexUtil.encodeHex(messageDigest.digest()));
        } catch (Exception ex) {
            log.error("getSHA256 error.", ex);
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException ex) {
                log.error("getSHA256 error when close inputStream.", ex);
            }
        }
}
```

其中，“MessageDigest“为“java.security“组件，“HexUtil“为“hutool“组件。

## 操作步骤

如下操作以CentOS 7.6 64位操作系统的ECS为例。

1.  [登录Linux弹性云服务器](https://support.huaweicloud.com/usermanual-ecs/zh-cn_topic_0013771089.html)。
2.  将已获取的SDK包和License文件上传至弹性云服务器，具体操作请参见[上传文件到云服务器方式概览](https://support.huaweicloud.com/ecs_faq/ecs_faq_0049.html)。
3.  执行如下命令，将SDK包解压缩到部署服务器的工作目录。

    ```
    tar -xvf {SDK包的名称} -C {部署服务器的工作目录}
    ```

    例如，将SDK包“TestApp-sdk-default“解压缩到“/root/deploytest“路径下。

    ```
    tar -xvf TestApp-sdk-default -C /root/deploytest
    ```

4.  执行如下命令，进入部署服务器的工作目录，例如“/root/deploytest“。

    ```
    cd /root/deploytest
    ```

5.  执行如下命令，打开“application.properties“文件。

    ```
    vim application.properties
    ```

6.  按**i**切换至编辑模式，并根据实际的[数据建模引擎运行SDK资源规划](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0008.html#ZH-CN_TOPIC_0000002101358320__section15530131311115)，按需添加对应资源的配置信息。

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
    
    其中，“License配置“的License文件路径需与**2**的存放路径保持一致。

7.  按**Esc**，输入**:wq**，保存文件并返回。
8.  执行如下命令，新建“startxdm.bash“脚本文件。

    ```
    vi startxdm.bash
    ```

9.  按**i**切换至编辑模式，输入以下内容，设置启动应用运行态SDK脚本。

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

10. 按**Esc**，输入**:wq**，保存文件并返回。
11. 执行如下命令，设置“startxdm.bash“脚本文件权限。

    ```
    chmod +x startxdm.bash
    ```

12. 执行如下命令，启动“startxdm.bash“脚本文件。

    ```
    ./startxdm.bash
    ```

13. 完成部署后，xDM-F支持如下几种验证方式。您可以根据实际情况选择验证。
    -   **方式一：查看服务日志**

        执行如下命令，查看服务日志。

        ```
        tail -f xdm.log
        ```

        显示结果为类似如下信息，则说明启动成功。

        ```
        Started RdmApplication in xxx.xxx seconds (JVM running for xxx.xxx)
        ```

    -   **方式二：调用指定接口**

        启动“startxdm.bash“脚本文件后，约等待2分钟，调用如下健康检查接口，查询服务是否正常启动。

        ```
        http://{部署服务器的IP地址}:{部署服务器的端口号}/rdm_{应用的唯一标识}_app/services/v1/health
        ```

        例如：

        ```
        http://127.0.0.1:8003/rdm_123456_app/services/v1/health
        ```

        显示结果为如下信息，则说明启动成功。

        ```
        {"result":"SUCCESS","data":["success"],"errors":[]}
        ```

    -   **方式三：访问可视化页面**

        如果您已在[数据建模引擎运行SDK资源规划](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0008.html#ZH-CN_TOPIC_0000002101358320__section15530131311115)中规划了单点登录的相关资源/云服务，并在部署时配置了单点登录信息，可以选择此方式进行验证。

        在浏览器中访问如下地址，查看服务是否启动成功。

        ```
        http://{部署服务器的IP地址}:{部署服务器的端口号}/rdm_{应用的唯一标识}_app/services/index.html
        ```

        例如：

        ```
        http://127.0.0.1:8003/rdm_123456_app/services/index.html
        ```

        转入应用运行态登录页面，输入登录账号和登录密码，成功登录并进入应用运行态页面，即表示启动成功。

