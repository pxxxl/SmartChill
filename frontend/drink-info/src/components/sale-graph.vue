<template>
    <van-popup v-model:show="showPopup" round class="graph-container" closeable style="
    " :duration="0" @close="onClose" >
        <template #default>
            <div class="title">销量信息</div>
            <van-cell title="选择日期区间" :value="date" @click="showDateSelect = true" style="cursor: pointer;" class="cell"/>
            <van-calendar v-model:show="showDateSelect" type="range" @confirm="onConfirm" :min-date="minDate" :max-date="maxDate"/>
            <div class="empty-container" v-show="!showGraph">
              <Empty description="暂无数据" />
            </div>
            <div v-show="showGraph" ref="chartDom" class="chart"></div>
        </template> 
    </van-popup>
</template>

<script setup>

import { ref, defineExpose, nextTick } from 'vue';
import * as echarts from 'echarts';
import { getStatistics } from '@/api/Statistics';
import { isMock, mockGraphData } from '@/mock/mock';
import { showLoadingToast, closeToast, Empty, showFailToast } from 'vant';

const formatDateForSubmit = (date) => {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0'); // 确保月份是两位数
  const day = String(date.getDate()).padStart(2, '0'); // 确保日期是两位数
  return `${year}-${month}-${day}`;
};
const showPopup = ref(false);
const showGraph = ref(false);
const date = ref('');
const showDateSelect = ref(false);
let chartDom = ref();
let minDate = new Date(2020, 0, 1)
let maxDate = new Date(2030, 0, 31)
let myChart = null;
const startDate = ref(formatDateForSubmit(minDate));
const endDate = ref(formatDateForSubmit(maxDate));

const onClose = () => {
  showPopup.value = false;
  showGraph.value = false;
  showDateSelect.value = false;
  startDate.value = formatDateForSubmit(minDate);
  endDate.value = formatDateForSubmit(maxDate);
  date.value = '';
  if (myChart) {
    myChart.dispose();
    myChart = null;
  }
};

const formatDateForShow = (date) => `${date.getFullYear()}/${date.getMonth() + 1}/${date.getDate()}`;

const onConfirm = async (values) => {
    const [start, end] = values;
    showDateSelect.value = false;
    date.value = `${formatDateForShow(start)} - ${formatDateForShow(end)}`;
    startDate.value = formatDateForSubmit(start);
    endDate.value = formatDateForSubmit(end);
    if (!isMock) {
      myChart.setOption({
        series: [
          {
            data: await getGraphData()
          }
        ]
      });
    }
};
const getGraphData = async () => {
  if (isMock) {
    return mockGraphData;
  } else {
    showLoadingToast({
      message: '加载中...',
      forbidClick: true,
      duration: 0
    });
    try {
      const res = await getStatistics(startDate.value, endDate.value);
      const graphData = res.data.drinks.map(({ count, ...rest }) => {
        return {
          ...rest,
          value: count
        };
      });
      if (graphData.length === 0) {
        showGraph.value = false;
        showFailToast({
          message: '暂无数据'
        });
      } else {
        showGraph.value = true;
      }
      return graphData;
    } catch (error) {
      console.error(error);
    } finally {
      closeToast();
    }
  }
};
const open = async () => {
  console.log("showGraph");
  showPopup.value = true;
  showGraph.value = true;
  await nextTick(); // 确保DOM更新完成
  if (chartDom.value) {
    myChart = echarts.init(chartDom.value, null, { renderer: 'svg' });
    const option = {
      tooltip: {
        trigger: 'item'
      },
      legend: {
        top: '5%',
        left: 'center'
      },
      series: [
        {
          type: 'pie',
          radius: '50%',
          emphasis: {
            itemStyle: {
              shadowBlur: 10,
              shadowOffsetX: 0,
              shadowColor: 'rgba(0, 0, 0, 0.5)'
            }
          }
        }
      ]
    };
    option.series[0].data = await getGraphData();
    console.log(option.series[0].data);
    myChart.setOption(option);
  }
};

defineExpose({ open });

</script>
<style scope>
.title {
  font-size: 20px;
  text-align: center;
  padding: 10px;
}
.graph-container {
  width: 90vw;
  height: 90vh;
}
.chart{
    width: 90vw;
    height: 80%;
    margin-top: 10px;
    overflow: hidden; 
    max-width: 900px;
}
.cell{
    display: relative;
    left: 50%;
    transform: translateX(-50%);
    border-radius: 10px;
    background-color: #4d648d !important;
    color: aliceblue !important;
    width: calc(100% - 20px) !important;
}
@media screen and (max-width: 376px) {
    .van-cell__title, .van-cell__value {
        flex: none !important;
        margin-right: 55px !important;
    }   
}
@media screen and (max-width: 320px) {
    .van-cell__title, .van-cell__value {
        flex: none !important;
        margin-right: 10px !important;
    }   
}
.empty-container {
  display: flex;
  justify-content: center;
  flex-direction: row;
  align-content: center;
  width: 90vw;
  height: 80%;
  max-width: 900px;
}
</style>