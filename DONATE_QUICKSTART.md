# 💖 打赏功能 - 5分钟快速开始

## 🚀 三步配置，立即使用

### 步骤 1️⃣: 配置微信号（必需）

**使用自动化脚本（推荐）**：

```bash
./configure-donate.sh
```

按提示输入您的微信号即可！脚本会自动：
- ✅ 配置主页面
- ✅ 配置AI页面  
- ✅ 创建备份文件
- ✅ 显示配置结果

**或手动配置**：

打开这两个文件，搜索 `your_wechat_id` 并替换为您的微信号：

1. `src/main/webapp/components/modals/donate/donate.js` (第5行)
2. `src/main/webapp/js/ai-app.js` (第1441行)

### 步骤 2️⃣: 添加收款码（可选）

将您的微信收款码图片放到：
```
src/main/webapp/assets/wechat-pay.png
```

**提示**：
- 支持格式：PNG、JPG、SVG
- 建议尺寸：200x200像素或更大
- 如果不添加，会显示提示信息

### 步骤 3️⃣: 编译运行

```bash
# 编译项目
mvn clean package

# 启动服务器
mvn jetty:run
```

访问 `http://localhost:8080/plantuml` 即可看到右上角的打赏按钮！

---

## ✨ 效果展示

### 主页面
```
┌────────────────────────────────────┐
│  PlantUML Server      [❤️ 打赏]   │  ← 红色渐变按钮
└────────────────────────────────────┘
```

点击后弹出：
```
╔══════════════════════════════╗
║      💖 打赏支持             ║
║──────────────────────────────║
║  [微信收款码图片]            ║
║                              ║
║  微信号: wxid_xxx  [复制]   ║
╚══════════════════════════════╝
```

### AI页面
```
┌────────────────────────────────────┐
│ AI生成 | 历史 | 收藏  [❤️打赏] 用户│  ← 导航栏打赏
└────────────────────────────────────┘
```

---

## 📱 体验测试

### 桌面端测试
1. ✅ 右上角看到红色打赏按钮
2. ✅ 鼠标悬停按钮有动画效果
3. ✅ 点击打开打赏弹窗
4. ✅ 查看收款码显示正常
5. ✅ 点击"复制"按钮复制微信号
6. ✅ 看到"已复制"提示

### 移动端测试
1. ✅ 右上角显示心形图标（节省空间）
2. ✅ 点击打开打赏弹窗
3. ✅ 弹窗自适应屏幕大小
4. ✅ 可以长按保存收款码

### 功能测试
- ✅ ESC键关闭弹窗
- ✅ 点击背景关闭弹窗
- ✅ 切换深色主题显示正常

---

## 🎨 自定义（可选）

### 修改按钮颜色

编辑 `src/main/webapp/components/header/donate-button.jsp`：

```css
/* 找到这行 */
background: linear-gradient(135deg, #ff6b6b, #ff8e53);

/* 改为您喜欢的颜色，例如绿色： */
background: linear-gradient(135deg, #4CAF50, #45a049);
```

### 修改按钮位置

```css
.donate-button-container {
  position: fixed;
  top: 1rem;      /* 距离顶部，可修改 */
  right: 1rem;    /* 距离右侧，可修改 */
}
```

### 修改按钮文字

在 `donate-button.jsp` 中：
```html
<span>打赏</span>  <!-- 改为您想要的文字 -->
```

---

## 🐛 常见问题

### ❓ 按钮不显示？

**解决方法**：
```bash
# 1. 清除编译缓存
mvn clean

# 2. 重新编译
mvn package

# 3. 重启服务器
mvn jetty:run
```

### ❓ 收款码不显示？

**检查清单**：
- [ ] 文件路径正确：`src/main/webapp/assets/wechat-pay.png`
- [ ] 文件名正确（不是 wechat-pay.PNG 或其他）
- [ ] 图片格式支持（PNG/JPG/SVG）
- [ ] 已重新编译项目

**提示**：如果不添加收款码图片，会显示上传提示，不影响微信号显示和复制功能。

### ❓ 复制功能不工作？

**可能原因**：
1. 浏览器版本太旧 → 更新浏览器
2. 使用HTTP访问 → 部分浏览器需要HTTPS
3. JavaScript被禁用 → 检查浏览器设置

**测试方法**：
```javascript
// 在浏览器控制台测试
navigator.clipboard.writeText("test").then(() => console.log("OK"))
```

### ❓ 如何回退？

如果使用了配置脚本，可以恢复备份：
```bash
mv src/main/webapp/components/modals/donate/donate.js.backup \
   src/main/webapp/components/modals/donate/donate.js

mv src/main/webapp/js/ai-app.js.backup \
   src/main/webapp/js/ai-app.js
```

或从Git恢复：
```bash
git checkout -- src/main/webapp/components/modals/donate/donate.js
git checkout -- src/main/webapp/js/ai-app.js
```

---

## 📚 更多资料

- **详细配置**: 查看 `DONATE_SETUP.md`
- **功能演示**: 查看 `DONATE_FEATURE_DEMO.md`
- **修改总结**: 查看 `DONATE_CHANGES_SUMMARY.md`

---

## 💡 小贴士

1. **建议添加收款码图片**：用户扫码更方便
2. **测试复制功能**：确保在您的目标浏览器上工作正常
3. **自定义样式**：让打赏按钮更符合您的品牌风格
4. **移动端测试**：确保在手机上显示正常

---

## 🎉 完成！

配置完成后，您的PlantUML服务器就有了专业的打赏功能！

**不要忘记**：
- 📱 在不同设备上测试
- 🎨 根据需要自定义样式
- 📸 准备好您的收款码图片

如果一切正常，恭喜您！开始享受用户的打赏支持吧！💰

---

**需要帮助？** 查看其他文档或提交Issue。

