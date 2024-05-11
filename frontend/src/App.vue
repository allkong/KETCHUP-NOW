<script setup>
import { RouterView } from 'vue-router'
</script>

<!-- 
  [ Antdv Layout ]
  https://antdv.com/components/layout
 -->
<template>
  <a-layout>
    <RouterView />
  </a-layout>
</template>

<style>
body {
  margin: 0;
}

@font-face {
  font-family: 'NanumSquareRound';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/noonfonts_two@1.0/NanumSquareRound.woff')
    format('woff');
  font-weight: normal;
  font-style: normal;
}

.ant-layout :not(#logo-text-ketchup, #logo-text-now) {
  font-family: 'NanumSquareRound';
}

.ant-menu-horizontal,
.ant-menu-item,
.ant-menu-item::after {
  border: none !important;
  color: inherit !important;
}
</style>
