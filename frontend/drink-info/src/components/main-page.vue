<template>
  <div id="app">
    <div class="header">
      <div>查看销量</div>
      <h1>智能冰柜-饮品信息</h1>
      <span>{{ currentTemperature ? currentTemperature + '℃' : '' }}</span>
    </div>
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
    <div ref="bottom" class="bottom"></div>
  </div>
</template>

<script setup>
import { ref, onMounted, onUnmounted } from 'vue';
import { isMock, mockDrinkInfo } from '@/mock/mock';
import { getTemperature, getDrinkInfo } from '@/api/User';
import { showLoadingToast, closeToast,Empty } from 'vant';

const currentTemperature = ref();
const drinks = ref([]);
const page = ref(1);
const pageSize = 4;
const isLoading = ref(false);

const loadMoreData = async () => {
  if (isLoading.value) return;
  isLoading.value = true;

  if (isMock) {
    drinks.value = [...drinks.value, ...mockDrinkInfo];
  } else {
    showLoadingToast({
      message: '加载中...',
      forbidClick: true,
    });
    try{
      const [temperature, drinkInfo] = await Promise.all([getTemperature(), getDrinkInfo(page.value, pageSize)]);
      currentTemperature.value = temperature.inner;
      drinks.value = drinkInfo.data.drinks;
      page.value += 1;
    } catch (error) {
      console.error(error);
    } finally {
      closeToast();
    }
  }
  isLoading.value = false;
};

if (isMock) {
  currentTemperature.value = 10;
  drinks.value = mockDrinkInfo;
} else {
  // onMounted(async () => {
  //   showLoadingToast({
  //     message: '加载中...',
  //     forbidClick: true,
  //   });
  //   try{
  //     const [temperature, drinkInfo] = await Promise.all([getTemperature(), getDrinkInfo(page.value, pageSize)]);
  //     currentTemperature.value = temperature.inner;
  //     drinks.value = drinkInfo.data.drinks;
  //     page.value += 1;
  //   } catch (error) {
  //     console.error(error);
  //   } finally {
  //     closeToast();
  //   }
  // });
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

onMounted(() => {
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
.bottom {
  height: 1px;
}
.empty-container {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-content: center;
  height: 100%;
  width: 100%;
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
.header div{
  font-size: 12px;
  width: 24px;
  background-color: #59a5f5;
  padding: 4px 4px;
  border-radius: 5px;
  margin-left: 10px;
  cursor: pointer;
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
  padding: 10px;
}



.drink img {
  max-width: 100%;
  height: auto;
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
}
</style>