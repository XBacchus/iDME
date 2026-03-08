# 在已有项目中部署数据建模引擎运行SDK

xDM-F不仅支持以独立服务的方式部署数据建模引擎运行SDK，还支持以依赖引入的方式嵌入用户已存在的项目中，将其作为一个数据底座，提供可直接调用的xDM-F内置Service方法，减少网络IO。

如下操作以Windows 11操作系统的本地服务器为例。

## 准备事项

在部署数据建模引擎运行SDK之前，需要提前准备如下事项。

-   从[Oracle官网](http://www.oracle.com/technetwork/java/archive-139210.html)下载和安装推荐使用的JDK版本。

    推荐使用的JDK版本：JDK 8 以上版本。

-   根据开发需要，下载并安装开发工具，例如Eclipse IDE、IntelliJ IDEA等。

    本文以IntelliJ IDEA开发工具为例。

-   获取SDK包和License文件。

    如何获取SDK请参见[获取数据建模引擎SDK](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0009.html)。

## 操作步骤

1.  解压缩已获取的SDK包。
2.  将解压缩后“lib“文件夹下的所有文件引入到已有的Maven工程中。
    1.  在“resources“目录下，单击“File  \>  Project Structure“。
    2.  在弹出的窗口中，选择“Modules  \>  Dependencies“，单击“+  \>  JARs or Directories…“。
    3.  全选“lib“文件夹下的所有文件，单击“OK“。
    4.  单击“Apply“。

3.  在启动类上方添加如下注解。

    ```
    @SpringBootApplication
    @EnableAsync(proxyTargetClass = true)
    @EnableScheduling
    @ComponentScan(
        basePackages = {"com.huawei.it", "com.huawei.innovation", "com.huawei.iit", "com.huawei.opendme", "com.huawei.xdm"})
    @PropertySource(value = {"classpath:application.properties"})
    @EnableCaching
    ```

4.  在启动类中添加System.setProperty\(\)方法，设置“olc.config.path“系统属性。

    ```
    System.setProperty("olc.config.path", "D:\\workspace\\sdk-test\\src\\main\\resources\\lib\\microserviceTemplate.app-1.0.0-SNAPSHOT-shaded-small.jar!\\olc");
    ```

    其中，“D:\\\\workspace\\\\sdk-test\\\\src\\\\main\\\\resources\\\\lib\\\\microserviceTemplate.app-1.0.0-SNAPSHOT-shaded-small.jar!\\\\olc“为“lib“文件夹下“microserviceTemplate.app-1.0.0-SNAPSHOT-small.jar“中的olc所在路径。

    完整的示例代码如下：

    ```
    package com.sz;
    
    import org.springframework.boot.SpringApplication;
    import org.springframework.boot.autoconfigure.SpringBootApplication;
    import org.springframework.cache.annotation.EnableCaching;
    import org.springframework.context.annotation.ComponentScan;
    import org.springframework.context.annotation.PropertySource;
    import org.springframework.scheduling.annotation.EnableAsync;
    import org.springframework.scheduling.annotation.EnableScheduling;
    
    /**
     * 启动类
     *
     **/
    @SpringBootApplication
    @EnableAsync(proxyTargetClass = true)
    @EnableScheduling
    @ComponentScan(
        basePackages = {"com.huawei.it", "com.huawei.innovation", "com.huawei.iit", "com.huawei.opendme", "com.huawei.xdm"})
    @PropertySource(value = {"classpath:application.properties"})
    @EnableCaching
    public class SdkApplication {
    
        public static void main(String[] args) {
            SpringApplication app = new SpringApplication(SdkApplication.class);
            System.setProperty("olc.config.path", "D:\\workspace\\sdk-test\\src\\main\\resources\\lib\\microserviceTemplate.app-1.0.0-SNAPSHOT-shaded-small.jar!\\olc");
            app.run(args);
        }
    }
    ```

5.  在已有的Maven工程中，添加SDK启动配置。
    - a. 单击“SdkApplication  \>  Edit Configurations...”。
    - b. 在弹出的窗口中，单击“Environment variables”。
    - c. 在弹出的窗口中，单击“Insert”，依次添加对应资源的配置信息。

        请根据实际的[数据建模引擎运行SDK资源规划](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0008.html#ZH-CN_TOPIC_0000002101358320__section15530131311115)，按需添加。

    - d. 单击“OK“。

6.  在已有的Maven工程中，单击“run”，启动项目。
7.  完成部署后，xDM-F支持如下几种验证方式。您可以根据实际情况选择验证。
    -   **方式一：查看服务日志**

        在项目运行日志中如果存在如下类型信息，则说明启动成功。

        ```
        Started RdmApplication in xxx.xxx seconds (JVM running for xxx.xxx)
        ```

    -   **方式二：调用指定接口**

        项目启动后，约等待2分钟，调用如下健康检查接口，查询服务是否正常启动。

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

        如果您已在[数据建模引擎运行SDK资源规划](https://support.huaweicloud.com/sdkreference-idme/idme_sdkreference_0008.html#ZH-CN_TOPIC_0000002101358320__section15530131311115)中规划了单点登录的相关资源/云服务，并在项目中添加了对应的单点登录配置信息，可以选择此方式进行验证。

        在浏览器中访问如下地址，查看服务是否启动成功。

        ```
        http://{部署服务器的IP地址}:{部署服务器的端口号}/rdm_{应用的唯一标识}_app/services/index.html
        ```

        例如：

        ```
        http://127.0.0.1:8003/rdm_123456_app/services/index.html
        ```

        转入应用运行态登录页面，输入登录账号和登录密码，成功登录并进入应用运行态页面，即表示启动成功。

## 异常处理

引入SDK包下的文件到Maven工程时，可能会存在引入失败的情况。

您可以参考如下操作进行修复。

1.  右键单击引入失败的JAR包，选择“Add as Library“。
2.  在弹出的窗口中，选择“Classes“，单击“OK“。
3.  在弹出的窗口中，将“Level“设置为“Module Library“，单击“OK“。
