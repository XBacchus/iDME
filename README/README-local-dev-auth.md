# 本地开发登录鉴权快速配置（无需外部 IAM）

适用场景：本地 SDK 独立运行，暂未接入外部 `authui/IAM`，需要先登录进入控制台开展开发。

## 一、配置 `application.properties`

在 `application.properties` 中使用以下关键配置（已在当前工程生效）：

```properties
# 本地登录模式
xdm.login.type=LOCAL
ssf.auth.type=LOCAL
xdm.runtime.auth.default-type=JWT
runningState.JWTTokenValidation=true
ssf.login.local.verifycode.verifyCode=false

# SessionFilter 本地登录入口
ssf.sysmgr.sessionfilter.loginUrl=LOCAL
ssf.login.loginUri=LOCAL

# 保留 IAM endpoint 占位，避免客户端初始化报空指针
xdm.iam.endpoint=http://127.0.0.1
xdm.iam.manage.endpoint=http://127.0.0.1
xdm.iam.login=http://127.0.0.1
xdm.runtime.iam-token.validation=false
xdm.iam.domain.check=false
xdm.token.cache.oidc.enable=false
xdm.oauth2.sso.enabled=false

# 放行本地登录/首登接口
ssf.sysmgr.sessionfilter.ignorePattern=/getIndexUrl;/v1/health;/v1/login/login;/v1/login/logout;/v1/login/verifycode;/v1/firstlogin/users;/v1/firstlogin/userpasswords:PUT;/static/**;/index.html;/HuaweiLog.ico;/HuaweiLog.png
iit.interceptors.token.exclude.uris=/v1/oauth2/login,/getIndexUrl,/v1/health,/v1/login/login,/v1/login/logout,/v1/login/verifycode,/v1/firstlogin/users,/v1/firstlogin/userpasswords
xdm.iit.interceptors.token.exclude-uris=/v1/oauth2/login,/getIndexUrl,/v1/health,/v1/login/login,/v1/login/logout,/v1/login/verifycode,/v1/firstlogin/users,/v1/firstlogin/userpasswords
```

## 二、启动与验证

1. 启动数据库与运行时服务。  
2. 访问：

```text
http://127.0.0.1:8003/rdm_497bc30622bb434c9096c34d1ba38f02_app/services/index.html
```

应进入 `#/SSFLogin` 登录页（不再重定向到 `/authui/login.action`）。

## 三、管理员首登初始化

1. 登录页点击“管理员第一次登录？”。
2. 账号默认 `sysadmin`，设置初始密码（长度至少 8，包含至少 3 种字符类型）。
3. 初始化成功后返回登录页，用 `sysadmin + 新密码` 登录即可进入控制台。

## 四、已验证结果（2026-03-08）

- `GET /v1/health` 返回 `200`。
- `GET /index.html` 返回 `200` 并进入 `#/SSFLogin`。
- 首登初始化成功后，`sysadmin` 可正常登录并进入 `#/Dashboard`。

## 五、注意事项

- 该方案用于本地开发调试，不替代生产环境统一认证接入。
- 若后续接入正式 IAM，请切回对应 `IAM/OIDC` 配置并恢复登录回调地址。
