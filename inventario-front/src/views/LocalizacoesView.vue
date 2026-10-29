<script setup>
import { localizacoesApi } from '../api/localizacoes'
import { useCrud } from '../composables/useCrud'
import PageHeader from '../components/PageHeader.vue'
import FormDialog from '../components/FormDialog.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import AcoesLinha from '../components/AcoesLinha.vue'

const headers = [
  { title: 'Nome', key: 'nome' },
  { title: 'Descrição', key: 'descricao' },
  { title: 'Ações', key: 'acoes', sortable: false, align: 'end' },
]

const {
  itens, carregando, busca,
  dialogForm, salvando, editandoId, form, errosCampos, erroGeral, abrirNovo, abrirEdicao, salvar,
  dialogExcluir, alvoExclusao, excluindo, pedirExclusao, confirmarExclusao,
} = useCrud({
  api: localizacoesApi,
  vazio: { nome: '', descricao: '' },
  rotulo: 'Localização',
})
</script>

<template>
  <v-container>
    <PageHeader titulo="Localizações">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNovo">Nova localização</v-btn>
    </PageHeader>

    <v-text-field
      v-model="busca"
      label="Buscar por nome"
      prepend-inner-icon="mdi-magnify"
      clearable
      hide-details
      class="mb-4"
    />

    <v-card>
      <v-data-table
        :headers="headers"
        :items="itens"
        :loading="carregando"
        no-data-text="Nenhuma localização encontrada"
      >
        <template #item.acoes="{ item }">
          <AcoesLinha @editar="abrirEdicao(item)" @excluir="pedirExclusao(item)" />
        </template>
      </v-data-table>
    </v-card>

    <FormDialog
      v-model="dialogForm"
      :titulo="editandoId ? 'Editar localização' : 'Nova localização'"
      :carregando="salvando"
      :erro="erroGeral"
      @salvar="salvar"
    >
      <v-text-field v-model="form.nome" label="Nome" :error-messages="errosCampos.nome" autofocus />
      <v-textarea
        v-model="form.descricao"
        label="Descrição"
        rows="3"
        :error-messages="errosCampos.descricao"
      />
    </FormDialog>

    <ConfirmDialog
      v-model="dialogExcluir"
      titulo="Excluir localização"
      :carregando="excluindo"
      @confirmar="confirmarExclusao"
    >
      Deseja excluir a localização <strong>{{ alvoExclusao?.nome }}</strong>?
      Localizações com bens ou que constam no histórico não podem ser excluídas.
    </ConfirmDialog>
  </v-container>
</template>