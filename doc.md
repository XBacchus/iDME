在Windows服务器中部署数据建模引擎运行SDK
更新时间：2025-10-24 GMT+08:00
查看PDF
分享
当您希望将iDME应用以微服务的形式部署到Windows服务器中，可以更好地控制和管理自己的应用数据、减少数据传输的延迟时，可以选择此实施方式进行数据建模引擎运行SDK独立部署。后续，您还可以基于应用运行态灵活扩展，为上层应用提供使用HTTP协议进行通信的API调用能力。

准备事项
请在部署数据建模引擎运行SDK之前，提前获取SDK包和License文件。

如何获取SDK请参见获取数据建模引擎SDK。

检查SDK包（可选）
为防止数据建模引擎运行SDK包在存储、下载、传输过程中被篡改，建议在部署前检查SDK包是否完整。

您可以使用如下校验函数进行校验，当生成的文件签名与您获取的SDK包文件签名一致时，表示SDK包完整。

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

其中，“MessageDigest”为“java.security”组件，“HexUtil”为“hutool”组件。

操作步骤
如下操作以Windows 64位操作系统为例。

登录Windows弹性云服务器。
将已获取的SDK包和License文件上传至Windows文件系统指定目录，如“D:\opt\cloud”。
进入“D:\opt\cloud”目录，解压已上传的SDK包。
在该目录（“D:\opt\cloud”）下，创建“application.properties”配置文件。
编辑“application.properties”文件，并根据实际的数据建模引擎运行SDK资源规划，按需添加对应资源的配置信息。
下方内容以数据建模引擎运行SDK通用配置和部分数据建模引擎运行SDK自定义配置为源进行处理。

以Postgresql数据库为基础的本地服务单点登录为例，以下为对应的环境变量配置示例：
## 数据库配置
# 数据库IP地址
RDS_IP=127.0.0.1
# 数据库使用端口
RDS_PORT=5432
# 数据库账号
RDS_NAME=root
# 数据库密码
RDS_PASSWORD=123456
# 数据库名
RDS_DATABASE=sdk17
# 使用的数据库类型
RDS_TYPE=postgresql
# 数据库连接url
spring.datasource.url=jdbc:postgresql://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}?stringtype=unspecified
# 数据库使用的连接驱动
spring.datasource.driverClassName=org.postgresql.Driver
# 数据建模引擎初始化环境使用的数据库类型
xdm.runtime.db-type=postgresql
# 数据建模引擎初始化模型使用的数据库方言
xdm.runtime.hibernate.dialect=com.huawei.it.rdm.configuration.XdmPostgresqlDialect

## SSF单点登录
# 单点登录使用的数据库类型
SSF_RDS_TYPE=postgresql
# 单点登录使用的数据库连接驱动
SSF_DRIVER=org.postgresql.Driver
# 单点登录的数据库连接信息
spring.datasource.ssf.url=jdbc:postgresql://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}_ssf

## Redis配置
# Redis数据库单点模式下的IP地址
REDIS_HOST=127.0.0.1
# Redis数据库集群模式下的IP地址
# REDIS_CLUSTER_NODES=127.0.0.1:6379,127.0.0.1:6380
# Redis数据库使用的端口
REDIS_PORT=6379
# Redis数据库密码
REDIS_PASSWORD=root
# Redis数据库的服务类型；SINGLE：单点模型，CLUSTER：集群模式
REDIS_TYPE=SINGLE

## 公共信息配置
# 数据建模引擎模型初始化配置，开启后会根据jar包中的模型信息更新数据
xdm.runtime.init=true
# 部署版本，建议和应用发布版本保持一致；同一个版本重复部署不会更新模型信息
DEPLOY_VERSION=2.25.070.01
# 当前部署应用ID
TENANT_ID=da1f9c962b264627840dd50ae9b2e1fe
# 当前部署应用名称
APP_NAME=XDMLocal
# 当前部署应用的唯一标识，一般为应用ID
APP_IDENTIFIER=da1f9c962b264627840dd50ae9b2e1fe
# 当前部署应用英文名称小写
PAAS_APP_NAME=xdmlocal
# 是否注册delegator代理器相关的bean
xdm.delegate.bean.inject=false
# 服务访问的上下文路径，格式需按照：/rdm_xxx_app/services/ 进行设置，xxx建议使用应用id
server.servlet.context-path=/rdm_da1f9c962b264627840dd50ae9b2e1fe_app/services/
# 服务设置的端口
server.port=8003
# 应用信息，当前配置使用环境变量进行填充；按需可以在设计态获取进行手动填充
xdm.runtime.tenant.info={"id":"${TENANT_ID}","creator":"test1 3c03e719256a427eb9277b64fc83fb40","createTime":"2022-07-19T12:02:45.000+0000","modifier":"test1 3c03e719256a427eb9277b64fc83fb40","lastModifiedTime":"2022-07-19T12:02:45.000+0000","markForDelete":false,"name":"${APP_NAME}","nameEn":"${APP_NAME}","description":"${APP_NAME}","descriptionEn":"","owner":"lundefined","shortName":"${APP_NAME}","codeRepository":"","dbConn":null,"dbReadOnlyConn":null,"hisDeployUnit":null,"databaseType":"postgresql","aliasName":"${APP_NAME}","hisAppId":"","tps":1000,"qps":1000,"operationTime":null,"appNameEn":"${APP_NAME}","certifiedDataSourceName":null,"certifiedDataSourceNumber":null,"integrationMode":"API","domain":null,"appEnvInfo":"dev","metadataSynchronization":false,"codeDownloadPath":"","jarDownloadPath":"","appStatus":1,"domainId":"ac15e3fa2b1d499788d2538044961bd0","domainName":null,"identifier":"${APP_NAME}","deployedStatus":true,"damintegrated":false,"xdmhosted":false,"classname":"com.huawei.it.rdm.tenantmgmt.bean.Application"}
# 应用运行态标识，格式为：rdm_{应用英文名称小写}_app
xdm.application.sub-app-id=rdm_xdmlocal_app

# 事务协调器开关，此项配置依赖xdm-tx-distributor.jar包的独立部署
xdm.tx-distributor.enable=true
# scc组件路径，需要置空
devspore.security.provider.scc.tools-dir=

## SDK许可证配置
# 是否为SDK部署标识，用于开放SDK的特有接口
XDM_SDK_DEPLOY_ENABLE=true

# license管理开关，开启后可以在web页面中管理license
xdm.runtime.license-control.enable=true
# SDK包部署方式，目前只支持主机(MACHINE)和容器(CONTAINER)部署
xdm.license.deploy-type=MACHINE
# license的校验方式
xdm.license.runtime.valid-type=LICENSE
# 许可证的初始化方式
xdm.license.init-type=SYSTEM_PATH
# license文件所在的位置
xdm.license.licenseFile=/opt/cloud/license.dat
# license解析所需密钥，申请license时获取
xdm.license.public-key=iDME SDK:1:5:A9D150C6F***

## 服务编排配置
# 服务编排全局开关
#CUSTOM_SERVICE_GLOBAL_SWITCH=true
## 服务编排管理开关
#CUSTOM_SERVICE_MANAGEABLE=true
## 服务编排是否可编辑开关
#CUSTOM_SERVICE_MODIFIABLE=true
## 服务编排是否可执行开关
#CUSTOM_SERVICE_EXECUTABLE=true
## 是否开启java服务编排
#OPEN_JAVA_CUSTOM_SERVICE=true
## java服务编排的编译路径
#xdm.runtime.java-compile.lib-path=/opt/cloud/java-compile

## MongoDB配置
# 是否开启MongoDB
# xdm.mongodb.init=true
## MongoDB的链接地址
#MONGODB_URI=mongodb://rwuser:root@127.0.0.1:8635/test?authSource=admin
## 使用的MongoDB数据库名
#MONGODB_DATABASE=test

## Elasticsearch配置
## Elasticsearch的IP端口信息
#ES_URL=192.168.0.54:9200
## Elasticsearch的用户名
#ES_USERNAME=admin
## Elasticsearch的密码
#ES_PASSWORD=admin
## Elasticsearch的类型
#ES_SCHEMA=http

## Kafka配置
## 是否开启Kafka
#xdm.kafka.init=true
## Kafka的连接地址
#KAFKA_BOOTSTRAP_SERVERS=127.0.0.1:9094

## 文件服务配置，在OBS配置和S3配置中进行选择
## #
## OBS服务配置
## OBS服务所在的地址
#OBS_END_POINT=https://obs.cn-north-4.myhuaweicloud.com
## OBS服务使用的桶名
#BUCKET_NAME=test
## 访问OBS使用的AK密钥
#RES_AK=res_ak1
## 访问OBS服务使用的SK密钥
#RES_SK=res_ak1
## #
## S3配置
## 对象存储服务的类型
#xdm.objectstorage.type=S3
## S3服务所在地址
#xdm.objectstorage.s3.host=127.0.0.1:9000
## S3服务使用的桶名
#BUCKET_NAME=test
## S3服务的账号
#xdm.objectstorage.s3.ak=admin
## S3服务的密码
#xdm.objectstorage.s3.sk=123456
###

## 部分特性配置
## 二级缓存功能开关
#OPEN_SECOND_LEVEL_CACHE=false
## 三级缓存开关
#THIRD_LEVEL_REDIS_CLOSED=false
## 设置是否开启多数据源功能
#DYNAMIC_DATASOURCE_ENABLED=false

## WSF组件配置
## 数据建模引擎全局配置开关开关
#dme.wsf.enable=true
## WSF组件开关
#devspore.security.wsf.enable=true
## WSF参数校验器开关
#dme.wsf.param.check.enable=true
## WSF自定义参数校验配置文件，指向目录，目录下需要有validate文件夹
#dme.wsf.param.config.path=/opt/cloud
## WSF文件上传校验开关
#dme.wsf.multipart.check.enable=true
## WSF自定义问价上传配置
#dme.wsf.upload.config.path=/opt/cloud/CustomUploadFileConfig.properties
## WSF csrf校验开关
#dme.wsf.csrf.check.enable=true

## 日志配置
## WSF框架日志等级
#WSF_LOG_LEVEL=INFO
## SSF框架日志等级
#SSF_LOG_LEVEL=INFO

其中，“License配置”的License文件路径需与2的存放路径保持一致。

如果当前使用的是MySQL数据库，则需要在PostgreSQL环境变量的基础上进行以下配置调整：

# # 数据库配置
# 数据库IP地址
RDS_IP=127.0.0.1
# 数据库使用端口
RDS_PORT=3306
# 数据库账号
RDS_NAME=root
# 数据库密码
RDS_PASSWORD=123456
# 数据库名
RDS_DATABASE=sdk17
# 使用的数据库类型
RDS_TYPE=mysql
# 数据库连接url
spring.datasource.url=jdbc:mariadb://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}
# 数据库使用的连接驱动
spring.datasource.driverClassName=org.mariadb.jdbc.Driver
# 数据建模引擎初始化环境使用的数据库类型
xdm.runtime.db-type=mysql
# 数据建模引擎初始化模型使用的数据库方言
xdm.runtime.hibernate.dialect=com.huawei.it.rdm.configuration.XdmMySqlDialect

# # SSF单点登录
# 单点登录使用的数据库类型
SSF_RDS_TYPE=mysql
# 单点登录使用的数据库连接驱动
SSF_DRIVER=org.mariadb.jdbc.Driver
# 单点登录的数据库连接信息
# ${RDS_DATABASE}_ssf数据库，需要手动执行"set global log_bin_trust_function_creators=true;"，避免登录服务初始化失败
spring.datasource.ssf.url=jdbc:mariadb://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}_ssf

如果当前使用的是GaussDB数据库，则需要在PostgreSQL环境变量的基础上进行以下配置调整：

## 数据库配置
# 数据库IP地址
RDS_IP=127.0.0.1
# 数据库使用端口
RDS_PORT=8000
# 数据库账号
RDS_NAME=root
# 数据库密码
RDS_PASSWORD=123456
# 数据库名
RDS_DATABASE=sdk17
# 使用的数据库类型
RDS_TYPE=gaussdb
# 数据库连接url
spring.datasource.url=jdbc:opengauss://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}?stringtype=unspecified&sslmode=require&batchMode=off&reWriteBatchedInserts=true
# 数据库使用的连接驱动
spring.datasource.driverClassName=com.huawei.opengauss.jdbc.Driver
# 数据建模引擎初始化环境使用的数据库类型
xdm.runtime.db-type=gaussdb
# 数据建模引擎初始化模型使用的数据库方言
xdm.runtime.hibernate.dialect=com.huawei.it.rdm.configuration.XdmGaussDbDialect

## SSF单点登录
# 单点登录使用的数据库类型
SSF_RDS_TYPE=opengauss
# 单点登录使用的数据库连接驱动
SSF_DRIVER=com.huawei.opengauss.jdbc.Driver
# 单点登录的数据库连接信息
spring.datasource.ssf.url=jdbc:opengauss://${RDS_IP}:${RDS_PORT}/${RDS_DATABASE}_ssf
spring.datasource.ssf.mapperLocations=classpath*:ssf/mapper_gauss/**/*.xml

保存编辑后的配置文件并返回上级目录。
在该目录（“D:\opt\cloud”）下，创建“startxdm.bat”脚本文件。
编辑“startxdm.bat”脚本文件，输入以下内容，设置启动应用运行态SDK脚本。
@echo off
:: 指定启动类
set START_CLASS=com.huawei.microservice.rdm.RdmApplication
:: 指定启动类所在路径，为lib目录下
set LOAD_CLASSPATH=./lib/*;./
:: 从部署包中解压出olc配置文件，加粗的字段需要根据实际包名填写，请确保替换为正确的包名(jar包位于lib目录下)
jar xvf ./lib/microserviceTemplate.app-1.0.0-SNAPSHOT-shaded-small.jar olc
:: JDK17执行以下Java命令启动服务，也可以在后面添加>xdm.log 2>&1 &参数，以后台方式运行，并将日志输出到xdm.log日志中
java -Xms8g -Xmx8g --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/javax.crypto=ALL-UNNAMED --add-opens java.base/sun.security.util=ALL-UNNAMED --add-opens java.base/sun.security.x509=ALL-UNNAMED --add-opens java.base/sun.security.pkcs=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED -Dolc.config.path=./olc -Dfile.encoding=UTF-8 -classpath "%LOAD_CLASSPATH%" %START_CLASS%

保存编辑后的配置文件并返回上级目录。
双击“startxdm.bat”脚本文件，启动应用部署。
完成部署后，xDM-F支持如下几种验证方式。您可以根据实际情况选择验证。
方式一：查看服务日志
打开xdm.log文件，查看服务日志。如果服务日志中存在类似如下信息，则说明启动成功。

Started RdmApplication in xxx.xxx seconds (JVM running for xxx.xxx)

方式二：调用指定接口
启动“startxdm.bat”脚本文件后，约等待2分钟，调用如下健康检查接口，查询服务是否正常启动。

http://{部署服务器的IP地址}:{部署服务器的端口号}/rdm_{应用的唯一标识}_app/services/v1/health

例如：

http://127.0.0.1:8003/rdm_123456_app/services/v1/health

显示结果为如下信息，则说明启动成功。

{"result":"SUCCESS","data":["success"],"errors":[]}

方式三：访问可视化页面
如果您已在数据建模引擎运行SDK资源规划中规划了单点登录的相关资源/云服务，并在部署时配置了单点登录信息，可以选择此方式进行验证。

在浏览器中访问如下地址，查看服务是否启动成功。

http://{部署服务器的IP地址}:{部署服务器的端口号}/rdm_{应用的唯一标识}_app/services/index.html

例如：

http://127.0.0.1:8003/rdm_123456_app/services/index.html

转入应用运行态登录页面，输入登录账号和登录密码，成功登录并进入应用运行态页面，即表示启动成功。