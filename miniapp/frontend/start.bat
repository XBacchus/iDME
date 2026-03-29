@echo off
echo ================================
echo iDME 前端项目启动脚本
echo ================================
echo.

cd /d %~dp0

echo [1/3] 检查 Node.js...
node -v >nul 2>&1
if errorlevel 1 (
    echo 错误: 未安装 Node.js，请先安装 Node.js >= 18.0.0
    pause
    exit /b 1
)

echo [2/3] 安装依赖...
if not exist "node_modules" (
    echo 首次运行，正在安装依赖...
    call npm install
    if errorlevel 1 (
        echo 错误: 依赖安装失败
        pause
        exit /b 1
    )
) else (
    echo 依赖已安装，跳过
)

echo [3/3] 启动开发服务器...
echo.
echo 前端地址: http://localhost:5173
echo 按 Ctrl+C 停止服务器
echo.
call npm run dev

pause
