<div id="donate" class="modal" style="display: none;" tabindex="-1">
  <div class="modal-content flex-rows donate-modal">
    <div class="modal-header">
      <h2>💖 打赏支持</h2>
      <div class="hr"></div>
    </div>
    <div class="modal-main flex-main donate-main">
      <div class="donate-info">
        <p>如果您觉得这个工具对您有帮助，欢迎打赏支持！</p>
      </div>
      <div class="donate-qrcode-container">
        <div class="qrcode-placeholder" id="wechat-qrcode">
          <!-- 这里放置微信收款码图片 -->
          <div class="qrcode-box">
            <p>请上传您的微信收款码到：</p>
            <p class="file-path">src/main/webapp/assets/wechat-pay.png</p>
          </div>
        </div>
      </div>
      <div class="donate-contact">
        <p class="wechat-id">
          <strong>微信号：</strong>
          <span id="wechat-id-text">请在donate.jsp中设置您的微信号</span>
          <button class="copy-btn" onclick="copyWechatId()" title="复制微信号">复制</button>
        </p>
      </div>
    </div>
    <div class="modal-footer">
      <input class="cancel" type="button" value="关闭" onclick="closeModal('donate');" />
    </div>
  </div>
</div>

