<template>
  <!-- 悬浮按钮 -->
  <div class="ai-widget">
    <button class="ai-fab" @click="toggleChat" :class="{ active: isOpen }">
      <span class="fab-icon">{{ isOpen ? '✕' : '🤖' }}</span>
      <span class="fab-text" v-if="!isOpen">AI 助手</span>
    </button>

    <!-- 对话框 -->
    <transition name="chat-slide">
      <div class="ai-chat-box" v-if="isOpen">
        <!-- 标题栏 -->
        <div class="chat-header">
          <div class="header-left">
            <span class="header-icon">🤖</span>
            <span class="header-title">AI 编程小助手</span>
          </div>
          <button class="close-btn" @click="toggleChat">✕</button>
        </div>

        <!-- 消息区 -->
        <div class="chat-messages" ref="messagesRef">
          <div class="msg-row ai">
            <div class="msg-avatar">🤖</div>
            <div class="msg-bubble ai-bubble">
              你好！有什么编程问题可以问我 👋
            </div>
          </div>

          <template v-for="(msg, i) in messages" :key="i">
            <div class="msg-row" :class="msg.role">
              <div class="msg-avatar">{{ msg.role === 'user' ? '👤' : '🤖' }}</div>
              <div class="msg-bubble" :class="msg.role === 'user' ? 'user-bubble' : 'ai-bubble'">
                <pre class="msg-text">{{ msg.content }}</pre>
              </div>
            </div>
          </template>

          <!-- 流式输出中 -->
          <div class="msg-row ai" v-if="streaming">
            <div class="msg-avatar">🤖</div>
            <div class="msg-bubble ai-bubble">
              <pre class="msg-text">{{ streaming }}</pre><span class="cursor">▍</span>
            </div>
          </div>

          <!-- 加载中 -->
          <div class="msg-row ai" v-if="loading && !streaming">
            <div class="msg-avatar">🤖</div>
            <div class="msg-bubble ai-bubble loading">
              <span class="dot"></span><span class="dot"></span><span class="dot"></span>
            </div>
          </div>
        </div>

        <!-- 输入区 -->
        <div class="chat-input-area">
          <textarea
            ref="inputRef"
            v-model="inputText"
            placeholder="输入问题，Enter 发送..."
            :disabled="loading"
            @keydown.enter.exact.prevent="send"
            rows="1"
          ></textarea>
          <button class="send-btn" @click="send" :disabled="loading || !inputText.trim()">
            发送
          </button>
        </div>
      </div>
    </transition>
  </div>
</template>

<script setup>
import { ref, nextTick } from 'vue'

const BASE_URL = 'http://localhost:8888'

const isOpen = ref(false)
const messages = ref([])
const inputText = ref('')
const loading = ref(false)
const streaming = ref('')
const messagesRef = ref(null)
const inputRef = ref(null)
const memoryId = ref(Math.floor(Math.random() * 1000000))

function toggleChat() {
  isOpen.value = !isOpen.value
  if (isOpen.value) {
    nextTick(() => inputRef.value?.focus())
  }
}

async function scrollBottom() {
  await nextTick()
  if (messagesRef.value) {
    messagesRef.value.scrollTop = messagesRef.value.scrollHeight
  }
}

let printQueue = []
let isPrinting = false
let doneCallback = null

function startPrinting() {
  if (isPrinting) return
  isPrinting = true
  const tick = () => {
    if (printQueue.length === 0) {
      isPrinting = false
      if (doneCallback) {
        const cb = doneCallback
        doneCallback = null
        cb()
      }
      return
    }
    streaming.value += printQueue.shift()
    scrollBottom()
    setTimeout(tick, 25)
  }
  tick()
}

function send() {
  const text = inputText.value.trim()
  if (!text || loading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  loading.value = true
  streaming.value = ''
  printQueue = []
  isPrinting = false
  doneCallback = null
  scrollBottom()

  const url = `${BASE_URL}/ai/chat?memoryId=${encodeURIComponent(memoryId.value)}&message=${encodeURIComponent(text)}`
  const es = new EventSource(url)

  es.onmessage = (e) => {
    for (const char of e.data) printQueue.push(char)
    startPrinting()
  }

  es.onerror = () => {
    es.close()
    doneCallback = () => {
      messages.value.push({ role: 'ai', content: streaming.value })
      streaming.value = ''
      loading.value = false
      scrollBottom()
    }
    if (!isPrinting) {
      const cb = doneCallback
      doneCallback = null
      cb()
    }
  }
}
</script>

<style scoped>
.ai-widget {
  position: fixed;
  bottom: 32px;
  right: 32px;
  z-index: 9999;
  display: flex;
  flex-direction: column;
  align-items: flex-end;
  gap: 12px;
}

/* 悬浮按钮 */
.ai-fab {
  display: flex;
  align-items: center;
  gap: 8px;
  padding: 12px 20px;
  background: linear-gradient(135deg, #ff5c5c, #e03030);
  color: #fff;
  border: none;
  border-radius: 50px;
  font-size: 15px;
  font-weight: 600;
  cursor: pointer;
  box-shadow: 0 4px 16px rgba(224, 48, 48, 0.45);
  transition: transform 0.2s, box-shadow 0.2s;
  white-space: nowrap;
}

.ai-fab:hover { transform: translateY(-2px); box-shadow: 0 6px 20px rgba(224, 48, 48, 0.5); }
.ai-fab.active { padding: 12px 16px; }
.fab-icon { font-size: 18px; }

/* 对话框 */
.ai-chat-box {
  width: 360px;
  height: 520px;
  background: #fff;
  border-radius: 16px;
  box-shadow: 0 8px 40px rgba(0, 0, 0, 0.18);
  display: flex;
  flex-direction: column;
  overflow: hidden;
}

/* 标题栏 */
.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 14px 16px;
  background: linear-gradient(135deg, #ff5c5c, #e03030);
  color: #fff;
  flex-shrink: 0;
}

.header-left { display: flex; align-items: center; gap: 8px; }
.header-icon { font-size: 20px; }
.header-title { font-size: 15px; font-weight: 600; }

.close-btn {
  background: none;
  border: none;
  color: #fff;
  font-size: 16px;
  cursor: pointer;
  opacity: 0.8;
  padding: 2px 6px;
  border-radius: 4px;
  transition: opacity 0.2s;
}
.close-btn:hover { opacity: 1; background: rgba(255,255,255,0.2); }

/* 消息区 */
.chat-messages {
  flex: 1;
  overflow-y: auto;
  padding: 14px 12px;
  display: flex;
  flex-direction: column;
  gap: 12px;
  background: #f7f8fc;
}

.chat-messages::-webkit-scrollbar { width: 4px; }
.chat-messages::-webkit-scrollbar-thumb { background: #ddd; border-radius: 2px; }

.msg-row { display: flex; align-items: flex-start; gap: 8px; }
.msg-row.user { flex-direction: row-reverse; }

.msg-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: #fff;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.1);
}

.msg-bubble {
  max-width: 75%;
  padding: 10px 13px;
  border-radius: 14px;
  font-size: 13px;
  line-height: 1.6;
  word-break: break-word;
}

.ai-bubble {
  background: #fff;
  border: 1px solid #eee;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 3px rgba(0,0,0,0.06);
}

.user-bubble {
  background: linear-gradient(135deg, #ff5c5c, #e03030);
  color: #fff;
  border-top-right-radius: 4px;
}

.msg-text { white-space: pre-wrap; font-family: inherit; font-size: 13px; }

.cursor {
  display: inline-block;
  animation: blink 0.8s step-end infinite;
  color: #e03030;
  font-weight: bold;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.loading { display: flex; align-items: center; gap: 4px; padding: 12px 14px; }
.dot {
  width: 7px; height: 7px;
  background: #bbb; border-radius: 50%;
  animation: bounce 1.2s infinite ease-in-out;
}
.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1.2); opacity: 1; }
}

/* 输入区 */
.chat-input-area {
  display: flex;
  align-items: flex-end;
  gap: 8px;
  padding: 10px 12px;
  border-top: 1px solid #eee;
  background: #fff;
  flex-shrink: 0;
}

.chat-input-area textarea {
  flex: 1;
  resize: none;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 8px 12px;
  font-size: 13px;
  font-family: inherit;
  line-height: 1.5;
  outline: none;
  max-height: 80px;
  overflow-y: auto;
  transition: border-color 0.2s;
}

.chat-input-area textarea:focus { border-color: #e03030; }
.chat-input-area textarea:disabled { background: #f5f5f5; }

.send-btn {
  padding: 8px 16px;
  background: linear-gradient(135deg, #ff5c5c, #e03030);
  color: #fff;
  border: none;
  border-radius: 10px;
  font-size: 13px;
  cursor: pointer;
  height: 36px;
  transition: opacity 0.2s;
  white-space: nowrap;
}

.send-btn:hover:not(:disabled) { opacity: 0.9; }
.send-btn:disabled { opacity: 0.5; cursor: not-allowed; }

/* 动画 */
.chat-slide-enter-active { animation: slideUp 0.25s ease-out; }
.chat-slide-leave-active { animation: slideUp 0.2s ease-in reverse; }

@keyframes slideUp {
  from { opacity: 0; transform: translateY(20px) scale(0.95); }
  to   { opacity: 1; transform: translateY(0) scale(1); }
}
</style>
