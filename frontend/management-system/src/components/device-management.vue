<template>
    <div class="device-management">
        <div class="device-management-header">
            <div class="device-management-title">设备管理</div>
        </div>
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

const deviceData = ref([]);

if(isMock) {
    deviceData.value = mockDeviceInfo;
}
else{
    onMounted(() => {
        getDeviceInfoAPI().then(res => {
            deviceData.value = res.data.device;
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