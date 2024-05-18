import '@/assets/styles/main.scss'

import { createApp } from 'vue'
import { createPinia } from 'pinia'

import App from './App.vue'
import router from './router'

/**
 * [ Antdv ]
 * npm i ant-design-vue
 * https://antdv.com/docs/vue/getting-started
 */
import Antd from 'ant-design-vue'

/**
 * npm i @ant-design/icons-vue
 * https://antdv.com/components/icon
 */

/**
 * [ axios ]
 * npm i axios
 * https://axios-http.com/docs/intro
 */
import { instance } from '@/api/axios'
import replaceDefaultImagePlugin from './plugins/replaceDefaultImage'

const app = createApp(App)

app.provide('axios', instance)

app.use(createPinia())
app.use(router)
app.use(Antd)

app.use(replaceDefaultImagePlugin)

app.mount('#app')
