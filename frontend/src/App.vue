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

.ant-menu-horizontal,
.ant-menu-item,
.ant-menu-item::after {
  border: none !important;
  color: inherit !important;
}
</style>
