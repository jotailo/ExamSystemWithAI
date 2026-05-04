<template>
  <div id="app">
    <router-view></router-view>
    <ai-chat-widget></ai-chat-widget>
  </div>
</template>

<script>
import AiChatWidget from './components/AiChatWidget.vue'

export default {
  name: 'app',
  components: { AiChatWidget }
}
</script>

<style>
  @import './assets/css/global.css';
  /*脚部的样式*/
  .el-footer {
    text-align: center;
    height: 50px !important;
    background-color: white;
    opacity: 0.6;
  }
</style>
