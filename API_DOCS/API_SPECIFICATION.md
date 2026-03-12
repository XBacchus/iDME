# 前端 API 接口规范文档

## 基础信息

**Base URL**: `http://localhost:8080`
**Content-Type**: `application/json`
**字符编码**: UTF-8

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
  "total": 100
}
```

---

### 1.2 获取物料详情

**接口**: `GET /api/parts/{id}`

**响应示例**:
```json
{
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

**响应**: 返回创建的物料对象

---

### 1.4 更新物料

**接口**: `PUT /api/parts/{id}`

**请求体**: 同创建物料

---

### 1.5 删除物料

**接口**: `DELETE /api/parts/{id}`

**响应**: `{ "message": "删除成功" }`

---

### 1.6 获取物料 BOM

**接口**: `GET /api/parts/{id}/bom`

**响应示例**:
```json
[
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
```

---

### 1.7 更新物料 BOM

**接口**: `PUT /api/parts/{id}/bom`

**请求体**: 同 BOM 响应格式

---

## 二、物料分类 API

### 2.1 获取分类树

**接口**: `GET /api/parts/categories`

**响应示例**:
```json
[
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

---

### 2.3 更新分类

**接口**: `PUT /api/parts/categories/{id}`

**请求体**:
```json
{
  "name": "更新后的分类名"
}
```

---

### 2.4 删除分类

**接口**: `DELETE /api/parts/categories/{id}`

---

## 三、版本管理 API

### 3.1 获取版本历史

**接口**: `GET /api/parts/{id}/versions`

**响应示例**:
```json
[
  {
    "version": "V1.0",
    "description": "初始版本",
    "status": "published",
    "createdBy": "张三",
    "createdAt": "2024-01-01 10:00:00"
  }
]
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
```

---

## 四、设备管理 API

### 4.1 获取设备列表
**接口**: `GET /api/equipments`

**请求参数**: keyword, page, size

**响应示例**:
```json
{
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
  "total": 50
}
```

**状态枚举**: running | idle | maintenance | fault

### 4.2 创建设备
**接口**: `POST /api/equipments`

### 4.3 更新设备
**接口**: `PUT /api/equipments/{id}`

### 4.4 删除设备
**接口**: `DELETE /api/equipments/{id}`

---

## 五、工序配置 API

### 5.1 获取工序列表
**接口**: `GET /api/procedures`

**固定5道工序**: 毛坯制造、粗加工、精加工、检测、入库

---

## 六、工艺路线管理 API

### 6.1 获取工艺路线列表
**接口**: `GET /api/working-plans`

### 6.2 获取工艺路线详情
**接口**: `GET /api/working-plans/{id}`

**响应包含**: procedures 数组（关联的工序列表）

### 6.3 创建工艺路线
**接口**: `POST /api/working-plans`

### 6.4 更新工艺路线
**接口**: `PUT /api/working-plans/{id}`

### 6.5 删除工艺路线
**接口**: `DELETE /api/working-plans/{id}`

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

