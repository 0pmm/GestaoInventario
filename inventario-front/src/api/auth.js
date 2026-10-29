import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import http from '../api/http'

export const useAuthStore = defineStore('auth', () => {
  const usuario = ref(null)
  const carregado = ref(false)

  const logado = computed(() => usuario.value !== null)
  const ehAdmin = computed(() => usuario.value?.perfil === 'ADMINISTRADOR')

  async function carregar() {
    try {
      usuario.value = (await http.get('/auth/me')).data
    } catch {
      usuario.value = null
    } finally {
      carregado.value = true
    }
  }

  async function entrar(email, senha) {
    usuario.value = (await http.post('/auth/login', { email, senha })).data
  }

  async function sair() {
    try {
      await http.post('/auth/logout')
    } finally {
      usuario.value = null
    }
  }

  return { usuario, carregado, logado, ehAdmin, carregar, entrar, sair }
})