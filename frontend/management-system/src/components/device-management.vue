<template>
    <div class="device-management">
        <div class="device-management-header">
            <div class="device-management-title">设备管理</div>
        </div>
        <a-table :columns="columns" :data="deviceData" :span-method="dataSpanMethod" :bordered="{wrapper: true, cell: true}" :loading="isLoading" page-position="bottom">
            <template #action="{ record }">
                <a-button type="primary" @click="handle(record)">test</a-button>
            </template>
        </a-table>
    </div>
</template>

<script setup>
import { onMounted, ref } from 'vue';
import { isMock, mockDeviceInfo } from '@/mock/mock'
import { getDeviceInfoAPI } from '@/api/device';

const isLoading = ref(false);
const deviceData = ref([]);
const deviceNumberPerFridge = ref(0);
const handle = (record) => {
    console.log(mockDeviceInfo)
    console.log(deviceData.value)
    console.log(record);
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