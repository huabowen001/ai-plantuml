# AI PlantUML Generator - 快速部署指南

## 前置要求

- Java 11 或更高版本
- Maven 3.6 或更高版本
- MySQL 8.0 或更高版本
- （可选）OpenAI API Key 或兼容的AI API

## 快速开始（5分钟部署）

### 步骤1：创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE plantuml_ai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出MySQL
exit;

# 导入schema
mysql -u root -p plantuml_ai < src/main/resources/schema.sql
```

### 步骤2：配置应用

编辑 `src/main/resources/config.properties`：

```properties
# 最小配置 - 修改这3项
db.url=jdbc:mysql://localhost:3306/plantuml_ai?useSSL=false&serverTimezone=UTC
db.username=root
db.password=YOUR_DB_PASSWORD

# JWT密钥 - 请修改为随机字符串
jwt.secret=CHANGE_THIS_TO_A_RANDOM_SECRET_KEY_123456789

# AI API（可选，不配置将使用示例生成器）
ai.api.key=your-openai-api-key-here
```

### 步骤3：构建和运行

```bash
# 构建项目
mvn clean package -DskipTests

# 使用Jetty运行（开发环境）
mvn jetty:run

# 或者部署到Tomcat
# 将 target/plantuml.war 复制到 Tomcat 的 webapps 目录
```

### 步骤4：访问应用

打开浏览器访问：
```
http://localhost:8080/plantuml/ai
```

默认测试账户：
- 用户名：`admin` 或 `demo`
- 密码：`password123`

## Docker部署（推荐生产环境）

### 创建docker-compose.yml

```yaml
version: '3.8'

services:
  mysql:
    image: mysql:8.0
    environment:
      MYSQL_ROOT_PASSWORD: rootpassword
      MYSQL_DATABASE: plantuml_ai
    volumes:
      - mysql_data:/var/lib/mysql
      - ./src/main/resources/schema.sql:/docker-entrypoint-initdb.d/schema.sql
    ports:
      - "3306:3306"
    networks:
      - plantuml-network

  plantuml-app:
    build:
      context: .
      dockerfile: Dockerfile.jetty
    environment:
      DB_URL: jdbc:mysql://mysql:3306/plantuml_ai?useSSL=false&serverTimezone=UTC
      DB_USERNAME: root
      DB_PASSWORD: rootpassword
      JWT_SECRET: your-super-secret-jwt-key-change-this
      AI_API_KEY: your-openai-api-key
    ports:
      - "8080:8080"
    depends_on:
      - mysql
    networks:
      - plantuml-network

volumes:
  mysql_data:

networks:
  plantuml-network:
```

### 启动服务

```bash
# 启动所有服务
docker-compose up -d

# 查看日志
docker-compose logs -f

# 停止服务
docker-compose down
```

## Kubernetes部署

### 1. 创建ConfigMap

```yaml
apiVersion: v1
kind: ConfigMap
metadata:
  name: plantuml-config
data:
  db.url: "jdbc:mysql://mysql-service:3306/plantuml_ai"
  db.username: "root"
  jwt.expiration: "86400000"
```

### 2. 创建Secret

```yaml
apiVersion: v1
kind: Secret
metadata:
  name: plantuml-secret
type: Opaque
stringData:
  db.password: "your-db-password"
  jwt.secret: "your-jwt-secret"
  ai.api.key: "your-openai-api-key"
```

### 3. 部署应用

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: plantuml-ai
spec:
  replicas: 3
  selector:
    matchLabels:
      app: plantuml-ai
  template:
    metadata:
      labels:
        app: plantuml-ai
    spec:
      containers:
      - name: plantuml-ai
        image: plantuml-ai-server:latest
        ports:
        - containerPort: 8080
        env:
        - name: DB_URL
          valueFrom:
            configMapKeyRef:
              name: plantuml-config
              key: db.url
        - name: DB_USERNAME
          valueFrom:
            configMapKeyRef:
              name: plantuml-config
              key: db.username
        - name: DB_PASSWORD
          valueFrom:
            secretKeyRef:
              name: plantuml-secret
              key: db.password
        - name: JWT_SECRET
          valueFrom:
            secretKeyRef:
              name: plantuml-secret
              key: jwt.secret
        - name: AI_API_KEY
          valueFrom:
            secretKeyRef:
              name: plantuml-secret
              key: ai.api.key
---
apiVersion: v1
kind: Service
metadata:
  name: plantuml-service
spec:
  selector:
    app: plantuml-ai
  ports:
  - protocol: TCP
    port: 80
    targetPort: 8080
  type: LoadBalancer
```

## 环境变量配置

支持通过环境变量覆盖配置文件：

| 环境变量 | 说明 | 默认值 |
|---------|------|--------|
| `DB_URL` | 数据库URL | - |
| `DB_USERNAME` | 数据库用户名 | root |
| `DB_PASSWORD` | 数据库密码 | - |
| `JWT_SECRET` | JWT密钥 | - |
| `JWT_EXPIRATION` | Token过期时间(ms) | 86400000 |
| `AI_API_URL` | AI API地址 | https://api.openai.com/v1/chat/completions |
| `AI_API_KEY` | AI API密钥 | - |
| `AI_MODEL` | AI模型 | gpt-3.5-turbo |

## 使用其他AI API

### 使用Azure OpenAI

```properties
ai.api.url=https://YOUR-RESOURCE.openai.azure.com/openai/deployments/YOUR-DEPLOYMENT/chat/completions?api-version=2023-05-15
ai.api.key=your-azure-api-key
```

修改 `AIService.java` 中的请求头：
```java
httpPost.setHeader("api-key", API_KEY);  // 替换 "Authorization"
```

### 使用国内AI服务

#### 智谱AI（ChatGLM）
```properties
ai.api.url=https://open.bigmodel.cn/api/paas/v4/chat/completions
ai.api.key=your-zhipu-api-key
ai.model=glm-4
```

#### 百度文心
```properties
ai.api.url=https://aip.baidubce.com/rpc/2.0/ai_custom/v1/wenxinworkshop/chat/completions
ai.api.key=your-baidu-api-key
```

## 生产环境配置建议

### 1. 数据库优化

```sql
-- 增加连接数
SET GLOBAL max_connections = 500;

-- 启用查询缓存
SET GLOBAL query_cache_size = 67108864;
SET GLOBAL query_cache_type = 1;

-- 优化InnoDB
SET GLOBAL innodb_buffer_pool_size = 1G;
SET GLOBAL innodb_log_file_size = 256M;
```

### 2. 应用配置优化

修改 `config.properties`：

```properties
# 数据库连接池
db.pool.initialSize=10
db.pool.maxTotal=100
db.pool.maxIdle=50
db.pool.minIdle=10

# JWT配置
jwt.expiration=3600000  # 1小时（生产环境建议缩短）
```

### 3. JVM参数优化

```bash
export JAVA_OPTS="-Xms2g -Xmx4g -XX:+UseG1GC -XX:MaxGCPauseMillis=200"
```

### 4. Nginx反向代理

```nginx
upstream plantuml_backend {
    server localhost:8080;
    server localhost:8081;
    server localhost:8082;
}

server {
    listen 80;
    server_name plantuml.yourdomain.com;

    # HTTPS redirect
    return 301 https://$server_name$request_uri;
}

server {
    listen 443 ssl http2;
    server_name plantuml.yourdomain.com;

    ssl_certificate /path/to/cert.pem;
    ssl_certificate_key /path/to/key.pem;

    location / {
        proxy_pass http://plantuml_backend;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
        
        # WebSocket support (if needed)
        proxy_http_version 1.1;
        proxy_set_header Upgrade $http_upgrade;
        proxy_set_header Connection "upgrade";
    }

    # Static files cache
    location ~* \.(css|js|jpg|jpeg|png|gif|ico|svg)$ {
        proxy_pass http://plantuml_backend;
        expires 30d;
        add_header Cache-Control "public, immutable";
    }
}
```

## 监控和日志

### 1. 启用应用日志

编辑 `src/main/resources/log4j.properties`：

```properties
log4j.rootLogger=INFO, file, console

log4j.appender.console=org.apache.log4j.ConsoleAppender
log4j.appender.console.layout=org.apache.log4j.PatternLayout
log4j.appender.console.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n

log4j.appender.file=org.apache.log4j.RollingFileAppender
log4j.appender.file.File=/var/log/plantuml-ai/app.log
log4j.appender.file.MaxFileSize=10MB
log4j.appender.file.MaxBackupIndex=10
log4j.appender.file.layout=org.apache.log4j.PatternLayout
log4j.appender.file.layout.ConversionPattern=%d{yyyy-MM-dd HH:mm:ss} %-5p %c{1}:%L - %m%n
```

### 2. 数据库慢查询

```sql
-- 启用慢查询日志
SET GLOBAL slow_query_log = 'ON';
SET GLOBAL long_query_time = 1;
SET GLOBAL slow_query_log_file = '/var/log/mysql/slow.log';
```

## 备份策略

### 数据库备份脚本

```bash
#!/bin/bash
# backup.sh

BACKUP_DIR="/backup/plantuml"
DATE=$(date +%Y%m%d_%H%M%S)
DB_NAME="plantuml_ai"
DB_USER="root"
DB_PASS="your-password"

mkdir -p $BACKUP_DIR

# 备份数据库
mysqldump -u$DB_USER -p$DB_PASS $DB_NAME > $BACKUP_DIR/backup_$DATE.sql

# 压缩
gzip $BACKUP_DIR/backup_$DATE.sql

# 删除7天前的备份
find $BACKUP_DIR -name "backup_*.sql.gz" -mtime +7 -delete

echo "Backup completed: $BACKUP_DIR/backup_$DATE.sql.gz"
```

### 定时备份（crontab）

```bash
# 每天凌晨2点备份
0 2 * * * /path/to/backup.sh
```

## 故障排查

### 问题1：无法连接数据库

```bash
# 检查MySQL状态
systemctl status mysql

# 检查端口
netstat -tlnp | grep 3306

# 测试连接
mysql -h localhost -u root -p plantuml_ai
```

### 问题2：JWT验证失败

```bash
# 检查系统时间
date

# 同步时间
ntpdate time.windows.com
```

### 问题3：AI API调用失败

```bash
# 测试API连接
curl -X POST https://api.openai.com/v1/chat/completions \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"model":"gpt-3.5-turbo","messages":[{"role":"user","content":"test"}]}'
```

## 性能测试

### 使用Apache Bench

```bash
# 登录接口测试
ab -n 1000 -c 10 -p login.json -T application/json \
  http://localhost:8080/plantuml/api/auth/login

# 生成图表测试
ab -n 100 -c 5 -p convert.json -T application/json \
  -H "Authorization: Bearer YOUR_TOKEN" \
  http://localhost:8080/plantuml/api/ai/convert
```

## 安全加固

1. **修改默认密码**：删除或修改schema.sql中的测试账户
2. **启用HTTPS**：生产环境必须使用HTTPS
3. **防火墙配置**：只开放必要的端口
4. **SQL注入防护**：已使用PreparedStatement
5. **XSS防护**：前端已做HTML转义
6. **CSRF防护**：可以添加CSRF Token
7. **限流**：建议添加接口限流
8. **审计日志**：记录重要操作

## 许可证

继承原PlantUML Server项目的开源协议。

