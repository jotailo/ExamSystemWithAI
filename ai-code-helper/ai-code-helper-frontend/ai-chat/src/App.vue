<template>
  <div class="chat-app">
    <!-- 原有的全屏聊天界面 -->
    <header class="chat-header">
      <div class="header-title">
        <span class="bot-icon">🤖</span>
        <span>AI 编程小助手</span>
      </div>
      <div class="session-id">会话 ID: {{ memoryId }}</div>
    </header>

    <main class="chat-body" ref="chatBodyRef">
      <div class="message-row ai">
        <div class="avatar ai-avatar">🤖</div>
        <div class="bubble ai-bubble">
          你好！我是 AI 编程小助手，可以帮你解答编程学习和求职面试相关的问题，随时问我吧 👋
        </div>
      </div>

      <template v-for="(msg, index) in messages" :key="index">
        <div class="message-row" :class="msg.role">
          <div class="avatar" :class="msg.role === 'user' ? 'user-avatar' : 'ai-avatar'">
            {{ msg.role === 'user' ? '👤' : '🤖' }}
          </div>
          <div class="bubble" :class="msg.role === 'user' ? 'user-bubble' : 'ai-bubble'">
            <pre class="message-text">{{ msg.content }}</pre>
          </div>
        </div>
      </template>

      <div class="message-row ai" v-if="streamingContent">
        <div class="avatar ai-avatar">🤖</div>
        <div class="bubble ai-bubble">
          <pre class="message-text">{{ streamingContent }}</pre>
          <span class="cursor">▍</span>
        </div>
      </div>

      <div class="message-row ai" v-if="isLoading && !streamingContent">
        <div class="avatar ai-avatar">🤖</div>
        <div class="bubble ai-bubble loading-bubble">
          <span class="dot"></span><span class="dot"></span><span class="dot"></span>
        </div>
      </div>
    </main>

    <footer class="chat-footer">
      <textarea
        class="chat-input"
        v-model="inputText"
        placeholder="输入你的编程问题，按 Enter 发送，Shift+Enter 换行..."
        :disabled="isLoading"
        @keydown.enter.exact.prevent="sendMessage"
        rows="1"
        ref="inputRef"
      ></textarea>
      <button class="send-btn" @click="sendMessage" :disabled="isLoading || !inputText.trim()">
        {{ isLoading ? '...' : '发送' }}
      </button>
    </footer>
  </div>

  <!-- 悬浮 AI 助手 -->
  <AiChatWidget />
</template>

<script setup>
import { ref, nextTick, onMounted } from 'vue'
import { chatStream } from './api/chat.js'
import AiChatWidget from './components/AiChatWidget.vue'

const memoryId = ref(Math.floor(Math.random() * 1000000))
const messages = ref([])
const inputText = ref('')
const isLoading = ref(false)
const streamingContent = ref('')
const chatBodyRef = ref(null)
const inputRef = ref(null)

async function scrollToBottom() {
  await nextTick()
  if (chatBodyRef.value) {
    chatBodyRef.value.scrollTop = chatBodyRef.value.scrollHeight
  }
}

// 逐字打印队列
let printQueue = []
let isPrinting = false
let doneCallback = null

function startPrinting() {
  if (isPrinting) return
  isPrinting = true
  const tick = () => {
    if (printQueue.length === 0) {
      isPrinting = false
      // 队列清空且流已结束，触发完成
      if (doneCallback) {
        const cb = doneCallback
        doneCallback = null
        cb()
      }
      return
    }
    // 每次取出一个字符显示
    streamingContent.value += printQueue.shift()
    scrollToBottom()
    setTimeout(tick, 30)
  }
  tick()
}

async function sendMessage() {
  const text = inputText.value.trim()
  if (!text || isLoading.value) return

  messages.value.push({ role: 'user', content: text })
  inputText.value = ''
  isLoading.value = true
  streamingContent.value = ''
  printQueue = []
  isPrinting = false
  doneCallback = null
  await scrollToBottom()

  chatStream(
    memoryId.value,
    text,
    (chunk) => {
      // 将 chunk 拆成单个字符推入队列
      for (const char of chunk) {
        printQueue.push(char)
      }
      startPrinting()
    },
    () => {
      // 流结束，等队列打印完再收尾
      doneCallback = () => {
        messages.value.push({ role: 'ai', content: streamingContent.value })
        streamingContent.value = ''
        isLoading.value = false
        scrollToBottom()
      }
      // 如果队列已空（打印已结束），立即执行
      if (!isPrinting) {
        const cb = doneCallback
        doneCallback = null
        cb()
      }
    }
  )
}

onMounted(() => {
  inputRef.value?.focus()
})
</script>

<style>
* { box-sizing: border-box; margin: 0; padding: 0; }

body {
  font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', sans-serif;
  background: #f0f2f5;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

#app {
  width: 100%;
  height: 100vh;
  display: flex;
  justify-content: center;
  align-items: center;
}

.chat-app {
  width: 100%;
  max-width: 800px;
  height: 100vh;
  display: flex;
  flex-direction: column;
  background: #fff;
  box-shadow: 0 0 20px rgba(0,0,0,0.1);
}

.chat-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding: 16px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  flex-shrink: 0;
}

.header-title {
  display: flex;
  align-items: center;
  gap: 8px;
  font-size: 18px;
  font-weight: 600;
}

.bot-icon { font-size: 22px; }

.session-id {
  font-size: 12px;
  opacity: 0.8;
  background: rgba(255,255,255,0.2);
  padding: 4px 10px;
  border-radius: 12px;
}

.chat-body {
  flex: 1;
  overflow-y: auto;
  padding: 20px 16px;
  display: flex;
  flex-direction: column;
  gap: 16px;
  background: #f7f8fc;
}

.chat-body::-webkit-scrollbar { width: 6px; }
.chat-body::-webkit-scrollbar-thumb { background: #ccc; border-radius: 3px; }

.message-row {
  display: flex;
  align-items: flex-start;
  gap: 10px;
}

.message-row.user { flex-direction: row-reverse; }

.avatar {
  width: 36px;
  height: 36px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 18px;
  flex-shrink: 0;
}

.ai-avatar { background: linear-gradient(135deg, #667eea, #764ba2); }
.user-avatar { background: #e8f4fd; }

.bubble {
  max-width: 70%;
  padding: 12px 16px;
  border-radius: 16px;
  font-size: 14px;
  line-height: 1.6;
  word-break: break-word;
}

.ai-bubble {
  background: #fff;
  border: 1px solid #e8e8e8;
  border-top-left-radius: 4px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.06);
}

.user-bubble {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border-top-right-radius: 4px;
}

.message-text {
  white-space: pre-wrap;
  font-family: inherit;
  font-size: 14px;
}

.cursor {
  display: inline-block;
  animation: blink 0.8s step-end infinite;
  color: #667eea;
  font-weight: bold;
}

@keyframes blink {
  0%, 100% { opacity: 1; }
  50% { opacity: 0; }
}

.loading-bubble {
  display: flex;
  align-items: center;
  gap: 5px;
  padding: 14px 18px;
}

.dot {
  width: 8px;
  height: 8px;
  background: #aaa;
  border-radius: 50%;
  animation: bounce 1.2s infinite ease-in-out;
}

.dot:nth-child(2) { animation-delay: 0.2s; }
.dot:nth-child(3) { animation-delay: 0.4s; }

@keyframes bounce {
  0%, 80%, 100% { transform: scale(0.8); opacity: 0.5; }
  40% { transform: scale(1.2); opacity: 1; }
}

.chat-footer {
  display: flex;
  align-items: flex-end;
  gap: 10px;
  padding: 14px 16px;
  background: #fff;
  border-top: 1px solid #eee;
  flex-shrink: 0;
}

.chat-input {
  flex: 1;
  resize: none;
  border: 1px solid #ddd;
  border-radius: 12px;
  padding: 10px 14px;
  font-size: 14px;
  font-family: inherit;
  line-height: 1.5;
  outline: none;
  max-height: 120px;
  overflow-y: auto;
  transition: border-color 0.2s;
}

.chat-input:focus { border-color: #667eea; }
.chat-input:disabled { background: #f5f5f5; cursor: not-allowed; }

.send-btn {
  padding: 10px 20px;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: #fff;
  border: none;
  border-radius: 12px;
  font-size: 14px;
  cursor: pointer;
  transition: opacity 0.2s;
  white-space: nowrap;
  height: 40px;
}

.send-btn:hover:not(:disabled) { opacity: 0.9; }
.send-btn:disabled { opacity: 0.5; cursor: not-allowed; }
</style>
