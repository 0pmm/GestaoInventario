<script setup>
import { onMounted, reactive, ref } from 'vue'
import { bensApi } from '../api/bens'
import { categoriasApi } from '../api/categorias'
import { localizacoesApi } from '../api/localizacoes'
import { useAuthStore } from '../stores/auth'
import { useCrud } from '../composables/useCrud'
import { useNotificacao } from '../composables/useNotificacao'
import { mensagemDeErro } from '../utils/erros'
import { ESTADOS, STATUS_BEM } from '../utils/enums'
import { formatarMoeda } from '../utils/formatadores'
import PageHeader from '../components/PageHeader.vue'
import FormDialog from '../components/FormDialog.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import AcoesLinha from '../components/AcoesLinha.vue'
import StatusChip from '../components/StatusChip.vue'

const auth = useAuthStore()
const { avisar } = useNotificacao()

const headers = [
  { title: 'Código', key: 'codigoPatrimonio' },
  { title: 'Nome', key: 'nome' },
  { title: 'Categorias', key: 'categorias', sortable: false },
  { title: 'Localização', key: 'localizacao', value: (bem) => bem.localizacao?.nome },
  { title: 'Qtd.', key: 'quantidade', align: 'end' },
  { title: 'Valor', key: 'valorAquisicao', align: 'end' },
  { title: 'Estado', key: 'estadoConservacao' },
  { title: 'Status', key: 'status' },
  { title: 'Ações', key: 'acoes', sortable: false, align: 'end' },
]

// ----- opções dos selects -----
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

// ----- filtros da lista -----
const filtrosVazios = {
  categoriaId: null,
  localizacaoId: null,
  status: null,
  aquisicaoDe: '',
  aquisicaoAte: '',
}
const filtros = reactive({ ...filtrosVazios })

// ----- formulário -----
const vazio = {
  codigoPatrimonio: '',
  nome: '',
  descricao: '',
  quantidade: 1,
  valorAquisicao: '',
  dataAquisicao: '',
  estadoConservacao: 'NOVO',
  status: 'ATIVO',
  localizacaoId: null,
  categoriaIds: [],
  motivo: '',
}

// O item da API traz objetos (localizacao, categorias); o formulário trabalha com ids
function paraForm(bem) {
  return {
    codigoPatrimonio: bem.codigoPatrimonio,
    nome: bem.nome,
    descricao: bem.descricao ?? '',
    quantidade: bem.quantidade,
    valorAquisicao: bem.valorAquisicao ?? '',
    dataAquisicao: bem.dataAquisicao ?? '',
    estadoConservacao: bem.estadoConservacao,
    status: bem.status,
    localizacaoId: bem.localizacao?.id ?? null,
    categoriaIds: bem.categorias.map((c) => c.id),
    motivo: '',
  }
}

// Campos vazios viram null, porque o back-end não aceita "" em número e data
function paraPayload(f, editandoId) {
  return {
    codigoPatrimonio: f.codigoPatrimonio,
    nome: f.nome,
    descricao: f.descricao,
    quantidade: Number(f.quantidade),
    valorAquisicao: f.valorAquisicao === '' || f.valorAquisicao == null ? null : Number(f.valorAquisicao),
    dataAquisicao: f.dataAquisicao || null,
    estadoConservacao: f.estadoConservacao,
    status: editandoId ? f.status : null,   // ao criar, o back-end assume ATIVO
    localizacaoId: f.localizacaoId,
    categoriaIds: f.categoriaIds,
    motivo: f.motivo,
  }
}

const {
  itens, carregando, busca, carregar,
  dialogForm, salvando, editandoId, form, errosCampos, erroGeral, abrirNovo, abrirEdicao, salvar,
} = useCrud({
  api: bensApi,
  vazio,
  rotulo: 'Bem',
  campoBusca: 'busca',
  filtros,
  paraForm,
  paraPayload,
})

function limparFiltros() {
  busca.value = ''
  Object.assign(filtros, filtrosVazios)
}

// ----- inativação (com motivo, que vai para o histórico) -----
const dialogInativar = ref(false)
const alvoInativar = ref(null)
const motivoInativar = ref('')
const inativando = ref(false)

function pedirInativacao(bem) {
  alvoInativar.value = bem
  motivoInativar.value = ''
  dialogInativar.value = true
}

async function confirmarInativacao() {
  inativando.value = true
  try {
    await bensApi.inativar(alvoInativar.value.id, motivoInativar.value)
    dialogInativar.value = false
    avisar('Bem inativado')
    await carregar()
  } catch (e) {
    dialogInativar.value = false
    avisar(mensagemDeErro(e), 'error')
  } finally {
    inativando.value = false
  }
}
</script>

<template>
  <v-container>
    <PageHeader titulo="Bens">
      <v-btn v-if="auth.ehAdmin" color="primary" prepend-icon="mdi-plus" @click="abrirNovo">
        Novo bem
      </v-btn>
    </PageHeader>

    <!-- Busca e filtros -->
    <v-card class="pa-4 mb-4">
      <v-row dense>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="busca"
            label="Buscar por nome ou código"
            prepend-inner-icon="mdi-magnify"
            clearable
            hide-details
          />
        </v-col>
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
        <v-col cols="12" sm="6" md="3">
          <v-select
            v-model="filtros.status"
            :items="STATUS_BEM"
            label="Status"
            clearable
            hint="Inativos só aparecem se escolhidos aqui"
            persistent-hint
          />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="filtros.aquisicaoDe" label="Adquirido de" type="date" />
        </v-col>
        <v-col cols="6" md="3">
          <v-text-field v-model="filtros.aquisicaoAte" label="Adquirido até" type="date" />
        </v-col>
        <v-col cols="12" md="3" class="d-flex align-center">
          <v-btn variant="text" prepend-icon="mdi-filter-remove" @click="limparFiltros">
            Limpar filtros
          </v-btn>
        </v-col>
      </v-row>
    </v-card>

    <v-card>
      <v-data-table
        :headers="headers"
        :items="itens"
        :loading="carregando"
        no-data-text="Nenhum bem encontrado"
      >
        <template #item.categorias="{ item }">
          <v-chip v-for="c in item.categorias" :key="c.id" size="small" class="mr-1">
            {{ c.nome }}
          </v-chip>
        </template>

        <template #item.valorAquisicao="{ item }">
          {{ formatarMoeda(item.valorAquisicao) }}
        </template>

        <template #item.estadoConservacao="{ item }">
          <StatusChip :valor="item.estadoConservacao" :opcoes="ESTADOS" />
        </template>

        <template #item.status="{ item }">
          <StatusChip :valor="item.status" :opcoes="STATUS_BEM" />
        </template>

        <template #item.acoes="{ item }">
          <!-- Todos os perfis -->
          <v-btn
            icon="mdi-eye"
            variant="text"
            title="Ver detalhes"
            :to="{ name: 'bem-detalhe', params: { id: item.id } }"
          />
          <!-- Só administrador -->
          <AcoesLinha v-if="auth.ehAdmin" sem-excluir @editar="abrirEdicao(item)">
            <v-btn
              v-if="item.status !== 'INATIVO'"
              icon="mdi-archive-arrow-down"
              variant="text"
              color="warning"
              title="Inativar"
              @click="pedirInativacao(item)"
            />
          </AcoesLinha>
        </template>
      </v-data-table>
    </v-card>

    <!-- Cadastro e edição -->
    <FormDialog
      v-model="dialogForm"
      :titulo="editandoId ? 'Editar bem' : 'Novo bem'"
      :carregando="salvando"
      :erro="erroGeral"
      :largura-max="720"
      @salvar="salvar"
    >
      <v-row dense>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="form.codigoPatrimonio"
            label="Código de patrimônio"
            :error-messages="errosCampos.codigoPatrimonio"
            autofocus
          />
        </v-col>
        <v-col cols="12" md="8">
          <v-text-field v-model="form.nome" label="Nome" :error-messages="errosCampos.nome" />
        </v-col>

        <v-col cols="12">
          <v-textarea
            v-model="form.descricao"
            label="Descrição"
            rows="2"
            :error-messages="errosCampos.descricao"
          />
        </v-col>

        <v-col cols="12" md="3">
          <v-text-field
            v-model.number="form.quantidade"
            label="Quantidade"
            type="number"
            min="1"
            :error-messages="errosCampos.quantidade"
          />
        </v-col>
        <v-col cols="12" md="4">
          <v-text-field
            v-model="form.valorAquisicao"
            label="Valor de aquisição (R$)"
            type="number"
            min="0"
            step="0.01"
            :error-messages="errosCampos.valorAquisicao"
          />
        </v-col>
        <v-col cols="12" md="5">
          <v-text-field
            v-model="form.dataAquisicao"
            label="Data de aquisição"
            type="date"
            :error-messages="errosCampos.dataAquisicao"
          />
        </v-col>

        <v-col cols="12" :md="editandoId ? 6 : 12">
          <v-select
            v-model="form.estadoConservacao"
            :items="ESTADOS"
            label="Estado de conservação"
            :error-messages="errosCampos.estadoConservacao"
          />
        </v-col>
        <v-col v-if="editandoId" cols="12" md="6">
          <v-select
            v-model="form.status"
            :items="STATUS_BEM"
            label="Status"
            :error-messages="errosCampos.status"
          />
        </v-col>

        <v-col cols="12" md="6">
          <v-autocomplete
            v-model="form.localizacaoId"
            :items="localizacoes"
            item-title="nome"
            item-value="id"
            label="Localização"
            :error-messages="errosCampos.localizacaoId"
          />
        </v-col>
        <v-col cols="12" md="6">
          <v-autocomplete
            v-model="form.categoriaIds"
            :items="categorias"
            item-title="nome"
            item-value="id"
            label="Categorias"
            multiple
            chips
            closable-chips
            :error-messages="errosCampos.categoriaIds"
          />
        </v-col>

        <v-col cols="12">
          <v-text-field
            v-model="form.motivo"
            label="Motivo da alteração (opcional, vai para o histórico)"
            :error-messages="errosCampos.motivo"
          />
        </v-col>
      </v-row>
    </FormDialog>

    <!-- Inativação -->
    <ConfirmDialog
      v-model="dialogInativar"
      titulo="Inativar bem"
      texto-confirmar="Inativar"
      cor="warning"
      :carregando="inativando"
      @confirmar="confirmarInativacao"
    >
      Deseja inativar <strong>{{ alvoInativar?.nome }}</strong>? Ele deixa de aparecer na lista
      padrão, mas o histórico é mantido.
      <v-text-field
        v-model="motivoInativar"
        label="Motivo (opcional)"
        class="mt-4"
        hide-details
      />
    </ConfirmDialog>
  </v-container>
</template>