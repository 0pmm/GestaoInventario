<script setup>
import { categoriasApi } from '../api/categorias'
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
  api: categoriasApi,
  vazio: { nome: '', descricao: '' },
  rotulo: 'Categoria',
})
</script>

<template>
  <v-container>
    <PageHeader titulo="Categorias">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNovo">Nova categoria</v-btn>
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
        no-data-text="Nenhuma categoria encontrada"
      >
        <template #item.acoes="{ item }">
          <AcoesLinha @editar="abrirEdicao(item)" @excluir="pedirExclusao(item)" />
        </template>
      </v-data-table>
    </v-card>

    <FormDialog
      v-model="dialogForm"
      :titulo="editandoId ? 'Editar categoria' : 'Nova categoria'"
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
      titulo="Excluir categoria"
      :carregando="excluindo"
      @confirmar="confirmarExclusao"
    >
      Deseja excluir a categoria <strong>{{ alvoExclusao?.nome }}</strong>?
      Categorias com bens vinculados não podem ser excluídas.
    </ConfirmDialog>
  </v-container>
</template>