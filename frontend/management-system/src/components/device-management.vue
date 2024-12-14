<template>
    <div class="device-management">
        <div class="device-management-header">
            <div class="device-management-title">设备管理</div>
        </div>
        <a-table :columns="columns" :data="deviceData" :span-method="dataSpanMethod" :bordered="{wrapper: true, cell: true}" :loading="isLoading" page-position="bottom">
            <template #action="{ record }">
                <a-button v-if="record.name.toLowerCase().startsWith('camera')" type="primary" @click="showImage(record)">查看</a-button>
                <a-image-preview v-model:visible="imagePreviewVisible" :src="cameraData" @close="onClose"/>
            </template>
        </a-table>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { isMock, mockDeviceInfo, mockDrinkBaseInfo } from '@/mock/mock'
import { getDeviceInfoAPI } from '@/api/device';
import { getCameraAPI } from '@/api/user';

const isLoading = ref(false);
const deviceData = ref([]);
const deviceNumberPerFridge = ref(0);
const cameraData = ref([]);
const imagePreviewVisible = ref(false);

const showImage = (record) => {
    console.log(mockDeviceInfo)
    console.log(deviceData.value)
    console.log(record);
    if(isMock) {
        cameraData.value = mockDrinkBaseInfo[0].image;
        imagePreviewVisible.value = true;
        return
    }
    isLoading.value = true;
    getCameraAPI(record.id).then(res => {
        const base64Data = res.data;
        const byteCharacters = atob(base64Data);
        const byteNumbers = new Array(byteCharacters.length);
        for (let i = 0; i < byteCharacters.length; i++) {
          byteNumbers[i] = byteCharacters.charCodeAt(i);
        }
        const byteArray = new Uint8Array(byteNumbers);
        const blob = new Blob([byteArray], { type: 'image/jpeg' });
        cameraData.value = URL.createObjectURL(blob);
        imagePreviewVisible.value = true;
    }).finally(() => {
        isLoading.value = false;
    })
}
const onClose = () => {
    cameraData.value = '';
    imagePreviewVisible.value = false;
}
const columns=[
    {
        title: '冰柜名称',
        dataIndex: 'fridgeName',
    },
    {
        title: '设备id',
        dataIndex: 'id',
    },
    {
        title: '设备名称',
        dataIndex: 'name',
    },
    {
        title: '设备状态',
        dataIndex: 'status',
    },
    {
        title: '层数',
        dataIndex: 'position',
    },
    {
        title: '操作',
        slotName: 'action',
    },
];

const processedList = (data) => {
    return data.flatMap(fridge => 
        fridge.items.map(item => ({
                fridgeName: fridge.name,
                name: item.name,
                id: item.id,
                position: item.position,
                status: item.status
            })
        )
    );
}
const dataSpanMethod = ({ record, column, rowIndex,  }) => {
  if (column.dataIndex === 'fridgeName') {
    const prevRecord = deviceData.value[rowIndex - 1];
    if (prevRecord && prevRecord.fridgeName === record.fridgeName) {
      return { rowspan: 0, colspan: 0 };
    }
    let rowspan = 1;
    for (let i = rowIndex + 1; i < deviceData.value.length; i++) {
      if (deviceData.value[i].fridgeName === record.fridgeName) {
        rowspan++;
      } else {
        break;
      }
    }
    return { rowspan, colspan: 1 };
  }
  return { rowspan: 1, colspan: 1 };
};
const initData = () => {
    if(isMock) {
        deviceData.value = processedList(mockDeviceInfo.slice());
        deviceNumberPerFridge.value = mockDeviceInfo[0].items.length
    }
    else{
        isLoading.value = true;
        getDeviceInfoAPI().then(res => {
            deviceNumberPerFridge.value = res.data[0].items.length;
            deviceData.value = processedList(res.data);
        })
        .catch(err => {
            console.log(err);
        })
        .finally(() => {
            isLoading.value = false;
        })
    }
}
onMounted(() => {
    initData();
})

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
</style>