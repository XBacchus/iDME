# iDME 记忆文档体系（.memories）

## 目标

`.memories` 用于沉淀 iDME 的长期上下文，减少重复沟通和规则偏移。

适用范围：
1. 业务边界与模块认知
2. 接口与口径约束
3. 测试与验收标准
4. 发布与运维注意事项

## 目录结构

```text
.memories/
├── README.md
├── general/
│   └── FRONTEND-DEV-RULES.md
└── modules/
    ├── INDEX.md
    ├── SYSTEM-PRICE.md
    ├── project-overview/
    ├── frontend-shell/
    ├── backend-gateway/
    ├── xdm-integration/
    ├── api-governance/
    ├── data-modeling/
    ├── auth/
    ├── workflow-rule/
    ├── validation/
    ├── monitoring/
    ├── deployment/
    ├── testing/
    ├── release/
    ├── security/
    ├── performance/
    ├── miniapp-runtime/
    └── tx-distributor/
```

## 使用约定

1. 开发前先读：
   - `general/*`
   - `modules/INDEX.md`
   - 目标模块的 `README.md`
2. 涉及 API 变更时，同步更新：
   - `API_DOCS/demanding_api.json`
   - `API_DOCS/api_doc.json`
   - `_verify/api-docs.json`
3. 涉及口径和验收变化时，同步更新：
   - `modules/SYSTEM-PRICE.md`
   - `miniapp/backend/tests/TEST_ACCEPTANCE_CHECKLIST.md`

## 维护规则

1. 新增模块时，在 `modules/` 下创建目录并更新 `modules/INDEX.md`。
2. 规则发生变化时，优先更新本目录文档，再进行代码实现。
3. 文档默认使用简体中文，必要时可附英文关键词。

## 最近更新

- 2026-03-09：初始化 iDME 记忆文档根说明。
