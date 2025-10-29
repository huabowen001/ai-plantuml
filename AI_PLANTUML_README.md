# AI PlantUML Generator - 智能图表生成器

## 项目简介

这是一个现代化的、基于AI的PlantUML图表生成系统，具有以下特点：

- 🎨 **现代化科技感UI设计** - 采用深色主题和渐变色，提供优秀的视觉体验
- 🤖 **AI智能生成** - 通过自然语言描述自动生成PlantUML代码
- 👤 **用户认证系统** - 支持用户注册、登录，使用JWT进行身份验证
- 📝 **历史记录管理** - 自动保存用户的图表历史，支持查看、编辑和删除
- ⭐ **收藏功能** - 可以收藏常用的图表
- 🖼️ **实时预览** - 实时预览生成的PlantUML图表
- 💾 **导出功能** - 支持导出PNG、SVG等多种格式

## 技术栈

### 后端
- Java 11
- Servlet 4.0
- MySQL 8.0
- JWT认证
- BCrypt密码加密
- OpenAI API（或兼容的API）

### 前端
- 纯JavaScript（无框架）
- CSS3（渐变、动画、Grid布局）
- Font Awesome图标
- 响应式设计

## 部署指南

### 1. 数据库配置

首先，创建MySQL数据库并导入schema：

```bash
mysql -u root -p < src/main/resources/schema.sql
```

或者手动执行SQL文件中的语句。

### 2. 配置文件

编辑 `src/main/resources/config.properties` 文件，配置数据库和AI API：

```properties
# 数据库配置
db.url=jdbc:mysql://localhost:3306/plantuml_ai?useSSL=false&serverTimezone=UTC
db.username=root
db.password=your_password

# JWT配置
jwt.secret=YourSuperSecretKeyForJWTTokenGeneration123456789
jwt.expiration=86400000

# AI API配置（支持OpenAI或兼容的API）
ai.api.url=https://api.openai.com/v1/chat/completions
ai.api.key=your-openai-api-key
ai.model=gpt-3.5-turbo
```

**注意：** 
- 请确保修改 `jwt.secret` 为一个随机的、足够长的密钥
- 如果没有配置AI API Key，系统会使用内置的示例PlantUML代码生成器

### 3. 构建项目

```bash
mvn clean package
```

### 4. 部署

将生成的 `target/plantuml.war` 文件部署到Tomcat或Jetty服务器。

#### 使用Jetty（开发环境）

```bash
mvn jetty:run
```

然后访问：http://localhost:8080/plantuml/ai

#### 使用Docker

```bash
# 构建镜像
docker build -f Dockerfile.jetty -t plantuml-ai-server .

# 运行容器
docker run -d -p 8080:8080 \
  -e DB_URL=jdbc:mysql://your-db-host:3306/plantuml_ai \
  -e DB_USERNAME=root \
  -e DB_PASSWORD=your_password \
  -e AI_API_KEY=your-api-key \
  plantuml-ai-server
```

## 使用指南

### 1. 注册/登录

首次访问需要注册账户：
- 点击"注册"标签
- 填写用户名、邮箱和密码
- 点击注册按钮

### 2. 生成图表

#### 使用AI生成
1. 在左侧"输入描述"区域，用自然语言描述你想要的图表
2. 可以输入图表标题（可选）
3. 勾选"保存到历史记录"（如果需要）
4. 点击"AI生成图表"按钮
5. 系统将自动生成PlantUML代码并显示预览

**示例描述：**
```
创建一个用户登录的序列图，包含以下步骤：
1. 用户输入用户名和密码
2. 前端发送登录请求到后端
3. 后端验证用户信息
4. 后端查询数据库
5. 返回JWT Token给前端
6. 前端保存Token并跳转到主页
```

#### 编辑PlantUML代码
- 可以直接在中间的代码编辑器中修改PlantUML代码
- 修改后点击预览区的"刷新预览"按钮更新图表

#### AI优化
- 在代码区域下方输入优化指令（如："添加颜色"、"调整布局"）
- 点击"AI优化"按钮

### 3. 导出图表

- **复制代码**：点击代码区域的复制图标
- **下载代码**：点击下载图标，保存为.puml文件
- **导出图片**：点击预览区的PNG或SVG按钮

### 4. 历史记录

- 点击顶部导航栏的"历史记录"
- 查看所有已保存的图表
- 点击卡片可以加载该图表进行编辑
- 可以收藏或删除图表

### 5. 收藏管理

- 点击顶部导航栏的"收藏"
- 查看所有收藏的图表
- 快速访问常用图表

## API接口文档

### 认证接口

#### 注册
```
POST /api/auth/register
Content-Type: application/json

{
  "username": "user123",
  "email": "user@example.com",
  "password": "password123",
  "fullName": "张三"
}
```

#### 登录
```
POST /api/auth/login
Content-Type: application/json

{
  "username": "user123",
  "password": "password123"
}
```

### AI转换接口

#### 文本转PlantUML
```
POST /api/ai/convert
Authorization: Bearer {token}
Content-Type: application/json

{
  "description": "创建一个用户登录序列图",
  "title": "用户登录流程",
  "saveHistory": true
}
```

#### 优化PlantUML
```
POST /api/ai/optimize
Authorization: Bearer {token}
Content-Type: application/json

{
  "plantumlCode": "@startuml\n...\n@enduml",
  "instruction": "添加颜色和样式"
}
```

### 历史记录接口

#### 获取历史列表
```
GET /api/history?page=1&pageSize=20
Authorization: Bearer {token}
```

#### 获取单条记录
```
GET /api/history/{id}
Authorization: Bearer {token}
```

#### 创建历史记录
```
POST /api/history
Authorization: Bearer {token}
Content-Type: application/json

{
  "originalText": "用户输入的描述",
  "plantumlCode": "@startuml\n...\n@enduml",
  "title": "图表标题"
}
```

#### 更新历史记录
```
PUT /api/history/{id}
Authorization: Bearer {token}
Content-Type: application/json

{
  "title": "新标题",
  "plantumlCode": "@startuml\n...\n@enduml"
}
```

#### 切换收藏状态
```
PUT /api/history/{id}/favorite
Authorization: Bearer {token}
Content-Type: application/json

{
  "isFavorite": true
}
```

#### 删除历史记录
```
DELETE /api/history/{id}
Authorization: Bearer {token}
```

## 支持的图表类型

- **序列图（Sequence Diagram）** - 表示对象之间的交互顺序
- **类图（Class Diagram）** - 展示系统的静态结构
- **活动图（Activity Diagram）** - 描述业务流程或算法
- **用例图（Use Case Diagram）** - 描述用户与系统的交互
- **组件图（Component Diagram）** - 展示系统组件及其关系
- **状态图（State Diagram）** - 描述对象的状态变化
- **对象图（Object Diagram）** - 展示对象实例
- **部署图（Deployment Diagram）** - 描述系统部署架构

## 常见问题

### 1. AI生成失败

**问题**：点击生成后显示错误
**解决**：
- 检查config.properties中的AI API配置
- 确认API Key是否有效
- 检查网络连接
- 如果没有配置API，系统会使用示例代码生成器

### 2. 数据库连接失败

**问题**：启动时报数据库连接错误
**解决**：
- 确认MySQL服务已启动
- 检查数据库配置（URL、用户名、密码）
- 确认数据库已创建
- 检查防火墙设置

### 3. 图片预览失败

**问题**：生成代码后预览显示错误
**解决**：
- 检查PlantUML代码语法是否正确
- 确认PlantUML服务正常运行
- 刷新浏览器页面

### 4. Token过期

**问题**：操作时提示"Token无效或已过期"
**解决**：
- 重新登录获取新Token
- Token默认有效期为24小时

## 安全建议

1. **修改JWT密钥**：在生产环境中，务必修改 `jwt.secret` 为强随机字符串
2. **HTTPS部署**：建议在生产环境中使用HTTPS协议
3. **密码策略**：建议实施强密码策略（目前最低6位）
4. **API Key保护**：不要将AI API Key提交到版本控制系统
5. **数据库权限**：使用最小权限原则配置数据库用户
6. **防火墙配置**：限制数据库只能从应用服务器访问

## 性能优化建议

1. **数据库索引**：已在schema中创建必要的索引
2. **连接池**：已配置数据库连接池，可根据需要调整大小
3. **缓存**：可以考虑添加Redis缓存常用的图表
4. **CDN**：可以将静态资源（CSS、JS）部署到CDN
5. **图片缓存**：可以缓存生成的PlantUML图片

## 扩展功能建议

1. **团队协作**：添加团队功能，支持图表分享
2. **版本控制**：记录图表的修改历史
3. **模板库**：提供常用图表模板
4. **批量导出**：支持批量导出多个图表
5. **评论功能**：允许用户对图表添加评论
6. **AI对话**：改进为对话式AI交互
7. **实时协作**：使用WebSocket实现多人实时编辑

## 开源协议

本项目基于原PlantUML Server项目扩展，继承其开源协议。

## 贡献指南

欢迎提交Issue和Pull Request！

## 联系方式

如有问题或建议，请通过以下方式联系：
- GitHub Issues
- Email: your-email@example.com

## 更新日志

### v1.0.0 (2025-10-29)
- ✨ 初始版本发布
- ✅ 实现用户认证系统
- ✅ 实现AI文本转PlantUML功能
- ✅ 实现历史记录管理
- ✅ 实现收藏功能
- ✅ 实现现代化UI界面

