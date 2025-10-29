// 全局状态
let currentUser = null;
let currentToken = null;
let currentHistoryId = null;
let currentIsFavorite = false;
let currentPage = 1;
const pageSize = 12;

// API基础URL
const API_BASE_URL = window.location.origin + '/plantuml/api';

// 初始化
document.addEventListener('DOMContentLoaded', function() {
    // 检查是否已登录
    const savedToken = localStorage.getItem('authToken');
    const savedUser = localStorage.getItem('currentUser');
    
    if (savedToken && savedUser) {
        currentToken = savedToken;
        currentUser = JSON.parse(savedUser);
        showMainApp();
    } else {
        showAuthModal();
    }
    
    // 设置模态框关闭事件
    setupModalEvents();
});

// 设置模态框事件
function setupModalEvents() {
    const modal = document.getElementById('authModal');
    const closeBtn = document.querySelector('.close');
    const authTabs = document.querySelectorAll('.auth-tab');
    
    if (closeBtn) {
        closeBtn.onclick = function() {
            if (currentToken) {
                modal.style.display = 'none';
            }
        };
    }
    
    authTabs.forEach(tab => {
        tab.addEventListener('click', function() {
            const targetTab = this.dataset.tab;
            switchAuthTab(targetTab);
        });
    });
    
    // 回车键提交
    document.getElementById('loginPassword').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            handleLogin();
        }
    });
    
    document.getElementById('registerPassword').addEventListener('keypress', function(e) {
        if (e.key === 'Enter') {
            handleRegister();
        }
    });
}

// 切换认证标签
function switchAuthTab(tab) {
    document.querySelectorAll('.auth-tab').forEach(t => t.classList.remove('active'));
    document.querySelectorAll('.auth-form').forEach(f => f.classList.remove('active'));
    
    document.querySelector(`[data-tab="${tab}"]`).classList.add('active');
    document.getElementById(tab + 'Form').classList.add('active');
    
    // 清空错误信息
    document.querySelectorAll('.error-message').forEach(e => {
        e.classList.remove('show');
        e.textContent = '';
    });
}

// 显示认证模态框
function showAuthModal() {
    document.getElementById('authModal').style.display = 'flex';
    document.getElementById('mainApp').style.display = 'none';
}

// 显示主应用
function showMainApp() {
    document.getElementById('authModal').style.display = 'none';
    document.getElementById('mainApp').style.display = 'block';
    
    if (currentUser) {
        document.getElementById('userDisplayName').textContent = 
            currentUser.fullName || currentUser.username;
    }
    
    showSection('editor');
}

// 处理登录
async function handleLogin() {
    const username = document.getElementById('loginUsername').value.trim();
    const password = document.getElementById('loginPassword').value;
    const errorDiv = document.getElementById('loginError');
    
    if (!username || !password) {
        showError(errorDiv, '请输入用户名和密码');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/login`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, password })
        });
        
        const data = await response.json();
        
        if (data.success) {
            currentToken = data.token;
            currentUser = data.user;
            
            localStorage.setItem('authToken', currentToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showToast('登录成功！', 'success');
            showMainApp();
        } else {
            showError(errorDiv, data.message || '登录失败');
        }
    } catch (error) {
        showError(errorDiv, '网络错误，请稍后再试');
        console.error('Login error:', error);
    }
}

// 处理注册
async function handleRegister() {
    const username = document.getElementById('registerUsername').value.trim();
    const email = document.getElementById('registerEmail').value.trim();
    const password = document.getElementById('registerPassword').value;
    const fullName = document.getElementById('registerFullName').value.trim();
    const errorDiv = document.getElementById('registerError');
    
    if (!username || !email || !password) {
        showError(errorDiv, '请填写所有必填项');
        return;
    }
    
    if (password.length < 6) {
        showError(errorDiv, '密码长度至少为6位');
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/auth/register`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({ username, email, password, fullName })
        });
        
        const data = await response.json();
        
        if (data.success) {
            currentToken = data.token;
            currentUser = data.user;
            
            localStorage.setItem('authToken', currentToken);
            localStorage.setItem('currentUser', JSON.stringify(currentUser));
            
            showToast('注册成功！', 'success');
            showMainApp();
        } else {
            showError(errorDiv, data.message || '注册失败');
        }
    } catch (error) {
        showError(errorDiv, '网络错误，请稍后再试');
        console.error('Register error:', error);
    }
}

// 处理登出
function handleLogout() {
    localStorage.removeItem('authToken');
    localStorage.removeItem('currentUser');
    currentToken = null;
    currentUser = null;
    
    showToast('已退出登录', 'success');
    showAuthModal();
}

// 显示错误信息
function showError(element, message) {
    element.textContent = message;
    element.classList.add('show');
    setTimeout(() => {
        element.classList.remove('show');
    }, 5000);
}

// 显示Toast通知
function showToast(message, type = 'success') {
    const toast = document.getElementById('toast');
    toast.textContent = message;
    toast.className = `toast ${type} show`;
    
    setTimeout(() => {
        toast.classList.remove('show');
    }, 3000);
}

// 切换内容区域
function showSection(section) {
    document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
    document.getElementById(section + 'Section').classList.add('active');
    
    if (section === 'history') {
        loadHistory();
    } else if (section === 'favorites') {
        loadFavorites();
    }
}

// 生成图表
async function generateDiagram() {
    const description = document.getElementById('descriptionInput').value.trim();
    const title = document.getElementById('diagramTitle').value.trim();
    const saveHistory = document.getElementById('saveHistory').checked;
    
    if (!description) {
        showToast('请输入图表描述', 'error');
        return;
    }
    
    const loadingDiv = document.getElementById('previewLoading');
    loadingDiv.style.display = 'flex';
    
    try {
        const response = await fetch(`${API_BASE_URL}/ai/convert`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify({ 
                description, 
                title: title || '未命名图表', 
                saveHistory 
            })
        });
        
        const data = await response.json();
        
        if (data.success) {
            document.getElementById('plantumlCode').value = data.plantumlCode;
            if (data.historyId) {
                currentHistoryId = data.historyId;
                currentIsFavorite = false; // 新生成的图表默认未收藏
                updateFavoriteButton();
            }
            showToast('生成成功！', 'success');
            refreshPreview();
        } else {
            showToast(data.message || '生成失败', 'error');
        }
    } catch (error) {
        showToast('网络错误，请稍后再试', 'error');
        console.error('Generate error:', error);
    } finally {
        loadingDiv.style.display = 'none';
    }
}

// 优化图表
async function optimizeDiagram() {
    const plantumlCodeElement = document.getElementById('plantumlCode');
    const plantumlCode = plantumlCodeElement.value.trim();
    const instructionElement = document.getElementById('optimizeInstruction');
    const instruction = instructionElement.value.trim();
    
    if (!plantumlCode) {
        showToast('请先生成PlantUML代码', 'error');
        return;
    }
    
    if (!instruction) {
        showToast('请输入优化指令', 'error');
        return;
    }
    
    const loadingDiv = document.getElementById('previewLoading');
    loadingDiv.style.display = 'flex';
    
    try {
        console.log('发送优化请求...');
        const response = await fetch(`${API_BASE_URL}/ai/optimize`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify({ plantumlCode, instruction })
        });
        
        const data = await response.json();
        console.log('优化响应:', data);
        
        if (data.success && data.plantumlCode) {
            // 强制更新代码编辑器的值
            plantumlCodeElement.value = data.plantumlCode;
            
            // 触发input事件确保更新
            plantumlCodeElement.dispatchEvent(new Event('input', { bubbles: true }));
            
            console.log('代码已更新:', data.plantumlCode.substring(0, 100) + '...');
            
            // 保存优化后的代码到历史记录（使用优化指令作为版本描述）
            const saveHistory = document.getElementById('saveHistory').checked;
            const versionDescription = '优化：' + instruction;
            if (currentHistoryId) {
                // 更新现有历史记录
                await updateHistoryRecord(currentHistoryId, data.plantumlCode, versionDescription);
            } else if (saveHistory) {
                // 创建新的历史记录
                await saveOptimizedToHistory(data.plantumlCode, versionDescription);
            }
            
            // 清空优化指令
            instructionElement.value = '';
            
            showToast('优化成功！代码已更新', 'success');
            
            // 延迟一下再刷新预览，确保DOM已更新
            setTimeout(() => {
                refreshPreview();
            }, 100);
        } else {
            console.error('优化失败:', data);
            showToast(data.message || '优化失败', 'error');
        }
    } catch (error) {
        console.error('Optimize error:', error);
        showToast('网络错误，请稍后再试', 'error');
    } finally {
        loadingDiv.style.display = 'none';
    }
}

// 更新历史记录
async function updateHistoryRecord(historyId, plantumlCode, versionDescription) {
    try {
        const response = await fetch(`${API_BASE_URL}/history/${historyId}`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify({
                plantumlCode: plantumlCode,
                title: document.getElementById('diagramTitle').value.trim() || '未命名图表',
                changeDescription: versionDescription || 'AI优化更新'
            })
        });
        
        const data = await response.json();
        if (data.success) {
            console.log('历史记录已更新，版本已保存:', versionDescription);
        }
    } catch (error) {
        console.error('Update history error:', error);
    }
}

// 查看历史记录的版本列表
async function viewVersionHistory(historyId) {
    try {
        const response = await fetch(`${API_BASE_URL}/history/${historyId}/versions`, {
            headers: {
                'Authorization': `Bearer ${currentToken}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            showVersionModal(data.data, historyId);
        } else {
            showToast(data.message || '获取版本失败', 'error');
        }
    } catch (error) {
        showToast('网络错误，请稍后再试', 'error');
        console.error('View version history error:', error);
    }
}

// 显示版本列表模态框
function showVersionModal(versions, historyId) {
    if (versions.length === 0) {
        showToast('暂无历史版本', 'info');
        return;
    }
    
    const modal = document.createElement('div');
    modal.className = 'version-modal';
    modal.innerHTML = `
        <div class="version-modal-content">
            <div class="version-modal-header">
                <h3><i class="fas fa-history"></i> 版本历史（最近20个版本）</h3>
                <button class="version-modal-close" onclick="this.closest('.version-modal').remove()">
                    <i class="fas fa-times"></i>
                </button>
            </div>
            <div class="version-modal-body">
                        <div class="version-list">
                            ${versions.map(v => `
                                <div class="version-item" onclick="restoreVersion(${v.id}, ${historyId})">
                                    <div class="version-item-header">
                                        <span class="version-number">${escapeHtml(v.title || '版本 ' + v.versionNumber)}</span>
                                        <span class="version-date">${formatDate(v.createdAt)}</span>
                                    </div>
                                    ${v.changeDescription ? `<div class="version-item-desc">${escapeHtml(v.changeDescription)}</div>` : ''}
                                </div>
                            `).join('')}
                        </div>
            </div>
            <div class="version-modal-footer">
                <button class="btn-secondary" onclick="this.closest('.version-modal').remove()">关闭</button>
            </div>
        </div>
    `;
    
    document.body.appendChild(modal);
    
    // 点击背景关闭
    modal.addEventListener('click', (e) => {
        if (e.target === modal) {
            modal.remove();
        }
    });
}

// 恢复到指定版本
async function restoreVersion(versionId, historyId) {
    if (!confirm('确定要恢复到此版本吗？当前未保存的修改将会丢失。')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/history/${historyId}/versions/${versionId}`, {
            headers: {
                'Authorization': `Bearer ${currentToken}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            const version = data.data;
            // 更新编辑器
            document.getElementById('plantumlCode').value = version.plantumlCode;
            
            // 关闭模态框
            document.querySelector('.version-modal')?.remove();
            
            // 刷新预览
            refreshPreview();
            
            showToast('已恢复到：' + (version.title || '版本 ' + version.versionNumber), 'success');
            
            // 自动保存为新版本
            await updateHistoryRecord(historyId, version.plantumlCode, '恢复版本：' + (version.title || '版本 ' + version.versionNumber));
        } else {
            showToast(data.message || '恢复失败', 'error');
        }
    } catch (error) {
        showToast('网络错误，请稍后再试', 'error');
        console.error('Restore version error:', error);
    }
}

// 保存优化后的代码到历史记录
async function saveOptimizedToHistory(plantumlCode, versionDescription) {
    try {
        const response = await fetch(`${API_BASE_URL}/history`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify({
                originalText: document.getElementById('descriptionInput').value.trim(),
                plantumlCode: plantumlCode,
                title: document.getElementById('diagramTitle').value.trim() || '未命名图表',
                description: versionDescription || '首次保存'
            })
        });
        
        const data = await response.json();
        if (data.success && data.id) {
            currentHistoryId = data.id;
            currentIsFavorite = false;
            updateFavoriteButton();
            console.log('已保存到历史记录:', versionDescription);
        }
    } catch (error) {
        console.error('Save optimized history error:', error);
    }
}

// 刷新预览
async function refreshPreview() {
    const plantumlCode = document.getElementById('plantumlCode').value.trim();
    
    if (!plantumlCode) {
        showToast('请先生成PlantUML代码', 'warning');
        return;
    }
    
    const previewContainer = document.getElementById('previewContainer');
    previewContainer.innerHTML = '<div class="loading-spinner"><i class="fas fa-spinner fa-spin"></i><p>生成预览中...</p></div>';
    
    try {
        // 使用PlantUML服务器的coder API进行正确编码
        const response = await fetch(`${window.location.origin}/plantuml/coder`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain'
            },
            body: plantumlCode
        });
        
        if (response.ok) {
            const encoded = await response.text();
            const imageUrl = `${window.location.origin}/plantuml/svg/${encoded}`;
            previewContainer.innerHTML = `<img src="${imageUrl}" alt="PlantUML Diagram" onerror="handlePreviewError()">`;
            
            // 显示全屏按钮
            setTimeout(() => {
                const fullscreenBtn = document.getElementById('fullscreenBtn');
                if (fullscreenBtn && previewContainer.querySelector('img')) {
                    fullscreenBtn.style.display = 'inline-flex';
                }
            }, 100);
        } else {
            handlePreviewError();
        }
    } catch (error) {
        console.error('Preview error:', error);
        handlePreviewError();
    }
}

// 处理预览错误
function handlePreviewError() {
    const previewContainer = document.getElementById('previewContainer');
    previewContainer.innerHTML = '<p class="preview-placeholder" style="color: var(--error-color);">预览生成失败，请检查PlantUML代码</p>';
}

// 清空输入
function clearInput() {
    document.getElementById('descriptionInput').value = '';
    document.getElementById('diagramTitle').value = '';
    document.getElementById('plantumlCode').value = '';
    document.getElementById('previewContainer').innerHTML = '<p class="preview-placeholder">图表预览将在这里显示</p>';
    currentHistoryId = null;
    currentIsFavorite = false;
    // 隐藏收藏和版本按钮
    document.getElementById('favoriteBtn').style.display = 'none';
    document.getElementById('versionBtn').style.display = 'none';
}

// 复制代码
function copyCode() {
    const code = document.getElementById('plantumlCode').value;
    
    if (!code) {
        showToast('没有可复制的代码', 'warning');
        return;
    }
    
    navigator.clipboard.writeText(code).then(() => {
        showToast('代码已复制到剪贴板', 'success');
    }).catch(() => {
        showToast('复制失败', 'error');
    });
}

// 下载代码
function downloadCode() {
    const code = document.getElementById('plantumlCode').value;
    const title = document.getElementById('diagramTitle').value || 'diagram';
    
    if (!code) {
        showToast('没有可下载的代码', 'warning');
        return;
    }
    
    const blob = new Blob([code], { type: 'text/plain' });
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = `${title}.puml`;
    a.click();
    URL.revokeObjectURL(url);
    
    showToast('代码已下载', 'success');
}

// 下载图片
async function downloadImage(format) {
    const plantumlCode = document.getElementById('plantumlCode').value.trim();
    const title = document.getElementById('diagramTitle').value || 'diagram';
    
    if (!plantumlCode) {
        showToast('请先生成图表', 'warning');
        return;
    }
    
    try {
        // 使用PlantUML服务器的coder API进行正确编码
        const response = await fetch(`${window.location.origin}/plantuml/coder`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain'
            },
            body: plantumlCode
        });
        
        if (response.ok) {
            const encoded = await response.text();
            const imageUrl = `${window.location.origin}/plantuml/${format}/${encoded}`;
            
            const a = document.createElement('a');
            a.href = imageUrl;
            a.download = `${title}.${format}`;
            a.click();
            
            showToast(`图片已下载 (${format.toUpperCase()})`, 'success');
        } else {
            showToast('编码失败，请检查PlantUML代码', 'error');
        }
    } catch (error) {
        console.error('Download error:', error);
        showToast('下载失败，请稍后再试', 'error');
    }
}

// 加载历史记录
async function loadHistory(page = 1) {
    currentPage = page;
    
    try {
        console.log('Loading history, page:', page);
        const response = await fetch(`${API_BASE_URL}/history?page=${page}&pageSize=${pageSize}`, {
            headers: {
                'Authorization': `Bearer ${currentToken}`
            }
        });
        
        console.log('Response status:', response.status);
        
        if (!response.ok) {
            throw new Error(`HTTP error! status: ${response.status}`);
        }
        
        const data = await response.json();
        console.log('History data:', data);
        
        if (data.success) {
            console.log('Displaying history list with', data.data.length, 'items');
            await displayHistoryList(data.data);
            displayPagination(data.total, data.page, data.pageSize);
        } else {
            console.error('API returned success=false:', data.message);
            showToast(data.message || '加载失败', 'error');
        }
    } catch (error) {
        console.error('Load history error:', error);
        console.error('Error stack:', error.stack);
        showToast('网络错误，请稍后再试: ' + error.message, 'error');
    }
}

// 显示历史记录列表
async function displayHistoryList(histories) {
    const container = document.getElementById('historyList');
    
    if (!histories || histories.length === 0) {
        container.innerHTML = '<p style="text-align: center; color: var(--text-muted); grid-column: 1/-1;">暂无历史记录</p>';
        return;
    }
    
    try {
        // 先显示占位符
        container.innerHTML = histories.map(history => {
            const isFavorite = history.isFavorite || false;
            const title = escapeHtml(history.title || '未命名图表');
            const originalText = escapeHtml(history.originalText || '');
            const createdAt = formatDate(history.createdAt);
            
            return `
                <div class="history-card" onclick="loadHistoryItem(${history.id})">
                    <div class="history-card-header">
                        <div>
                            <div class="history-card-title">${title}</div>
                            <div class="history-card-date">${createdAt}</div>
                        </div>
                        <div class="history-card-actions">
                            <button onclick="event.stopPropagation(); toggleFavorite(${history.id}, ${!isFavorite})" title="${isFavorite ? '取消收藏' : '收藏'}">
                                <i class="${isFavorite ? 'fas' : 'far'} fa-star"></i>
                            </button>
                            <button onclick="event.stopPropagation(); deleteHistory(${history.id})" title="删除">
                                <i class="fas fa-trash"></i>
                            </button>
                        </div>
                    </div>
                    <div class="history-card-description">${originalText}</div>
                    <div class="history-card-preview">
                        <div class="loading-spinner" data-id="${history.id}">加载中...</div>
                    </div>
                </div>
            `;
        }).join('');
        
        // 异步加载所有预览图
        for (const history of histories) {
            encodePlantUML(history.plantumlCode).then(encoded => {
                const placeholder = document.querySelector(`.loading-spinner[data-id="${history.id}"]`);
                if (placeholder && placeholder.parentElement) {
                    placeholder.parentElement.innerHTML = `<img src="${window.location.origin}/plantuml/svg/${encoded}" alt="Preview" onerror="this.src='data:image/svg+xml;base64,PHN2ZyB3aWR0aD0iMTAwIiBoZWlnaHQ9IjEwMCIgeG1sbnM9Imh0dHA6Ly93d3cudzMub3JnLzIwMDAvc3ZnIj48dGV4dCB4PSI1MCIgeT0iNTAiIHRleHQtYW5jaG9yPSJtaWRkbGUiPumihOiniOWksei0pTwvdGV4dD48L3N2Zz4='">`;
                }
            }).catch(err => {
                console.error('Failed to encode PlantUML for history', history.id, err);
                const placeholder = document.querySelector(`.loading-spinner[data-id="${history.id}"]`);
                if (placeholder && placeholder.parentElement) {
                    placeholder.parentElement.innerHTML = '<div style="text-align:center;color:#999;">预览失败</div>';
                }
            });
        }
    } catch (error) {
        console.error('Error displaying history list:', error);
        container.innerHTML = '<p style="text-align: center; color: var(--text-muted); grid-column: 1/-1;">显示历史记录时出错</p>';
    }
}

// 显示分页
function displayPagination(total, page, pageSize) {
    const totalPages = Math.ceil(total / pageSize);
    const container = document.getElementById('historyPagination');
    
    if (totalPages <= 1) {
        container.innerHTML = '';
        return;
    }
    
    let html = '';
    
    html += `<button onclick="loadHistory(${page - 1})" ${page <= 1 ? 'disabled' : ''}>上一页</button>`;
    
    for (let i = 1; i <= totalPages; i++) {
        if (i === 1 || i === totalPages || (i >= page - 2 && i <= page + 2)) {
            html += `<button onclick="loadHistory(${i})" class="${i === page ? 'active' : ''}">${i}</button>`;
        } else if (i === page - 3 || i === page + 3) {
            html += '<span>...</span>';
        }
    }
    
    html += `<button onclick="loadHistory(${page + 1})" ${page >= totalPages ? 'disabled' : ''}>下一页</button>`;
    
    container.innerHTML = html;
}

// 加载历史记录项
async function loadHistoryItem(id) {
    try {
        const response = await fetch(`${API_BASE_URL}/history/${id}`, {
            headers: {
                'Authorization': `Bearer ${currentToken}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            const history = data.data;
            document.getElementById('descriptionInput').value = history.originalText;
            document.getElementById('plantumlCode').value = history.plantumlCode;
            document.getElementById('diagramTitle').value = history.title;
            currentHistoryId = history.id;
            currentIsFavorite = history.isFavorite || false;
            
            // 更新收藏按钮状态
            updateFavoriteButton();
            
            showSection('editor');
            refreshPreview();
            showToast('已加载历史记录', 'success');
        } else {
            showToast(data.message || '加载失败', 'error');
        }
    } catch (error) {
        showToast('网络错误，请稍后再试', 'error');
        console.error('Load history item error:', error);
    }
}

// 切换收藏状态
async function toggleFavorite(id, isFavorite) {
    try {
        const response = await fetch(`${API_BASE_URL}/history/${id}/favorite`, {
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${currentToken}`
            },
            body: JSON.stringify({ isFavorite })
        });
        
        const data = await response.json();
        
        if (data.success) {
            showToast(data.message, 'success');
            
            // 根据当前页面刷新对应的列表
            const currentSection = document.querySelector('.content-section.active');
            if (currentSection && currentSection.id === 'favoritesSection') {
                // 如果在收藏页面，刷新收藏列表
                loadFavorites();
            } else {
                // 否则刷新历史记录列表
                loadHistory(currentPage);
            }
            
            // 如果当前编辑器中的图表就是这个，更新收藏状态
            if (currentHistoryId === id) {
                currentIsFavorite = isFavorite;
                updateFavoriteButton();
            }
        } else {
            showToast(data.message || '操作失败', 'error');
        }
    } catch (error) {
        showToast('网络错误，请稍后再试', 'error');
        console.error('Toggle favorite error:', error);
    }
}

// 切换当前图表的收藏状态（在编辑器页面）
async function toggleCurrentFavorite() {
    // 如果没有历史记录ID，需要先保存
    if (!currentHistoryId) {
        const plantumlCode = document.getElementById('plantumlCode').value.trim();
        const originalText = document.getElementById('descriptionInput').value.trim();
        const title = document.getElementById('diagramTitle').value.trim() || '未命名图表';
        
        if (!plantumlCode || !originalText) {
            showToast('请先生成图表', 'error');
            return;
        }
        
        // 创建历史记录
        try {
            const response = await fetch(`${API_BASE_URL}/history`, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${currentToken}`
                },
                body: JSON.stringify({
                    originalText,
                    plantumlCode,
                    title,
                    isFavorite: true
                })
            });
            
            const data = await response.json();
            
            if (data.success) {
                currentHistoryId = data.id;
                currentIsFavorite = true;
                updateFavoriteButton();
                showToast('已收藏', 'success');
            } else {
                showToast(data.message || '收藏失败', 'error');
            }
        } catch (error) {
            showToast('网络错误，请稍后再试', 'error');
            console.error('Save and favorite error:', error);
        }
    } else {
        // 已有历史记录，直接切换收藏状态
        const newFavoriteState = !currentIsFavorite;
        
        try {
            const response = await fetch(`${API_BASE_URL}/history/${currentHistoryId}/favorite`, {
                method: 'PUT',
                headers: {
                    'Content-Type': 'application/json',
                    'Authorization': `Bearer ${currentToken}`
                },
                body: JSON.stringify({ isFavorite: newFavoriteState })
            });
            
            const data = await response.json();
            
            if (data.success) {
                currentIsFavorite = newFavoriteState;
                updateFavoriteButton();
                showToast(newFavoriteState ? '已收藏' : '已取消收藏', 'success');
            } else {
                showToast(data.message || '操作失败', 'error');
            }
        } catch (error) {
            showToast('网络错误，请稍后再试', 'error');
            console.error('Toggle current favorite error:', error);
        }
    }
}

// 更新收藏按钮的显示状态
function updateFavoriteButton() {
    const favoriteBtn = document.getElementById('favoriteBtn');
    const versionBtn = document.getElementById('versionBtn');
    const icon = favoriteBtn.querySelector('i');
    
    // 显示按钮
    favoriteBtn.style.display = '';
    
    // 如果有历史记录ID，显示版本按钮
    if (currentHistoryId) {
        versionBtn.style.display = '';
    }
    
    // 更新图标和提示
    if (currentIsFavorite) {
        icon.className = 'fas fa-star';
        favoriteBtn.title = '取消收藏';
    } else {
        icon.className = 'far fa-star';
        favoriteBtn.title = '收藏当前图表';
    }
}

// 删除历史记录
async function deleteHistory(id) {
    if (!confirm('确定要删除这条记录吗？')) {
        return;
    }
    
    try {
        const response = await fetch(`${API_BASE_URL}/history/${id}`, {
            method: 'DELETE',
            headers: {
                'Authorization': `Bearer ${currentToken}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            showToast('删除成功', 'success');
            loadHistory(currentPage);
        } else {
            showToast(data.message || '删除失败', 'error');
        }
    } catch (error) {
        showToast('网络错误，请稍后再试', 'error');
        console.error('Delete history error:', error);
    }
}

// 加载收藏列表
async function loadFavorites() {
    try {
        const response = await fetch(`${API_BASE_URL}/history/favorites`, {
            headers: {
                'Authorization': `Bearer ${currentToken}`
            }
        });
        
        const data = await response.json();
        
        if (data.success) {
            const container = document.getElementById('favoritesList');
            
            if (data.data.length === 0) {
                container.innerHTML = '<p style="text-align: center; color: var(--text-muted); grid-column: 1/-1;">暂无收藏</p>';
                return;
            }
            
            // 先显示占位符
            container.innerHTML = data.data.map(history => {
                const title = escapeHtml(history.title || '未命名图表');
                const originalText = escapeHtml(history.originalText || '');
                const createdAt = formatDate(history.createdAt);
                
                return `
                    <div class="history-card" onclick="loadHistoryItem(${history.id})">
                        <div class="history-card-header">
                            <div>
                                <div class="history-card-title">${title}</div>
                                <div class="history-card-date">${createdAt}</div>
                            </div>
                            <div class="history-card-actions">
                                <button onclick="event.stopPropagation(); toggleFavorite(${history.id}, false)" title="取消收藏">
                                    <i class="fas fa-star"></i>
                                </button>
                            </div>
                        </div>
                        <div class="history-card-description">${originalText}</div>
                        <div class="history-card-preview">
                            <div class="loading-spinner" data-id="${history.id}">加载中...</div>
                        </div>
                    </div>
                `;
            }).join('');
            
            // 异步加载所有预览图
            for (const history of data.data) {
                encodePlantUML(history.plantumlCode).then(encoded => {
                    const placeholder = document.querySelector(`.loading-spinner[data-id="${history.id}"]`);
                    if (placeholder && placeholder.parentElement) {
                        placeholder.parentElement.innerHTML = `<img src="${window.location.origin}/plantuml/svg/${encoded}" alt="Preview" onerror="this.parentElement.innerHTML='<div style=\\'text-align:center;color:#999;\\'>预览失败</div>'">`;
                    }
                }).catch(err => {
                    console.error('Failed to encode PlantUML for favorite', history.id, err);
                    const placeholder = document.querySelector(`.loading-spinner[data-id="${history.id}"]`);
                    if (placeholder && placeholder.parentElement) {
                        placeholder.parentElement.innerHTML = '<div style="text-align:center;color:#999;">预览失败</div>';
                    }
                });
            }
        } else {
            showToast(data.message || '加载失败', 'error');
        }
    } catch (error) {
        showToast('网络错误，请稍后再试', 'error');
        console.error('Load favorites error:', error);
    }
}

// 工具函数：转义HTML
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// 工具函数：编码PlantUML代码用于URL（使用服务器端编码API）
async function encodePlantUML(plantumlCode) {
    if (!plantumlCode) return '';
    try {
        // 使用PlantUML服务器的encode API
        const response = await fetch(`${window.location.origin}/plantuml/coder`, {
            method: 'POST',
            headers: {
                'Content-Type': 'text/plain'
            },
            body: plantumlCode
        });
        
        if (response.ok) {
            const encoded = await response.text();
            return encoded;
        }
    } catch (e) {
        console.error('Error encoding PlantUML:', e);
    }
    // 降级方案：使用简单的URL编码
    return encodeURIComponent(plantumlCode);
}

// 工具函数：格式化日期
function formatDate(dateString) {
    const date = new Date(dateString);
    const now = new Date();
    const diff = now - date;
    
    if (diff < 60000) {
        return '刚刚';
    } else if (diff < 3600000) {
        return `${Math.floor(diff / 60000)}分钟前`;
    } else if (diff < 86400000) {
        return `${Math.floor(diff / 3600000)}小时前`;
    } else if (diff < 604800000) {
        return `${Math.floor(diff / 86400000)}天前`;
    } else {
        return date.toLocaleDateString('zh-CN');
    }
}

// ========== 全屏预览功能 ==========
let currentZoom = 1;
let isDragging = false;
let startX = 0;
let startY = 0;
let scrollLeft = 0;
let scrollTop = 0;

// 打开全屏预览
function openFullscreenPreview() {
    const previewContainer = document.getElementById('previewContainer');
    const img = previewContainer.querySelector('img');
    
    if (!img) {
        showToast('没有可预览的图片', 'warning');
        return;
    }
    
    const fullscreenModal = document.getElementById('fullscreenPreviewModal');
    const fullscreenContainer = document.getElementById('fullscreenPreviewContainer');
    
    // 克隆图片到全屏容器
    const clonedImg = img.cloneNode(true);
    clonedImg.style.transform = 'scale(1)';
    currentZoom = 1;
    
    fullscreenContainer.innerHTML = '';
    fullscreenContainer.appendChild(clonedImg);
    
    // 显示模态框
    fullscreenModal.style.display = 'flex';
    
    // 添加拖拽功能
    setupImageDrag(fullscreenContainer);
    
    // ESC键关闭
    document.addEventListener('keydown', handleEscKey);
}

// 关闭全屏预览
function closeFullscreenPreview() {
    const fullscreenModal = document.getElementById('fullscreenPreviewModal');
    fullscreenModal.style.display = 'none';
    currentZoom = 1;
    document.removeEventListener('keydown', handleEscKey);
}

// ESC键处理
function handleEscKey(e) {
    if (e.key === 'Escape') {
        closeFullscreenPreview();
    }
}

// 缩放预览
function zoomPreview(action) {
    const fullscreenContainer = document.getElementById('fullscreenPreviewContainer');
    const img = fullscreenContainer.querySelector('img');
    
    if (!img) return;
    
    if (action === 'in') {
        currentZoom = Math.min(currentZoom + 0.2, 5);
    } else if (action === 'out') {
        currentZoom = Math.max(currentZoom - 0.2, 0.5);
    } else if (action === 'reset') {
        currentZoom = 1;
    }
    
    img.style.transform = `scale(${currentZoom})`;
    img.classList.toggle('zoomed', currentZoom > 1);
}

// 设置图片拖拽
function setupImageDrag(container) {
    container.addEventListener('mousedown', (e) => {
        if (e.target.tagName === 'IMG' && currentZoom > 1) {
            isDragging = true;
            startX = e.pageX - container.offsetLeft;
            startY = e.pageY - container.offsetTop;
            scrollLeft = container.scrollLeft;
            scrollTop = container.scrollTop;
            container.style.cursor = 'grabbing';
            e.preventDefault();
        }
    });
    
    container.addEventListener('mouseleave', () => {
        isDragging = false;
        container.style.cursor = 'default';
    });
    
    container.addEventListener('mouseup', () => {
        isDragging = false;
        const img = container.querySelector('img');
        if (img && currentZoom > 1) {
            container.style.cursor = 'grab';
        } else {
            container.style.cursor = 'default';
        }
    });
    
    container.addEventListener('mousemove', (e) => {
        if (!isDragging) return;
        e.preventDefault();
        const x = e.pageX - container.offsetLeft;
        const y = e.pageY - container.offsetTop;
        const walkX = (x - startX) * 2;
        const walkY = (y - startY) * 2;
        container.scrollLeft = scrollLeft - walkX;
        container.scrollTop = scrollTop - walkY;
    });
    
    // 滚轮缩放
    container.addEventListener('wheel', (e) => {
        if (e.ctrlKey || e.metaKey) {
            e.preventDefault();
            if (e.deltaY < 0) {
                zoomPreview('in');
            } else {
                zoomPreview('out');
            }
        }
    }, { passive: false });
}


