# iDME 数据结构与数据源说明

## 当前数据层
- 主数据层：xDM-F（必选）
- 后端工程：`miniapp/backend`
- 当前后端样板未引入本地 ORM 实体和迁移文件，数据访问主要通过网关服务调用 xDM-F。
- 设备 `productionDate` 当前采用“xDM-F + 后端持久化回显缓存”双通道：
  - 优先读取 xDM-F 返回值；
  - 当 xDM-F 对该字段写入受限时，后端将请求值持久化到 `miniapp/backend/data/equipment-production-dates.json` 并用于回显。

## 已知配置
- 后端配置文件：`miniapp/backend/src/main/resources/application.yml`
- xDM 服务地址：`xdm.base-url`
- 当前后端端口：`server.port=8080`

## 变更要求
1. 数据模型/表结构/字段口径有变更时，必须更新本文件。
2. 财务与报表相关字段需说明口径、来源、时间边界、对账方式。

## 变更记录
- 2026-03-09：建立数据库与数据层信息台账基线。
- 2026-03-13：补充设备 `productionDate` 的数据口径说明与后端持久化回显缓存文件（`miniapp/backend/data/equipment-production-dates.json`）。
