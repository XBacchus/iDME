# 前端架构说明

## 技术栈
- Vue 3 + Vite
- Element Plus（UI组件库）
- Tailwind CSS
- Axios

## 四大核心模块

### 1. 物料管理 (/parts)
**功能：**
- 物料列表（搜索、分页、分类筛选）
- 物料CRUD
- BOM管理（树形结构）
- 版本管理（新增功能）
  - 版本历史列表
  - 创建新版本
  - 版本对比（选择2个版本）
  - 版本回滚
  - 版本状态管理（草稿/已发布）

**页面路由：**
- `/parts` - 物料列表
- `/parts/add` - 新增物料
- `/parts/edit/:id` - 编辑物料
- `/parts/bom/:id` - BOM管理
- `/parts/versions/:id` - 版本管理（新增）

### 2. 设备管理 (/equipments)
**功能：**
- 设备列表（搜索、分页）
- 设备CRUD
- 设备状态：运行中/空闲/维护中/故障

**页面路由：**
- `/equipments` - 设备列表
- `/equipments/add` - 新增设备
- `/equipments/edit/:id` - 编辑设备

### 3. 工序配置 (/procedures)
**功能：**
- 固定5道工序展示
- 工序名称：毛坯制造、粗加工、精加工、检测、入库

**页面路由：**
- `/procedures` - 工序列表

### 4. 工艺路线管理 (/working-plans)
**功能：**
- 工艺路线列表
- 工艺路线CRUD
- 工艺路线详情（流程可视化）
- 关联物料和工序

**页面路由：**
- `/working-plans` - 工艺路线列表
- `/working-plans/new` - 新增工艺路线
- `/working-plans/:id/edit` - 编辑工艺路线
- `/working-plans/:id` - 工艺路线详情

## API调用方式

**Base URL：** `http://localhost:8080/api`

**请求封装：** `src/utils/request.js`

**API文件：**
- `src/api/parts.js` - 物料相关API
- `src/api/equipments.js` - 设备相关API
- `src/api/procedures.js` - 工序相关API
- `src/api/workingPlans.js` - 工艺路线相关API

## 数据格式要求

### 分页响应格式
```json
{
  "records": [],
  "total": 100
}
```

### 时间格式
`YYYY-MM-DD HH:mm:ss`

### 状态枚举
- 设备状态：`running` | `idle` | `maintenance` | `fault`
- 版本状态：`draft` | `published`

## 重要示例数据

### 中心轮零件加工 V1.0（必需）
工艺路线详情页需要展示这个示例数据，包含完整的5道工序流程。

## 后端对接步骤

1. 完成xDM-F数据建模（4个模型+关系）
2. 实现API接口（参考 `frontend_api_requirements.json`）
3. 通知前端修改 `src/utils/request.js` 的 baseURL
4. 前端删除Mock数据
5. 联调测试
