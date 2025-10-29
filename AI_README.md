# 🤖 AI PlantUML Generator

一个具有AI能力的PlantUML图表生成系统，提供现代化的用户界面和智能化的图表创建体验。

![Version](https://img.shields.io/badge/version-1.0.0-blue.svg)
![Java](https://img.shields.io/badge/Java-11+-orange.svg)
![License](https://img.shields.io/badge/license-GPL-green.svg)

## ✨ 特性亮点

### 🎨 现代化UI设计
- 科技感深色主题
- 流畅的动画效果
- 响应式布局
- 优雅的交互体验

### 🤖 AI智能生成
- 自然语言转PlantUML
- 智能代码优化
- 支持多种图表类型
- 实时预览渲染

### 👤 用户系统
- 注册/登录认证
- JWT安全验证
- 个人资料管理
- 会话持久化

### 📊 历史记录
- 自动保存图表
- 收藏管理
- 分页浏览
- 快速检索

### 💾 导出功能
- PNG/SVG格式
- 代码下载
- 一键复制

## 🚀 快速开始

### 一键部署（推荐）

```bash
# 克隆项目
git clone <repository-url>
cd plantuml-server

# 运行快速启动脚本
chmod +x quick-start.sh
./quick-start.sh

# 按照提示完成配置，然后访问
open http://localhost:8080/plantuml/ai
```

### 手动部署

#### 1️⃣ 安装依赖

```bash
# 确保已安装
- Java 11+
- Maven 3.6+
- MySQL 8.0+
```

#### 2️⃣ 创建数据库

```bash
mysql -u root -p
CREATE DATABASE plantuml_ai;
USE plantuml_ai;
SOURCE src/main/resources/schema.sql;
```

#### 3️⃣ 配置应用

编辑 `src/main/resources/config.properties`:

```properties
# 数据库
db.url=jdbc:mysql://localhost:3306/plantuml_ai
db.username=root
db.password=your_password

# JWT密钥（请修改）
jwt.secret=your_random_secret_key

# AI API（可选）
ai.api.key=your-openai-api-key
```

#### 4️⃣ 构建运行

```bash
# 构建
mvn clean package -DskipTests

# 运行
mvn jetty:run

# 访问
open http://localhost:8080/plantuml/ai
```

## 📱 使用指南

### 注册账户

1. 访问 http://localhost:8080/plantuml/ai
2. 点击"注册"标签
3. 填写用户信息
4. 点击"注册"按钮

### 生成图表

1. 登录后在左侧输入描述，例如：
   ```
   创建一个用户登录的序列图，包含：
   - 用户输入账号密码
   - 前端验证
   - 后端认证
   - 数据库查询
   - 返回JWT Token
   ```

2. 点击"AI生成图表"按钮

3. 查看生成的PlantUML代码和预览

4. 可以进一步优化或导出

### 管理历史

- 点击"历史记录"查看所有图表
- 点击图表卡片可加载编辑
- 星标收藏常用图表
- 删除不需要的记录

## 🎯 支持的图表类型

- ✅ 序列图 (Sequence Diagram)
- ✅ 类图 (Class Diagram)
- ✅ 活动图 (Activity Diagram)
- ✅ 用例图 (Use Case Diagram)
- ✅ 组件图 (Component Diagram)
- ✅ 状态图 (State Diagram)
- ✅ 对象图 (Object Diagram)
- ✅ 部署图 (Deployment Diagram)

## 📚 文档

- 📖 [完整功能说明](AI_PLANTUML_README.md)
- 🔧 [构建运行指南](BUILD_AND_RUN.md)
- 🚀 [部署指南](DEPLOYMENT_GUIDE.md)
- 📊 [项目总结](PROJECT_SUMMARY.md)

## 🏗️ 技术架构

### 后端
- **Java 11** - 核心语言
- **Servlet 4.0** - Web框架
- **MySQL 8.0** - 数据存储
- **JWT** - 身份认证
- **BCrypt** - 密码加密
- **Gson** - JSON处理

### 前端
- **Pure JavaScript** - 无框架依赖
- **CSS3** - 现代化样式
- **Font Awesome** - 图标库

## 🔧 配置选项

### 数据库配置
```properties
db.url=jdbc:mysql://localhost:3306/plantuml_ai
db.username=root
db.password=your_password
db.pool.maxTotal=20
```

### JWT配置
```properties
jwt.secret=your_secret_key
jwt.expiration=86400000  # 24小时
```

### AI API配置
```properties
# OpenAI
ai.api.url=https://api.openai.com/v1/chat/completions
ai.api.key=sk-xxx
ai.model=gpt-3.5-turbo

# 或使用其他兼容的API
# 智谱AI
# ai.api.url=https://open.bigmodel.cn/api/paas/v4/chat/completions
# ai.model=glm-4
```

## 📊 API接口

### 认证
- `POST /api/auth/register` - 注册
- `POST /api/auth/login` - 登录

### AI转换
- `POST /api/ai/convert` - 文本转PlantUML
- `POST /api/ai/optimize` - 优化代码

### 历史记录
- `GET /api/history` - 获取列表
- `POST /api/history` - 创建记录
- `PUT /api/history/{id}` - 更新记录
- `DELETE /api/history/{id}` - 删除记录
- `PUT /api/history/{id}/favorite` - 切换收藏

详细API文档见 [AI_PLANTUML_README.md](AI_PLANTUML_README.md)

## 🐳 Docker部署

```bash
# 构建镜像
docker build -f Dockerfile.jetty -t plantuml-ai .

# 运行
docker-compose up -d
```

## 🔒 安全性

- ✅ JWT Token认证
- ✅ BCrypt密码加密
- ✅ SQL注入防护
- ✅ XSS防护
- ✅ HTTPS支持

## 📈 性能

- JWT验证: < 10ms
- 数据库查询: < 50ms
- AI转换: 2-10秒
- 图片生成: < 1秒

## 🐛 故障排查

### 无法连接数据库
```bash
# 检查MySQL状态
systemctl status mysql

# 测试连接
mysql -u root -p -e "SELECT 1"
```

### AI生成失败
- 检查API Key配置
- 验证网络连接
- 查看应用日志

### 端口被占用
```bash
# 查找占用进程
lsof -i :8080

# 或更换端口
mvn jetty:run -Djetty.http.port=9090
```

## 🎨 UI截图

### 登录界面
![Login](docs/login.png)

### 编辑器界面
![Editor](docs/editor.png)

### 历史记录
![History](docs/history.png)

## 🤝 贡献

欢迎提交Issue和Pull Request！

### 开发环境设置
```bash
# 克隆项目
git clone <repository-url>

# 安装依赖
mvn clean install

# 启动开发服务器
mvn jetty:run

# 热重载（CSS/JS）
mvn fizzed-watcher:run
```

## 📝 更新日志

### v1.0.0 (2025-10-29)
- ✨ 初始发布
- ✅ 用户认证系统
- ✅ AI智能生成
- ✅ 历史记录管理
- ✅ 现代化UI

## 📄 许可证

继承原PlantUML Server项目的开源协议。

## 🙏 致谢

- [PlantUML](https://plantuml.com/) - 强大的UML图表工具
- [OpenAI](https://openai.com/) - AI技术支持
- 所有开源贡献者

## 📧 联系方式

- GitHub Issues
- Email: your-email@example.com

---

**开发团队**: AI PlantUML Team  
**最后更新**: 2025-10-29  
**版本**: 1.0.0

## ⭐ Star History

如果这个项目对你有帮助，请给个Star吧！

---

Made with ❤️ by AI PlantUML Team

