<script setup>
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth'
import { useDisplay } from 'vuetify'

const auth = useAuthStore()
const router = useRouter()
const route = useRoute()
const { mdAndUp } = useDisplay()
const drawer = ref(true)

const grupos = computed(() => [
  {
    titulo: 'Operação',
    itens: [
      { titulo: 'Início', icone: 'mdi-view-dashboard-outline', to: '/' },
      { titulo: 'Bens', icone: 'mdi-package-variant-closed', to: '/bens' },
      { titulo: 'Histórico', icone: 'mdi-history', to: '/historico' },
      { titulo: 'Estatísticas', icone: 'mdi-chart-box-outline', to: '/estatisticas' },
    ],
  },
  ...(auth.ehAdmin
    ? [{
        titulo: 'Administração',
        itens: [
          { titulo: 'Categorias', icone: 'mdi-tag-multiple-outline', to: '/categorias' },
          { titulo: 'Localizações', icone: 'mdi-map-marker-outline', to: '/localizacoes' },
          { titulo: 'Usuários', icone: 'mdi-account-group-outline', to: '/usuarios' },
        ],
      }]
    : []),
])

const secao = computed(() => {
  const todos = grupos.value.flatMap((g) => g.itens)
  const atual = todos.find((i) => i.to !== '/' && route.path.startsWith(i.to)) ?? todos[0]
  return atual.titulo
})

const iniciais = computed(() =>
  (auth.usuario?.nome ?? '?').split(' ').filter(Boolean).slice(0, 2).map((p) => p[0]).join('').toUpperCase(),
)

async function sair() {
  await auth.sair()
  router.push({ name: 'login' })
}
</script>

<template>
  <v-navigation-drawer v-model="drawer" :permanent="mdAndUp" width="256" class="menu-lateral" border="0">
    <div class="marca">
      <div class="marca-icone"><v-icon icon="mdi-warehouse" size="20" /></div>
      <div>
        <div class="marca-nome">Inventário</div>
        <div class="marca-sub">Gestão de patrimônio</div>
      </div>
    </div>

    <template v-for="g in grupos" :key="g.titulo">
      <div class="grupo-titulo">{{ g.titulo }}</div>
      <v-list nav density="comfortable" class="px-3 py-0" bg-color="transparent">
        <v-list-item
          v-for="item in g.itens"
          :key="item.to"
          :to="item.to"
          :prepend-icon="item.icone"
          :title="item.titulo"
          rounded="lg"
          exact
          class="item-menu"
        />
      </v-list>
    </template>

    <template #append>
      <div class="usuario">
        <v-avatar color="primary" size="36"><span class="text-body-2 font-weight-bold">{{ iniciais }}</span></v-avatar>
        <div class="usuario-info">
          <div class="usuario-nome">{{ auth.usuario?.nome }}</div>
          <div class="usuario-perfil">{{ auth.ehAdmin ? 'Administrador' : 'Usuário comum' }}</div>
        </div>
        <v-btn icon="mdi-logout" variant="text" size="small" color="white" title="Sair" @click="sair" />
      </div>
    </template>
  </v-navigation-drawer>

  <v-app-bar flat color="background" height="64" class="barra-topo">
    <v-app-bar-nav-icon v-if="!mdAndUp" @click="drawer = !drawer" />
    <div class="text-body-2 texto-suave ml-4">
      Inventário <v-icon icon="mdi-chevron-right" size="small" />
      <span class="text-high-emphasis font-weight-medium">{{ secao }}</span>
    </div>
  </v-app-bar>

  <v-main>
    <v-container fluid class="pagina">
      <router-view />
    </v-container>
  </v-main>
</template>

<style scoped>
.menu-lateral { background: #0B1F1E !important; color: #fff; }
.marca { display: flex; align-items: center; gap: 12px; padding: 22px 20px 18px; }
.marca-icone {
  width: 36px; height: 36px; border-radius: 10px; display: grid; place-items: center;
  background: #14B8A6; color: #04201E;
}
.marca-nome { font-weight: 700; font-size: 1rem; line-height: 1.1; }
.marca-sub { font-size: 0.72rem; color: #8FB3AF; }
.grupo-titulo {
  padding: 18px 24px 8px; font-size: 0.68rem; font-weight: 600; letter-spacing: 0.08em;
  text-transform: uppercase; color: #6B908C;
}
.item-menu { color: #C5DAD7; margin-bottom: 2px; }
.item-menu :deep(.v-list-item-title) { font-size: 0.9rem; font-weight: 500; }
.item-menu:hover { background: rgba(255, 255, 255, 0.06); }
.item-menu.v-list-item--active { background: rgba(20, 184, 166, 0.18); color: #5EEAD4; }
.item-menu.v-list-item--active :deep(.v-list-item__overlay) { opacity: 0; }
.usuario {
  display: flex; align-items: center; gap: 10px; padding: 14px 16px;
  border-top: 1px solid rgba(255, 255, 255, 0.08);
}
.usuario-info { flex: 1; min-width: 0; }
.usuario-nome { font-size: 0.85rem; font-weight: 600; white-space: nowrap; overflow: hidden; text-overflow: ellipsis; }
.usuario-perfil { font-size: 0.72rem; color: #8FB3AF; }
.barra-topo { border-bottom: 1px solid var(--borda) !important; }
</style>
