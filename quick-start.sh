#!/bin/bash

# AI PlantUML Generator 快速启动脚本
# 此脚本将帮助你快速部署应用

set -e

echo "========================================="
echo "AI PlantUML Generator - 快速部署"
echo "========================================="
echo ""

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 检查Java版本
echo "检查Java版本..."
if ! command -v java &> /dev/null; then
    echo -e "${RED}错误: 未找到Java，请先安装JDK 11或更高版本${NC}"
    exit 1
fi

JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F. '{print $1}')
if [ "$JAVA_VERSION" -lt 11 ]; then
    echo -e "${RED}错误: Java版本过低，需要JDK 11或更高版本${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Java版本检查通过${NC}"

# 检查Maven
echo "检查Maven..."
if ! command -v mvn &> /dev/null; then
    echo -e "${RED}错误: 未找到Maven，请先安装Maven 3.6或更高版本${NC}"
    exit 1
fi
echo -e "${GREEN}✓ Maven检查通过${NC}"

# 检查MySQL
echo "检查MySQL..."
if ! command -v mysql &> /dev/null; then
    echo -e "${YELLOW}警告: 未找到MySQL客户端${NC}"
    echo "请确保MySQL服务器已安装并运行"
    read -p "是否已安装并启动MySQL？(y/n) " -n 1 -r
    echo
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo -e "${RED}请先安装并启动MySQL${NC}"
        exit 1
    fi
else
    echo -e "${GREEN}✓ MySQL检查通过${NC}"
fi

echo ""
echo "========================================="
echo "步骤1: 数据库配置"
echo "========================================="
echo ""

read -p "请输入MySQL主机地址 [127.0.0.1]: " DB_HOST
DB_HOST=${DB_HOST:-127.0.0.1}

read -p "请输入MySQL端口 [3306]: " DB_PORT
DB_PORT=${DB_PORT:-3306}

read -p "请输入MySQL用户名 [root]: " DB_USER
DB_USER=${DB_USER:-root}

read -sp "请输入MySQL密码: " DB_PASSWORD
DB_PASSWORD=${DB_PASSWORD:-123456}
echo ""

read -p "请输入数据库名 [plantuml_ai]: " DB_NAME
DB_NAME=${DB_NAME:-plantuml_ai}

# 测试数据库连接
echo "测试数据库连接..."
if mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" -e "SELECT 1" &> /dev/null; then
    echo -e "${GREEN}✓ 数据库连接成功${NC}"
else
    echo -e "${RED}错误: 无法连接到MySQL，请检查配置${NC}"
    exit 1
fi

# 创建数据库
echo "创建数据库..."
mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" -e "CREATE DATABASE IF NOT EXISTS $DB_NAME DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;" 2>/dev/null || true
echo -e "${GREEN}✓ 数据库创建完成${NC}"

# 导入schema
if [ -f "src/main/resources/schema.sql" ]; then
    echo "导入数据库schema..."
    mysql -h"$DB_HOST" -P"$DB_PORT" -u"$DB_USER" -p"$DB_PASSWORD" "$DB_NAME" < src/main/resources/schema.sql
    echo -e "${GREEN}✓ Schema导入完成${NC}"
    echo -e "${GREEN}已创建测试账户:${NC}"
    echo "  用户名: admin, 密码: password123"
    echo "  用户名: demo, 密码: password123"
else
    echo -e "${YELLOW}警告: 未找到schema.sql文件${NC}"
fi

echo ""
echo "========================================="
echo "步骤2: 应用配置"
echo "========================================="
echo ""

# 生成JWT密钥
JWT_SECRET=$(cat /dev/urandom | tr -dc 'a-zA-Z0-9' | fold -w 64 | head -n 1)
echo "已生成JWT密钥"

read -p "是否配置AI API？(y/n) [n]: " CONFIGURE_AI
CONFIGURE_AI=${CONFIGURE_AI:-n}

AI_API_URL="https://api.openai.com/v1/chat/completions"
AI_API_KEY="your-api-key-here"
AI_MODEL="gpt-3.5-turbo"

if [[ $CONFIGURE_AI =~ ^[Yy]$ ]]; then
    read -p "请输入AI API URL [${AI_API_URL}]: " INPUT_API_URL
    AI_API_URL=${INPUT_API_URL:-$AI_API_URL}
    
    read -sp "请输入AI API Key: " AI_API_KEY
    echo ""
    
    read -p "请输入AI模型名称 [${AI_MODEL}]: " INPUT_MODEL
    AI_MODEL=${INPUT_MODEL:-$AI_MODEL}
else
    echo "跳过AI API配置，将使用内置示例生成器"
fi

# 更新配置文件
echo "更新配置文件..."
cat > src/main/resources/config.properties << EOF
#PlantUML configuration file
SHOW_GITHUB_RIBBON=on

# Database Configuration
db.url=jdbc:mysql://${DB_HOST}:${DB_PORT}/${DB_NAME}?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=${DB_USER}
db.password=${DB_PASSWORD}
db.driver=com.mysql.cj.jdbc.Driver
db.pool.initialSize=5
db.pool.maxTotal=20
db.pool.maxIdle=10
db.pool.minIdle=5

# JWT Configuration
jwt.secret=${JWT_SECRET}
jwt.expiration=86400000

# AI API Configuration
ai.api.url=${AI_API_URL}
ai.api.key=${AI_API_KEY}
ai.model=${AI_MODEL}
EOF

echo -e "${GREEN}✓ 配置文件已更新${NC}"

echo ""
echo "========================================="
echo "步骤3: 构建项目"
echo "========================================="
echo ""

echo "开始构建项目（这可能需要几分钟）..."
mvn clean package -DskipTests

if [ $? -eq 0 ]; then
    echo -e "${GREEN}✓ 项目构建成功${NC}"
else
    echo -e "${RED}错误: 项目构建失败${NC}"
    exit 1
fi

echo ""
echo "========================================="
echo "步骤4: 启动应用"
echo "========================================="
echo ""

read -p "请选择启动方式 (1: Jetty开发模式, 2: 生成WAR包): " START_MODE

if [ "$START_MODE" = "1" ]; then
    echo ""
    echo -e "${GREEN}正在启动应用...${NC}"
    echo ""
    echo "应用将在以下地址可用："
    echo -e "${GREEN}  原版PlantUML: http://localhost:8080/plantuml/${NC}"
    echo -e "${GREEN}  AI版本: http://localhost:8080/plantuml/ai${NC}"
    echo ""
    echo "按 Ctrl+C 停止应用"
    echo ""
    mvn jetty:run
elif [ "$START_MODE" = "2" ]; then
    echo ""
    echo -e "${GREEN}WAR包已生成: target/plantuml.war${NC}"
    echo ""
    echo "部署到Tomcat的步骤："
    echo "1. 将 target/plantuml.war 复制到 Tomcat 的 webapps 目录"
    echo "2. 启动 Tomcat: \$TOMCAT_HOME/bin/startup.sh"
    echo "3. 访问: http://localhost:8080/plantuml/ai"
    echo ""
else
    echo "无效的选择"
    exit 1
fi

