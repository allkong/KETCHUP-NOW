<script setup>
import HeaderView from '@/views/mobile/includes/HeaderView.vue'
import NavigationView from '@/views/mobile/includes/NavigationView.vue'
import { LayoutContent, Divider, Card } from 'ant-design-vue'
</script>

<template>
  <LayoutContent id="content-container">
    <h1>
      여행자
      <span id="heading-nickname">팔척장신</span>님!
    </h1>
    <Card> asd </Card>
    <Divider />
  </LayoutContent>
  <NavigationView />
</template>

<style scoped lang="scss">
#content-container {
  text-align: center;
  background: $whiteBackgroundColor;
}
h1 {
  text-align: left;
  padding: 1rem;
  margin: 0.5rem 0.5rem 0 0.5rem;
}
#heading-nickname {
  color: tomato;
}
.ant-divider {
  margin-top: 0;
  margin-bottom: 0;
}
.ant-card {
  margin: 1rem;
}
</style>
