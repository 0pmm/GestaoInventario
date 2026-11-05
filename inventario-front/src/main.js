import { createApp } from 'vue'
import { createPinia } from 'pinia'
import App from './App.vue'
import router from './router'
import { setUnauthorizedHandler } from './api/http'
import { useAuthStore } from './stores/auth'

import '@fontsource-variable/inter'
import 'vuetify/styles'
import '@mdi/font/css/materialdesignicons.css'
import './styles/main.css'
import { createVuetify } from 'vuetify'
import * as components from 'vuetify/components'
import * as directives from 'vuetify/directives'
import { pt } from 'vuetify/locale'

const vuetify = createVuetify({
    components,
    directives,
    locale: { locale: 'pt', messages: { pt } },
    theme: {
        defaultTheme: 'inventario',
        themes: {
            inventario: {
                dark: false,
                colors: {
                    background: '#F5F6F8',
                    surface: '#FFFFFF',
                    'surface-variant': '#475467',
                    primary: '#0F766E',
                    secondary: '#344054',
                    success: '#12B76A',
                    info: '#2E90FA',
                    warning: '#F79009',
                    error: '#F04438',
                },
            },
        },
    },
    defaults: {
        global: { ripple: false },
        VBtn: { variant: 'flat', rounded: 'lg', class: 'text-none font-weight-medium', color: 'primary' },
        VCard: { variant: 'flat', rounded: 'xl', border: true },
        VTextField: { variant: 'outlined', density: 'comfortable', rounded: 'lg', color: 'primary' },
        VTextarea: { variant: 'outlined', density: 'comfortable', rounded: 'lg', color: 'primary' },
        VSelect: { variant: 'outlined', density: 'comfortable', rounded: 'lg', color: 'primary' },
        VAutocomplete: { variant: 'outlined', density: 'comfortable', rounded: 'lg', color: 'primary' },
        VChip: { rounded: 'lg' },
        VDataTable: { hover: true, density: 'comfortable' },
        VDialog: { scrim: '#101828' },
        VAlert: { rounded: 'lg' },
        VSnackbar: { rounded: 'lg' },
    },
})

if (import.meta.env.VITE_MOCK) {
    const { instalarMock } = await import('./mock/mock')
    instalarMock()
}

const app = createApp(App)
app.use(createPinia()).use(router).use(vuetify)

setUnauthorizedHandler(() => {
    useAuthStore().usuario = null
    router.push({ name: 'login' })
})

app.mount('#app')
