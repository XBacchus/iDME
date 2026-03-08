# iDME MiniApp 项目骨架

## 1. 技术栈
- 展现层（前端）：Node.js + Vue3 + Vite + HTML/CSS/JS（VSCode）
- 业务层（后端）：JDK17 + Maven + Spring Boot 3.0（IntelliJ IDEA）
- 数据层（必选）：xDM-F 运行态 API（通过后端统一访问）

## 2. 目录结构
```text
miniapp
├─ backend   # Spring Boot 后端
└─ frontend  # Vue 前端
```

## 3. 后端说明
后端已内置 xDM-F 登录会话管理：
1. 调用 `/v1/login/verifycode` 初始化 SESSION 和 XSRF。
2. 自动注入 `csrf_token` Cookie 与 `x-csrf-token` 头。
3. 调用 `/v1/login/login` 建立登录态。
4. 用会话访问其他 xDM-F API。

默认账号已配置为：
- username: `admin`
- password: `admin123`

配置文件：`backend/src/main/resources/application.yml`

## 4. 运行方式
### 4.1 启动后端
```bash
cd miniapp/backend
mvn spring-boot:run
```

### 4.2 启动前端
```bash
cd miniapp/frontend
npm install
npm run dev
```

默认访问：
- 前端：http://127.0.0.1:5173
- 后端：http://127.0.0.1:8080

## 5. 已提供的联调接口
- `GET /api/health`：后端健康检查
- `GET /api/xdm/health`：xDM-F 健康检查
- `GET /api/xdm/me`：验证 xDM-F 登录会话
- `POST /api/xdm/proxy`：代理调用任意 xDM-F API

`/api/xdm/proxy` 请求体示例：
```json
{
  "path": "/rdm/basic/api/configs",
  "method": "GET"
}
```
