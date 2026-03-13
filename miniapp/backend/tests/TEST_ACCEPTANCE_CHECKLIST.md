# TEST_ACCEPTANCE_CHECKLIST

## 使用说明
1. 接口契约、网关代理、流程规则相关改动必须更新本清单。
2. 每条用例标记状态：`✅`（通过）/`⚠️`（部分）/`❌`（失败）。
3. 每条记录必须关联测试文件或测试命令。
4. 若接口契约变更，同时核对 `API_DOCS/*` 与 `_verify/api-docs.json`。

## 三段式验收模板
1. 设计输入
2. 设计预期
3. 输出对比

## 验收记录
- 2026-03-09 | 规则体系初始化基线 | 状态：✅ | 文件：规则文档初始化（无业务逻辑变更）
- 2026-03-12 | 后端适配层（parts/equipments/working-plans/procedures）打通并统一返回结构 | 状态：✅ | 文件：ApiAdapterIntegrationTest.java、XdmRuntimeServiceTest.java | 命令：`mvn clean test`
- 2026-03-13 | Equipment 字段口径对齐（12字段）、创建日期容错与回显、扩展字段清空、前端查询与全字段展示 | 状态：✅ | 文件：MiniAppAdapterService.java、EquipmentList.vue、EquipmentForm.vue、ApiAdapterIntegrationTest.java、ApiIntegrationTest.java | 命令：`mvn "-Dtest=ApiAdapterIntegrationTest,ApiIntegrationTest,XdmRuntimeServiceTest" test`、`npm run build`、MCP验收（/equipments CRUD）
- 2026-03-13 | Equipment `productionDate` 回显改为可持久化（重启后不丢失） | 状态：✅ | 文件：EquipmentProductionDateStore.java、MiniAppAdapterService.java、API_DOCS/frontend_api_requirements.json、API_DOCS/miniapp-backend.json | 命令：`mvn "-Dtest=ApiAdapterIntegrationTest,ApiIntegrationTest,XdmRuntimeServiceTest" test`、手工重启穿透（create->restart->get）
