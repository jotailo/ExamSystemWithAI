<template>
  <el-scrollbar :native="false" style="height: 100%">
    <div>
      <h1 class="title">
        <a href="https://gitee.com/wzhouzhou/exam_system" target="_blank">Dieog考试系统</a>
      </h1>
      <img src="@/assets/imgs/dieog.jpg" alt="dieog">
    </div>

  </el-scrollbar>
</template>

<script>
  export default {
    name: 'Dashboard',
    created () {
      // 调用父组件Main的展示系统公告方法
      this.$emit('showSystemNotice')
    },
  }
</script>

<style scoped lang="scss">
  div {
    animation: leftMoveIn .7s ease-in;
  }

  @keyframes leftMoveIn {
    0% {
      transform: translateX(-100%);
      opacity: 0;
    }
    100% {
      transform: translateX(0%);
      opacity: 1;
    }
  }

  .title {
    text-align: center;
    font-size: 25px;
  }


  p {
    text-indent: 2em;
  }

  a {
    color: green;
    text-decoration: none
  }

  img{
    height: 500px;
    width: 500px;
    margin-left: 400px;
  }
</style>
