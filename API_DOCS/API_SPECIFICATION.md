# 前端 API 接口规范文档

## 基础信息

**Base URL**: `http://localhost:8080`
**Content-Type**: `application/json`
**字符编码**: UTF-8

---

## 统一响应格式

### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

### 错误响应
```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

### 分页响应
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

## 请求头

| 请求头 | 必填 | 说明 |
|--------|------|------|
| Content-Type | 是 | application/json |
| Authorization | 否 | Bearer {token}（如需认证） |

---

## 状态码

| 状态码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 401 | 未授权 |
| 403 | 禁止访问 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 日期时间格式

所有日期时间字段统一使用 ISO 8601 格式：`yyyy-MM-dd'T'HH:mm:ss`

示例：`2024-01-01T10:00:00`

---

## 一、物料管理 API

### 1.1 获取物料列表

**接口**: `GET /api/parts`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| keyword | string | 否 | 搜索关键词（物料编号/名称） |
| categoryId | number | 否 | 分类ID |
| page | number | 否 | 页码，默认1 |
| size | number | 否 | 每页数量，默认10 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "partNo": "MTR-2023-001",
        "partName": "中心轮组件",
        "specification": "CL-100",
        "stockQty": 50,
        "supplier": "供应商A",
        "categoryName": "机械零件",
        "version": "V1.0"
      }
    ],
    "total": 100,
    "size": 10,
    "current": 1,
    "pages": 10
  }
}
```

---

### 1.2 获取物料详情

**接口**: `GET /api/parts/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "partNo": "MTR-2023-001",
    "partName": "中心轮组件",
    "specification": "CL-100",
    "stockQty": 50,
    "supplier": "供应商A",
    "categoryId": 1,
    "categoryName": "机械零件",
    "version": "V1.0"
  }
}
```

---

### 1.3 创建物料

**接口**: `POST /api/parts`

**请求体**:
```json
{
  "partNo": "MTR-2023-001",
  "partName": "中心轮组件",
  "specification": "CL-100",
  "stockQty": 50,
  "supplier": "供应商A",
  "categoryId": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "partNo": "MTR-2023-001",
    "partName": "中心轮组件",
    "specification": "CL-100",
    "stockQty": 50,
    "supplier": "供应商A",
    "categoryId": 1,
    "version": "V1.0"
  }
}
```

---

### 1.4 更新物料

**接口**: `PUT /api/parts/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "partNo": "MTR-2023-001",
    "partName": "中心轮组件",
    "specification": "CL-100",
    "stockQty": 50,
    "supplier": "供应商A",
    "categoryId": 1,
    "version": "V1.0"
  }
}
```

---

### 1.5 删除物料

**接口**: `DELETE /api/parts/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

### 1.6 获取物料 BOM

**接口**: `GET /api/parts/{id}/bom`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "partNo": "MTR-2023-042",
      "partName": "轴承单元",
      "quantity": 2,
      "version": "V1.0",
      "children": [
        {
          "id": 4,
          "partNo": "MTR-2023-044",
          "partName": "轴承",
          "quantity": 1,
          "version": "V1.0",
          "children": []
        }
      ]
    }
  ]
}
```

---

### 1.7 更新物料 BOM

**接口**: `PUT /api/parts/{id}/bom`

**请求体**: 同 BOM 响应格式的 data 字段

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 二、物料分类 API

### 2.1 获取分类树

**接口**: `GET /api/parts/categories`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "机械零件",
      "children": [
        {
          "id": 2,
          "name": "轴类",
          "children": []
        }
      ]
    }
  ]
}
```

---

### 2.2 创建分类

**接口**: `POST /api/parts/categories`

**请求体**:
```json
{
  "name": "新分类",
  "parentId": 1
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 3,
    "name": "新分类",
    "parentId": 1
  }
}
```

---

### 2.3 更新分类

**接口**: `PUT /api/parts/categories/{id}`

**请求体**:
```json
{
  "name": "更新后的分类名"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "更新后的分类名"
  }
}
```

---

### 2.4 删除分类

**接口**: `DELETE /api/parts/categories/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 三、版本管理 API

### 3.1 获取版本历史

**接口**: `GET /api/parts/{id}/versions`

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "version": "V1.0",
      "description": "初始版本",
      "status": "published",
      "createdBy": "张三",
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 3.2 创建新版本

**接口**: `POST /api/parts/{id}/versions`

**请求体**:
```json
{
  "description": "版本说明"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "version": "V2.0",
    "description": "版本说明",
    "status": "draft",
    "createdBy": "张三",
    "createdAt": "2024-01-02T10:00:00"
  }
}
```

---

### 3.3 版本对比

**接口**: `GET /api/parts/{id}/versions/compare`

**请求参数**:
| 参数 | 类型 | 必填 | 说明 |
|------|------|------|------|
| v1 | string | 是 | 版本1 |
| v2 | string | 是 | 版本2 |

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "v1": {
      "version": "V1.0",
      "partName": "中心轮组件",
      "specification": "CL-100"
    },
    "v2": {
      "version": "V2.0",
      "partName": "中心轮组件",
      "specification": "CL-200"
    }
  }
}
```

---

## 四、设备管理 API

### 4.1 获取设备列表
**接口**: `GET /api/equipments`

**请求参数**: keyword, page, size

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "equipmentNo": "EQ-001",
        "equipmentName": "数控车床",
        "model": "CNC-100",
        "manufacturer": "厂家A",
        "status": "running",
        "location": "车间A"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

**状态枚举**: running | idle | maintenance | fault

### 4.2 创建设备
**接口**: `POST /api/equipments`

**请求体**:
```json
{
  "equipmentNo": "EQ-001",
  "equipmentName": "数控车床",
  "model": "CNC-100",
  "manufacturer": "厂家A",
  "status": "idle",
  "location": "车间A"
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "equipmentNo": "EQ-001",
    "equipmentName": "数控车床",
    "model": "CNC-100",
    "manufacturer": "厂家A",
    "status": "idle",
    "location": "车间A"
  }
}
```

### 4.3 更新设备
**接口**: `PUT /api/equipments/{id}`

**请求体**: 同创建设备

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "equipmentNo": "EQ-001",
    "equipmentName": "数控车床",
    "model": "CNC-100",
    "manufacturer": "厂家A",
    "status": "running",
    "location": "车间A"
  }
}
```

### 4.4 删除设备
**接口**: `DELETE /api/equipments/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 五、工序配置 API

### 5.1 获取工序列表
**接口**: `GET /api/procedures`

**固定5道工序**: 毛坯制造、粗加工、精加工、检测、入库

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "毛坯制造",
      "order": 1
    },
    {
      "id": 2,
      "name": "粗加工",
      "order": 2
    },
    {
      "id": 3,
      "name": "精加工",
      "order": 3
    },
    {
      "id": 4,
      "name": "检测",
      "order": 4
    },
    {
      "id": 5,
      "name": "入库",
      "order": 5
    }
  ]
}
```

---

## 六、工艺路线管理 API

### 6.1 获取工艺路线列表
**接口**: `GET /api/working-plans`

**请求参数**: keyword, page, size

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "planName": "中心轮零件加工 V1.0",
        "partId": 1,
        "partName": "中心轮组件",
        "status": "active",
        "createdAt": "2024-01-01T10:00:00"
      }
    ],
    "total": 10,
    "size": 10,
    "current": 1,
    "pages": 1
  }
}
```

### 6.2 获取工艺路线详情
**接口**: `GET /api/working-plans/{id}`

**响应包含**: procedures 数组（关联的工序列表）

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "planName": "中心轮零件加工 V1.0",
    "partId": 1,
    "partName": "中心轮组件",
    "status": "active",
    "procedures": [
      {
        "id": 1,
        "name": "毛坯制造",
        "order": 1,
        "equipmentId": 1,
        "equipmentName": "数控车床",
        "duration": 120
      }
    ],
    "createdAt": "2024-01-01T10:00:00"
  }
}
```

### 6.3 创建工艺路线
**接口**: `POST /api/working-plans`

**请求体**:
```json
{
  "planName": "中心轮零件加工 V1.0",
  "partId": 1,
  "status": "active",
  "procedures": [
    {
      "procedureId": 1,
      "equipmentId": 1,
      "duration": 120
    }
  ]
}
```

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "planName": "中心轮零件加工 V1.0",
    "partId": 1,
    "status": "active"
  }
}
```

### 6.4 更新工艺路线
**接口**: `PUT /api/working-plans/{id}`

**请求体**: 同创建工艺路线

**响应示例**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "planName": "中心轮零件加工 V1.0",
    "partId": 1,
    "status": "active"
  }
}
```

### 6.5 删除工艺路线
**接口**: `DELETE /api/working-plans/{id}`

**响应示例**:
```json
{
  "code": 200,
  "message": "删除成功",
  "data": null
}
```

---

## 七、错误码

| 错误码 | 说明 |
|--------|------|
| 200 | 成功 |
| 400 | 参数错误 |
| 404 | 资源不存在 |
| 500 | 服务器错误 |

---

## 八、测试数据要求

1. 物料: 至少5条，包含"中心轮组件"
2. 设备: 至少3条
3. 工艺路线: "中心轮零件加工 V1.0"，包含完整5道工序
4. 工序: 固定5道

---

## 九、xDM-F 模型映射说明

### 9.1 架构说明

本项目采用三层架构，所有数据操作必须通过 xDM-F API：

```
前端 → 后端 Spring Boot → xDM-F 运行态 API → xDM-F 数据模型
```

后端需要将前端 API 字段映射到 xDM-F 模型字段。

---

### 9.2 物料管理映射（Part）

**xDM-F 模型**: `Part`

| 前端 API 字段 | xDM-F 字段 | 类型 | 说明 |
|--------------|-----------|------|------|
| id | id | string | 物料ID |
| partNo | partCode | string | 物料编号 |
| partName | partName | string | 物料名称 |
| specification | specModel | string | 规格型号 |
| stockQty | stockQty | number | 库存数量 |
| supplier | supplier | string | 供应商 |
| categoryId | - | number | 分类ID（前端使用） |
| categoryName | categoryPath | string | 分类路径 |
| version | versionNo | string | 版本号 |

**xDM-F API 调用**:
- 创建: `POST /rdm_{appId}_app/services/dynamic/api/Part/create`
- 查询: `POST /rdm_{appId}_app/services/dynamic/api/Part/query`
- 更新: `POST /rdm_{appId}_app/services/dynamic/api/Part/update`
- 删除: `POST /rdm_{appId}_app/services/dynamic/api/Part/delete`

---

### 9.3 BOM 关系映射（Part_Part）

**xDM-F 关系**: `Part_Part`

| 前端 API 字段 | xDM-F 字段 | 说明 |
|--------------|-----------|------|
| id | id | 子物料ID |
| partNo | target.partCode | 子物料编号 |
| partName | target.partName | 子物料名称 |
| quantity | - | 数量（关系属性） |
| version | target.versionNo | 版本号 |
| children | - | 递归子节点 |

**xDM-F API 调用**:
- 创建关系: `POST /rdm_{appId}_app/services/dynamic/api/Part_Part/create`
- 查询关系: `POST /rdm_{appId}_app/services/dynamic/api/Part_Part/query`

---

### 9.4 设备管理映射（Equipment）

**xDM-F 模型**: `Equipment`

| 前端 API 字段 | xDM-F 字段 | 类型 | 说明 |
|--------------|-----------|------|------|
| id | id | string | 设备ID |
| equipmentNo | equipmentCode | string | 设备编码 |
| equipmentName | equipmentName | string | 设备名称 |
| model | specModel | string | 规格型号 |
| manufacturer | manufacturer | string | 生产厂家 |
| status | - | string | 设备状态（前端扩展） |
| location | location | string | 位置 |
| brand | brand | string | 品牌 |
| supplier | supplier | string | 供应商 |
| productionDate | productionDate | date | 生产日期 |
| serviceLifeYears | serviceLifeYears | number | 使用年限 |
| depreciationMethod | depreciationMethod | string | 折旧方式 |
| technicalParams | technicalParams | string | 技术参数信息 |
| sparePartsInfo | sparePartsInfo | string | 备品备件信息 |

**xDM-F API 调用**:
- 创建: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/create`
- 查询: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/query`
- 更新: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/update`
- 删除: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/delete`

---


### 9.5 工序配置映射（WorkingProcedure）

**xDM-F 模型**: `WorkingProcedure`

| 前端 API 字段 | xDM-F 字段 | 类型 | 说明 |
|--------------|-----------|------|------|
| id | id | string | 工序ID |
| name | procedureName | string | 工序名称 |
| order | - | number | 工序顺序（前端扩展） |
| procedureCode | procedureCode | string | 工序编号 |
| productionStep | productionStep | string | 生产步骤 |
| operatorName | operatorName | string | 操作人员 |
| startTime | startTime | datetime | 开始时间 |
| endTime | endTime | datetime | 结束时间 |

**固定5道工序**:
1. 毛坯制造
2. 粗加工
3. 精加工
4. 检测
5. 入库

**xDM-F API 调用**:
- 创建: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure/create`
- 查询: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure/query`

---

### 9.6 工艺路线映射（WorkingPlan）

**xDM-F 模型**: `WorkingPlan`

| 前端 API 字段 | xDM-F 字段 | 类型 | 说明 |
|--------------|-----------|------|------|
| id | id | string | 工艺ID |
| planName | planName | string | 工艺名称 |
| planCode | planCode | string | 工艺编号 |
| partId | - | number | 物料ID（前端关联） |
| partName | - | string | 物料名称（前端显示） |
| status | - | string | 状态（前端扩展） |
| versionNo | versionNo | string | 版本号 |
| belongProduct | belongProduct | string | 所属产品 |
| operatorName | operatorName | string | 操作人员 |
| operationTime | operationTime | datetime | 操作时间 |
| equipmentUsage | equipmentUsage | string | 设备使用情况 |
| procedures | - | array | 关联的工序列表 |

**xDM-F API 调用**:
- 创建: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/create`
- 查询: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/query`
- 更新: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/update`
- 删除: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/delete`

---

### 9.7 工艺-工序关系映射（WorkingPlan_WorkingProcedure）

**xDM-F 关系**: `WorkingPlan_WorkingProcedure`

| 前端 API 字段 | xDM-F 字段 | 说明 |
|--------------|-----------|------|
| procedureId | target.id | 工序ID |
| name | target.procedureName | 工序名称 |
| order | - | 工序顺序（关系属性） |
| equipmentId | - | 设备ID（通过 WorkingProcedure_Equipment 关系） |
| equipmentName | - | 设备名称 |
| duration | - | 工时（关系属性） |

**xDM-F API 调用**:
- 创建关系: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan_WorkingProcedure/create`
- 查询关系: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan_WorkingProcedure/query`

---

### 9.8 工序-设备关系映射（WorkingProcedure_Equipment）

**xDM-F 关系**: `WorkingProcedure_Equipment`

**xDM-F API 调用**:
- 创建关系: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Equipment/create`
- 查询关系: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Equipment/query`

---

### 9.9 工序-物料关系映射（WorkingProcedure_Part）

**xDM-F 关系**: `WorkingProcedure_Part`

**xDM-F API 调用**:
- 创建关系: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Part/create`
- 查询关系: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Part/query`

---

### 9.10 后端实现注意事项

1. **统一响应封装**: 所有 xDM-F API 返回需要包装成 `{ code, message, data }` 格式
2. **字段映射**: 后端 Service 层负责前端字段和 xDM-F 字段的转换
3. **分页处理**: xDM-F 查询结果需要转换为前端分页格式
4. **关系查询**: 需要通过多次 API 调用组装完整的业务对象（如 BOM 树、工艺路线详情）
5. **错误处理**: xDM-F API 错误需要转换为前端统一错误格式
6. **日期格式**: xDM-F 的 Timestamp 需要转换为 ISO 8601 格式字符串

---

