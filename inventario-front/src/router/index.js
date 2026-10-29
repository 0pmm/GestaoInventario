import { createRouter, createWebHashHistory } from 'vue-router'
import { useAuthStore } from '../stores/auth'

// Hash (/#/bens): o F5 funciona sem configurar nada no servidor, inclusive dentro do WAR
const router = createRouter({
  history: createWebHashHistory(),
  routes: [
    {
      path: '/login',
      name: 'login',
      component: () => import('../views/LoginView.vue'),
      meta: { publica: true },
    },
    {
      path: '/',
      component: () => import('../layouts/MainLayout.vue'),
      children: [
        { path: '', name: 'home', component: () => import('../views/HomeView.vue') },

        // Telas para os dois perfis
        { path: 'bens', name: 'bens', component: () => import('../views/BensView.vue') },

        // Telas só do administrador
        {
          path: 'categorias',
          name: 'categorias',
          component: () => import('../views/CategoriasView.vue'),
          meta: { admin: true },
        },
        {
          path: 'localizacoes',
          name: 'localizacoes',
          component: () => import('../views/LocalizacoesView.vue'),
          meta: { admin: true },
        },
        {
          path: 'usuarios',
          name: 'usuarios',
          component: () => import('../views/UsuariosView.vue'),
          meta: { admin: true },
        },

        { path: 'bens/:id', name: 'bem-detalhe', component: () => import('../views/BemDetalheView.vue') },
        { path: 'historico', name: 'historico', component: () => import('../views/HistoricoView.vue') },
        { path: 'estatisticas', name: 'estatisticas', component: () => import('../views/EstatisticasView.vue') },
      ],
    },
    // Qualquer endereço desconhecido volta para o início
    { path: '/:pathMatch(.*)*', redirect: '/' },
  ],
})

// Roda antes de cada navegação: exige login e perfil
router.beforeEach(async (to) => {
  const auth = useAuthStore()
  if (!auth.carregado) await auth.carregar()

  if (to.meta.publica) {
    return auth.logado ? { name: 'home' } : true
  }
  if (!auth.logado) return { name: 'login' }
  if (to.meta.admin && !auth.ehAdmin) return { name: 'home' }
  return true
})

export default router