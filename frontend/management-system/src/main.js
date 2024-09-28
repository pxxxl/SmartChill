import { createApp } from 'vue'
import App from './App.vue'
import ArcoVue from '@arco-design/web-vue';
import '@arco-design/web-vue/dist/arco.css';
import { Message } from '@arco-design/web-vue';
import ArcoVueIcon from '@arco-design/web-vue/es/icon';
import router from './router/router'
import pinia from './stores';

const app = createApp(App)
Message._context = app._context;
app.use(ArcoVue)
app.use(ArcoVueIcon)
app.use(router)
app.use(pinia)
app.mount('#app')
