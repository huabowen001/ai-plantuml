# 构建和运行指南

## 前置准备

确保你的开发环境已安装：
- **JDK 11** 或更高版本
- **Maven 3.6** 或更高版本
- **MySQL 8.0** 或更高版本

## 步骤 1: 安装依赖

所有依赖已在 `pom.parent.xml` 中配置，执行以下命令安装：

```bash
# 清理并安装依赖
mvn clean install -DskipTests
```

## 步骤 2: 数据库设置

### 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 执行以下SQL
CREATE DATABASE plantuml_ai DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
exit;
```

### 导入数据表

```bash
# 导入schema.sql
mysql -u root -p plantuml_ai < src/main/resources/schema.sql
```

这将创建以下表：
- `users` - 用户表
- `diagram_history` - 图表历史记录表
- `tags` - 标签表
- `diagram_tags` - 图表标签关联表
- `diagram_shares` - 分享表

并插入两个测试账户：
- 用户名: `admin` / 密码: `password123`
- 用户名: `demo` / 密码: `password123`

## 步骤 3: 配置应用

编辑 `src/main/resources/config.properties`：

```properties
#PlantUML configuration file
SHOW_GITHUB_RIBBON=on

# 数据库配置 - 请修改为你的MySQL配置
db.url=jdbc:mysql://localhost:3306/plantuml_ai?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
db.username=root
db.password=your_mysql_password

# 数据库连接池配置
db.driver=com.mysql.cj.jdbc.Driver
db.pool.initialSize=5
db.pool.maxTotal=20
db.pool.maxIdle=10
db.pool.minIdle=5

# JWT配置 - 请修改jwt.secret为随机字符串
jwt.secret=YourSuperSecretKeyForJWTTokenGeneration123456789
jwt.expiration=86400000

# AI API配置（可选）
# 如果不配置，系统将使用内置的示例生成器
ai.api.url=https://api.openai.com/v1/chat/completions
ai.api.key=your-api-key-here
ai.model=gpt-3.5-turbo
```

**重要提示：**
1. 必须修改 `db.password` 为你的MySQL密码
2. 强烈建议修改 `jwt.secret` 为一个长随机字符串
3. `ai.api.key` 是可选的，不配置将使用内置示例

## 步骤 4: 构建项目

```bash
# 编译打包（跳过测试）
mvn clean package -DskipTests

# 或者运行测试
mvn clean package
```

编译成功后会生成：
- `target/plantuml.war` - WAR包，用于部署到Tomcat
- `target/classes/` - 编译后的类文件

## 步骤 5: 运行应用

### 方式1: 使用Maven Jetty插件（推荐开发环境）

```bash
mvn jetty:run
```

启动后访问：
- 原版PlantUML界面: http://localhost:8080/plantuml/
- AI版本界面: http://localhost:8080/plantuml/ai

### 方式2: 部署到Tomcat

```bash
# 复制WAR包到Tomcat
cp target/plantuml.war $TOMCAT_HOME/webapps/

# 启动Tomcat
$TOMCAT_HOME/bin/startup.sh

# 查看日志
tail -f $TOMCAT_HOME/logs/catalina.out
```

访问：http://localhost:8080/plantuml/ai

### 方式3: 使用Docker

```bash
# 构建Docker镜像
docker build -f Dockerfile.jetty -t plantuml-ai:latest .

# 运行容器
docker run -d \
  -p 8080:8080 \
  -e DB_URL="jdbc:mysql://host.docker.internal:3306/plantuml_ai" \
  -e DB_USERNAME="root" \
  -e DB_PASSWORD="your_password" \
  -e JWT_SECRET="your_jwt_secret" \
  --name plantuml-ai \
  plantuml-ai:latest

# 查看日志
docker logs -f plantuml-ai
```

**注意**：在Docker中访问宿主机的MySQL，使用 `host.docker.internal` 作为主机名。

## 验证安装

### 1. 检查应用是否运行

```bash
curl http://localhost:8080/plantuml/ai
```

应该返回HTML页面。

### 2. 测试注册接口

```bash
curl -X POST http://localhost:8080/plantuml/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "testuser",
    "email": "test@example.com",
    "password": "test123456"
  }'
```

成功响应：
```json
{
  "success": true,
  "message": "注册成功",
  "token": "eyJ...",
  "user": {
    "id": 3,
    "username": "testuser",
    "email": "test@example.com"
  }
}
```

### 3. 测试登录接口

```bash
curl -X POST http://localhost:8080/plantuml/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "password123"
  }'
```

### 4. 测试AI转换接口

```bash
# 先登录获取token
TOKEN=$(curl -s -X POST http://localhost:8080/plantuml/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"password123"}' \
  | grep -o '"token":"[^"]*"' | cut -d'"' -f4)

# 使用token调用AI转换
curl -X POST http://localhost:8080/plantuml/api/ai/convert \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "description": "创建一个简单的序列图，显示用户登录流程",
    "title": "用户登录",
    "saveHistory": true
  }'
```

## 常见问题排查

### 问题1: 编译错误 - 找不到依赖

**错误信息**:
```
The import com.google.gson cannot be resolved
The import javax.servlet cannot be resolved
```

**解决方法**:
```bash
# 清理并重新安装
mvn clean
mvn install -DskipTests

# 确保pom.parent.xml中的依赖都正确配置
```

注意：javax.servlet相关的错误在IDE中可能会显示，但实际运行时不会有问题，因为Servlet容器会提供这些类。

### 问题2: 无法连接数据库

**错误信息**:
```
java.sql.SQLException: Access denied for user 'root'@'localhost'
```

**解决方法**:
1. 检查 `config.properties` 中的数据库配置
2. 确认MySQL服务已启动: `systemctl status mysql`
3. 测试数据库连接: `mysql -u root -p plantuml_ai`
4. 检查MySQL用户权限:
```sql
GRANT ALL PRIVILEGES ON plantuml_ai.* TO 'root'@'localhost';
FLUSH PRIVILEGES;
```

### 问题3: AI生成失败

**错误信息**: "转换失败" 或超时

**解决方法**:
1. 检查 `config.properties` 中的AI API配置
2. 如果没有配置API key，系统会使用内置示例生成器
3. 检查网络连接到OpenAI API
4. 测试API key是否有效:
```bash
curl https://api.openai.com/v1/chat/completions \
  -H "Authorization: Bearer YOUR_API_KEY" \
  -H "Content-Type: application/json" \
  -d '{"model":"gpt-3.5-turbo","messages":[{"role":"user","content":"test"}]}'
```

### 问题4: 端口已被占用

**错误信息**:
```
Address already in use: bind
```

**解决方法**:
```bash
# 查找占用8080端口的进程
lsof -i :8080
# 或
netstat -tlnp | grep 8080

# 杀死进程
kill -9 <PID>

# 或者修改端口
mvn jetty:run -Djetty.http.port=9090
```

### 问题5: JWT Token无效

**错误信息**: "Token无效或已过期"

**原因和解决**:
1. Token过期（默认24小时）- 重新登录
2. jwt.secret配置不一致 - 检查配置文件
3. 系统时间不正确 - 同步系统时间

## 开发环境配置

### IDE配置（IntelliJ IDEA）

1. 导入项目:
   - File → Open → 选择项目根目录
   - 选择"Maven Project"

2. 配置JDK:
   - File → Project Structure → Project SDK → 选择JDK 11

3. 配置Tomcat/Jetty:
   - Run → Edit Configurations → + → Maven
   - Command line: `jetty:run`
   - 点击Run

### 热重载配置

在开发时启用热重载：

```bash
# 安装并运行fizzed-watcher
mvn fizzed-watcher:run
```

这会监听JS和CSS文件的变化并自动重新压缩。

## 项目结构说明

```
plantuml-server/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── net/sourceforge/plantuml/servlet/
│   │   │       ├── model/          # 实体类
│   │   │       │   ├── User.java
│   │   │       │   └── DiagramHistory.java
│   │   │       ├── dao/            # 数据访问层
│   │   │       │   ├── UserDao.java
│   │   │       │   └── DiagramHistoryDao.java
│   │   │       ├── util/           # 工具类
│   │   │       │   ├── DatabaseUtil.java
│   │   │       │   ├── JWTUtil.java
│   │   │       │   └── PasswordUtil.java
│   │   │       ├── service/        # 业务逻辑层
│   │   │       │   └── AIService.java
│   │   │       ├── AuthServlet.java         # 认证API
│   │   │       ├── AIConvertServlet.java    # AI转换API
│   │   │       ├── DiagramHistoryServlet.java # 历史记录API
│   │   │       └── AIHomeServlet.java       # 主页控制器
│   │   ├── resources/
│   │   │   ├── config.properties   # 配置文件
│   │   │   ├── schema.sql         # 数据库schema
│   │   │   └── log4j.properties   # 日志配置
│   │   └── webapp/
│   │       ├── ai-plantuml.jsp    # AI版主页
│   │       ├── css/
│   │       │   └── ai-styles.css  # 样式文件
│   │       └── js/
│   │           └── ai-app.js      # 前端逻辑
│   └── test/                      # 测试文件
├── pom.xml                        # Maven配置
├── pom.parent.xml                 # 父POM配置
├── AI_PLANTUML_README.md          # 功能说明
├── DEPLOYMENT_GUIDE.md            # 部署指南
└── BUILD_AND_RUN.md              # 本文件
```

## API测试

### 使用Postman

导入以下集合进行测试：

```json
{
  "info": {
    "name": "PlantUML AI API",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "item": [
    {
      "name": "Register",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "http://localhost:8080/plantuml/api/auth/register",
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"test\",\"email\":\"test@test.com\",\"password\":\"123456\"}"
        }
      }
    },
    {
      "name": "Login",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "url": "http://localhost:8080/plantuml/api/auth/login",
        "body": {
          "mode": "raw",
          "raw": "{\"username\":\"admin\",\"password\":\"password123\"}"
        }
      }
    }
  ]
}
```

## 性能优化建议

1. **数据库连接池**: 已配置，可根据负载调整大小
2. **JVM参数**: 
   ```bash
   export MAVEN_OPTS="-Xms512m -Xmx2g -XX:+UseG1GC"
   ```
3. **静态资源缓存**: 配置Nginx进行缓存
4. **日志级别**: 生产环境设置为WARN或ERROR

## 下一步

- 阅读 [AI_PLANTUML_README.md](AI_PLANTUML_README.md) 了解功能详情
- 阅读 [DEPLOYMENT_GUIDE.md](DEPLOYMENT_GUIDE.md) 了解生产环境部署
- 访问 http://localhost:8080/plantuml/ai 开始使用

## 获取帮助

如果遇到问题：
1. 查看日志文件
2. 检查MySQL慢查询日志
3. 使用浏览器开发者工具检查网络请求
4. 提交Issue到GitHub

## 许可证

继承原PlantUML Server项目的开源协议。

