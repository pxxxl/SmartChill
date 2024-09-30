import axios from 'axios'
import { useUserStore } from '@/stores/index'
import { Message } from '@arco-design/web-vue'
import router from '@/router/router'

export const baseURL = 'http://localhost:18080'

const request = axios.create({
  baseURL: baseURL,
  timeout: 10000
})

request.interceptors.request.use(
  (config) => {
    const useStore = useUserStore()
    if (useStore.token) {
      config.headers.Authorization = useStore.token
    }
    return config
  },
  (err) => Promise.reject(err)
)

request.interceptors.response.use(
  (res) => {
    if (res.data.code !== 200) {
        Message.error(res?.data.message || '服务异常')
      return Promise.reject(res)
    } else {
      return res.data
    }
  },
  (err) => {
    Message({
      message: err.response?.data.message || '服务异常',
      type: 'error'
    })
    console.log(err)
    if (err.response?.status === 401) {
      router.push('/login')
    }
    return Promise.reject(err)
  }
)

export default request
