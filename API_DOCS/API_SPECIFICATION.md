# 鍓嶇 API 鎺ュ彛瑙勮寖鏂囨。

## 鍩虹淇℃伅

**Base URL**: `http://localhost:8080`
**Content-Type**: `application/json`
**瀛楃缂栫爜**: UTF-8

---

## 缁熶竴鍝嶅簲鏍煎紡

### 鎴愬姛鍝嶅簲
```json
{
  "code": 200,
  "message": "success",
  "data": { }
}
```

### 閿欒鍝嶅簲
```json
{
  "code": 400,
  "message": "鍙傛暟閿欒",
  "data": null
}
```

### 鍒嗛〉鍝嶅簲
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

## 璇锋眰澶?

| 璇锋眰澶?| 蹇呭～ | 璇存槑 |
|--------|------|------|
| Content-Type | 鏄?| application/json |
| Authorization | 鍚?| Bearer {token}锛堝闇€璁よ瘉锛?|

---

## 鐘舵€佺爜

| 鐘舵€佺爜 | 璇存槑 |
|--------|------|
| 200 | 鎴愬姛 |
| 400 | 鍙傛暟閿欒 |
| 401 | 鏈巿鏉?|
| 403 | 绂佹璁块棶 |
| 404 | 璧勬簮涓嶅瓨鍦?|
| 500 | 鏈嶅姟鍣ㄩ敊璇?|

---

## 鏃ユ湡鏃堕棿鏍煎紡

鎵€鏈夋棩鏈熸椂闂村瓧娈电粺涓€浣跨敤 ISO 8601 鏍煎紡锛歚yyyy-MM-dd'T'HH:mm:ss`

绀轰緥锛歚2024-01-01T10:00:00`

---

## 涓€銆佺墿鏂欑鐞?API

### 1.1 鑾峰彇鐗╂枡鍒楄〃

**鎺ュ彛**: `GET /api/parts`

**璇锋眰鍙傛暟**:
| 鍙傛暟 | 绫诲瀷 | 蹇呭～ | 璇存槑 |
|------|------|------|------|
| keyword | string | 鍚?| 鎼滅储鍏抽敭璇嶏紙鐗╂枡缂栧彿/鍚嶇О锛?|
| categoryId | number | 否 | 分类ID（精确过滤） |
| categoryIds | string | 否 | 分类ID列表（逗号分隔，父分类含子分类场景） |
| page | number | 鍚?| 椤电爜锛岄粯璁? |
| size | number | 鍚?| 姣忛〉鏁伴噺锛岄粯璁?0 |

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "partNo": "MTR-2023-001",
        "partName": "涓績杞粍浠?,
        "specification": "CL-100",
        "stockQty": 50,
        "supplier": "渚涘簲鍟咥",
        "categoryName": "鏈烘闆朵欢",
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

### 1.2 鑾峰彇鐗╂枡璇︽儏

**鎺ュ彛**: `GET /api/parts/{id}`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "partNo": "MTR-2023-001",
    "partName": "涓績杞粍浠?,
    "specification": "CL-100",
    "stockQty": 50,
    "supplier": "渚涘簲鍟咥",
    "categoryId": 1,
    "categoryName": "鏈烘闆朵欢",
    "version": "V1.0"
  }
}
```

---

### 1.3 鍒涘缓鐗╂枡

**鎺ュ彛**: `POST /api/parts`

**蹇呭～瀛楁**:
| 瀛楁 | 绫诲瀷 | 璇存槑 |
|------|------|------|
| partNo | string | 鐗╂枡缂栧彿 |
| partName | string | 鐗╂枡鍚嶇О |
| specification | string | 瑙勬牸鍨嬪彿 |
| stockQty | number | 搴撳瓨鏁伴噺锛?=0锛?|
| supplier | string | 渚涘簲鍟?|

**璇锋眰浣?*:
```json
{
  "partNo": "MTR-2023-001",
  "partName": "涓績杞粍浠?,
  "specification": "CL-100",
  "stockQty": 50,
  "supplier": "渚涘簲鍟咥",
  "categoryId": 1
}
```

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "partNo": "MTR-2023-001",
    "partName": "涓績杞粍浠?,
    "specification": "CL-100",
    "stockQty": 50,
    "supplier": "渚涘簲鍟咥",
    "categoryId": 1,
    "version": "V1.0"
  }
}
```

**鏍￠獙澶辫触绀轰緥**:
```json
{
  "code": 400,
  "message": "瑙勬牸鍨嬪彿涓嶈兘涓虹┖",
  "data": null
}
```

---

### 1.4 鏇存柊鐗╂枡

**鎺ュ彛**: `PUT /api/parts/{id}`

**璇锋眰浣擄紙鏀寔閮ㄥ垎鏇存柊锛?*:
```json
{
  "supplier": "渚涘簲鍟咮"
}
```

> 璇存槑锛氬悗绔細鍏堝悎骞跺凡鏈夌墿鏂欐暟鎹悗鍐嶆牎楠岋紝鏈€缁堜粛闇€婊¤冻鐗╂枡蹇呭～瀛楁绾︽潫銆?
**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "partNo": "MTR-2023-001",
    "partName": "涓績杞粍浠?,
    "specification": "CL-100",
    "stockQty": 50,
    "supplier": "渚涘簲鍟咥",
    "categoryId": 1,
    "version": "V1.0"
  }
}
```

---

### 1.5 鍒犻櫎鐗╂枡

**鎺ュ彛**: `DELETE /api/parts/{id}`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "鍒犻櫎鎴愬姛",
  "data": null
}
```

---

### 1.6 鑾峰彇鐗╂枡 BOM

**鎺ュ彛**: `GET /api/parts/{id}/bom`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 2,
      "partNo": "MTR-2023-042",
      "partName": "杞存壙鍗曞厓",
      "quantity": 2,
      "version": "V1.0",
      "children": [
        {
          "id": 4,
          "partNo": "MTR-2023-044",
          "partName": "杞存壙",
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

### 1.7 鏇存柊鐗╂枡 BOM

**鎺ュ彛**: `PUT /api/parts/{id}/bom`

**璇锋眰浣?*: 鍚?BOM 鍝嶅簲鏍煎紡鐨?data 瀛楁

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": null
}
```

---

## 浜屻€佺墿鏂欏垎绫?API

### 2.1 鑾峰彇鍒嗙被鏍?

**鎺ュ彛**: `GET /api/parts/categories`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": 1,
      "name": "鏈烘闆朵欢",
      "children": [
        {
          "id": 2,
          "name": "杞寸被",
          "children": []
        }
      ]
    }
  ]
}
```

---

### 2.2 鍒涘缓鍒嗙被

**鎺ュ彛**: `POST /api/parts/categories`

**璇锋眰浣?*:
```json
{
  "name": "鏂板垎绫?,
  "parentId": 1
}
```

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 3,
    "name": "鏂板垎绫?,
    "parentId": 1
  }
}
```

---

### 2.3 鏇存柊鍒嗙被

**鎺ュ彛**: `PUT /api/parts/categories/{id}`

**璇锋眰浣?*:
```json
{
  "name": "鏇存柊鍚庣殑鍒嗙被鍚?
}
```

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "name": "鏇存柊鍚庣殑鍒嗙被鍚?
  }
}
```

---

### 2.4 鍒犻櫎鍒嗙被

**鎺ュ彛**: `DELETE /api/parts/categories/{id}`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "鍒犻櫎鎴愬姛",
  "data": null
}
```

---

## 涓夈€佺増鏈鐞?API

### 3.1 鑾峰彇鐗堟湰鍘嗗彶

**鎺ュ彛**: `GET /api/parts/{id}/versions`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "version": "V1.0",
      "description": "鍒濆鐗堟湰",
      "status": "published",
      "createdBy": "寮犱笁",
      "createdAt": "2024-01-01T10:00:00"
    }
  ]
}
```

---

### 3.2 鍒涘缓鏂扮増鏈?

**鎺ュ彛**: `POST /api/parts/{id}/versions`

**璇锋眰浣?*:
```json
{
  "description": "鐗堟湰璇存槑"
}
```

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "version": "V2.0",
    "description": "鐗堟湰璇存槑",
    "status": "draft",
    "createdBy": "寮犱笁",
    "createdAt": "2024-01-02T10:00:00"
  }
}
```

---

### 3.3 鐗堟湰瀵规瘮

**鎺ュ彛**: `GET /api/parts/{id}/versions/compare`

**璇锋眰鍙傛暟**:
| 鍙傛暟 | 绫诲瀷 | 蹇呭～ | 璇存槑 |
|------|------|------|------|
| v1 | string | 鏄?| 鐗堟湰1 |
| v2 | string | 鏄?| 鐗堟湰2 |

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "v1": {
      "version": "V1.0",
      "partName": "涓績杞粍浠?,
      "specification": "CL-100"
    },
    "v2": {
      "version": "V2.0",
      "partName": "涓績杞粍浠?,
      "specification": "CL-200"
    }
  }
}
```

---

## 鍥涖€佽澶囩鐞?API

### 4.1 鑾峰彇璁惧鍒楄〃
**鎺ュ彛**: `GET /api/equipments`

**璇锋眰鍙傛暟**: keyword, page, size

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "records": [
      {
        "id": 1,
        "equipmentNo": "EQ-001",
        "equipmentName": "鏁版帶杞﹀簥",
        "model": "CNC-100",
        "manufacturer": "鍘傚A",
        "status": "running",
        "location": "杞﹂棿A"
      }
    ],
    "total": 50,
    "size": 10,
    "current": 1,
    "pages": 5
  }
}
```

**鐘舵€佹灇涓?*: running | idle | maintenance | fault

### 4.2 鍒涘缓璁惧
**鎺ュ彛**: `POST /api/equipments`

**璇锋眰浣?*:
```json
{
  "equipmentNo": "EQ-001",
  "equipmentName": "鏁版帶杞﹀簥",
  "model": "CNC-100",
  "manufacturer": "鍘傚A",
  "status": "idle",
  "location": "杞﹂棿A"
}
```

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "equipmentNo": "EQ-001",
    "equipmentName": "鏁版帶杞﹀簥",
    "model": "CNC-100",
    "manufacturer": "鍘傚A",
    "status": "idle",
    "location": "杞﹂棿A"
  }
}
```

### 4.3 鏇存柊璁惧
**鎺ュ彛**: `PUT /api/equipments/{id}`

**璇锋眰浣?*: 鍚屽垱寤鸿澶?

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": 1,
    "equipmentNo": "EQ-001",
    "equipmentName": "鏁版帶杞﹀簥",
    "model": "CNC-100",
    "manufacturer": "鍘傚A",
    "status": "running",
    "location": "杞﹂棿A"
  }
}
```

### 4.4 鍒犻櫎璁惧
**鎺ュ彛**: `DELETE /api/equipments/{id}`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "鍒犻櫎鎴愬姛",
  "data": null
}
```

---

## 浜斻€佸伐搴忛厤缃?API

### 5.1 鑾峰彇宸ュ簭鍒楄〃
**鎺ュ彛**: `GET /api/procedures`

**璇存槑**:
- 杩斿洖 `WorkingProcedure` 妯″瀷鏍稿績瀛楁锛屽苟琛ュ厖 `productionAndTestingEquipment`锛堢敱 `WorkingProcedure_Equipment` 鍏崇郴鑱氬悎璁惧鍚嶇О锛夈€?- 褰?xDM 灏氭棤宸ュ簭鏁版嵁鏃讹紝鍚庣鍥為€€杩斿洖榛樿 5 閬撳伐搴忥紙姣涘澂鍒堕€犮€佺矖鍔犲伐銆佺簿鍔犲伐銆佹娴嬨€佸叆搴擄級銆?- 鏃堕棿瀛楁 `startTime/endTime` 缁熶竴浠ュ悗绔鑼冨寲瀛楃涓茶繑鍥烇紙绀轰緥锛歚2026-03-13T09:00:00.000+0800`锛夈€?
**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "990000000000003",
      "procedureCode": "WP-TEST-001",
      "procedureName": "WorkingProcedure_Test_001",
      "productionStep": "step-1",
      "productionAndTestingEquipment": "CNC-01銆佷笁鍧愭爣妫€娴嬩华",
      "operatorName": "寮犱笁",
      "startTime": "2026-03-13T09:00:00.000+0800",
      "endTime": "2026-03-13T10:00:00.000+0800"
    }
  ]
}
```

### 5.2 鏇存柊宸ュ簭
**鎺ュ彛**: `PUT /api/procedures/{id}`

**璇锋眰浣撶ず渚?*:
```json
{
  "procedureCode": "WP-TEST-001",
  "procedureName": "WorkingProcedure_Test_001",
  "productionStep": "step-1",
  "operatorName": "寮犱笁",
  "startTime": "2026-03-13 09:00:00",
  "endTime": "2026-03-13 10:00:00"
}
```

**鏃堕棿杈撳叆鍏煎**:
- 鏀寔锛歚yyyy-MM-dd HH:mm:ss`
- 鏀寔锛歚yyyy-MM-ddTHH:mm:ss(.SSS)+0800`锛堟垨 `+08:00`锛屽悗绔細褰掍竴鍖栵級

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "990000000000003",
    "procedureCode": "WP-TEST-001",
    "procedureName": "WorkingProcedure_Test_001",
    "productionStep": "step-1",
    "productionAndTestingEquipment": "CNC-01銆佷笁鍧愭爣妫€娴嬩华",
    "operatorName": "寮犱笁",
    "startTime": "2026-03-13T09:00:00.000+0800",
    "endTime": "2026-03-13T10:00:00.000+0800"
  }
}
```

---

## 鍏€佸伐鑹鸿矾绾跨鐞?API

### 6.1 鑾峰彇宸ヨ壓璺嚎鍒楄〃
**鎺ュ彛**: `GET /api/working-plans`

**璇锋眰鍙傛暟**: `keyword`锛堝彲閫夛紝鎸夊伐鑹虹紪鍙?宸ヨ壓鍚嶇О杩囨护锛?
**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": [
    {
      "id": "873975767468347392",
      "code": "WP-2026-001",
      "name": "涓績杞浂浠跺姞宸?,
      "version": "1.0",
      "product": "涓績杞粍浠?,
      "description": "涓績杞浂浠跺姞宸ユ爣鍑嗗伐鑹?,
      "operator": "寮犲伐",
      "equipment": "CNC-01, CMM-02",
      "operationTime": "2026-03-13 10:00:00"
    }
  ]
}
```

### 6.2 鑾峰彇宸ヨ壓璺嚎璇︽儏
**鎺ュ彛**: `GET /api/working-plans/{id}`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "873975767468347392",
    "code": "WP-2026-001",
    "name": "涓績杞浂浠跺姞宸?,
    "version": "1.0",
    "product": "涓績杞粍浠?,
    "description": "涓績杞浂浠跺姞宸ユ爣鍑嗗伐鑹?,
    "operator": "寮犲伐",
    "equipment": "CNC-01, CMM-02",
    "operationTime": "2026-03-13 10:00:00"
  }
}
```

### 6.3 鍒涘缓宸ヨ壓璺嚎
**鎺ュ彛**: `POST /api/working-plans`

**璇锋眰浣撶ず渚?*:
```json
{
  "code": "WP-2026-001",
  "name": "涓績杞浂浠跺姞宸?,
  "version": "1.0",
  "product": "涓績杞粍浠?,
  "description": "涓績杞浂浠跺姞宸ユ爣鍑嗗伐鑹?,
  "operator": "寮犲伐",
  "equipment": "CNC-01, CMM-02",
  "operationTime": "2026-03-13 10:00:00"
}
```

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "873975767468347392",
    "code": "WP-2026-001",
    "name": "涓績杞浂浠跺姞宸?,
    "version": "1.0",
    "product": "涓績杞粍浠?,
    "description": "涓績杞浂浠跺姞宸ユ爣鍑嗗伐鑹?,
    "operator": "寮犲伐",
    "equipment": "CNC-01, CMM-02",
    "operationTime": "2026-03-13 10:00:00"
  }
}
```

### 6.4 鏇存柊宸ヨ壓璺嚎
**鎺ュ彛**: `PUT /api/working-plans/{id}`

**璇锋眰浣?*: 鍚屽垱寤烘帴鍙ｏ紝鍙寜闇€浼犲叆閮ㄥ垎瀛楁銆?
**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "id": "873975767468347392",
    "code": "WP-2026-001",
    "name": "涓績杞浂浠跺姞宸?宸叉洿鏂?,
    "version": "1.1",
    "product": "涓績杞粍浠?,
    "description": "涓績杞浂浠跺姞宸ユ爣鍑嗗伐鑹?鏇存柊",
    "operator": "鏉庡伐",
    "equipment": "CNC-03, CMM-02",
    "operationTime": "2026-03-14 09:30:00"
  }
}
```

### 6.5 鍒犻櫎宸ヨ壓璺嚎
**鎺ュ彛**: `DELETE /api/working-plans/{id}`

**鍝嶅簲绀轰緥**:
```json
{
  "code": 200,
  "message": "鍒犻櫎鎴愬姛",
  "data": null
}
```

### 6.6 鑾峰彇宸ヨ壓娴佺▼
**鎺ュ彛**: `GET /api/working-plans/{id}/processes`

### 6.7 鏇存柊宸ヨ壓娴佺▼
**鎺ュ彛**: `PUT /api/working-plans/{id}/processes`

### 6.8 杩藉姞宸ュ簭鍒板伐鑹鸿矾绾?**鎺ュ彛**: `POST /api/working-plans/{id}/procedures`

---

## 涓冦€侀敊璇爜

| 閿欒鐮?| 璇存槑 |
|--------|------|
| 200 | 鎴愬姛 |
| 400 | 鍙傛暟閿欒 |
| 404 | 璧勬簮涓嶅瓨鍦?|
| 500 | 鏈嶅姟鍣ㄩ敊璇?|

---

## 鍏€佹祴璇曟暟鎹姹?

1. 鐗╂枡: 鑷冲皯5鏉★紝鍖呭惈"涓績杞粍浠?
2. 璁惧: 鑷冲皯3鏉?
3. 宸ヨ壓璺嚎: "涓績杞浂浠跺姞宸?V1.0"锛屽寘鍚畬鏁?閬撳伐搴?
4. 宸ュ簭: 鍥哄畾5閬?

---

## 涔濄€亁DM-F 妯″瀷鏄犲皠璇存槑

### 9.1 鏋舵瀯璇存槑

鏈」鐩噰鐢ㄤ笁灞傛灦鏋勶紝鎵€鏈夋暟鎹搷浣滃繀椤婚€氳繃 xDM-F API锛?

```
鍓嶇 鈫?鍚庣 Spring Boot 鈫?xDM-F 杩愯鎬?API 鈫?xDM-F 鏁版嵁妯″瀷
```

鍚庣闇€瑕佸皢鍓嶇 API 瀛楁鏄犲皠鍒?xDM-F 妯″瀷瀛楁銆?

---

### 9.2 鐗╂枡绠＄悊鏄犲皠锛圥art锛?

**xDM-F 妯″瀷**: `Part`

| 鍓嶇 API 瀛楁 | xDM-F 瀛楁 | 绫诲瀷 | 璇存槑 |
|--------------|-----------|------|------|
| id | id | string | 鐗╂枡ID |
| partNo | partCode | string | 鐗╂枡缂栧彿 |
| partName | partName | string | 鐗╂枡鍚嶇О |
| specification | specModel | string | 瑙勬牸鍨嬪彿 |
| stockQty | stockQty | number | 搴撳瓨鏁伴噺 |
| supplier | supplier | string | 渚涘簲鍟?|
| categoryId | - | number | 分类ID（前端使用） |
| categoryName | categoryPath | string | 鍒嗙被璺緞 |
| version | versionNo | string | 鐗堟湰鍙?|

**xDM-F API 璋冪敤**:
- 鍒涘缓: `POST /rdm_{appId}_app/services/dynamic/api/Part/create`
- 鏌ヨ: `POST /rdm_{appId}_app/services/dynamic/api/Part/query`
- 鏇存柊: `POST /rdm_{appId}_app/services/dynamic/api/Part/update`
- 鍒犻櫎: `POST /rdm_{appId}_app/services/dynamic/api/Part/delete`

---

### 9.3 BOM 鍏崇郴鏄犲皠锛圥art_Part锛?

**xDM-F 鍏崇郴**: `Part_Part`

| 鍓嶇 API 瀛楁 | xDM-F 瀛楁 | 璇存槑 |
|--------------|-----------|------|
| id | id | 瀛愮墿鏂橧D |
| partNo | target.partCode | 瀛愮墿鏂欑紪鍙?|
| partName | target.partName | 瀛愮墿鏂欏悕绉?|
| quantity | - | 鏁伴噺锛堝叧绯诲睘鎬э級 |
| version | target.versionNo | 鐗堟湰鍙?|
| children | - | 閫掑綊瀛愯妭鐐?|

**xDM-F API 璋冪敤**:
- 鍒涘缓鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/Part_Part/create`
- 鏌ヨ鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/Part_Part/query`

---

### 9.4 璁惧绠＄悊鏄犲皠锛圗quipment锛?

**xDM-F 妯″瀷**: `Equipment`

| 鍓嶇 API 瀛楁 | xDM-F 瀛楁 | 绫诲瀷 | 璇存槑 |
|--------------|-----------|------|------|
| id | id | string | 璁惧ID |
| equipmentNo | equipmentCode | string | 璁惧缂栫爜 |
| equipmentName | equipmentName | string | 璁惧鍚嶇О |
| model | specModel | string | 瑙勬牸鍨嬪彿 |
| manufacturer | manufacturer | string | 鐢熶骇鍘傚 |
| status | - | string | 璁惧鐘舵€侊紙鍓嶇鎵╁睍锛?|
| location | location | string | 浣嶇疆 |
| brand | brand | string | 鍝佺墝 |
| supplier | supplier | string | 渚涘簲鍟?|
| productionDate | productionDate | date | 鐢熶骇鏃ユ湡 |
| serviceLifeYears | serviceLifeYears | number | 浣跨敤骞撮檺 |
| depreciationMethod | depreciationMethod | string | 鎶樻棫鏂瑰紡 |
| technicalParams | technicalParams | string | 鎶€鏈弬鏁颁俊鎭?|
| sparePartsInfo | sparePartsInfo | string | 澶囧搧澶囦欢淇℃伅 |

**xDM-F API 璋冪敤**:
- 鍒涘缓: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/create`
- 鏌ヨ: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/query`
- 鏇存柊: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/update`
- 鍒犻櫎: `POST /rdm_{appId}_app/services/dynamic/api/Equipment/delete`

---


### 9.5 宸ュ簭閰嶇疆鏄犲皠锛圵orkingProcedure锛?

**xDM-F 妯″瀷**: `WorkingProcedure`

| 鍓嶇 API 瀛楁 | xDM-F 瀛楁 | 绫诲瀷 | 璇存槑 |
|--------------|-----------|------|------|
| id | id | string | 宸ュ簭ID |
| name | procedureName | string | 宸ュ簭鍚嶇О |
| order | - | number | 宸ュ簭椤哄簭锛堝墠绔墿灞曪級 |
| procedureCode | procedureCode | string | 宸ュ簭缂栧彿 |
| productionStep | productionStep | string | 鐢熶骇姝ラ |
| productionAndTestingEquipment | - | string | 鐢熶骇鍜屾娴嬭澶囷紙鐢?`WorkingProcedure_Equipment` 鍏崇郴鑱氬悎锛?|
| operatorName | operatorName | string | 鎿嶄綔浜哄憳 |
| startTime | startTime | datetime | 寮€濮嬫椂闂?|
| endTime | endTime | datetime | 缁撴潫鏃堕棿 |

**鍥哄畾5閬撳伐搴?*:
1. 姣涘澂鍒堕€?
2. 绮楀姞宸?
3. 绮惧姞宸?
4. 妫€娴?
5. 鍏ュ簱

**xDM-F API 璋冪敤**:
- 鍒涘缓: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure/create`
- 鏌ヨ: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure/query`

---

### 9.6 宸ヨ壓璺嚎鏄犲皠锛圵orkingPlan锛?

**xDM-F 妯″瀷**: `WorkingPlan`

| 鍓嶇 API 瀛楁 | xDM-F 瀛楁 | 绫诲瀷 | 璇存槑 |
|--------------|-----------|------|------|
| id | id | string | 宸ヨ壓ID |
| planName | planName | string | 宸ヨ壓鍚嶇О |
| planCode | planCode | string | 宸ヨ壓缂栧彿 |
| partId | - | number | 鐗╂枡ID锛堝墠绔叧鑱旓級 |
| partName | - | string | 鐗╂枡鍚嶇О锛堝墠绔樉绀猴級 |
| status | - | string | 鐘舵€侊紙鍓嶇鎵╁睍锛?|
| versionNo | versionNo | string | 鐗堟湰鍙?|
| belongProduct | belongProduct | string | 鎵€灞炰骇鍝?|
| operatorName | operatorName | string | 鎿嶄綔浜哄憳 |
| operationTime | operationTime | datetime | 鎿嶄綔鏃堕棿 |
| equipmentUsage | equipmentUsage | string | 璁惧浣跨敤鎯呭喌 |
| procedures | - | array | 鍏宠仈鐨勫伐搴忓垪琛?|

**xDM-F API 璋冪敤**:
- 鍒涘缓: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/create`
- 鏌ヨ: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/query`
- 鏇存柊: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/update`
- 鍒犻櫎: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan/delete`

---

### 9.7 宸ヨ壓-宸ュ簭鍏崇郴鏄犲皠锛圵orkingPlan_WorkingProcedure锛?

**xDM-F 鍏崇郴**: `WorkingPlan_WorkingProcedure`

| 鍓嶇 API 瀛楁 | xDM-F 瀛楁 | 璇存槑 |
|--------------|-----------|------|
| procedureId | target.id | 宸ュ簭ID |
| name | target.procedureName | 宸ュ簭鍚嶇О |
| order | - | 宸ュ簭椤哄簭锛堝叧绯诲睘鎬э級 |
| equipmentId | - | 璁惧ID锛堥€氳繃 WorkingProcedure_Equipment 鍏崇郴锛?|
| equipmentName | - | 璁惧鍚嶇О |
| duration | - | 宸ユ椂锛堝叧绯诲睘鎬э級 |

**xDM-F API 璋冪敤**:
- 鍒涘缓鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan_WorkingProcedure/create`
- 鏌ヨ鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/WorkingPlan_WorkingProcedure/query`

---

### 9.8 宸ュ簭-璁惧鍏崇郴鏄犲皠锛圵orkingProcedure_Equipment锛?

**xDM-F 鍏崇郴**: `WorkingProcedure_Equipment`

**xDM-F API 璋冪敤**:
- 鍒涘缓鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Equipment/create`
- 鏌ヨ鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Equipment/query`

---

### 9.9 宸ュ簭-鐗╂枡鍏崇郴鏄犲皠锛圵orkingProcedure_Part锛?

**xDM-F 鍏崇郴**: `WorkingProcedure_Part`

**xDM-F API 璋冪敤**:
- 鍒涘缓鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Part/create`
- 鏌ヨ鍏崇郴: `POST /rdm_{appId}_app/services/dynamic/api/WorkingProcedure_Part/query`

---

### 9.10 鍚庣瀹炵幇娉ㄦ剰浜嬮」

1. **缁熶竴鍝嶅簲灏佽**: 鎵€鏈?xDM-F API 杩斿洖闇€瑕佸寘瑁呮垚 `{ code, message, data }` 鏍煎紡
2. **瀛楁鏄犲皠**: 鍚庣 Service 灞傝礋璐ｅ墠绔瓧娈靛拰 xDM-F 瀛楁鐨勮浆鎹?
3. **鍒嗛〉澶勭悊**: xDM-F 鏌ヨ缁撴灉闇€瑕佽浆鎹负鍓嶇鍒嗛〉鏍煎紡
4. **鍏崇郴鏌ヨ**: 闇€瑕侀€氳繃澶氭 API 璋冪敤缁勮瀹屾暣鐨勪笟鍔″璞★紙濡?BOM 鏍戙€佸伐鑹鸿矾绾胯鎯咃級
5. **閿欒澶勭悊**: xDM-F API 閿欒闇€瑕佽浆鎹负鍓嶇缁熶竴閿欒鏍煎紡
6. **鏃ユ湡鏍煎紡**: xDM-F 鐨?Timestamp 闇€瑕佽浆鎹负 ISO 8601 鏍煎紡瀛楃涓?

---





