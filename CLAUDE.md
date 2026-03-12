# CLAUDE.md

## Project Overview
- Repository: iDME
- Runtime stack:
  - Backend: Spring Boot 3.0, Java 17, Maven (`miniapp/backend`)
  - Frontend: Vue 3 + Vite + Node.js (`miniapp/frontend`)
  - Data layer: xDM-F SDK (root runtime and service endpoints)

## Working Agreement
1. Read memory docs first: `.memories/general/*`, `.memories/modules/*`.
2. Keep outputs and docs in Simplified Chinese by default.
3. Prefer parallel execution for independent tasks.
4. Use PowerShell-compatible commands.

## Standard Commands
- Frontend dev: `cd miniapp/frontend` then `npm install`, `npm run dev`
- Frontend build: `cd miniapp/frontend` then `npm run build`
- Backend dev: `cd miniapp/backend` then `mvn spring-boot:run`
- Backend build: `cd miniapp/backend` then `mvn clean package`

## API & Documentation Flow
1. New API request: `API_DOCS/demanding_api.json`
2. Implemented API docs: app-level JSON in `API_DOCS/`
3. Final summary: `API_DOCS/api_doc.json`
4. No comments inside JSON API docs.
5. Before integration/release, sync runtime OpenAPI snapshot to `_verify/api-docs.json` (recommended source: `/v3/api-docs`).

## Mandatory Sync Files
- Structure changes: `.cursor/project_structure.md`
- DB/model changes: `.cursor/database_datainfo.md`
- API gateway/workflow acceptance: `miniapp/backend/tests/TEST_ACCEPTANCE_CHECKLIST.md`

## Frontend Development
- 前端开发时必须参考设计规范：`miniapp/frontend/设计规范.md`
- 设计模板预览：`miniapp/frontend/设计模板.html`
- 前端开发计划：`miniapp/frontend/前端开发计划.md`
- 风格要求：暗色主题（Earth Edition）、Tailwind CSS、Arc 浏览器风格

## Git Hooks
- Use `scripts/install-git-hooks.bat` (Windows) or `scripts/install-git-hooks.sh` (Linux/macOS)
