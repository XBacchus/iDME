@echo off
setlocal EnableExtensions EnableDelayedExpansion
cd /d "%~dp0"

if not defined JAVA_HOME (
  for /f "tokens=2*" %%A in ('reg query "HKLM\SOFTWARE\JavaSoft\JDK\17" /v JavaHome 2^>nul ^| find "JavaHome"') do set "JAVA_HOME=%%B"
)
if defined JAVA_HOME if exist "%JAVA_HOME%\bin\java.exe" set "PATH=%JAVA_HOME%\bin;%PATH%"

where java >nul 2>nul
if errorlevel 1 (
  echo [ERROR] java not found. Install JDK 17 and set PATH.
  exit /b 1
)

where jar >nul 2>nul
if errorlevel 1 (
  echo [ERROR] jar not found. Check JDK 17 installation.
  exit /b 1
)

if not exist ".\application.properties" (
  echo [ERROR] application.properties is missing.
  exit /b 1
)

if not exist ".\license.dat" (
  echo [ERROR] license.dat is missing in current directory.
  exit /b 1
)

if not exist ".\hosts_override" (
  echo [ERROR] hosts_override is missing in current directory.
  exit /b 1
)

:: Start class
set START_CLASS=com.huawei.microservice.rdm.RdmApplication
:: Classpath in lib
set LOAD_CLASSPATH=.\lib\*;.

set APP_JAR=
for /f "delims=" %%f in ('dir /b /a:-d ".\lib\microserviceTemplate.app*-shaded-small.jar" 2^>nul') do (
  set APP_JAR=.\lib\%%f
  goto :jar_found
)

echo [ERROR] .\lib\microserviceTemplate.app*-shaded-small.jar not found.
exit /b 1

:jar_found
echo [INFO] using boot jar !APP_JAR!
:: Extract olc config folder only when missing (Windows may lock files in use)
if not exist ".\olc" (
  jar xf "!APP_JAR!" olc
  if errorlevel 1 (
    echo [ERROR] failed to extract olc.
    exit /b 1
  )
) else (
  echo [INFO] existing .\olc found, skip extract.
)

:: Start service and redirect log
java -Xms2g -Xmx2g --add-opens java.base/java.lang=ALL-UNNAMED --add-opens java.base/java.util=ALL-UNNAMED --add-opens java.base/javax.crypto=ALL-UNNAMED --add-opens java.base/sun.security.util=ALL-UNNAMED --add-opens java.base/sun.security.x509=ALL-UNNAMED --add-opens java.base/sun.security.pkcs=ALL-UNNAMED --add-opens=java.base/java.io=ALL-UNNAMED -Dolc.config.path=./olc -Dfile.encoding=UTF-8 -Djdk.net.hosts.file=./hosts_override -Dspring.config.location=file:./application.properties -classpath "%LOAD_CLASSPATH%" %START_CLASS% -Diit.test=true --spring.cloud.discovery.enabled=false --spring.cloud.servicecomb.discovery.enabled=false --xdm.mongodb.init=false --spring.main.allow-circular-references=true --spring.autoconfigure.exclude=com.huaweicloud.servicecomb.discovery.DiscoveryAutoConfiguration,com.huaweicloud.servicecomb.discovery.registry.ServiceCombRegistryAutoConfiguration,com.huaweicloud.servicecomb.discovery.discovery.ServiceCombDiscoveryClientConfiguration,com.huaweicloud.servicecomb.discovery.discovery.ServiceCombReactiveDiscoveryClientConfiguration,com.huaweicloud.servicecomb.discovery.graceful.ServicecombGracefulAutoConfiguration,com.huaweicloud.servicecomb.discovery.check.RegistryHealthIndicatorConfiguration,org.springframework.boot.autoconfigure.session.SessionAutoConfiguration > xdm.log 2>&1
