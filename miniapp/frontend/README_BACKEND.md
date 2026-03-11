# 前端项目交付文档 - 后端对接指南

## 一、环境要求

### 必需软件
- Node.js >= 18.0.0
- npm >= 9.0.0

### 安装步骤
```bash
# 1. 进入前端目录
cd miniapp/frontend

# 2. 安装依赖
npm install

# 3. 启动开发服务器
npm run dev

# 4. 构建生产版本
npm run build
```

## 二、项目架构

### 技术栈
- Vue 3.5.13
- Vite 6.2.1
- Element Plus 2.13.5
- Tailwind CSS 3.4.17
- Axios 1.8.2
- vue3-tree-org 4.2.2

### 目录结构
```
miniapp/frontend/
├── src/
│   ├── api/              # API接口封装
│   ├── assets/           # 静态资源
│   ├── components/       # 组件
│   ├── router/           # 路由配置
│   ├── utils/            # 工具函数
│   ├── views/            # 页面
│   └── main.js           # 入口文件
├── .env.development      # 开发环境配置
├── .env.production       # 生产环境配置
└── package.json
```

## 三、API 对接配置

### 修改 API Base URL
编辑 `.env.development` 文件：
```
VITE_API_BASE_URL=http://your-backend-server:8080
```

### 当前 Mock 数据位置
所有 API 文件位于 `src/api/` 目录，当前使用 Mock 数据作为 fallback。

后端接口就绪后，Mock 数据会自动失效，直接调用真实接口。

## 四、跨域配置

如果遇到跨域问题，后端需要配置 CORS：

### Spring Boot 配置示例
```java
@Configuration
public class CorsConfig {
    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.addAllowedOrigin("http://localhost:5173");
        config.addAllowedMethod("*");
        config.addAllowedHeader("*");
        config.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsFilter(source);
    }
}
```

## 五、API 规范文档

详见：`API_DOCS/frontend_api_requirements.json`

### 通用响应格式

#### 成功响应
```json
{
  "code": 200,
  "message": "success",
  "data": {}
}
```

#### 分页响应
```json
{
  "records": [],
  "total": 100
}
```

#### 错误响应
```json
{
  "code": 400,
  "message": "错误信息"
}
```

## 六、联调测试清单

### 物料管理模块
- [ ] GET /api/parts - 物料列表
- [ ] POST /api/parts - 创建物料
- [ ] PUT /api/parts/{id} - 更新物料
- [ ] DELETE /api/parts/{id} - 删除物料
- [ ] GET /api/parts/{id}/bom - 获取BOM
- [ ] PUT /api/parts/{id}/bom - 更新BOM
- [ ] GET /api/parts/categories - 分类树
- [ ] GET /api/parts/{id}/versions - 版本历史

### 设备管理模块
- [ ] GET /api/equipments - 设备列表
- [ ] POST /api/equipments - 创建设备
- [ ] PUT /api/equipments/{id} - 更新设备
- [ ] DELETE /api/equipments/{id} - 删除设备

### 工艺路线模块
- [ ] GET /api/working-plans - 工艺路线列表
- [ ] GET /api/working-plans/{id} - 工艺路线详情
- [ ] POST /api/working-plans - 创建工艺路线
- [ ] PUT /api/working-plans/{id} - 更新工艺路线

### 工序配置模块
- [ ] GET /api/procedures - 工序列表（固定5道）

## 七、注意事项

1. **时间格式**：统一使用 `YYYY-MM-DD HH:mm:ss`
2. **ID类型**：建议使用数字类型
3. **分页参数**：page（页码，从1开始）、size（每页数量）
4. **搜索参数**：keyword（关键词搜索）
5. **固定工序名称**：毛坯制造、粗加工、精加工、检测、入库

## 八、联系方式

如有问题请联系前端开发团队。
