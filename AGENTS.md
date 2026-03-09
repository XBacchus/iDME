# iDME 协作规则（AGENTS）

## 1. 全局协作规则（最高优先）

1. 开发前必须先阅读：`.memories/general/*`、`.memories/modules/*`，尤其 `SYSTEM-PRICE.md`（历史文件名，实际为系统口径基线）。
2. 对外输出优先简体中文。
3. 任务执行优先并行拆解：无依赖并行，有强依赖链串行。
4. 网关/API/流程规则按第一性原则：统一口径、单一数据源、可追溯、可对账、可回放。

## 2. 开发流程与环境约束

1. 技术栈固定：
   - 前端：`Node.js + Vue + Vite`（开发工具建议 VSCode）
   - 后端：`JDK 17 + Maven + Spring Boot 3.0`（开发工具建议 IntelliJ IDEA）
   - 数据层：`xDM-F`（必选）
2. 命令必须兼容 Windows PowerShell，避免 `&&` 作为分隔符。
3. 默认前后端可常驻运行，非必要不重复启动。
4. 端口以配置文件为准：当前后端默认 `8080`（见 `miniapp/backend/src/main/resources/application.yml`）。

## 3. API 需求-实现-文档闭环

1. 新 API 需求先写 `API_DOCS/demanding_api.json`，标记 `unimplemented` 与日期。
2. 后端实现后，从 `demanding_api.json` 移除并写入对应 app 的 API JSON 文档。
3. 最终汇总到 `API_DOCS/api_doc.json`。
4. API JSON 文档禁止注释。
5. 联调或发布前，需将运行态 OpenAPI 快照同步到 `_verify/api-docs.json`（建议来源：`/v3/api-docs`）。

## 4. 变更后的强制同步文档

1. 文件/目录增删改后，必须更新 `.cursor/project_structure.md`。
2. 数据模型/数据库结构变更后，必须更新 `.cursor/database_datainfo.md`。

## 5. 测试与验收规则

1. 接口契约、网关代理、流程规则改动统一走：`miniapp/backend/tests/TEST_ACCEPTANCE_CHECKLIST.md`。
2. 清单必须标记覆盖状态（`✅/⚠️/❌`）并注明对应测试文件。
3. 核心场景使用三段式：设计输入 -> 设计预期 -> 输出对比。
4. 后台执行单测最大超时建议 60 秒，避免任务卡死。

## 6. 发布与打包关键规则

1. 前端发布使用 `npm run build`（在 `miniapp/frontend`）。
2. 后端发布使用 `mvn clean package`（在 `miniapp/backend`）。
3. 发布前后必须核对：版本号、构建产物、配置项、服务重启、接口连通性。

## 7. Git 规则（Hook 强约束）

1. `commit-msg`：Conventional Commit，格式 `<type>(<scope>): <subject>`。
2. `pre-push`：分支名必须匹配 `feature|fix|refactor|test|docs|hotfix|release/xxx` 或受保护分支。
3. `post-merge`：拉代码后自动处理依赖与后端构建校验。

## 8. 风险操作确认机制

以下高风险操作前必须明确确认：
- 删除文件/目录、批量修改
- 破坏性数据库操作
- 调用生产 API
- 核心依赖升级
- 系统级配置修改

确认模板：

```text
⚠️ 危险操作检测！

操作类型：[具体操作]
影响范围：[详细说明]
风险评估：[潜在后果]

请确认是否继续？（回复“是”/“确认”/“继续”）
```
