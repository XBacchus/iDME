# 工业 miniApp 4天需求拆分（分发总览）

## 1. 目标

在 4 天内完成可演示、可验收、可提交的工业 miniApp：

1. 数据层：xDM-F 模型与关系正确发布。
2. 业务层：Spring Boot 3.0 后端统一调用 xDM-F API。
3. 展现层：Vue 前端完成四大模块闭环。
4. 交付层：录屏、代码、模型材料、API 清单齐全。

## 2. 分发方式（建议）

每天按并行任务包分发给不同成员，避免写冲突：

1. xDM-F 建模与运行态组
2. 后端接口与网关组
3. 前端页面与交互组
4. 测试验收与材料组

## 3. 每日文档

1. Day1：`Day1-建模与环境需求.md`
2. Day2：`Day2-核心接口与前端骨架需求.md`
3. Day3：`Day3-业务功能完善与联调需求.md`
4. Day4：`Day4-验收优化与交付需求.md`

## 4. 每日验收清单

1. Day1：`Day1-验收清单.md`
2. Day2：`Day2-验收清单.md`
3. Day3：`Day3-验收清单.md`
4. Day4：`Day4-验收清单.md`

## 5. 不可违反约束

1. 所有业务数据交互必须通过 xDM-F API。
2. 强制环境：JDK17、Spring Boot 3.0、Node.js、Vue、xDM-F。
3. API 变更遵循闭环：`API_DOCS` -> 实现 -> 汇总 -> `_verify/api-docs.json`。
4. 核心验收以 `doc/赛题验收清单.md` 和 `miniapp/backend/tests/TEST_ACCEPTANCE_CHECKLIST.md` 为准。
