# iDME 项目结构（规则基线）

## 顶层目录
- `miniapp/backend`: Spring Boot 后端（JDK17/Maven）
- `miniapp/frontend`: Vue 前端（Node.js/Vite）
- `API_DOCS`: API 需求与文档闭环
- `_verify`: 运行态校验与 OpenAPI 快照产物
- `.cursor`: 项目规则、结构与数据库说明
- `.memories`: 记忆库（业务和模块知识）
- `doc`: 赛题资料、说明文档与分析沉淀
- `doc/4天需求拆分`: Day1~Day4 需求拆分、分发与验收材料
- `db`: 数据库相关资源
- `lib`: 本地依赖与扩展库
- `META-INF`: 运行时元数据
- `olc`: 临时运行产物
- `pg13`: PostgreSQL 相关资源
- `scripts`: Git Hooks 与自动化脚本
- `validate`: 校验规则文件
- `xdm-tx-distributor`: xDM 事务分发组件与相关产物

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
- 2026-03-12：新增后端适配层与 REST 控制器（`dto/ApiResponse`、`service/XdmRuntimeService`、`service/MiniAppAdapterService`、`controller/PartController`、`controller/EquipmentController`、`controller/WorkingPlanController`、`controller/ProcedureController`、`controller/GlobalExceptionHandler`），并新增后端单元/集成测试与 API 文档快照同步。
- 2026-03-13：设备模块字段口径修复（前端 `EquipmentList.vue`、`EquipmentForm.vue`），新增后端持久化组件 `service/EquipmentProductionDateStore`，运行时新增 `miniapp/backend/data/equipment-production-dates.json` 作为 `productionDate` 回显缓存文件。
- 2026-03-13：新增赛题建模清单文档 `doc/四个核心模型字段清单.md`，用于固定 Equipment/Part/WorkingProcedure/WorkingPlan 字段口径。
- 2026-03-13：物料模块补齐必需字段校验（前端 `PartForm.vue`、后端 `MiniAppAdapterService.java`），并修复 Part 更新时字段合并逻辑，避免部分更新导致必填字段被误清空。
- 2026-03-13：更新根目录 `start.bat`，新增 miniapp 后端（8080）与前端（5173）一键启动及 `-force` 停止流程。
- 2026-03-13：工艺路线模块补齐 `operationTime` 前后端口径：前端 `WorkingPlanForm.vue`、`WorkingPlanList.vue`、`WorkingPlanDetail.vue` 新增字段展示；后端新增 `service/WorkingPlanOperationTimeStore.java`，并在 `MiniAppAdapterService.java` 增加 `operationTime` 兼容重试与缓存回写逻辑。
- 2026-03-13：同步 Part 契约文档到 `API_DOCS`（必填字段与更新语义）并更新验收清单 `miniapp/backend/tests/TEST_ACCEPTANCE_CHECKLIST.md`。
- 2026-03-23：前端主布局与物料列表视觉重构，更新 `src/assets/styles/*`、`src/components/layout/*`、`src/router/index.js`、`src/views/parts/PartList.vue` 以对齐新的玻璃拟态暗色风格，并修正内容滚动容器与部分 API 路径重复前缀问题。
- 2026-03-28：前端控制台按参考稿 `ai_studio_code (4).html` 回收为固定亮色壳层，重写 `src/components/layout/*`、`src/views/Home.vue`、`src/router/index.js`、`src/main.js`、`src/App.vue`，移除亮暗切换并统一首页文案与结构。
- 2026-03-29：前端控制台亮色模式按参考稿 `ai_studio_code (2).html` 仅调整配色层，更新 `src/components/layout/AppLayout.vue`、`src/components/layout/Sidebar.vue`、`src/components/layout/Header.vue`、`src/assets/styles/common.css`、`src/views/Home.vue`，保留现有布局与模块结构不变。
- 2026-03-29：前端首页控制台 `/` 路由改为默认应用亮色壳层，并将统计卡、导航卡、内容区卡片与表格底色统一提升为白底，更新 `src/components/layout/AppLayout.vue` 与 `src/views/Home.vue`，不调整任何布局结构。
- 2026-03-29：前端主题切换逻辑修正为“首次默认亮色、后续按用户切换状态持久化”，移除首页 `/` 路由强制亮色限制，更新 `src/components/layout/AppLayout.vue`，恢复亮暗模式双向切换能力。
- 2026-03-29：前端明亮模式继续向四个模块页对齐控制台页视觉口径，补充 `src/assets/styles/common.css` 的亮色主题公共规则，并重构 `src/views/parts/PartList.vue`、`src/views/equipments/EquipmentList.vue`、`src/views/working-plans/WorkingPlanList.vue`、`src/views/procedures/ProcedureList.vue` 与 `src/components/business/OperationCard.vue` 的标题区、指标卡、工作区和状态卡样式。
