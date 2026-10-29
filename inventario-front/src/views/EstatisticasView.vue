<script setup>
import { computed, onMounted, ref } from 'vue'
import { Bar, Doughnut } from 'vue-chartjs'
import {
  ArcElement,
  BarElement,
  CategoryScale,
  Chart as ChartJS,
  Legend,
  LinearScale,
  Tooltip,
} from 'chart.js'
import { categoriasApi } from '../api/categorias'
import { localizacoesApi } from '../api/localizacoes'
import { useEstatisticas } from '../composables/useEstatisticas'
import { useNotificacao } from '../composables/useNotificacao'
import { mensagemDeErro } from '../utils/erros'
import { STATUS_BEM } from '../utils/enums'
import { formatarMoeda, formatarNumero } from '../utils/formatadores'
import PageHeader from '../components/PageHeader.vue'

// O Chart.js só desenha o que for registrado aqui
ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend)

const { avisar } = useNotificacao()

const {
  carregando, filtros, incluirInativos, limparFiltros,
  resumo, porCategoria, porLocalizacao, porStatus, porEstado, semDados, paraGrafico,
} = useEstatisticas()

// ----- opções dos filtros -----
const categorias = ref([])
const localizacoes = ref([])

onMounted(async () => {
  try {
    const [cats, locs] = await Promise.all([categoriasApi.listar(), localizacoesApi.listar()])
    categorias.value = cats
    localizacoes.value = locs
  } catch (e) {
    avisar(mensagemDeErro(e), 'error')
  }
})

// ----- métrica dos gráficos de barras -----
const metrica = ref('bens')
const ROTULO_METRICA = { bens: 'Bens', unidades: 'Unidades', valor: 'Valor total' }

// ----- gráficos -----
const PALETA = [
  '#1976D2', '#388E3C', '#F57C00', '#7B1FA2', '#C62828',
  '#00838F', '#5D4037', '#455A64', '#AFB42B', '#E91E63',
]
const cores = (n) => Array.from({ length: n }, (_, i) => PALETA[i % PALETA.length])

function dadosBarra(grupos) {
  const g = paraGrafico(grupos, metrica.value)
  return {
    labels: g.labels,
    datasets: [{ ...g.datasets[0], label: ROTULO_METRICA[metrica.value], backgroundColor: '#1976D2' }],
  }
}

function dadosRosca(grupos) {
  const g = paraGrafico(grupos, 'bens')
  return {
    labels: g.labels,
    datasets: [{ ...g.datasets[0], backgroundColor: cores(g.labels.length) }],
  }
}

const dadosCategoria = computed(() => dadosBarra(porCategoria.value))
const dadosLocalizacao = computed(() => dadosBarra(porLocalizacao.value))
const dadosStatus = computed(() => dadosRosca(porStatus.value))
const dadosEstado = computed(() => dadosRosca(porEstado.value))

const opcoesBarra = computed(() => ({
  indexAxis: 'y',
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: { display: false },
    tooltip: {
      callbacks: {
        label: (ctx) =>
          metrica.value === 'valor' ? formatarMoeda(ctx.parsed.x) : formatarNumero(ctx.parsed.x),
      },
    },
  },
  scales: { x: { beginAtZero: true, ticks: { precision: 0 } } },
}))

const opcoesRosca = {
  responsive: true,
  maintainAspectRatio: false,
  plugins: { legend: { position: 'bottom' } },
}

// Altura proporcional ao número de barras, para os nomes não ficarem espremidos
const alturaBarras = (grupos) => `${Math.max(220, grupos.length * 36 + 60)}px`
</script>

<template>
  <v-container>
    <PageHeader titulo="Estatísticas" />

    <!-- Filtros -->
    <v-card class="pa-4 mb-4">
      <v-row dense>
        <v-col cols="12" sm="6" md="4">
          <v-autocomplete
            v-model="filtros.categoriaId"
            :items="categorias"
            item-title="nome"
            item-value="id"
            label="Categoria"
            clearable
            hide-details
          />
        </v-col>
        <v-col cols="12" sm="6" md="4">
          <v-autocomplete
            v-model="filtros.localizacaoId"
            :items="localizacoes"
            item-title="nome"
            item-value="id"
            label="Localização"
            clearable
            hide-details
          />
        </v-col>
        <v-col cols="12" sm="6" md="4">
          <v-select
            v-model="filtros.status"
            :items="STATUS_BEM"
            label="Status"
            clearable
            hide-details
          />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="filtros.aquisicaoDe" label="Adquirido de" type="date" hide-details />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="filtros.aquisicaoAte" label="Adquirido até" type="date" hide-details />
        </v-col>
        <v-col cols="12" md="3" class="d-flex align-center">
          <v-switch
            v-model="incluirInativos"
            label="Incluir inativos"
            color="primary"
            :disabled="!!filtros.status"
            hide-details
          />
        </v-col>
        <v-col cols="12" md="3" class="d-flex align-center">
          <v-btn variant="text" prepend-icon="mdi-filter-remove" @click="limparFiltros">
            Limpar filtros
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <v-progress-linear v-if="carregando" indeterminate class="mb-4" />

    <v-alert v-if="semDados" type="info" variant="tonal">
      Nenhum bem encontrado para os filtros selecionados.
    </v-alert>

    <template v-else-if="!carregando">
      <!-- Números gerais -->
      <v-row class="mb-2">
        <v-col cols="6" md="3">
          <v-card class="pa-4">
            <div class="text-caption text-medium-emphasis">Bens cadastrados</div>
            <div class="text-h5">{{ formatarNumero(resumo.bens) }}</div>
          </v-card>
        </v-col>
        <v-col cols="6" md="3">
          <v-card class="pa-4">
            <div class="text-caption text-medium-emphasis">Unidades</div>
            <div class="text-h5">{{ formatarNumero(resumo.unidades) }}</div>
          </v-card>
        </v-col>
        <v-col cols="12" md="6">
          <v-card class="pa-4">
            <div class="text-caption text-medium-emphasis">Valor total (valor × quantidade)</div>
            <div class="text-h5">{{ formatarMoeda(resumo.valorTotal) }}</div>
            <div v-if="resumo.semValor" class="text-caption text-warning">
              {{ resumo.semValor }} bem(ns) sem valor cadastrado não entram na soma
            </div>
          </v-card>
        </v-col>
      </v-row>

      <!-- Escolha da métrica -->
      <div class="d-flex align-center mb-2">
        <span class="text-body-2 mr-3">Gráficos de barras mostram:</span>
        <v-btn-toggle v-model="metrica" mandatory density="comfortable" color="primary">
          <v-btn value="bens">Bens</v-btn>
          <v-btn value="unidades">Unidades</v-btn>
          <v-btn value="valor">Valor</v-btn>
        </v-btn-toggle>
      </div>

      <v-row>
        <v-col cols="12" md="6">
          <v-card class="pa-4" title="Por categoria">
            <v-alert type="info" variant="tonal" density="compact" class="mb-3">
              Um bem com mais de uma categoria conta em cada uma delas.
            </v-alert>
            <div :style="{ height: alturaBarras(porCategoria), position: 'relative' }">
              <Bar :data="dadosCategoria" :options="opcoesBarra" />
            </div>
          </v-card>
        </v-col>

        <v-col cols="12" md="6">
          <v-card class="pa-4" title="Por localização">
            <div :style="{ height: alturaBarras(porLocalizacao), position: 'relative' }">
              <Bar :data="dadosLocalizacao" :options="opcoesBarra" />
            </div>
          </v-card>
        </v-col>

        <v-col cols="12" md="6">
          <v-card class="pa-4" title="Por status (bens)">
            <div style="height: 300px; position: relative">
              <Doughnut :data="dadosStatus" :options="opcoesRosca" />
            </div>
          </v-card>
        </v-col>

        <v-col cols="12" md="6">
          <v-card class="pa-4" title="Por estado de conservação (bens)">
            <div style="height: 300px; position: relative">
              <Doughnut :data="dadosEstado" :options="opcoesRosca" />
            </div>
          </v-card>
        </v-col>
      </v-row>
    </template>
  </v-container>
</template>