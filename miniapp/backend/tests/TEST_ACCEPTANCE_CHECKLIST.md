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
