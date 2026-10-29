import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { setUnauthorizedHandler } from './api/http'
import { useAuthStore } from './stores/auth'

import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { pt } from 'vuetify/locale'

const vuetify = createVuetify({
    components,
    directives,
    locale: { locale: 'pt', messages: { pt } },
    defaults: {
        VTextField: { variant: 'outlined', density: 'comfortable' },
        VTextarea: { variant: 'outlined', density: 'comfortable' },
        VSelect: { variant: 'outlined', density: 'comfortable' },
        VAutocomplete: { variant: 'outlined', density: 'comfortable' },
    },
})

const app = createApp(App)
app.use(createPinia()).use(router).use(vuetify)

setUnauthorizedHandler(() => {
    useAuthStore().usuario = null
    router.push({ name: 'login' })
})

app.mount('#app')