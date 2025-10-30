/*****************
* Donate Modal JS *
******************/

// 设置您的微信号
const WECHAT_ID = "hualinyuezhao";  // 请在这里修改为您的微信号

// 微信收款码图片路径
const WECHAT_PAY_QR_CODE = "assets/wechat-pay.png";  // 可选：如果您上传了收款码图片

function initDonate() {
  // 设置微信号
  const wechatIdElement = document.getElementById('wechat-id-text');
  if (wechatIdElement) {
    wechatIdElement.textContent = WECHAT_ID;
  }

  // 尝试加载微信收款码图片
  loadWechatQRCode();
}

function loadWechatQRCode() {
  const qrcodeContainer = document.getElementById('wechat-qrcode');
  if (!qrcodeContainer) return;

  // 创建图片元素
  const img = new Image();
  img.src = WECHAT_PAY_QR_CODE;
  
  img.onload = function() {
    // 图片加载成功，替换占位符
    qrcodeContainer.innerHTML = '';
    qrcodeContainer.appendChild(img);
  };
  
  img.onerror = function() {
    // 图片加载失败，保持占位符显示
    console.log('微信收款码图片未找到，显示占位符');
  };
}

function copyWechatId() {
  const wechatId = document.getElementById('wechat-id-text').textContent;
  
  // 使用现代 Clipboard API
  if (navigator.clipboard && navigator.clipboard.writeText) {
    navigator.clipboard.writeText(wechatId).then(function() {
      showCopySuccess();
    }).catch(function(err) {
      console.error('复制失败:', err);
      fallbackCopyTextToClipboard(wechatId);
    });
  } else {
    // 降级方案
    fallbackCopyTextToClipboard(wechatId);
  }
}

function fallbackCopyTextToClipboard(text) {
  const textArea = document.createElement("textarea");
  textArea.value = text;
  textArea.style.position = "fixed";
  textArea.style.top = "0";
  textArea.style.left = "0";
  textArea.style.width = "2em";
  textArea.style.height = "2em";
  textArea.style.padding = "0";
  textArea.style.border = "none";
  textArea.style.outline = "none";
  textArea.style.boxShadow = "none";
  textArea.style.background = "transparent";
  document.body.appendChild(textArea);
  textArea.focus();
  textArea.select();

  try {
    const successful = document.execCommand('copy');
    if (successful) {
      showCopySuccess();
    } else {
      alert('复制失败，请手动复制微信号');
    }
  } catch (err) {
    console.error('复制失败:', err);
    alert('复制失败，请手动复制微信号');
  }

  document.body.removeChild(textArea);
}

function showCopySuccess() {
  const copyBtn = document.querySelector('.donate-modal .copy-btn');
  if (copyBtn) {
    const originalText = copyBtn.textContent;
    copyBtn.textContent = '已复制!';
    copyBtn.style.backgroundColor = '#07C160';
    copyBtn.style.color = 'white';
    
    setTimeout(function() {
      copyBtn.textContent = originalText;
      copyBtn.style.backgroundColor = '';
      copyBtn.style.color = '';
    }, 2000);
  }
}

