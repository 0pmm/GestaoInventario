<script setup>
import { usuariosApi } from '../api/usuarios'
import { useAuthStore } from '../stores/auth'
import { useCrud } from '../composables/useCrud'
import { useNotificacao } from '../composables/useNotificacao'
import { mensagemDeErro } from '../utils/erros'
import { PERFIS } from '../utils/enums'
import PageHeader from '../components/PageHeader.vue'
import FormDialog from '../components/FormDialog.vue'
import ConfirmDialog from '../components/ConfirmDialog.vue'
import AcoesLinha from '../components/AcoesLinha.vue'
import StatusChip from '../components/StatusChip.vue'

const auth = useAuthStore()
const { avisar } = useNotificacao()

const headers = [
  { title: 'Nome', key: 'nome' },
  { title: 'E-mail', key: 'email' },
  { title: 'Perfil', key: 'perfil' },
  { title: 'Situação', key: 'ativo' },
  { title: 'Ações', key: 'acoes', sortable: false, align: 'end' },
]

const {
  itens, carregando, busca, carregar,
  dialogForm, salvando, editandoId, form, errosCampos, erroGeral, abrirNovo, abrirEdicao, salvar,
  dialogExcluir, alvoExclusao, excluindo, pedirExclusao, confirmarExclusao,
} = useCrud({
  api: usuariosApi,
  vazio: { nome: '', email: '', senha: '', perfil: 'COMUM' },
  rotulo: 'Usuário',
})

const ehVoceMesmo = (usuario) => usuario.id === auth.usuario?.id

async function alternarSituacao(usuario) {
  try {
    if (usuario.ativo) {
      await usuariosApi.inativar(usuario.id)
      avisar('Usuário inativado')
    } else {
      await usuariosApi.reativar(usuario.id)
      avisar('Usuário reativado')
    }
    await carregar()
  } catch (e) {
    avisar(mensagemDeErro(e), 'error')   // ex.: 409 "precisa ter ao menos um administrador"
  }
}
</script>

<template>
  <v-container>
    <PageHeader titulo="Usuários">
      <v-btn color="primary" prepend-icon="mdi-plus" @click="abrirNovo">Novo usuário</v-btn>
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
        no-data-text="Nenhum usuário encontrado"
      >
        <template #item.perfil="{ item }">
          <StatusChip :valor="item.perfil" :opcoes="PERFIS" />
        </template>

        <template #item.ativo="{ item }">
          <v-chip :color="item.ativo ? 'success' : 'grey'" size="small" variant="tonal">
            {{ item.ativo ? 'Ativo' : 'Inativo' }}
          </v-chip>
        </template>

        <template #item.acoes="{ item }">
          <AcoesLinha
            :sem-excluir="ehVoceMesmo(item)"
            @editar="abrirEdicao(item)"
            @excluir="pedirExclusao(item)"
          >
            <v-btn
              :icon="item.ativo ? 'mdi-account-off' : 'mdi-account-check'"
              :title="item.ativo ? 'Inativar' : 'Reativar'"
              :disabled="ehVoceMesmo(item)"
              variant="text"
              color="warning"
              @click="alternarSituacao(item)"
            />
          </AcoesLinha>
        </template>
      </v-data-table>
    </v-card>

    <FormDialog
      v-model="dialogForm"
      :titulo="editandoId ? 'Editar usuário' : 'Novo usuário'"
      :carregando="salvando"
      :erro="erroGeral"
      @salvar="salvar"
    >
      <v-text-field v-model="form.nome" label="Nome" :error-messages="errosCampos.nome" autofocus />
      <v-text-field
        v-model="form.email"
        label="E-mail"
        type="email"
        :error-messages="errosCampos.email"
      />
      <v-text-field
        v-model="form.senha"
        :label="editandoId ? 'Nova senha (vazio mantém a atual)' : 'Senha'"
        type="password"
        autocomplete="new-password"
        :error-messages="errosCampos.senha"
      />
      <v-select
        v-model="form.perfil"
        :items="PERFIS"
        label="Perfil"
        :error-messages="errosCampos.perfil"
      />
    </FormDialog>

    <ConfirmDialog
      v-model="dialogExcluir"
      titulo="Excluir usuário"
      :carregando="excluindo"
      @confirmar="confirmarExclusao"
    >
      Deseja excluir <strong>{{ alvoExclusao?.nome }}</strong>?
      Usuários com ações no histórico ou com observações só podem ser inativados.
    </ConfirmDialog>
  </v-container>
</template>