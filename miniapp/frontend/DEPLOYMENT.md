# 前端部署说明

## 开发环境启动

```bash
cd miniapp/frontend
npm install
npm run dev
```

访问: http://localhost:5173

## 生产环境构建

```bash
npm run build
```

构建产物在 `dist/` 目录

## Nginx 部署配置

```nginx
server {
    listen 80;
    server_name your-domain.com;

    root /var/www/idme/dist;
    index index.html;

    location / {
        try_files $uri $uri/ /index.html;
    }

    location /api {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 环境变量配置

修改 `.env.production` 中的后端地址：
```
VITE_API_BASE_URL=http://your-backend-server:8080
```
