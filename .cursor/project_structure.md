# iDME 项目结构（规则基线）

## 顶层目录
- `miniapp/backend`: Spring Boot 后端（JDK17/Maven）
- `miniapp/frontend`: Vue 前端（Node.js/Vite）
- `doc`: 赛题资料、说明文档与分析沉淀
- `API_DOCS`: API 需求与文档闭环
- `_verify`: 运行态校验与 OpenAPI 快照产物
- `.cursor/rules`: Cursor 规则集
- `.memories`: 记忆库（业务和模块知识）
- `scripts`: Git Hooks 与自动化脚本

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
- 2026-03-12：新增后端适配层与 REST 控制器（`dto/ApiResponse`、`service/XdmRuntimeService`、`service/MiniAppAdapterService`、`controller/PartController`、`controller/EquipmentController`、`controller/WorkingPlanController`、`controller/ProcedureController`、`controller/GlobalExceptionHandler`），并新增后端单元/集成测试与 API 文档快照同步。
- 2026-03-13：设备模块字段口径修复（前端 `EquipmentList.vue`、`EquipmentForm.vue`），新增后端持久化组件 `service/EquipmentProductionDateStore`，运行时新增 `miniapp/backend/data/equipment-production-dates.json` 作为 `productionDate` 回显缓存文件。
- 2026-03-13：新增赛题建模清单文档 `doc/四个核心模型字段清单.md`，用于固定 Equipment/Part/WorkingProcedure/WorkingPlan 字段口径。
