import { createApp } from 'vue'
import App from './App.vue'
import { Toast } from 'vant';
import 'vant/lib/index.css'

createApp(App).use(Toast).mount('#app')
