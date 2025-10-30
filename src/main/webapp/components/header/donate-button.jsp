<div class="donate-button-container">
  <button class="donate-button" onclick="openModal('donate')" title="打赏支持">
    <img src="assets/actions/donate.svg" alt="打赏" />
    <span>打赏</span>
  </button>
</div>

<style>
.donate-button-container {
  position: fixed;
  top: 1rem;
  right: 1rem;
  z-index: 999;
}

.donate-button {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #ff6b6b, #ff8e53);
  color: white;
  border: none;
  padding: 0.6rem 1.2rem;
  border-radius: 25px;
  cursor: pointer;
  font-size: 1rem;
  font-weight: bold;
  box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
  transition: all 0.3s ease;
  animation: pulse 2s infinite;
}

.donate-button:hover {
  transform: translateY(-2px);
  box-shadow: 0 6px 20px rgba(255, 107, 107, 0.6);
  animation: none;
}

.donate-button:active {
  transform: translateY(0);
  box-shadow: 0 2px 10px rgba(255, 107, 107, 0.4);
}

.donate-button img {
  width: 1.2rem;
  height: 1.2rem;
  filter: brightness(0) invert(1);
}

@keyframes pulse {
  0%, 100% {
    box-shadow: 0 4px 15px rgba(255, 107, 107, 0.4);
  }
  50% {
    box-shadow: 0 4px 20px rgba(255, 107, 107, 0.7);
  }
}

/* 移动端适配 */
@media screen and (max-width: 900px) {
  .donate-button-container {
    top: 0.5rem;
    right: 0.5rem;
  }
  
  .donate-button {
    padding: 0.5rem 1rem;
    font-size: 0.9rem;
  }
  
  .donate-button img {
    width: 1rem;
    height: 1rem;
  }
  
  .donate-button span {
    display: none;
  }
}

/* 深色主题适配 */
[data-theme="dark"] .donate-button {
  background: linear-gradient(135deg, #e74c3c, #ff6b6b);
}
</style>

