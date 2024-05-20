<script setup>
import { GithubOutlined } from '@ant-design/icons-vue'
</script>

<template>
  <a-layout-footer>
    <div>
      Developed by
      <a href="https://github.com/allkong">Dabin Jeong <GithubOutlined /></a>
      &
      <a href="https://github.com/pcjs156">Yubin Park <GithubOutlined /></a>
    </div>
    <div>Copyright ©SSAFY All Rights Reserved.</div>
  </a-layout-footer>
</template>

<style scoped lang="scss">
.ant-layout-footer {
  height: $footerHeight;
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: rgb(105, 105, 105);
}

.ant-layout-footer a {
  color: rgb(105, 105, 105);
}
</style>
