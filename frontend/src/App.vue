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

@font-face {
  font-family: 'KCC-Hanbit';
  src: url('https://fastly.jsdelivr.net/gh/projectnoonnu/2403-2@1.0/KCC-Hanbit.woff2')
    format('woff2');
  font-weight: normal;
  font-style: normal;
}

.ant-layout :not(#logo-text-ketchup, #logo-text-now),
.ant-modal,
.ant-col,
.ant-tabs,
.ant-card,
.ant-btn {
  font-family: 'NanumSquareRound';
}

.ant-menu-horizontal,
.ant-menu-item,
.ant-menu-item::after {
  border: none !important;
  color: inherit !important;
}
</style>
