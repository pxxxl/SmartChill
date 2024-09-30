<template>
<div class="drink-info">
    <div class="drink-info-header">
        <div class="drink-info-title">饮品基本信息</div>
        <a-button type="primary" @click='handleDrink(false)' style="margin-left: 20px;">添加饮品</a-button>
    </div>
    <a-table :columns="columns" :data="tableData" row-key="name" page-position="bottom">
        <template #columns>
            <a-table-column title="名称" data-index="name"></a-table-column>
            <a-table-column title="图片">
                <template #cell="{ record }">
                    <img :src="record.image" alt="饮品图片" style="width: 50px; height: 50px;" />
                </template>
            </a-table-column>
            <a-table-column title="价格" data-index="price"></a-table-column>
            <a-table-column title="操作" width="200">
                <template #cell="{ record }" >
                    <a-button type="primary" @click='handleDrink(record)' style="margin-right: 10px;">编辑</a-button>
                    <a-button type="danger" @click="handleDeleteDrink(record) ">删除</a-button>
                </template>
            </a-table-column>
        </template>
    </a-table>
    <a-modal v-model:visible="visible" :title="title" @cancel="handleCancel" :on-before-ok="submit" :closable="false">
        <a-form :model="form" ref="formRef" :rules="rules">
        <a-form-item field="name" label="名称">
            <a-input v-model="form.name" />
        </a-form-item>
        <a-form-item field="price" label="价格">
            <a-input-number v-model="form.price" />
        </a-form-item>
        <a-form-item field="image" label="图片">
            <a-upload
            class="avatar-uploader"
            action="#"
            :show-file-list="false"
            :on-before-upload="beforeUpload"
            >
                <a-button type="outline" style="display: block">选择图片</a-button>
            </a-upload>
            <imgCropper
            ref="imgCropperRef"
            @submit="
                ({ resizeImg, data }) => {
                form.image = data
                resizeImgRef = resizeImg
                console.log(resizeImgRef)
                }
            "
            prop="image"
            ></imgCropper>
        </a-form-item>
        <div v-if="resizeImgRef.url" :style="resizeImgRef?.div || ''" class="preview">
                <img :src="resizeImgRef.url" :style="resizeImgRef?.img || ''" />
            </div>
        </a-form>
    </a-modal>
</div>
</template>

<script setup>
import { ref, reactive } from 'vue';
import { isMock, mockDrinkBaseInfo } from '@/mock/mock'
import { Message } from '@arco-design/web-vue';
import imgCropper from '@/components/img-cropper.vue';
import { getDrinkBaseInfoAPI, setDrinkBaseInfoAPI } from '@/api/drink';
import { uploadAPI } from '@/api/common';

const tableData = ref([]);

if(isMock) {
    tableData.value = mockDrinkBaseInfo;
}
else{
    getDrinkBaseInfoAPI().then(res => {
        tableData.value = res.data.drinks;
    })
}
const rules = {
      name: [
        {
          required: true,
          message:'name is required',
        },
      ],
      price: [
        {
          required: true,
          message:'price is required',
        },
      ],
        image: [
            {
            required: true,
            message:'image is required',
            },
        ],
}
const title = ref('添加饮品');
const imgCropperRef = ref();
const resizeImgRef = ref({
  img: '',
  url: '',
  div: ''
})
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
}
]);
const formRef = ref();


const visible = ref(false);
const form = reactive({
    name: '',
    price: '',
    image: ''
});

const handleDrink = (record) => {
    if(record) {
        title.value = '编辑饮品';
        form.name = record.name;
        form.price = Number(record.price);
        form.image = record.image;
        resizeImgRef.value.url = record.image
        resizeImgRef.value.div = {
            width: '100px',
            height: '100px'
        }
        resizeImgRef.value.img = {
            width: '100px',
            height: '100px'
        }
    } else {
        title.value = '添加饮品';
        form.name = '';
        form.price = '';
        form.image = '';
    }
    visible.value = true;
};
const handleCancel = () => {
    formRef.value.resetFields()
    formRef.value.clearValidate()
    resizeImgRef.value.url = ''
    for(let key in form) {
        form[key] = ''
    }
    visible.value = false;
    console.log('cancel');
    
};

const beforeUpload = (rawFile) => {
  console.log(rawFile)
  if (rawFile.type.indexOf('image/') == -1) {
    Message.error('请上传图片类型文件!')
    return false
  }
  if (rawFile.size / 1024 / 1024 > 2) {
    Message.error('文件大小不能超过2MB!')
    return false
  }
  const reader = new FileReader()
  reader.readAsDataURL(rawFile)
  reader.onload = () => {
    if (typeof reader.result === 'string') {
        imgCropperRef.value.open(reader.result)
    }
  }
}
const submit = async () => {
    try {
        const validRes = await formRef.value.validate()
        if(validRes !== undefined) {
            return false
        }
        const formData = new FormData();
        formData.append('file' , form.image);
        if(!isMock) {
            const uploadRes = await uploadAPI(formData);
            form.image = uploadRes.data.url;
            await setDrinkBaseInfoAPI(form);
            Message.success('操作成功');
            refreshData();
        }
        else {
            Message.success('操作成功');
        }
        handleCancel();
    } catch (error) {
        console.log(error);     
        return false
    }
};
const refreshData = () => {
    getDrinkBaseInfoAPI().then(res => {
        tableData.value = res.data.drinks;
    })
};
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
.preview {
  overflow: hidden;
  border: 1px solid var(--el-color-primary);
  background: #cccccc;
  margin-bottom: 10px;
  margin-left: 100px;
  display: block;
  height: fit-content;
  width: fit-content;
}
img {
  display: block;
}
</style>