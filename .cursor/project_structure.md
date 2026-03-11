# iDME 项目结构（规则基线）

## 顶层目录
- `miniapp/backend`: Spring Boot 后端（JDK17/Maven）
- `miniapp/frontend`: Vue 前端（Node.js/Vite）
- `API_DOCS`: API 需求与文档闭环
- `_verify`: 运行态校验与 OpenAPI 快照产物
- `.cursor/rules`: Cursor 规则集
- `.memories`: 记忆库（业务和模块知识）
- `scripts`: Git Hooks 与自动化脚本
- `doc/4天需求拆分`: 4天需求拆分与任务分发文档

## 规则文件
- `AGENTS.md`
- `CLAUDE.md`
- `.cursor/project_structure.md`
- `.cursor/database_datainfo.md`

## 更新要求
1. 新增/删除/移动文件或目录后，必须更新本文件。
2. 重大结构调整需补充“变更记录”。
3. API 变更时需同步核对 `API_DOCS` 与 `_verify/api-docs.json` 一致性。

## 变更记录
- 2026-03-09：初始化协作规则体系（AGENTS、CLAUDE、.cursor/rules、.memories、API_DOCS、git hooks、验收清单）。
- 2026-03-09：新增 `doc/4天需求拆分` 目录，包含 Day1~Day4 需求文档、分发总览和成员分工模板。
- 2026-03-09：补充 Day1~Day4 每日验收清单文档，支持按天勾检、状态标记和测试文件映射。
- 2026-03-11：新增 `ManufacturingApiController.java` 与 `ManufacturingApiControllerTest.java`，封装制造域 API（parts/equipments/working-plans）并通过网关代理。
