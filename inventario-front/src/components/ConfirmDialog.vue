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
    <v-dialog v-model="aberto" max-width="420">
        <v-card :title="titulo">
            <v-card-text><slot /></v-card-text>
            <v-card-actions>
                <v-spacer />
                <v-btn @click="aberto = false">Cancelar</v-btn>
                <v-btn :color="cor" variant="flat" :loading="carregando" @click="$emit('confirmar')">
                    {{ textoConfirmar }}
                </v-btn>
            </v-card-actions>
        </v-card>
    </v-dialog>
</template>