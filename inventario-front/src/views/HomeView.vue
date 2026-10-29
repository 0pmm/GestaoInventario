<script setup>
import { computed } from 'vue'
import { useAuthStore } from '../stores/auth'

const auth = useAuthStore()

const atalhos = computed(() =>
  [
    { titulo: 'Bens', texto: 'Consulte e filtre o inventário', icone: 'mdi-package-variant-closed', to: '/bens' },
    { titulo: 'Histórico', texto: 'Veja as movimentações dos bens', icone: 'mdi-history', to: '/historico' },
    { titulo: 'Estatísticas', texto: 'Gráficos por categoria, local e status', icone: 'mdi-chart-bar', to: '/estatisticas' },
    { titulo: 'Categorias', texto: 'Gerencie as categorias', icone: 'mdi-tag-multiple', to: '/categorias', admin: true },
    { titulo: 'Localizações', texto: 'Gerencie as localizações', icone: 'mdi-map-marker', to: '/localizacoes', admin: true },
    { titulo: 'Usuários', texto: 'Gerencie os usuários', icone: 'mdi-account-group', to: '/usuarios', admin: true },
  ].filter((a) => !a.admin || auth.ehAdmin),
)
</script>

<template>
  <v-container>
    <h1 class="text-h5 mb-1">Olá, {{ auth.usuario?.nome }}</h1>
    <p class="text-medium-emphasis mb-6">O que você quer fazer?</p>

    <v-row>
      <v-col v-for="atalho in atalhos" :key="atalho.to" cols="12" sm="6" md="4">
        <v-card :to="atalho.to" :prepend-icon="atalho.icone" :title="atalho.titulo" :text="atalho.texto" />
      </v-col>
    </v-row>
  </v-container>
</template>