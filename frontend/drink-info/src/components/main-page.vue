<template>
  <div id="app">
    <div class="header">
      <button class="sale" @click="open">查看销量</button>
      <h1>智能冰柜-饮品信息</h1>
      <span>{{ currentTemperature ? currentTemperature + '℃' : '' }}</span>
    </div>
    <van-pull-refresh
      v-model="isRefreshing"
      success-text="刷新成功"
      @refresh="onRefresh"
      :disabled="isLoading"
    >
    
    <div class="empty-container" v-if="drinks.length == 0">
        <Empty description="暂无数据" />
    </div>
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
  <div ref="bottom" class="bottom"></div>
</template>

<script setup>
import { ref, onMounted, onUnmounted, nextTick } from 'vue';
import { isMock, mockDrinkInfo } from '@/mock/mock';
import { getTemperature, getDrinkInfo } from '@/api/User';
import { showLoadingToast, closeToast,Empty } from 'vant';
import saleGraph from '@/components/sale-graph.vue';

const currentTemperature = ref();
const drinks = ref([]);
const page = ref(1);
const pageSize = 20;
const isLoading = ref(false);
const saleGraphRef = ref();
const isRefreshing = ref(false);
const enableLoadMore = ref(false)

const open = () => {
  saleGraphRef.value.open();
};

const onRefresh = async () => {
  if (isMock) {
    setTimeout(() => {
      drinks.value = mockDrinkInfo;
      isRefreshing.value = false;
      console.log('refresh');
    }, 1000);
  } else {
    try{
      const [temperature, drinkInfo] = await Promise.all([getTemperature(), getDrinkInfo(1, pageSize)]);
      currentTemperature.value = temperature.inner;
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
      currentTemperature.value = temperature.inner;
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
  await loadMoreData();
  checkContentHeight();
});

onUnmounted(() => {
  if (bottom.value) {
    observer.unobserve(bottom.value);
  }
});
</script>

<style scoped>
.bottom {
  margin-top: -10px;
  height: 1px;
  background-color: transparent; /* 确保 bottom 元素不会被遮挡 */
}
.empty-container {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-content: center;
  height: 100%;
  width: 100%;
}
.loadingStatus {
  padding: 10px;
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-content: center;
  width: 100%;
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

.header span{
  font-size: 12px;
  border-radius: 50%;
  display: block;
  transform: translateY(30px);
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
  .drink-info span {
    font-size: 16px;
  }
  
}
</style>