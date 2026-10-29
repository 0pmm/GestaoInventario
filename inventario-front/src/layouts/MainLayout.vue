<script setup>
import { computed } from 'vue';
import { useRouter } from 'vue-router';
import { useAuthStore } from '@/api/auth';

const auth = useAuthStore()
const router = useRouter()

const menu = computed(() =>
  [
    { titulo: 'Início', icone: 'mdi-home', to: '/' },
    { titulo: 'Bens', icone: 'mdi-package-variant-closed', to: '/bens' },
    { titulo: 'Histórico', icone: 'mdi-history', to: '/historico' },
    { titulo: 'Estatísticas', icone: 'mdi-chart-bar', to: '/estatisticas' },
    { titulo: 'Categorias', icone: 'mdi-tag-multiple', to: '/categorias', admin: true },
    { titulo: 'Localizações', icone: 'mdi-map-marker', to: '/localizacoes', admin: true },
    { titulo: 'Usuários', icone: 'mdi-account-group', to: '/usuarios', admin: true },
  ].filter((item) => !item.admin || auth.ehAdmin),
)

async function sair() {
    await auth.sair()
    router.push({ name: 'login'})
}
</script>

<template>
  <v-navigation-drawer permanent>
    <v-list nav>
      <v-list-item
        v-for="item in menu"
        :key="item.to"
        :to="item.to"
        :prepend-icon="item.icone"
        :title="item.titulo"
      />
    </v-list>
  </v-navigation-drawer>

  <v-app-bar flat border>
    <v-app-bar-title>Gestão de Inventário</v-app-bar-title>
    <span class="text-body-2 mr-2">
      {{ auth.usuario?.nome }} ({{ auth.ehAdmin ? 'Administrador' : 'Usuário' }})
    </span>
    <v-btn icon="mdi-logout" title="Sair" @click="sair" />
  </v-app-bar>

  <v-main>
    <router-view />
  </v-main>
</template>