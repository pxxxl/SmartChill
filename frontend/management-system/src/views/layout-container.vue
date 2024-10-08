<template>
<div class="layout-container">
    <div class="menu">
        <img src="../assets/logo-2.jpg" alt="logo" class="logo"/>
        <a-menu :default-selected-keys="['/device']" @menu-item-click="handleClick">
            <a-menu-item key="/device">
                <template #icon><icon-printer /></template>
                设备管理
            </a-menu-item>
            <a-sub-menu key="1">
                <template #icon><icon-common /></template>
                <template #title>饮品管理</template>
                <a-menu-item key="/drink/base">饮品基本信息</a-menu-item>
                <a-menu-item key="/drink/realtime">冰柜实时信息</a-menu-item>
            </a-sub-menu>
        </a-menu>
    </div>
    <a-tooltip content="退出登录" position="left">
        <div class="logout" @click="logout">
            <icon-poweroff />
        </div>
    </a-tooltip>
    <router-view />
</div>
</template>
<script setup>
import { useRouter } from 'vue-router';
import { useUserStore } from '@/stores';
import { onMounted } from 'vue';

const router = useRouter();
const userStore = useUserStore();

const logout = () => {
    userStore.removeToken();
    router.push('/login');
};

const handleClick = ( key ) => {
    router.push(`${key}`);
};
onMounted(
    () => {
        router.push('/device');
    }
);

</script>
<style scoped>
.layout-container {
    display: flex;
    min-height: 100vh;
    height: auto; 
}
.logo {
    width: 160px;
    height: 130px;
    margin: 20px;
}
.logout {
    background-color: #FF3D3D !important; 
    position: absolute;
    right: 0;
    top: 0;
    z-index: 1000;
    border-radius: 50%;
    width: 40px;
    height: 40px;
    text-align: center;
    line-height: 40px;
    font-size: 20px !important;
    color: #fff !important;
    padding: 0 !important;
    margin: 20px;
    /* 加阴影 */
    box-shadow: 0 0 10px 0 rgba(0, 0, 0, 0.5);
    cursor: pointer;
}
.menu {
box-sizing: border-box;
width: 220px;
border-right: 1px solid #000;
}
</style>
  

