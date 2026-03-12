import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import 'element-plus/theme-chalk/dark/css-vars.css'
import vue3TreeOrg from 'vue3-tree-org'
import 'vue3-tree-org/lib/vue3-tree-org.css'
import App from './App.vue'
import router from './router'
import './assets/styles/variables.css'
import './assets/styles/common.css'

const app = createApp(App)
app.use(router)
app.use(ElementPlus, { size: 'default' })
app.use(vue3TreeOrg)
app.mount('#app')
