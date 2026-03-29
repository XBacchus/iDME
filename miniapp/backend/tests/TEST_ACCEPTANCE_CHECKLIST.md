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

## 2026-03-11 制造域 API 封装验收
1. 设计输入
- 调用 `POST /api/parts`、`GET /api/parts`、`DELETE /api/parts/{id}`、`GET /api/parts/categories`、`POST /api/parts/{id}/bom`
- 调用 `POST /api/equipments`
- 调用 `POST /api/working-plans`、`GET /api/working-plans/{id}`、`POST /api/working-plans/{id}/procedures`
2. 设计预期
- 所有接口均转发到 `XdmGatewayService.proxy()`
- HTTP 方法和目标路径与接口定义一致
- `GET /api/parts` 透传查询参数用于搜索
3. 输出对比
- 状态：✅
- 测试文件：`src/test/java/com/idme/miniapp/controller/ManufacturingApiControllerTest.java`
- 测试命令：`mvn -Dtest=ManufacturingApiControllerTest test`
- 结果：10/10 通过

- 2026-03-12 | 后端适配层（parts/equipments/working-plans/procedures）打通并统一返回结构 | 状态：✅ | 文件：ApiAdapterIntegrationTest.java、XdmRuntimeServiceTest.java | 命令：`mvn clean test`
- 2026-03-13 | Equipment 字段口径对齐（12字段）、创建日期容错与回显、扩展字段清空、前端查询与全字段展示 | 状态：✅ | 文件：MiniAppAdapterService.java、EquipmentList.vue、EquipmentForm.vue、ApiAdapterIntegrationTest.java、ApiIntegrationTest.java | 命令：`mvn "-Dtest=ApiAdapterIntegrationTest,ApiIntegrationTest,XdmRuntimeServiceTest" test`、`npm run build`、MCP验收（/equipments CRUD）
- 2026-03-13 | Equipment `productionDate` 回显改为可持久化（重启后不丢失） | 状态：✅ | 文件：EquipmentProductionDateStore.java、MiniAppAdapterService.java、API_DOCS/frontend_api_requirements.json、API_DOCS/miniapp-backend.json | 命令：`mvn "-Dtest=ApiAdapterIntegrationTest,ApiIntegrationTest,XdmRuntimeServiceTest" test`、手工重启穿透（create->restart->get）
- 2026-03-13 | Part 必填字段契约收敛（partNo/partName/specification/stockQty/supplier）+ 更新合并校验 | 状态：✅ | 文件：MiniAppAdapterService.java、PartForm.vue、ApiAdapterIntegrationTest.java、API_DOCS/API_SPECIFICATION.md、API_DOCS/frontend_api_requirements.json、API_DOCS/api_doc.json | 命令：`mvn test`、`npm run build`、MCP sub-agent 验收（/parts CRUD、分类管理、版本管理）
- 2026-03-13 | Procedures 页面补齐 `WorkingProcedure` 字段口径、设备聚合展示与在线编辑能力 | 状态：✅ | 文件：MiniAppAdapterService.java、ProcedureList.vue、API_DOCS/API_SPECIFICATION.md、API_DOCS/frontend_api_requirements.json、.cursor/project_structure.md | 命令：`mvn -q -DskipTests compile`、`npm run build`
- 2026-03-13 | 物料列表分类树筛选支持父类展开、`categoryIds` 透传与页面返回状态保留 | 状态：✅ | 文件：PartList.vue、PartController.java、MiniAppAdapterService.java、API_DOCS/API_SPECIFICATION.md、API_DOCS/frontend_api_requirements.json、.cursor/project_structure.md | 命令：`mvn -q "-Dtest=ApiIntegrationTest" test`、`npm run build`
- 2026-03-13 | 物料列表分类树筛选终验：一级展开子分类、父类携带 `categoryIds` 过滤、返回后状态保留 | 状态：✅ | 文件：PartList.vue、PartController.java、MiniAppAdapterService.java | 证据：`GET /api/parts?keyword=&categoryId=1001&categoryIds=1001,1002,1003&page=1&size=20`
