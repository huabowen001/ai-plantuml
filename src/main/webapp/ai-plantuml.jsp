<%@ page info="ai-plantuml" contentType="text/html; charset=utf-8" pageEncoding="utf-8" session="false" %>
<!DOCTYPE html>
<html lang="zh-CN">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>AI PlantUML Generator - 智能图表生成器</title>
    <link rel="stylesheet" href="css/ai-styles.css">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
</head>
<body>
    <!-- 登录/注册模态框 -->
    <div id="authModal" class="modal">
        <div class="modal-content">
            <span class="close">&times;</span>
            <div class="auth-container">
                <div class="auth-tabs">
                    <button class="auth-tab active" data-tab="login">登录</button>
                    <button class="auth-tab" data-tab="register">注册</button>
                </div>
                
                <!-- 登录表单 -->
                <div id="loginForm" class="auth-form active">
                    <h2>欢迎回来</h2>
                    <div class="form-group">
                        <i class="fas fa-user"></i>
                        <input type="text" id="loginUsername" placeholder="用户名或邮箱" required>
                    </div>
                    <div class="form-group">
                        <i class="fas fa-lock"></i>
                        <input type="password" id="loginPassword" placeholder="密码" required>
                    </div>
                    <button class="btn-primary" onclick="handleLogin()">
                        <i class="fas fa-sign-in-alt"></i> 登录
                    </button>
                    <div id="loginError" class="error-message"></div>
                </div>
                
                <!-- 注册表单 -->
                <div id="registerForm" class="auth-form">
                    <h2>创建账户</h2>
                    <div class="form-group">
                        <i class="fas fa-user"></i>
                        <input type="text" id="registerUsername" placeholder="用户名" required>
                    </div>
                    <div class="form-group">
                        <i class="fas fa-envelope"></i>
                        <input type="email" id="registerEmail" placeholder="邮箱" required>
                    </div>
                    <div class="form-group">
                        <i class="fas fa-lock"></i>
                        <input type="password" id="registerPassword" placeholder="密码（至少6位）" required>
                    </div>
                    <div class="form-group">
                        <i class="fas fa-id-card"></i>
                        <input type="text" id="registerFullName" placeholder="姓名（可选）">
                    </div>
                    <button class="btn-primary" onclick="handleRegister()">
                        <i class="fas fa-user-plus"></i> 注册
                    </button>
                    <div id="registerError" class="error-message"></div>
                </div>
            </div>
        </div>
    </div>

    <!-- 主界面 -->
    <div class="main-container" id="mainApp" style="display: none;">
        <!-- 顶部导航栏 -->
        <nav class="navbar">
            <div class="navbar-brand">
                <i class="fas fa-project-diagram"></i>
                <span>AI PlantUML Generator</span>
            </div>
            <div class="navbar-menu">
                <button class="nav-btn" onclick="showSection('editor')">
                    <i class="fas fa-edit"></i> AI生成
                </button>
                <button class="nav-btn" onclick="showSection('history')">
                    <i class="fas fa-history"></i> 历史记录
                </button>
                <button class="nav-btn" onclick="showSection('favorites')">
                    <i class="fas fa-star"></i> 收藏
                </button>
            </div>
            <div class="navbar-user">
                <button class="nav-btn" onclick="openDonateModal()" title="打赏支持" style="color: #ff6b6b;">
                    <i class="fas fa-heart"></i> 打赏
                </button>
                <span id="userDisplayName">用户</span>
                <button class="btn-secondary" onclick="handleLogout()">
                    <i class="fas fa-sign-out-alt"></i> 退出
                </button>
            </div>
        </nav>

        <!-- 编辑器区域 -->
        <div id="editorSection" class="content-section active">
            <div class="editor-container">
                <div class="editor-panel">
                    <div class="panel-header">
                        <h3><i class="fas fa-keyboard"></i> 输入描述</h3>
                        <div class="panel-actions">
                            <button class="btn-icon" onclick="clearInput()" title="清空">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    <div class="panel-body">
                        <textarea id="descriptionInput" placeholder="请用自然语言描述你想要生成的图表...&#10;&#10;例如：&#10;- 创建一个用户登录的序列图，包含用户、前端、后端和数据库&#10;- 画一个电商系统的类图，包含用户、订单、商品等实体&#10;- 生成一个订单处理的活动图"></textarea>
                        <div class="input-toolbar">
                            <input type="text" id="diagramTitle" placeholder="图表标题（可选）" class="title-input">
                            <div class="toolbar-actions">
                                <label class="checkbox-label">
                                    <input type="checkbox" id="saveHistory" checked>
                                    <span>保存</span>
                                </label>
                                <button class="btn-ai" onclick="generateDiagram()">
                                    <i class="fas fa-magic"></i> AI生成
                                </button>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="editor-panel">
                    <div class="panel-header">
                        <h3><i class="fas fa-code"></i> PlantUML代码</h3>
                        <div class="panel-actions">
                            <button class="btn-icon" onclick="copyCode()" title="复制代码">
                                <i class="fas fa-copy"></i>
                            </button>
                            <button class="btn-icon" onclick="downloadCode()" title="下载代码">
                                <i class="fas fa-download"></i>
                            </button>
                        </div>
                    </div>
                    <div class="panel-body">
                        <textarea id="plantumlCode" placeholder="PlantUML代码将在这里显示..." spellcheck="false"></textarea>
                        <div class="input-toolbar">
                            <input type="text" id="optimizeInstruction" placeholder="输入优化指令，如：添加颜色、调整布局等" class="title-input">
                            <button class="btn-ai" onclick="optimizeDiagram()">
                                <i class="fas fa-wand-magic-sparkles"></i> AI优化
                            </button>
                        </div>
                    </div>
                </div>

                <div class="editor-panel preview-panel">
                    <div class="panel-header">
                        <h3><i class="fas fa-image"></i> 预览</h3>
                        <div class="panel-actions">
                            <button class="btn-icon" id="fullscreenBtn" onclick="openFullscreenPreview()" title="全屏预览" style="display: none;">
                                <i class="fas fa-expand"></i>
                            </button>
                            <button class="btn-icon" id="saveVersionBtn" onclick="showSaveVersionDialog()" title="保存版本" style="display: none;">
                                <i class="fas fa-save"></i>
                            </button>
                            <button class="btn-icon" id="versionBtn" onclick="viewVersionHistory(currentHistoryId)" title="查看版本历史" style="display: none;">
                                <i class="fas fa-code-branch"></i>
                            </button>
                            <button class="btn-icon" id="favoriteBtn" onclick="toggleCurrentFavorite()" title="收藏当前图表" style="display: none;">
                                <i class="far fa-star"></i>
                            </button>
                            <button class="btn-icon" onclick="refreshPreview()" title="刷新预览">
                                <i class="fas fa-sync-alt"></i>
                            </button>
                            <button class="btn-icon" onclick="downloadImage('png')" title="下载PNG">
                                <i class="fas fa-file-image"></i> PNG
                            </button>
                            <button class="btn-icon" onclick="downloadImage('svg')" title="下载SVG">
                                <i class="fas fa-file-code"></i> SVG
                            </button>
                        </div>
                    </div>
                    <div class="panel-body preview-body">
                        <div id="previewLoading" class="loading-spinner" style="display: none;">
                            <i class="fas fa-spinner fa-spin"></i>
                            <p>生成中...</p>
                        </div>
                        <div id="previewContainer" class="preview-container">
                            <p class="preview-placeholder">图表预览将在这里显示</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>

        <!-- 历史记录区域 -->
        <div id="historySection" class="content-section">
            <div class="section-header">
                <h2><i class="fas fa-history"></i> 历史记录</h2>
                <button class="btn-primary" onclick="loadHistory()">
                    <i class="fas fa-sync-alt"></i> 刷新
                </button>
            </div>
            <div id="historyList" class="history-grid">
                <!-- 历史记录将动态加载到这里 -->
            </div>
            <div id="historyPagination" class="pagination">
                <!-- 分页将动态生成 -->
            </div>
        </div>

        <!-- 收藏区域 -->
        <div id="favoritesSection" class="content-section">
            <div class="section-header">
                <h2><i class="fas fa-star"></i> 我的收藏</h2>
                <button class="btn-primary" onclick="loadFavorites()">
                    <i class="fas fa-sync-alt"></i> 刷新
                </button>
            </div>
            <div id="favoritesList" class="history-grid">
                <!-- 收藏记录将动态加载到这里 -->
            </div>
        </div>
    </div>

    <!-- 全屏预览模态框 -->
    <div id="fullscreenPreviewModal" class="fullscreen-modal" style="display: none;">
        <div class="fullscreen-header">
            <h3><i class="fas fa-image"></i> 全屏预览</h3>
            <div class="fullscreen-actions">
                <button class="btn-icon" onclick="zoomPreview('in')" title="放大">
                    <i class="fas fa-search-plus"></i>
                </button>
                <button class="btn-icon" onclick="zoomPreview('out')" title="缩小">
                    <i class="fas fa-search-minus"></i>
                </button>
                <button class="btn-icon" onclick="zoomPreview('reset')" title="重置">
                    <i class="fas fa-undo"></i>
                </button>
                <button class="btn-icon" onclick="downloadImage('png')" title="下载PNG">
                    <i class="fas fa-file-image"></i> PNG
                </button>
                <button class="btn-icon" onclick="downloadImage('svg')" title="下载SVG">
                    <i class="fas fa-file-code"></i> SVG
                </button>
                <button class="btn-icon" onclick="closeFullscreenPreview()" title="关闭">
                    <i class="fas fa-times"></i>
                </button>
            </div>
        </div>
        <div class="fullscreen-body">
            <div id="fullscreenPreviewContainer" class="fullscreen-preview-container">
                <!-- 图片将在这里显示 -->
            </div>
        </div>
    </div>

    <!-- Toast通知 -->
    <div id="toast" class="toast"></div>

    <!-- 打赏模态框 -->
    <div id="donateModal" class="modal" style="display: none;">
        <div class="modal-content donate-modal-ai" style="max-width: 500px;">
            <span class="close" onclick="closeDonateModal()">&times;</span>
            <div class="donate-container">
                <h2 style="text-align: center; color: #ff6b6b;">
                    <i class="fas fa-heart"></i> 打赏支持
                </h2>
                <p style="text-align: center; color: #666; margin: 1rem 0;">
                    如果您觉得这个工具对您有帮助，欢迎打赏支持！
                </p>
                <div style="text-align: center; margin: 2rem 0;">
                    <div style="width: 200px; height: 200px; border: 2px dashed #ccc; border-radius: 8px; margin: 0 auto; display: flex; align-items: center; justify-content: center;">
                        <img id="donateQRCode" src="assets/wechat-pay.png" alt="微信收款码" 
                             style="max-width: 100%; max-height: 100%; border-radius: 8px; display: none;"
                             onload="this.style.display='block'; this.nextElementSibling.style.display='none';">
                        <div style="padding: 1rem; text-align: center; color: #999;">
                            <p style="margin: 0.5rem 0; font-size: 0.9rem;">请上传您的微信收款码到：</p>
                            <p style="margin: 0.5rem 0; font-size: 0.8rem; font-family: monospace; background: #f5f5f5; padding: 0.5rem; border-radius: 4px;">src/main/webapp/assets/wechat-pay.png</p>
                        </div>
                    </div>
                </div>
                <div style="text-align: center; padding-top: 1rem; border-top: 1px solid #eee;">
                    <p style="margin: 0.5rem 0;">
                        <strong>微信号：</strong>
                        <span id="donateWechatId" style="color: #07C160; font-weight: bold; font-size: 1.1rem;">请在ai-app.js中设置</span>
                        <button onclick="copyDonateWechatId()" style="margin-left: 0.5rem; padding: 0.3rem 0.8rem; background: white; border: 1px solid #ccc; border-radius: 4px; cursor: pointer;">
                            <i class="fas fa-copy"></i> 复制
                        </button>
                    </p>
                </div>
            </div>
        </div>
    </div>

    <script src="js/ai-app.js"></script>
</body>
</html>

