<script setup>
    import { storeToRefs } from 'pinia'
    import { useUserStore } from '@/stores/user-store'

    const useStore = useUserStore()
    const { menuList } = storeToRefs(useStore)
</script>

<template>
    <a-menu mode="horizontal" class="menu-items">
        <a-menu-item v-for="menu in menuList" :key="menu.name">
            <RouterLink :to="{ name: menu.routeName }">{{ menu.name }}</RouterLink>
        </a-menu-item>
    </a-menu>
</template>

<style scoped>
.menu-items {
    float: right;
}
</style>