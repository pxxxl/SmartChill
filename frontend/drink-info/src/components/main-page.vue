<template>
  <div id="app">
    <div class="header">
      <button class="sale" @click="open">查看销量</button>
      <h1>智能冰柜-饮品信息</h1>
      <span class="temperature">{{ currentTemperature ? currentTemperature + '℃' : '' }}</span>
    </div>
    <Empty description="暂无数据" style="position: absolute; top: 50%; left: 50%; transform: translate(-50%, -50%); z-index: 999;" v-if="drinks.length === 0"/>
    <van-pull-refresh
      v-model="isRefreshing"
      success-text="刷新成功"
      @refresh="onRefresh"
      :disabled="isLoading"
    >
    

      <div class="drink-container">
        <div class="drink" v-for="drink in drinks" :key="drink.name">
          <img :src="drink.image" :alt="drink.name" />
          <div class="drink-info">
              <div>{{ drink.name}}  </div>
              <span :class="{'exist': drink.count > 0, 'not-exist': drink.count === 0}">数量：{{ drink.count + '  '}} </span>
              <span class="price"> {{ '￥' + drink.price + '  ' }}</span>
              <span :class="{'hot': drink.temperature >= 10, 'cold': drink.temperature < 10}"> {{ drink.temperature + '℃' }}</span>
        </div>
        </div>
      </div>
    </van-pull-refresh>
    
  </div>
  <saleGraph ref="saleGraphRef"/>
  <div class="loadingStatus" v-show="isLoading" v-if="enableLoadMore">
    <van-loading type="spinner" size="20px"/>
    <span style="font-size: 16px;line-height: 23px;margin-left: 5px;color: darkgray;">加载中...</span>
  </div>
  <div class="screen-button" v-if="false">
    <van-icon :size="30" color="#f0f0f0" name="shop-o" @click="()=>{showPicker = true}"/>
  </div>
  <van-popup v-model:show="showPicker" round position="bottom">
    <van-picker
      :columns="fridgeList"
      @cancel="showPicker = false"
      @confirm="onConfirm"
    />
  </van-popup>
  <div ref="bottom" class="bottom"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { isMock, mockDrinkInfo, mockDeviceInfo } from '@/mock/mock';
import { getTemperature, getDrinkInfo } from '@/api/User';
import { showLoadingToast, closeToast,Empty } from 'vant';
import { getDeviceInfoAPI } from '@/api/device';
import saleGraph from '@/components/sale-graph.vue';

const currentTemperature = ref();
const drinks = ref([]);
const page = ref(1);
const pageSize = 20;
const isLoading = ref(false);
const saleGraphRef = ref();
const isRefreshing = ref(false);
const enableLoadMore = ref(false)
const showPicker = ref(false);
const fridgeList = ref([]);

const onConfirm = (value) => {
  console.log(value);
  onRefresh(value.selectedOptions[0].value);
  showPicker.value = false;
};
const open = () => {
  saleGraphRef.value.open();
};

const onRefresh = async (fridgeId) => {
  if (isMock) {
    setTimeout(() => {
      drinks.value = mockDrinkInfo;
      isRefreshing.value = false;
      console.log('refresh');
    }, 1000);
  } else {
    try{
      const [temperature, drinkInfo] = await Promise.all([getTemperature(), getDrinkInfo(1, pageSize, false, fridgeId)]);
      currentTemperature.value = temperature.data.inner;
      drinks.value = drinkInfo.data.drinks;
      page.value = 2;
    } catch (error) {
      console.error(error);
    } finally {
      isRefreshing.value = false;
    }
  }
};

const loadMoreData = async () => {
  console.log('load more data');
  if (isLoading.value || isRefreshing.value) {
    console.log('loading block');
    return
  }
  isLoading.value = true;
  if (isMock) {
    setTimeout(() => {
      drinks.value = [...drinks.value, ...mockDrinkInfo];
      isLoading.value = false;
      checkContentHeight();
    }, 5000);
  } else {
    try{
      const [temperature, drinkInfo] = await Promise.all([getTemperature(), getDrinkInfo(page.value, pageSize)]);
      currentTemperature.value = temperature.data.inner;
      drinks.value = [...drinks.value, ...drinkInfo.data.drinks];
      page.value += 1;
      if (drinkInfo.data.drinks.length < pageSize) {
        enableLoadMore.value = false;
        observer.unobserve(bottom.value);
      }
      checkContentHeight();
    } catch (error) {
      console.error(error);
    } finally {
      
      isLoading.value = false;
    }
  }
  
};
const initData = async () => {
  if (isMock) {
    isLoading.value = true;
    showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    });
    setTimeout(() => {
      drinks.value = mockDrinkInfo;
      currentTemperature.value = 10;
      checkContentHeight();
      fridgeList.value = processedList(mockDeviceInfo);
      closeToast();
      isLoading.value = false;
      enableLoadMore.value = true;
    }, 1000);
  } else {
      isLoading.value = true;
      showLoadingToast({
        message: '加载中...',
        forbidClick: true,
        duration: 0
      });
      try{
        const [temperature, drinkInfo] = await Promise.all([getTemperature(), getDrinkInfo(page.value, pageSize)]);
        currentTemperature.value = temperature.data.inner;
        drinks.value = drinkInfo.data.drinks;
        page.value += 1;
        checkContentHeight();
        const deviceInfoRes = await getDeviceInfoAPI();
        fridgeList.value = processedList(deviceInfoRes.data);
      } catch (error) {
        console.error(error);
      } finally {
        isLoading.value = false;
        enableLoadMore.value = true;
        closeToast();
      }
  }
}
const checkContentHeight = async () => {
  await nextTick(); // 确保DOM更新完成
  const contentHeight = document.querySelector('#app').offsetHeight;
  const windowHeight = window.innerHeight;
  if (contentHeight < windowHeight) {
    await loadMoreData();
  }
};
const processedList = (data) =>{
    return data.map(fridge => ({
        text: fridge.name,
        value: fridge.id
    }));
}
const bottom = ref(null);

const observer = new IntersectionObserver(async (entries) => {
  if (entries[0].isIntersecting) {
    await loadMoreData();
  }
}, {
  root: null,
  threshold: 1.0
});

onMounted(async () => {
  initData();
  if (bottom.value) {
    observer.observe(bottom.value);
  }
  
});

onUnmounted(() => {
  if (bottom.value) {
    observer.unobserve(bottom.value);
  }
});
</script>

<style scoped>
.screen-button {
  position: fixed;
  right: 10px;
  bottom: 10px;
  z-index: 100;
  text-align: center;
  border-radius: 50%;
  background-color: #252841;
  height: 50px;
  width: 50px;
  line-height: 60px;
}

.screen-button:active .van-icon-shop-o:before {
  color: #007BBB;
}


.temperature{
  display: block;
  position: relative;
  right: 0;
  bottom: 0;
  z-index: 100;
  right: 3px;
  bottom: -22px;
}
.bottom {
  margin-top: -10px;
  height: 1px;
  width: 1px;
  background-color: transparent; /* 确保 bottom 元素不会被遮挡 */
}
.loadingStatus {
  padding: 10px;
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-content: center;
  width: 95vw;
  height: 25px;
}
.header{
  text-align: center;
  background-color: #252841;
  color: #fff;
  padding: 10px 0;
  display: flex;
  justify-content: space-between;
}
.header h1{
  position: absolute;
  left: 50%;
  white-space: nowrap;
  width: auto;
  transform: translateX(-50%);
  text-align: center;
  font-family: 'YouYuan', cursive;
  font-weight: normal;
  width: fit-content;
}
.header .sale{
  display: block;
  font-size: 12px;
  width: 32px;
  background-color: #59a5f5;
  padding: 4px 4px;
  border-radius: 5px;
  border: none;
  margin-left: 10px;
  cursor: pointer;
}

.header .sale:active{
  background-color: #007BBB;
}


.drink-container {
  display: grid;
  grid-template-columns: 1fr 1fr 1fr;
  column-gap: 10px;
  margin: auto 10px;
  text-align: center;
}
.drink {
  background-color: #fff;
  border-radius: 15px;
  margin-top: 5px;
  padding: 8px;
}



.drink img {
  width: 100%;
  height: 200px;
}

.drink h2 {
  font-weight: normal;
  float: left;
}


.drink-info-left div {
  text-align: left;
}
.drink-info-right div {
  text-align: right;
}

.exist, .not-exist, .price, .hot, .cold {
  color: #f0f0f0;
  border-radius: 10px;
  margin: 0 5px;
  padding: 0 5px;
}


.exist {
  background: linear-gradient(90deg,#019B98 0%,#55CCC9 100%); 

}
.not-exist {
  background: linear-gradient(90deg,#9B111E 0%,#D24A46 100%); 
}

.price {
  background: linear-gradient(90deg,#FF4081 0%,#FF79B0 100%); 
}

.hot {
  background: linear-gradient(90deg,#8F1E00 0%,#FF7F50 100%); 
}
.cold {
  background: linear-gradient(90deg,#007BBB 0%,#00BFFF 100%); 
}
.drink-info span {
  font-size: 12px;
}
@media screen and (max-width: 425px) {
  .drink-info span {
    font-size: 10px;
  }
  .drink img {
  height: 150px;
  }
  .header .sale{
    font-size: 10px;
  }
}
@media screen and (max-width: 320px) {
  .header h1{
    font-size: 25px;
    line-height: 39px;
  }
  .exist, .not-exist, .price, .hot, .cold {
    margin: 0 2px;
  }
  .drink-info span {
    font-size: 9px;
  } 
  .drink img {
  height: 100px;
  }
}

@media screen and (max-width: 576px) {
  .drink-container {
    grid-template-columns: 1fr 1fr ;
  }
}
@media screen and (min-width: 576px) {
  .drink-container {
    grid-template-columns: 1fr 1fr 1fr;
  }
}
 @media screen and (min-width: 769px) {
  .drink-container {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }
  .drink-info span {
    font-size: 14px;
  }
}
 @media screen and (min-width: 992px) {
  .drink-container {
    grid-template-columns: 1fr 1fr 1fr 1fr;
  }
  .drink-info span {
    font-size: 15px;
  }
  .drink-info div{
    font-size: 20px;
  }
}

 @media screen and (min-width: 1200px) {
  .drink-container {
    grid-template-columns: 1fr 1fr 1fr 1fr;
    margin: auto 100px;
  }
  .header span{
    margin-right: 100px;
  }
  .header div{
    margin-left: 100px;
  }
  .sale{
    margin-left: 100px !important;
  }
  .temperature{
    margin-left: 100px;
  } 
}
 @media screen and (min-width: 1400px) {
  .drink-container {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr;
    margin: auto 200px;
  }
  .header span{
    margin-right: 200px;
  }
  .header div{
    margin-left: 200px;
  }
  .sale{
    margin-left: 200px !important;
  }
  .temperature{
    margin-left: 200px;
  }
  .drink-info span {
    font-size: 13px;
  }
}
@media screen and (min-width: 1600px) {
  .drink-container {
    grid-template-columns: 1fr 1fr 1fr 1fr 1fr 1fr;
    margin: auto 300px;
  }
  .header span{
    margin-right: 300px;
  }
  .header div{
    margin-left: 300px;
  }
  .sale{
    margin-left: 300px !important;
  }
  .temperature{
    margin-left: 300px;
  }
  .drink-info span {
    font-size: 16px;
  }
  
}
</style>