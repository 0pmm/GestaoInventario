<script setup>
import { useNotificacao } from '@/composables/useNotificacao';
const { estado } = useNotificacao()
</script>

<template>
    <v-snackbar v-model="estado.aberto" :color="estado.cor" timeout="3500">
        {{  estado.texto  }}
    </v-snackbar>
</template>