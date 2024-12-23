<template>
    <div class="screen-container">
        <a-select :style="{width:'320px'}" placeholder="Please select ..." allow-search @change="onChange" v-model="screenForm.fridgeId" @onChange="onChange">
            <a-option v-for="item in fridgeInfo" :key="item" :value="item.id" :label="item.name"></a-option>
        </a-select>
        <a-button type="primary" @click="onSubmit" style="margin-left: 10px;">查询</a-button>
    </div>
    <a-divider/>
</template>
<script setup>
import { ref, reactive, defineProps, defineEmits, onUpdated } from 'vue';
const fridgeInfo = ref([]);
const screenForm = reactive({
    fridgeId: ''
});
const processedList = (data) =>{
    return data.map(fridge => ({
        name: fridge.name,
        id: fridge.id
    }));
}
const props = defineProps({
    fridgeInfo: {
        type: Array,
        required: true,
    }
});
const onChange = () => {
    console.log(screenForm.fridgeId);
}
onUpdated(() => {
    console.log(props.fridgeInfo)
    fridgeInfo.value = processedList(props.fridgeInfo);
    console.log(fridgeInfo.value);
});
const emit = defineEmits(['updateData']);
const onSubmit = () => {
    emit('updateData', screenForm.fridgeId);
}
</script>
<style scoped>

</style>
<style>
.screen-container{
    margin-left: 20px;
    display: flex;
    justify-content:flex-start !important;
    margin-right: 20px;
}
.screenForm .arco-form-item{
    margin-bottom: 0px;
}
</style>