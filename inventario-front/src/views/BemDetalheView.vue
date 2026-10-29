<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'
import { bensApi } from '../api/bens'
import { observacoesApi } from '../api/observacoes'
import { useNotificacao } from '../composables/useNotificacao'
import { errosDeCampos, mensagemDeErro } from '../utils/erros'
import { ESTADOS, STATUS_BEM } from '../utils/enums'
import { formatarData, formatarDataHora, formatarMoeda } from '../utils/formatadores'
import StatusChip from '../components/StatusChip.vue'

const route = useRoute()
const { avisar } = useNotificacao()

const id = computed(() => route.params.id)
const bem = ref(null)
const carregando = ref(true)
const erro = ref('')

// ----- observações -----
const observacoes = ref([])
const novoTexto = ref('')
const enviando = ref(false)
const erroTexto = ref('')

async function carregarObservacoes() {
  try {
    observacoes.value = await observacoesApi.listar(id.value)
  } catch (e) {
    avisar(mensagemDeErro(e), 'error')
  }
}

async function carregar() {
  carregando.value = true
  erro.value = ''
  bem.value = null
  try {
    bem.value = await bensApi.buscar(id.value)
    await carregarObservacoes()
  } catch (e) {
    erro.value = mensagemDeErro(e)   // 404 se o bem não existir
  } finally {
    carregando.value = false
  }
}

// Recarrega também ao navegar de um bem para outro
watch(id, carregar, { immediate: true })

async function adicionarObservacao() {
  enviando.value = true
  erroTexto.value = ''
  try {
    await observacoesApi.criar(id.value, novoTexto.value)
    novoTexto.value = ''
    avisar('Observação adicionada')
    await carregarObservacoes()
  } catch (e) {
    erroTexto.value = errosDeCampos(e).texto || mensagemDeErro(e)
  } finally {
    enviando.value = false
  }
}
</script>

<template>
  <v-container>
    <v-btn variant="text" prepend-icon="mdi-arrow-left" :to="{ name: 'bens' }" class="mb-2">
      Voltar para bens
    </v-btn>

    <v-progress-linear v-if="carregando" indeterminate />

    <v-alert v-if="erro" type="error" variant="tonal">{{ erro }}</v-alert>

    <template v-if="bem">
      <div class="d-flex align-center mb-4">
        <div>
          <h1 class="text-h5">{{ bem.nome }}</h1>
          <div class="text-medium-emphasis">Patrimônio {{ bem.codigoPatrimonio }}</div>
        </div>
        <v-spacer />
        <v-btn
          color="primary"
          prepend-icon="mdi-history"
          :to="{ name: 'historico', query: { bemId: bem.id } }"
        >
          Histórico
        </v-btn>
      </div>

      <!-- Dados -->
      <v-card class="pa-4 mb-4">
        <v-row>
          <v-col cols="6" md="3">
            <div class="text-caption text-medium-emphasis">Status</div>
            <StatusChip :valor="bem.status" :opcoes="STATUS_BEM" />
          </v-col>
          <v-col cols="6" md="3">
            <div class="text-caption text-medium-emphasis">Estado de conservação</div>
            <StatusChip :valor="bem.estadoConservacao" :opcoes="ESTADOS" />
          </v-col>
          <v-col cols="6" md="3">
            <div class="text-caption text-medium-emphasis">Localização</div>
            <div>{{ bem.localizacao?.nome }}</div>
          </v-col>
          <v-col cols="6" md="3">
            <div class="text-caption text-medium-emphasis">Quantidade</div>
            <div>{{ bem.quantidade }}</div>
          </v-col>

          <v-col cols="6" md="3">
            <div class="text-caption text-medium-emphasis">Valor de aquisição</div>
            <div>{{ formatarMoeda(bem.valorAquisicao) }}</div>
          </v-col>
          <v-col cols="6" md="3">
            <div class="text-caption text-medium-emphasis">Data de aquisição</div>
            <div>{{ formatarData(bem.dataAquisicao) }}</div>
          </v-col>
          <v-col cols="12" md="6">
            <div class="text-caption text-medium-emphasis">Categorias</div>
            <v-chip v-for="c in bem.categorias" :key="c.id" size="small" class="mr-1">
              {{ c.nome }}
            </v-chip>
          </v-col>

          <v-col cols="12">
            <div class="text-caption text-medium-emphasis">Descrição</div>
            <div style="white-space: pre-wrap">{{ bem.descricao || '—' }}</div>
          </v-col>
        </v-row>
      </v-card>

      <!-- Observações -->
      <v-card class="pa-4">
        <h2 class="text-h6 mb-3">Observações</h2>

        <v-form @submit.prevent="adicionarObservacao">
          <v-textarea
            v-model="novoTexto"
            label="Nova observação"
            rows="2"
            counter="1000"
            :error-messages="erroTexto"
          />
          <div class="d-flex justify-end mb-4">
            <v-btn
              type="submit"
              color="primary"
              :loading="enviando"
              :disabled="!novoTexto.trim()"
            >
              Adicionar
            </v-btn>
          </div>
        </v-form>

        <v-list v-if="observacoes.length" lines="three">
          <template v-for="(o, i) in observacoes" :key="o.id">
            <v-divider v-if="i > 0" />
            <v-list-item>
              <div style="white-space: pre-wrap">{{ o.texto }}</div>
              <div class="text-caption text-medium-emphasis mt-1">
                {{ o.autor }} · {{ formatarDataHora(o.dataHora) }}
              </div>
            </v-list-item>
          </template>
        </v-list>
        <div v-else class="text-medium-emphasis">Nenhuma observação registrada.</div>
      </v-card>
    </template>
  </v-container>
</template>