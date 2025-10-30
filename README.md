# PlantUML Server with AI

[![GNU GENERAL PUBLIC LICENSE, Version 3, 29 June 2007](https://img.shields.io/github/license/plantuml/plantuml-server.svg?color=blue)](https://www.gnu.org/licenses/gpl-3.0)
[![Java](https://img.shields.io/badge/Java-11+-orange.svg)](https://www.oracle.com/java/)
[![MySQL](https://img.shields.io/badge/MySQL-8.0+-blue.svg)](https://www.mysql.com/)

一个基于PlantUML的智能图表生成系统，集成了AI能力、用户认证、历史记录管理和现代化UI界面。

![PlantUML Server](https://raw.githubusercontent.com/plantuml/plantuml-server/master/docs/screenshot.png)

---

## ✨ 核心功能

### 🎨 经典PlantUML功能
- **在线编辑器** - 实时编辑和预览PlantUML图表
- **多格式导出** - 支持PNG、SVG、TXT、PDF等格式
- **URL编码** - 通过URL分享图表
- **代理服务** - 支持通过URL获取远程PlantUML
- **多页面支持** - 单个文件包含多个图表

### 🤖 AI智能生成（新增）
- **自然语言转图表** - 用中文描述自动生成PlantUML代码
- **智能优化** - AI辅助优化图表布局和样式
- **多种图表类型** - 支持序列图、类图、活动图等
- **实时预览** - 即时查看生成效果

### 👤 用户系统（新增）
- **注册/登录** - 基于JWT的安全认证
- **历史记录** - 自动保存生成的图表
- **收藏管理** - 收藏常用图表快速访问
- **个人空间** - 管理所有图表和版本

### 🎯 现代化UI（新增）
- **科技感设计** - 深色主题 + 渐变色 + 发光效果
- **响应式布局** - 完美适配桌面、平板、手机
- **流畅动画** - 优雅的交互体验
- **三栏编辑器** - 描述、代码、预览同屏显示

### 💖 打赏功能（新增）
- **右上角打赏按钮** - 支持项目开发
- **微信收款码** - 便捷的打赏方式
- **一键复制微信号** - 方便添加好友

---

## 🚀 快速开始

### 方式1: Maven运行（推荐开发）

```bash
# 克隆项目
git clone <repository-url>
cd plantuml-server

# 启动服务
mvn jetty:run

# 访问应用
# 经典版: http://localhost:8080/plantuml
# AI版: http://localhost:8080/plantuml/ai-plantuml.jsp
```

### 方式2: Docker部署（推荐生产）

```bash
# 使用Jetty
docker run -d -p 8080:8080 plantuml/plantuml-server:jetty

# 或使用Tomcat
docker run -d -p 8080:8080 plantuml/plantuml-server:tomcat

# 访问应用
open http://localhost:8080/plantuml
```

### 方式3: 构建WAR包

```bash
# 构建
mvn clean package

# 部署到Tomcat/Jetty
cp target/plantuml.war /path/to/tomcat/webapps/
```

---

## 🤖 AI功能配置

### 1. 数据库准备

AI功能需要MySQL数据库支持：

```bash
# 创建数据库
mysql -u root -p

CREATE DATABASE plantuml_ai;
USE plantuml_ai;
SOURCE src/main/resources/schema.sql;
```

数据库表结构：
- `users` - 用户信息
- `history` - 图表历史记录
- `diagram_versions` - 图表版本控制

### 2. 配置文件

编辑 `src/main/resources/config.properties`：

```properties
# 数据库配置
db.url=jdbc:mysql://localhost:3306/plantuml_ai?useSSL=false&serverTimezone=UTC
db.username=root
db.password=your_password
db.pool.maxTotal=20

# JWT配置（请修改为随机密钥）
jwt.secret=YourSuperSecretKeyForJWTTokenGeneration123456789
jwt.expiration=86400000

# AI API配置（可选，不配置则使用示例生成器）
ai.api.url=https://api.openai.com/v1/chat/completions
ai.api.key=your-openai-api-key
ai.model=gpt-3.5-turbo

# 或使用其他兼容API（如智谱AI）
# ai.api.url=https://open.bigmodel.cn/api/paas/v4/chat/completions
# ai.model=glm-4
```

### 3. 启动应用

```bash
# 重新编译
mvn clean package

# 启动
mvn jetty:run

# 访问AI版本
open http://localhost:8080/plantuml/ai-plantuml.jsp
```

---

## 💖 配置打赏功能

打赏功能已集成到项目中，需要简单配置：

### 快速配置（推荐）

```bash
# 运行配置脚本
./configure-donate.sh

# 按提示输入您的微信号即可
```

### 手动配置

#### 1. 设置微信号

**主页面** - 编辑 `src/main/webapp/components/modals/donate/donate.js`：
```javascript
const WECHAT_ID = "your_wechat_id";  // 改为您的微信号
```

**AI页面** - 编辑 `src/main/webapp/js/ai-app.js`：
```javascript
const DONATE_WECHAT_ID = "your_wechat_id";  // 改为您的微信号
```

#### 2. 添加收款码（可选）

将您的微信收款码图片保存为：
```
src/main/webapp/assets/wechat-pay.png
```

支持格式：PNG、JPG、SVG

#### 3. 重新编译

```bash
mvn clean package
mvn jetty:run
```

#### 效果

- **主页面**: 右上角固定打赏按钮（红色渐变，带心形图标）
- **AI页面**: 导航栏打赏按钮
- **弹窗内容**: 收款码 + 微信号 + 一键复制

---

## 📱 使用指南

### 经典PlantUML编辑器

1. 访问 `http://localhost:8080/plantuml`
2. 在编辑器中输入PlantUML代码
3. 实时查看预览
4. 导出为PNG/SVG等格式

### AI智能生成器

#### 注册/登录

1. 访问 `http://localhost:8080/plantuml/ai-plantuml.jsp`
2. 首次使用点击"注册"
3. 填写用户名、邮箱、密码
4. 登录进入主界面

#### 生成图表

1. 在左侧"输入描述"区域用自然语言描述，例如：
   ```
   创建一个用户登录的序列图：
   1. 用户输入用户名和密码
   2. 前端发送登录请求到后端
   3. 后端验证用户信息
   4. 后端查询数据库
   5. 返回JWT Token给前端
   6. 前端保存Token并跳转到主页
   ```

2. 可选输入图表标题

3. 勾选"保存"（保存到历史记录）

4. 点击"AI生成"按钮

5. 中间栏显示生成的PlantUML代码，右侧显示预览

#### 优化图表

1. 在代码编辑器下方输入优化指令，如：
   - "添加颜色"
   - "调整布局"
   - "美化样式"

2. 点击"AI优化"按钮

3. 查看优化后的效果

#### 管理历史

- 点击顶部"历史记录"查看所有图表
- 点击卡片加载图表进行编辑
- 星标收藏常用图表
- 删除不需要的记录

---

## 🎯 支持的图表类型

- ✅ **序列图** (Sequence Diagram) - 对象交互时序
- ✅ **类图** (Class Diagram) - 系统静态结构
- ✅ **活动图** (Activity Diagram) - 业务流程
- ✅ **用例图** (Use Case Diagram) - 用户交互
- ✅ **组件图** (Component Diagram) - 组件关系
- ✅ **状态图** (State Diagram) - 状态转换
- ✅ **对象图** (Object Diagram) - 对象实例
- ✅ **部署图** (Deployment Diagram) - 部署架构
- ✅ **时序图** (Timing Diagram) - 时间约束
- ✅ **网络图** (Network Diagram) - 网络拓扑

---

## 🔧 环境配置

### 系统要求

- **JDK**: 11或更高版本
- **Maven**: 3.0.2或更高版本
- **MySQL**: 8.0或更高版本（仅AI功能需要）

### 推荐配置

- **Jetty**: 11或更高版本
- **Tomcat**: 10或更高版本

### 环境变量

通过环境变量配置PlantUML选项：

```bash
# Maven运行
mvn jetty:run \
  -DBASE_URL=plantuml \
  -DPLANTUML_LIMIT_SIZE=8192 \
  -DPLANTUML_SECURITY_PROFILE=INTERNET

# Docker运行
docker run -d -p 8080:8080 \
  -e BASE_URL=plantuml \
  -e PLANTUML_LIMIT_SIZE=8192 \
  plantuml/plantuml-server:jetty
```

**可用环境变量：**

| 变量名 | 说明 | 默认值 |
|--------|------|--------|
| `BASE_URL` | 基础URL路径 | `ROOT` |
| `PLANTUML_SECURITY_PROFILE` | 安全配置 | `INTERNET` |
| `PLANTUML_LIMIT_SIZE` | 图片尺寸限制 | `4096` |
| `PLANTUML_STATS` | 统计报告 | `off` |
| `HTTP_AUTHORIZATION` | 代理认证 | `null` |

---

## 🏗️ 技术架构

### 后端技术栈

- **Java 11** - 核心语言
- **Servlet 4.0** - Web框架
- **MySQL 8.0** - 数据存储（AI功能）
- **JWT** - 身份认证
- **BCrypt** - 密码加密
- **Gson** - JSON处理
- **Apache Commons Pool** - 连接池
- **PlantUML** - 图表渲染引擎

### 前端技术栈

- **Pure JavaScript** - 无框架依赖
- **CSS3** - 现代化样式
  - Flexbox/Grid布局
  - 渐变和动画
  - 深色主题
- **Font Awesome** - 图标库
- **Monaco Editor** - 代码编辑器（经典版）

### 数据库设计

```sql
-- 用户表
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) UNIQUE,
    email VARCHAR(100) UNIQUE,
    password_hash VARCHAR(255),
    created_at TIMESTAMP
);

-- 历史记录表
CREATE TABLE history (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT,
    original_text TEXT,
    plantuml_code TEXT,
    title VARCHAR(255),
    is_favorite BOOLEAN,
    created_at TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id)
);
```

---

## 📊 API接口

### 认证接口

**注册**
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "张三"
}
```

**登录**
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}

Response:
{
  "token": "eyJhbGciOiJIUzI1NiIs...",
  "user": {
    "id": 1,
    "username": "user123",
    "email": "user@example.com"
  }
}
```

### AI转换接口

**文本转PlantUML**
```http
POST /api/ai/convert
Authorization: Bearer {token}
Content-Type: application/json

{
  "description": "创建一个用户登录序列图",
  "title": "用户登录流程",
  "saveHistory": true
}

Response:
{
  "plantumlCode": "@startuml\n...\n@enduml",
  "historyId": 123
}
```

**优化PlantUML**
```http
POST /api/ai/optimize
Authorization: Bearer {token}
Content-Type: application/json

{
  "plantumlCode": "@startuml\n...\n@enduml",
  "instruction": "添加颜色和样式"
}

Response:
{
  "plantumlCode": "@startuml\n...\n@enduml"
}
```

### 历史记录接口

**获取历史列表**
```http
GET /api/history?page=1&pageSize=20
Authorization: Bearer {token}

Response:
{
  "items": [...],
  "total": 100,
  "page": 1,
  "pageSize": 20
}
```

**创建记录**
```http
POST /api/history
Authorization: Bearer {token}
Content-Type: application/json

{
  "originalText": "用户描述",
  "plantumlCode": "@startuml\n...\n@enduml",
  "title": "图表标题"
}
```

**更新记录**
```http
PUT /api/history/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "新标题",
  "plantumlCode": "@startuml\n...\n@enduml"
}
```

**切换收藏**
```http
PUT /api/history/{id}/favorite
Authorization: Bearer {token}
Content-Type: application/json

{
  "isFavorite": true
}
```

**删除记录**
```http
DELETE /api/history/{id}
Authorization: Bearer {token}
```

---

## 🐳 Docker部署

### 预构建镜像

```bash
# Jetty版本（推荐）
docker run -d -p 8080:8080 plantuml/plantuml-server:jetty

# Tomcat版本
docker run -d -p 8080:8080 plantuml/plantuml-server:tomcat

# 只读文件系统（更安全）
docker run -d -p 8080:8080 --read-only \
  -v /tmp/jetty \
  plantuml/plantuml-server:jetty
```

### 自定义构建

```bash
# 构建Jetty镜像
docker build -f Dockerfile.jetty -t my-plantuml:jetty .

# 构建Alpine版本（更小）
docker build -f Dockerfile.jetty-alpine -t my-plantuml:alpine .

# 运行
docker run -d -p 8080:8080 my-plantuml:jetty
```

### Docker Compose

```yaml
version: '3'
services:
  plantuml:
    image: plantuml/plantuml-server:jetty
    ports:
      - "8080:8080"
    environment:
      - BASE_URL=plantuml
      - PLANTUML_LIMIT_SIZE=8192
    volumes:
      - ./config:/config
    restart: unless-stopped

  mysql:
    image: mysql:8.0
    environment:
      - MYSQL_ROOT_PASSWORD=password
      - MYSQL_DATABASE=plantuml_ai
    volumes:
      - ./mysql-data:/var/lib/mysql
    ports:
      - "3306:3306"
```

运行：
```bash
docker-compose up -d
```

---

## 🔒 安全性

### 认证安全

- ✅ **JWT Token** - 基于令牌的无状态认证
- ✅ **BCrypt加密** - 密码哈希存储
- ✅ **HTTPS支持** - 传输加密（生产环境推荐）
- ✅ **Token过期** - 24小时自动过期

### API安全

- ✅ **CORS配置** - 跨域访问控制
- ✅ **SQL注入防护** - PreparedStatement
- ✅ **XSS防护** - 输入过滤和输出编码
- ✅ **CSRF防护** - Token验证

### PlantUML安全

- ✅ **安全配置** - `PLANTUML_SECURITY_PROFILE=INTERNET`
- ✅ **端口限制** - 仅允许80/443端口
- ✅ **文件访问控制** - 限制本地文件访问
- ⚠️ **不要降低安全级别** - 避免设置为`UNSECURE`

> **注意**: PlantUML不受log4j漏洞影响

### 生产环境建议

1. **修改JWT密钥** - 使用强随机字符串
2. **启用HTTPS** - 使用SSL/TLS证书
3. **数据库权限** - 最小权限原则
4. **防火墙配置** - 限制数据库访问
5. **定期备份** - 数据库和配置文件
6. **日志监控** - 异常访问检测

---

## 📈 性能优化

### 数据库优化

```sql
-- 已创建的索引
CREATE INDEX idx_user_username ON users(username);
CREATE INDEX idx_user_email ON users(email);
CREATE INDEX idx_history_user_id ON history(user_id);
CREATE INDEX idx_history_created_at ON history(created_at);
CREATE INDEX idx_history_is_favorite ON history(is_favorite);
```

### 连接池配置

```properties
# config.properties
db.pool.maxTotal=20
db.pool.maxIdle=10
db.pool.minIdle=5
db.pool.maxWaitMillis=10000
```

### 缓存策略

- 图片缓存：浏览器缓存生成的PlantUML图片
- Token缓存：JWT验证结果缓存
- 静态资源：使用CDN加速CSS/JS

### 性能指标

| 操作 | 响应时间 |
|------|---------|
| JWT验证 | < 10ms |
| 数据库查询 | < 50ms |
| PlantUML渲染 | < 1s |
| AI转换 | 2-10s |

---

## 🐛 故障排查

### 常见问题

#### 1. 无法连接数据库

**问题**: 启动时报`Cannot connect to database`

**解决**:
```bash
# 检查MySQL状态
systemctl status mysql

# 测试连接
mysql -u root -p -e "SELECT 1"

# 检查配置
cat src/main/resources/config.properties | grep db.
```

#### 2. AI生成失败

**问题**: 点击生成后显示错误

**解决**:
- 检查API Key配置
- 验证网络连接
- 查看应用日志: `tail -f logs/plantuml.log`
- 如无配置，系统会使用示例生成器

#### 3. 端口被占用

**问题**: `Address already in use: bind`

**解决**:
```bash
# 查找占用进程
lsof -i :8080
netstat -ano | grep 8080

# 杀掉进程或更换端口
mvn jetty:run -Djetty.http.port=9090
```

#### 4. Token过期

**问题**: `Token无效或已过期`

**解决**:
- 重新登录获取新Token
- Token默认24小时有效
- 可在config.properties中调整过期时间

#### 5. 图片预览失败

**问题**: 生成代码后预览显示错误

**解决**:
- 检查PlantUML代码语法
- 确认PlantUML服务正常
- 清除浏览器缓存
- 查看浏览器控制台错误

---

## 📚 更多文档

- 🔧 [构建运行指南](BUILD_AND_RUN.md)
- 🚀 [部署指南](DEPLOYMENT_GUIDE.md)
- 📊 [项目总结](PROJECT_SUMMARY.md)
- 🌐 [Web UI功能](docs/WebUI/README.md)
- 📖 [PlantUML官方文档](https://plantuml.com)

---

## 🤝 贡献

欢迎提交Issue和Pull Request！

### 开发环境设置

```bash
# 克隆项目
git clone <repository-url>
cd plantuml-server

# 安装依赖
mvn clean install

# 启动开发服务器
mvn jetty:run

# CSS/JS热重载（另一个终端）
mvn fizzed-watcher:run
```

### 代码规范

- 遵循Java代码规范
- 提交前运行测试：`mvn test`
- 保持代码简洁可读
- 添加必要的注释

---

## 📝 更新日志

### v1.2.0 (2025-10-30)
- ✨ 新增打赏功能
- ✨ 优化AI按钮样式统一
- ✨ 预览标题不换行
- 🐛 修复弹窗居中问题
- 🐛 修复全屏预览图片居中

### v1.1.0 (2025-10-29)
- ✨ 新增AI智能生成功能
- ✨ 新增用户认证系统
- ✨ 新增历史记录管理
- ✨ 新增收藏功能
- ✨ 新增现代化UI界面
- ✨ 新增三栏编辑器布局

### v1.0.0
- ✅ 基础PlantUML服务器功能
- ✅ 在线编辑器
- ✅ 多格式导出
- ✅ URL编码支持

---

## 📄 许可证

本项目基于 [GNU General Public License v3.0](LICENSE) 开源。

继承自 [PlantUML Server](https://github.com/plantuml/plantuml-server) 项目。

---

## 🙏 致谢

- [PlantUML](https://plantuml.com/) - 强大的UML图表工具
- [OpenAI](https://openai.com/) - AI技术支持
- 所有开源贡献者

---

## 📧 支持

- 📖 [GitHub Issues](https://github.com/plantuml/plantuml-server/issues)
- 💬 [GitHub Discussions](https://github.com/plantuml/plantuml-server/discussions)
- 🌐 [PlantUML Forum](https://forum.plantuml.net/)

---

**Made with ❤️ by PlantUML Community**

**最后更新**: 2025-10-30  
**版本**: 1.2.0
