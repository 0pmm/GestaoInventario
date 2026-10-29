<script setup>
defineProps({
    titulo: {type: String, required: true},
    carregando: Boolean,
    erro: { type: String, default: ''},
    larguraMax: { type: [String, Number], default: 500},
})
defineEmits(['salvar'])

const aberto = defineModel({ default: false })
</script>

<template>
    <v-dialog v-model="aberto" :max-width="larguraMax" persistent>
        <v-card :title="titulo">
            <v-form @submit.prevent="$emit('salvar')">
                <v-card-text>
                    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>
                    <slot />
                </v-card-text>
                <v-card-actions>
                    <v-spacer />
                    <v-btn @click="aberto = false">Cancelar</v-btn>
                    <v-btn type="submit" color="primary" variant="flat" :loading="carregando">Salvar</v-btn>
                </v-card-actions>
            </v-form>
        </v-card>
    </v-dialog>
</template>