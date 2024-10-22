<template>
    <div class="device-management">
        <div class="device-management-header">
            <div class="device-management-title">设备管理</div>
        </div>
        <a-empty v-if="deviceData.length === 0 && !isLoading" description="暂无设备信息" style="position: relative; transform: translate(-50%, -50%); left: 50%; top: 50%" />
        <icon-loading :size="50" v-if="isLoading" style="position: relative; transform: translateX(-50%); left: 50%;"/>
        <div class="card-container">
            <a-card :title="item.name" v-for="item in deviceData" :key="item.name" class="card">
                {{ item.desc }}
            </a-card>
        </div>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { isMock, mockDeviceInfo } from '@/mock/mock'
import { getDeviceInfoAPI } from '@/api/device';

const isLoading = ref(false);
const deviceData = ref([]);

if(isMock) {
    deviceData.value = mockDeviceInfo;
    deviceData.value = []
}
else{
    onMounted(() => {
        isLoading.value = true;
        getDeviceInfoAPI().then(res => {
            deviceData.value = res.data.device;
        })
        .finally(() => {
            isLoading.value = false;
        })
    })
}
</script>

<style scoped>
.device-management-header {
    margin: 20px 0px 20px 0px;
}
.device-management-title {
    font-family: 'YouYuan', cursive;
    margin: 20px;
    font-size: 30px;
    font-weight: bold;
}
.device-management {
    width: 100%;
    height: 100%;
}
.card-container {
    width: 100%;
    display: grid;
    grid-template-columns: 1fr 1fr;
    padding-left: 20px;
    /* padding-right: 20px; */
}
.card{
    width: auto;
    margin-top: 20px;
    margin-right: 20px;
}
</style>