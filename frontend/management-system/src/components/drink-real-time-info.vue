<template>
<div class="drink-info">
    <div class="drink-info-header">
        <div class="drink-info-title">冰柜实时信息</div>
        <a-button type="primary" @click="supply" style="margin-left: 20px;">补货</a-button>
    </div>
    <a-table :columns="columns" :data="tableData" row-key="name" page-position="bottom" :pagination="{total: totalRef}">
        <template #columns>
            <a-table-column title="名称" data-index="name"></a-table-column>
            <a-table-column title="图片">
                <template #cell="{ record }">
                    <img :src="record.image" alt="饮品图片" style="width: 50px; height: 50px;" />
                </template>
            </a-table-column>
            <a-table-column title="价格" data-index="price"></a-table-column>
            <a-table-column title="温度" data-index="temperature"></a-table-column>
            <a-table-column title="数量" data-index="count"></a-table-column>
            <a-table-column title="位置" data-index="position"></a-table-column>
        </template>
    </a-table>
    <a-modal v-model:visible="visible" :title="title" @cancel="handleCancel" :on-before-ok="submit" :closable="false">
        <a-form :model="form" ref="formRef" :rules="rules">
        <a-form-item field="id" label="饮品">
            <a-select :style="{width:'320px'}" placeholder="Please select ..." allow-search @change="onChange" v-model="selVal">
                <a-option v-for="item in drinkBaseInfo" :key="item" :value="item.id" :label="item.name"></a-option>
            </a-select>
        </a-form-item>
        <a-form-item field="count" label="数量">
            <a-input-number v-model="form.count" />
        </a-form-item>
        <a-form-item field="position" label="位置">
            <a-input-number v-model="form.position" />
        </a-form-item>
        </a-form>
    </a-modal>
</div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue';
import { isMock, mockDrinkRealtimeInfo, mockDrinkBaseInfo } from '@/mock/mock'
import { getDrinkBaseInfoAPI, getDrinkRealTimeInfoAPI, supplyAPI } from '@/api/drink';
import { Message } from '@arco-design/web-vue';

const isLoading = ref(false);
const columns = ref([
{
    title: '名称',
    dataIndex: 'name',
    key: 'name',
},
{
    title: '图片',
    dataIndex: 'image',
    key: 'image',
    customRender: 'image',
},
{
    title: '价格',
    dataIndex: 'price',
    key: 'price',
    customRender: 'price',
},
{
    title: '温度',
    dataIndex: 'temperature',
    key: 'temperature',
    customRender: 'temperature',
},
{
    title: '数量',
    dataIndex: 'count',
    key: 'count',
    customRender: 'count',
},
{
    title: '位置',
    dataIndex: 'position',
    key: 'position',
    customRender: 'position',
}
]);
const page = ref(0);
const pageSize = 20;
const totalRef = ref(0);
const tableData = ref([]);
const drinkBaseInfo = ref([]);
const formRef = ref();
const visible = ref(false);
const form = reactive({
    id: '',
    count: '',
    position: ''
});

const refreshData = async () => {
    isLoading.value = true;
    try {
        tableData.value = await getDrinkRealTimeInfoAPI(page.value, pageSize);
    } catch (error) {
        console.log(error);
    } finally {
        isLoading.value = false;
    }
};

if(isMock) {
    tableData.value = mockDrinkRealtimeInfo;
    drinkBaseInfo.value = mockDrinkBaseInfo;
    totalRef.value = mockDrinkRealtimeInfo.length;
}
else{
    onMounted(async () => {
        isLoading.value = true;
        try {
            const realtimeInfoRes = await getDrinkRealTimeInfoAPI(page.value, pageSize);
            tableData.value = realtimeInfoRes.data.drinks;
            totalRef.value = realtimeInfoRes.data.total;
            const baseInfoRes = await getDrinkBaseInfoAPI();
            drinkBaseInfo.value = baseInfoRes.data.drinks;
        } catch (error) {
            console.log(error);
        } finally {
            isLoading.value = false;
        }
    })
}
const supply = () => {
    visible.value = true;
};
const handleCancel = () => {
    formRef.value.resetFields()
    formRef.value.clearValidate()
    for(let key in form) {
        form[key] = ''
    }
    visible.value = false;
};
const submit = async () => {
    isLoading.value = true;
    try {
        const validRes = await formRef.value.validate()
        if(validRes !== undefined) {
            return false
        }
        if(isMock) {
            Message.success('操作成功');
        }
        else{
            await supplyAPI(form);
            Message.success('操作成功');
            refreshData();
        }
        handleCancel();
    } catch (error) {
        console.log(error);     
        return false
    } finally {
        isLoading.value = false;
    }
};
const onChange = (value) => {
    form.id = value;
    console.log(form.id);
};
const rules = {
    id: [
    {
        required: true,
        message:'please select drink',
    },
    ],
    count: [
    {
        required: true,
        message:'count is required',
    },
    ],
    position: [
        {
        required: true,
        message:'position is required',
        },
    ],
}
</script>

<style scoped>
.drink-info-header {
    margin: 20px 0px 20px 0px;
}
.drink-info-title {
    font-family: 'YouYuan', cursive;
    margin: 20px;
    font-size: 30px;
    font-weight: bold;
}
.drink-info {
    width: 100%;
    height: 100%;
}
img {
    border-radius: 4px;
}
.arco-table-pagination::after {
    content: none;
    display: block;
}
</style>