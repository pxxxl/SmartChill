import { createApp } from 'vue'
import App from './App.vue'
import { Toast, Popup, Cell, Calendar, PullRefresh, Loading, Icon, Picker} from 'vant';
import 'vant/lib/index.css'

createApp(App).use(Toast).use(Popup).use(Cell).use(Calendar).use(PullRefresh).use(Loading).use(Icon).use(Picker).mount('#app')
