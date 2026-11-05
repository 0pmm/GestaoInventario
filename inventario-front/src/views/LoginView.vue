<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { mensagemDeErro } from '../utils/erros'

const auth = useAuthStore()
const router = useRouter()

const email = ref('')
const senha = ref('')
const mostrarSenha = ref(false)
const carregando = ref(false)
const erro = ref('')

async function entrar() {
  carregando.value = true
  erro.value = ''
  try {
    await auth.entrar(email.value, senha.value)
    router.push({ name: 'home' })
  } catch (e) {
    erro.value = mensagemDeErro(e)
  } finally {
    carregando.value = false
  }
}

const destaques = [
  { icone: 'mdi-package-variant-closed', texto: 'Cadastro e consulta de bens' },
  { icone: 'mdi-history', texto: 'Histórico completo de movimentações' },
  { icone: 'mdi-chart-box-outline', texto: 'Estatísticas por categoria e local' },
]
</script>

<template>
  <div class="login">
    <aside class="login-lado">
      <div class="marca">
        <div class="marca-icone"><v-icon icon="mdi-warehouse" size="22" /></div>
        <span>Inventário</span>
      </div>
      <div>
        <h2 class="login-titulo">Todo o patrimônio,<br />num só lugar.</h2>
        <ul class="destaques">
          <li v-for="d in destaques" :key="d.texto"><v-icon :icon="d.icone" size="20" />{{ d.texto }}</li>
        </ul>
      </div>
      <small>Sistema de Gestão de Inventário</small>
    </aside>

    <main class="login-form">
      <div class="caixa">
        <h1 class="text-h5 font-weight-bold">Entrar</h1>
        <p class="texto-suave text-body-2 mt-1 mb-6">Use seu e-mail e senha para acessar.</p>

        <v-alert v-if="erro" type="error" variant="tonal" density="compact" class="mb-4">{{ erro }}</v-alert>

        <v-form @submit.prevent="entrar">
          <v-text-field v-model="email" label="E-mail" type="email" prepend-inner-icon="mdi-email-outline" autofocus />
          <v-text-field
            v-model="senha"
            label="Senha"
            :type="mostrarSenha ? 'text' : 'password'"
            prepend-inner-icon="mdi-lock-outline"
            :append-inner-icon="mostrarSenha ? 'mdi-eye-off-outline' : 'mdi-eye-outline'"
            @click:append-inner="mostrarSenha = !mostrarSenha"
          />
          <v-btn type="submit" block size="large" :loading="carregando" class="mt-2">Entrar</v-btn>
        </v-form>
      </div>
    </main>
  </div>
</template>

<style scoped>
.login { display: grid; grid-template-columns: minmax(320px, 5fr) 6fr; min-height: 100vh; }
.login-lado {
  background: linear-gradient(160deg, #0B1F1E 0%, #0F3B37 100%);
  color: #fff; padding: 40px 48px; display: flex; flex-direction: column; justify-content: space-between;
}
.marca { display: flex; align-items: center; gap: 12px; font-weight: 700; font-size: 1.1rem; }
.marca-icone { width: 40px; height: 40px; border-radius: 12px; display: grid; place-items: center; background: #14B8A6; color: #04201E; }
.login-titulo { font-size: 2.2rem; line-height: 1.15; font-weight: 700; letter-spacing: -0.03em; margin-bottom: 28px; }
.destaques { list-style: none; padding: 0; display: grid; gap: 14px; color: #B6D3CF; }
.destaques li { display: flex; align-items: center; gap: 12px; }
.destaques .v-icon { color: #5EEAD4; }
small { color: #6B908C; }
.login-form { display: grid; place-items: center; padding: 32px; background: #fff; }
.caixa { width: 100%; max-width: 380px; }
@media (max-width: 800px) { .login { grid-template-columns: 1fr; } .login-lado { display: none; } }
</style>
