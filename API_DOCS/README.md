# API_DOCS 维护规范（iDME）

## 目标

建立 iDME 的 API 文档闭环，确保需求、实现、联调快照一致。

## 闭环流程

1. 新 API 需求先登记到 `demanding_api.json`，标记 `unimplemented` 与日期。
2. API 实现后，从 `demanding_api.json` 移除对应项，并更新 app 级文档。
3. 汇总更新 `api_doc.json`。
4. 联调或发布前，同步运行态 OpenAPI 快照到 `_verify/api-docs.json`。

## 推荐做法

1. 以运行中的后端 `/v3/api-docs` 作为 `_verify/api-docs.json` 的来源。
2. `API_DOCS` 侧重需求与归档，`_verify` 侧重运行态事实。
3. 两处文档不一致时，以运行态快照先定位差异，再回写 `API_DOCS`。

## 注意事项

1. JSON 文件中禁止注释。
2. 每次接口契约变更后，需同步更新验收清单：
   `miniapp/backend/tests/TEST_ACCEPTANCE_CHECKLIST.md`。
