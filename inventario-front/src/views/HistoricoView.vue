<script setup>
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { bensApi } from '../api/bens'
import { movimentacoesApi } from '../api/movimentacoes'
import { usuariosApi } from '../api/usuarios'
import { useAuthStore } from '../stores/auth'
import { useNotificacao } from '../composables/useNotificacao'
import { mensagemDeErro } from '../utils/erros'
import { TIPOS_MOVIMENTACAO, rotulo } from '../utils/enums'
import { formatarDataHora } from '../utils/formatadores'
import PageHeader from '../components/PageHeader.vue'
import StatusChip from '../components/StatusChip.vue'

const auth = useAuthStore()
const route = useRoute()
const router = useRouter()
const { avisar } = useNotificacao()

// ----- filtro vindo da URL (botão "Histórico" do bem) -----
const bemId = computed(() => (route.query.bemId ? Number(route.query.bemId) : null))
const bemFiltrado = ref(null)

async function carregarBem() {
  bemFiltrado.value = null
  if (!bemId.value) return
  try {
    bemFiltrado.value = await bensApi.buscar(bemId.value)
  } catch (e) {
    avisar(mensagemDeErro(e), 'error')
    removerFiltroBem()
  }
}

function removerFiltroBem() {
  router.replace({ name: 'historico', query: {} })
}

// ----- demais filtros -----
const filtros = reactive({ tipo: null, de: '', ate: '', responsavelId: null })

const tiposDisponiveis = computed(() =>
  auth.ehAdmin ? TIPOS_MOVIMENTACAO : TIPOS_MOVIMENTACAO.filter((t) => t.visivelParaComum),
)

const usuarios = ref([])   // só o administrador usa (a rota de usuários é restrita a ele)

onMounted(async () => {
  if (!auth.ehAdmin) return
  try {
    usuarios.value = await usuariosApi.listar()
  } catch (e) {
    avisar(mensagemDeErro(e), 'error')
  }
})

function limparFiltros() {
  Object.assign(filtros, { tipo: null, de: '', ate: '', responsavelId: null })
}

// ----- lista -----
const movimentacoes = ref([])
const carregando = ref(false)

async function carregar() {
  carregando.value = true
  try {
    movimentacoes.value = await movimentacoesApi.listar({ bemId: bemId.value, ...filtros })
  } catch (e) {
    avisar(mensagemDeErro(e), 'error')
  } finally {
    carregando.value = false
  }
}

watch(bemId, carregarBem, { immediate: true })
watch([bemId, filtros], carregar, { deep: true, immediate: true })

const headers = computed(() => [
  { title: 'Data e hora', key: 'dataHora' },
  { title: 'Bem', key: 'bem', sortable: false },
  { title: 'Tipo', key: 'tipo', sortable: false },
  { title: 'Mudança', key: 'mudanca', sortable: false },
  { title: 'Origem → Destino', key: 'locais', sortable: false },
  ...(auth.ehAdmin ? [{ title: 'Responsável', key: 'responsavel', sortable: false }] : []),
  { title: '', key: 'acoes', sortable: false, align: 'end' },
])

// ----- detalhes de uma movimentação -----
const dialogDetalhe = ref(false)
const detalhe = ref(null)

function abrirDetalhe(item) {
  detalhe.value = item
  dialogDetalhe.value = true
}

const camposDetalhe = computed(() => {
  const m = detalhe.value
  if (!m) return []
  const campos = [
    { rotulo: 'Data e hora', valor: formatarDataHora(m.dataHora) },
    { rotulo: 'Bem', valor: `${m.bem.codigoPatrimonio} — ${m.bem.nome}` },
    { rotulo: 'Tipo', valor: rotulo(TIPOS_MOVIMENTACAO, m.tipo) },
    { rotulo: 'Valor anterior', valor: m.valorAnterior },
    { rotulo: 'Valor novo', valor: m.valorNovo },
    { rotulo: 'Origem', valor: m.origem },
    { rotulo: 'Destino', valor: m.destino },
    { rotulo: 'Motivo', valor: m.motivo },
  ]
  if (auth.ehAdmin) campos.push({ rotulo: 'Responsável', valor: m.responsavel })
  return campos
})
</script>

<template>
  <v-container>
    <PageHeader titulo="Histórico de movimentações" />

    <v-alert v-if="!auth.ehAdmin" type="info" variant="tonal" density="compact" class="mb-4">
      Aqui aparecem as mudanças sofridas pelos bens (transferências, avarias, vendas, trocas de
      categoria e similares).
    </v-alert>

    <v-card class="pa-4 mb-4">
      <v-chip
        v-if="bemId"
        closable
        color="primary"
        class="mb-3"
        @click:close="removerFiltroBem"
      >
        Bem: {{ bemFiltrado ? `${bemFiltrado.codigoPatrimonio} — ${bemFiltrado.nome}` : '...' }}
      </v-chip>

      <v-row dense>
        <v-col cols="12" md="3">
          <v-select
            v-model="filtros.tipo"
            :items="tiposDisponiveis"
            label="Tipo de movimentação"
            clearable
            hide-details
          />
        </v-col>
        <v-col cols="6" md="2">
          <v-text-field v-model="filtros.de" label="De" type="date" hide-details />
        </v-col>
        <v-col cols="6" md="2">
          <v-text-field v-model="filtros.ate" label="Até" type="date" hide-details />
        </v-col>
        <v-col v-if="auth.ehAdmin" cols="12" md="3">
          <v-autocomplete
            v-model="filtros.responsavelId"
            :items="usuarios"
            item-title="nome"
            item-value="id"
            label="Responsável"
            clearable
            hide-details
          />
        </v-col>
        <v-col cols="12" md="2" class="d-flex align-center">
          <v-btn variant="text" prepend-icon="mdi-filter-remove" @click="limparFiltros">
            Limpar
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <v-card>
      <v-data-table
        :headers="headers"
        :items="movimentacoes"
        :loading="carregando"
        :items-per-page="15"
        no-data-text="Nenhuma movimentação encontrada"
      >
        <template #item.dataHora="{ item }">{{ formatarDataHora(item.dataHora) }}</template>

        <template #item.bem="{ item }">
          <router-link :to="{ name: 'bem-detalhe', params: { id: item.bem.id } }">
            {{ item.bem.codigoPatrimonio }} — {{ item.bem.nome }}
          </router-link>
        </template>

        <template #item.tipo="{ item }">
          <StatusChip :valor="item.tipo" :opcoes="TIPOS_MOVIMENTACAO" />
        </template>

        <template #item.mudanca="{ item }">
          <div
            v-if="item.valorAnterior || item.valorNovo"
            class="texto-curto"
            :title="`${item.valorAnterior ?? '—'} → ${item.valorNovo ?? '—'}`"
          >
            <span class="text-medium-emphasis">{{ item.valorAnterior ?? '—' }}</span>
            <v-icon size="small" icon="mdi-arrow-right" />
            {{ item.valorNovo ?? '—' }}
          </div>
        </template>

        <template #item.locais="{ item }">
          <span v-if="item.origem || item.destino">
            {{ item.origem ?? '—' }}
            <v-icon size="small" icon="mdi-arrow-right" />
            {{ item.destino ?? '—' }}
          </span>
        </template>

        <template #item.acoes="{ item }">
          <v-btn icon="mdi-eye" variant="text" title="Ver detalhes" @click="abrirDetalhe(item)" />
        </template>
      </v-data-table>
    </v-card>

    <!-- Detalhes -->
    <v-dialog v-model="dialogDetalhe" max-width="560">
      <v-card title="Detalhes da movimentação">
        <v-card-text>
          <div v-for="campo in camposDetalhe" :key="campo.rotulo" class="mb-3">
            <div class="text-caption text-medium-emphasis">{{ campo.rotulo }}</div>
            <div style="white-space: pre-wrap">{{ campo.valor || '—' }}</div>
          </div>
        </v-card-text>
        <v-card-actions>
          <v-spacer />
          <v-btn @click="dialogDetalhe = false">Fechar</v-btn>
        </v-card-actions>
      </v-card>
    </v-dialog>
  </v-container>
</template>

<style scoped>
.texto-curto {
  max-width: 320px;
  overflow: hidden;
  text-overflow: ellipsis;
  white-space: nowrap;
}
</style>