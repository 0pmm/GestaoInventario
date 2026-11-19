<script setup>
import { computed, onMounted, ref } from 'vue'
import { useAuthStore } from '../stores/auth'
import { bensApi } from '../api/bens'
import { movimentacoesApi } from '../api/movimentacoes'
import { useNotificacao } from '../composables/useNotificacao'
import { mensagemDeErro } from '../utils/erros'
import { STATUS_BEM, TIPOS_MOVIMENTACAO } from '../utils/enums'
import { formatarDataHora, formatarMoeda, formatarNumero } from '../utils/formatadores'
import KpiCard from '../components/KpiCard.vue'
import StatusChip from '../components/StatusChip.vue'

const auth = useAuthStore()
const { avisar } = useNotificacao()

const bens = ref([])
const movimentacoes = ref([])
const carregando = ref(true)

onMounted(async () => {
  try {
    const [b, m] = await Promise.all([bensApi.listar(), movimentacoesApi.listar()])
    bens.value = b
    movimentacoes.value = m
  } catch (e) {
    avisar(mensagemDeErro(e), 'error')
  } finally {
    carregando.value = false
  }
})

const resumo = computed(() => ({
  bens: bens.value.length,
  unidades: bens.value.reduce((t, b) => t + b.quantidade, 0),
  valor: bens.value.reduce((t, b) => t + (b.valorAquisicao ?? 0) * b.quantidade, 0),
  manutencao: bens.value.filter((b) => b.status === 'EM_MANUTENCAO').length,
}))

const recentes = computed(() =>
  [...movimentacoes.value].sort((a, b) => new Date(b.dataHora) - new Date(a.dataHora)).slice(0, 6),
)

const atalhos = computed(() =>
  [
    { titulo: 'Bens', texto: 'Consulte e filtre o inventário', icone: 'mdi-package-variant-closed', to: '/bens' },
    { titulo: 'Histórico', texto: 'Movimentações dos bens', icone: 'mdi-history', to: '/historico' },
    { titulo: 'Estatísticas', texto: 'Gráficos por categoria e local', icone: 'mdi-chart-box-outline', to: '/estatisticas' },
    { titulo: 'Categorias', texto: 'Organize os tipos de bem', icone: 'mdi-tag-multiple-outline', to: '/categorias', admin: true },
    { titulo: 'Localizações', texto: 'Salas, prédios e setores', icone: 'mdi-map-marker-outline', to: '/localizacoes', admin: true },
    { titulo: 'Usuários', texto: 'Acessos e perfis', icone: 'mdi-account-group-outline', to: '/usuarios', admin: true },
  ].filter((a) => !a.admin || auth.ehAdmin),
)

const primeiroNome = computed(() => auth.usuario?.nome?.split(' ')[0] ?? '')
</script>

<template>
  <div>
    <div class="mb-6">
      <h1 class="text-h5 font-weight-bold">Olá, {{ primeiroNome }}</h1>
      <p class="texto-suave text-body-2 mt-1">Um resumo do patrimônio cadastrado.</p>
    </div>

    <v-row>
      <v-col cols="6" md="3">
        <KpiCard rotulo="Bens cadastrados" :valor="formatarNumero(resumo.bens)" icone="mdi-package-variant-closed" />
      </v-col>
      <v-col cols="6" md="3">
        <KpiCard rotulo="Unidades" :valor="formatarNumero(resumo.unidades)" icone="mdi-layers-outline" cor="info" />
      </v-col>
      <v-col cols="6" md="3">
        <KpiCard rotulo="Valor total" :valor="formatarMoeda(resumo.valor)" icone="mdi-cash-multiple" cor="success" />
      </v-col>
      <v-col cols="6" md="3">
        <KpiCard rotulo="Em manutenção" :valor="formatarNumero(resumo.manutencao)" icone="mdi-wrench-outline" cor="warning" />
      </v-col>
    </v-row>

    <v-row class="mt-2">
      <v-col cols="12" md="7">
        <v-card class="h-100">
          <div class="d-flex align-center px-5 pt-4 pb-2">
            <h2 class="text-subtitle-1 font-weight-bold">Últimas movimentações</h2>
            <v-spacer />
            <v-btn variant="text" size="small" append-icon="mdi-arrow-right" to="/historico">Ver tudo</v-btn>
          </div>
          <v-list v-if="recentes.length" bg-color="transparent" class="py-0">
            <v-list-item
              v-for="m in recentes"
              :key="m.id"
              :to="{ name: 'bem-detalhe', params: { id: m.bem.id } }"
              class="px-5"
            >
              <v-list-item-title class="text-body-2 font-weight-medium">{{ m.bem.nome }}</v-list-item-title>
              <v-list-item-subtitle class="text-caption">
                {{ m.bem.codigoPatrimonio }} · {{ formatarDataHora(m.dataHora) }}
              </v-list-item-subtitle>
              <template #append><StatusChip :valor="m.tipo" :opcoes="TIPOS_MOVIMENTACAO" /></template>
            </v-list-item>
          </v-list>
          <div v-else class="pa-8 text-center texto-suave text-body-2">
            <v-icon icon="mdi-history" size="32" class="mb-2" /><br />
            {{ carregando ? 'Carregando…' : 'Nenhuma movimentação ainda.' }}
          </div>
        </v-card>
      </v-col>

      <v-col cols="12" md="5">
        <div class="rotulo mb-3">Acesso rápido</div>
        <div class="atalhos">
          <v-card v-for="a in atalhos" :key="a.to" :to="a.to" class="atalho pa-4">
            <v-avatar color="primary" variant="tonal" rounded="lg" size="36" class="mb-3">
              <v-icon :icon="a.icone" size="20" />
            </v-avatar>
            <div class="text-body-2 font-weight-bold">{{ a.titulo }}</div>
            <div class="text-caption texto-suave">{{ a.texto }}</div>
          </v-card>
        </div>
      </v-col>
    </v-row>
  </div>
</template>

<style scoped>
.atalhos { display: grid; grid-template-columns: repeat(2, 1fr); gap: 12px; }
.atalho { transition: border-color 0.15s, transform 0.15s; }
.atalho:hover { border-color: rgb(var(--v-theme-primary)); transform: translateY(-2px); }
</style>
