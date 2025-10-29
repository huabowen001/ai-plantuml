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
                                    <span>保存到历史记录</span>
                                </label>
                                <button class="btn-primary btn-large" onclick="generateDiagram()">
                                    <i class="fas fa-magic"></i> AI生成图表
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
                            <button class="btn-secondary" onclick="optimizeDiagram()">
                                <i class="fas fa-wand-magic-sparkles"></i> AI优化
                            </button>
                        </div>
                    </div>
                </div>

                <div class="editor-panel preview-panel">
                    <div class="panel-header">
                        <h3><i class="fas fa-image"></i> 预览</h3>
                        <div class="panel-actions">
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

    <!-- Toast通知 -->
    <div id="toast" class="toast"></div>

    <script src="js/ai-app.js"></script>
</body>
</html>

