<script setup>
defineProps({
    titulo: { type: String, required: true },
    carregando: Boolean,
    textoConfirmar: { type: String, default: 'Excluir' },
    cor: { type: String, default: 'error' },
})
defineEmits(['confirmar'])

const aberto = defineModel({ default: false })
</script>

<template>
    <v-dialog v-model="aberto" max-width="440">
        <v-card class="pa-2">
            <v-card-title class="text-h6 font-weight-bold pt-4">{{ titulo }}</v-card-title>
            <v-card-text class="texto-suave"><slot /></v-card-text>
            <v-card-actions class="px-4 pb-4">
                <v-spacer />
                <v-btn variant="text" color="secondary" @click="aberto = false">Cancelar</v-btn>
                <v-btn :color="cor" :loading="carregando" @click="$emit('confirmar')">
                    {{ textoConfirmar }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>
