<script setup>
import { ref } from 'vue'
import { useRouter } from 'vue-router'
import { useAuthStore } from '../stores/auth'
import { mensagemDeErro } from '../utils/erros'

const auth = useAuthStore()
const router = useRouter()

const email = ref('')
const senha = ref('')
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
</script>

<template>
  <v-container class="fill-height" fluid>
    <v-row justify="center" align="center">
      <v-col cols="12" sm="8" md="4">
        <v-card class="pa-4">
          <v-card-title class="text-h5">Gestão de Inventário</v-card-title>
          <v-card-text>
            <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>

            <v-form @submit.prevent="entrar">
              <v-text-field v-model="email" label="E-mail" type="email" autofocus />
              <v-text-field v-model="senha" label="Senha" type="password" />
              <v-btn type="submit" color="primary" block size="large" :loading="carregando">
                Entrar
              </v-btn>
            </v-form>
          </v-card-text>
        </v-card>
      </v-col>
    </v-row>
  </v-container>
</template>