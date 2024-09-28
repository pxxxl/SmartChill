<script setup>
import { ref, defineEmits, defineExpose } from 'vue'
import { VueCropper } from 'vue-cropper'
import 'vue-cropper/dist/index.css'
const dialogVisible = ref(false)

const option = ref({
  img: '', //裁剪图片的地址
  outputSize: 1, //裁剪生成图片的质量(可选0.1 - 1)
  outputType: 'jpeg', //裁剪生成图片的格式（jpeg || png || webp）
  info: true, //图片大小信息
  canScale: false, //图片是否允许滚轮缩放
  autoCrop: true, //是否默认生成截图框
  autoCropWidth: 300, //默认生成截图框宽度
  autoCropHeight: 300, //默认生成截图框高度
  fixed: true, //是否开启截图框宽高固定比例
  fixedNumber: [1, 1], //截图框的宽高比例
  full: false, //false按原比例裁切图片，不失真
  fixedBox: false, //固定截图框大小，不允许改变
  canMove: false, //上传图片是否可以移动
  canMoveBox: true, //截图框能否拖动
  original: false, //上传图片按照原始比例渲染
  centerBox: true, //截图框是否被限制在图片里面
  height: true, //是否按照设备的dpr 输出等比例图片
  infoTrue: false, //true为展示真实输出图片宽高，false展示看到的截图框宽高
  maxImgSize: 5000, //限制图片最大宽度和高度
  enlarge: 1 //图片根据截图框输出比例倍数
})

let previews = ref({})
const open = (imgUrl) => {
  dialogVisible.value = true
  option.value.img = imgUrl
}
// 实时预览事件
const realTime = (data) => {
  previews.value = data
}

const cropper = ref(null)
const handleClose = () => {
  option.value.img = ''
  dialogVisible.value = false
}
const submit = () => {
  dialogVisible.value = false
  console.log(previews.value)
  cropper.value.getCropBlob((data) => {
    console.log(data);
    
    emit('submit', { resizeImg: previews.value, data: data })
  })
}
const emit = defineEmits(['submit'])
defineExpose({ open })
</script>
<template>
  <a-modal v-model:visible="dialogVisible" title="裁剪" width="900" @cancel="handleClose"  :closable="false" @ok="submit">
    <div class="avatar-container">
      <a-row>
        <a-col :span="24" style="width: 800px; height: 400px">
          <vue-cropper
            ref="cropper"
            :img="option.img"
            :outputSize="option.outputSize"
            :outputType="option.outputType"
            :info="option.info"
            :canScale="option.canScale"
            :autoCrop="option.autoCrop"
            :autoCropWidth="option.autoCropWidth"
            :autoCropHeight="option.autoCropHeight"
            :fixed="option.fixed"
            :fixedNumber="option.fixedNumber"
            :full="option.full"
            :fixedBox="option.fixedBox"
            :canMove="option.canMove"
            :canMoveBox="option.canMoveBox"
            :original="option.original"
            :centerBox="option.centerBox"
            :height="option.height"
            :infoTrue="option.infoTrue"
            :maxImgSize="option.maxImgSize"
            :enlarge="option.enlarge"
            :mode="option.mode"
            @realTime="realTime"
          >
          </vue-cropper>
        </a-col>
      </a-row>
    </div>
  </a-modal>
</template>

