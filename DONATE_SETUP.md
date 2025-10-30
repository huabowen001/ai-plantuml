# 打赏功能配置说明

## 📝 配置步骤

### 1. 设置您的微信号

#### 主页面（PlantUML Server）

编辑文件：`src/main/webapp/components/modals/donate/donate.js`

找到第 **第5行** 并修改为您的微信号：

```javascript
const WECHAT_ID = "your_wechat_id";  // 修改这里！例如："wxid_abc123"
```

#### AI页面（AI PlantUML Generator）

编辑文件：`src/main/webapp/js/ai-app.js`

找到第 **1441行** 并修改为您的微信号：

```javascript
const DONATE_WECHAT_ID = "your_wechat_id";  // 修改这里！例如："wxid_abc123"
```

### 2. 添加微信收款码图片（可选）

1. 准备您的微信收款码图片
2. 将图片重命名为 `wechat-pay.png`
3. 将图片放到：`wechat-pay.png`

**支持的图片格式：** PNG、JPG、SVG

**建议尺寸：** 200x200 像素或更大（会自动缩放）

### 3. 重新编译项目

打赏功能的代码已经添加到项目中，但需要重新编译才能生效：

```bash
# 使用Maven编译
mvn clean package

# 或者如果您在开发，可以使用
mvn fizzed-watcher:run
```

### 4. 启动服务器

```bash
# 使用Jetty
mvn jetty:run

# 或使用Docker
docker-compose up
```

## 🎨 功能特点

- ✅ 右上角显眼的打赏按钮（渐变色，带动画效果）
- ✅ 点击打开打赏弹窗
- ✅ 显示微信收款码（如果已上传）
- ✅ 显示微信号，支持一键复制
- ✅ 支持深色主题
- ✅ 移动端自适应

## 📱 效果预览

### 桌面端
- 右上角红色渐变按钮
- 显示"❤️ 打赏"文字和图标

### 移动端
- 右上角仅显示心形图标（节省空间）
- 点击后显示完整的打赏信息src/main/webapp/assets/

## 🎯 自定义样式（可选）

如果您想修改打赏按钮的样式，编辑：
- 按钮样式：`src/main/webapp/components/header/donate-button.jsp` (内联CSS)
- 弹窗样式：`src/main/webapp/components/modals/donate/donate.css`

### 修改按钮颜色示例

在 `donate-button.jsp` 中找到：
```css
background: linear-gradient(135deg, #ff6b6b, #ff8e53);
```

改为您喜欢的颜色，例如：
```css
background: linear-gradient(135deg, #4CAF50, #45a049);  /* 绿色渐变 */
```

## 🔧 故障排除

### 打赏按钮没有显示？
1. 确认已重新编译：`mvn clean package`
2. 清除浏览器缓存（Ctrl+F5 或 Cmd+Shift+R）
3. 检查浏览器控制台是否有错误

### 收款码图片不显示？
1. 确认图片路径正确：`src/main/webapp/assets/wechat-pay.png`
2. 确认图片格式支持（PNG、JPG、SVG）
3. 重新编译项目

### 微信号显示错误？
1. 检查 `donate.js` 中的 `WECHAT_ID` 常量
2. 重新编译项目
3. 刷新页面

## 💡 提示

- 如果您不想上传收款码图片，弹窗会显示占位符提示
- 微信号复制功能支持所有现代浏览器
- 打赏按钮在所有页面都会显示

## 📄 相关文件

### 主页面文件

```
src/main/webapp/
├── assets/
│   ├── actions/
│   │   └── donate.svg                    # 打赏图标
│   └── wechat-pay.png                    # 微信收款码（需要您添加）
├── components/
│   ├── header/
│   │   ├── header.jsp                    # 已修改：引入打赏按钮
│   │   └── donate-button.jsp            # 新增：打赏按钮组件
│   └── modals/
│       ├── modals.js                     # 已修改：添加donate初始化
│       └── donate/
│           ├── donate.jsp                # 新增：打赏弹窗
│           ├── donate.css                # 新增：打赏样式
│           └── donate.js                 # 新增：打赏逻辑（需配置微信号）
└── index.jsp                             # 已修改：引入打赏弹窗
```

### AI页面文件

```
src/main/webapp/
├── ai-plantuml.jsp                       # 已修改：添加打赏按钮和弹窗
└── js/
    └── ai-app.js                         # 已修改：添加打赏功能（需配置微信号）
```

---

如有问题，请查看项目文档或提交Issue。

