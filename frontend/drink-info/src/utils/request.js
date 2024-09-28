import axios from 'axios';
import { showFailToast } from 'vant';

const request = axios.create({
    baseURL: 'https://localhost:8080/',
    timeout: 1000,
});
request.interceptors.request.use(
    (config) => {
      return config
    },
    (err) => {
        showFailToast('请求失败')
        console.log(err)
    }
  )
  
request.interceptors.response.use(
    (res) => {
        if (res.data.code === 1 || res.data.code === 2) {
            console.log(res.data)
            showFailToast('请求失败')
        } else {
            return res.data
        }
    },
    (err) => {
        if (err.code === 'ECONNABORTED') {
            showFailToast('请求超时')
        } else {
            showFailToast('请求失败')
        }
        console.log(err)
    }
)


export default request;