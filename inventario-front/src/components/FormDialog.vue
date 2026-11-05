<script setup>
defineProps({
    titulo: { type: String, required: true },
    carregando: Boolean,
    erro: { type: String, default: '' },
    larguraMax: { type: [String, Number], default: 500 },
})
defineEmits(['salvar'])

const aberto = defineModel({ default: false })
</script>

<template>
    <v-dialog v-model="aberto" :max-width="larguraMax" persistent>
        <v-card>
            <v-form @submit.prevent="$emit('salvar')">
                <div class="d-flex align-center px-6 pt-5 pb-2">
                    <h2 class="text-h6 font-weight-bold">{{ titulo }}</h2>
                    <v-spacer />
                    <v-btn icon="mdi-close" variant="text" size="small" color="secondary" @click="aberto = false" />
                </div>
                <v-card-text class="pt-4">
                    <v-alert v-if="erro" type="error" variant="tonal" class="mb-4">{{ erro }}</v-alert>
                    <slot />
                </v-card-text>
                <v-divider />
                <v-card-actions class="px-6 py-4">
                    <v-spacer />
                    <v-btn variant="text" color="secondary" @click="aberto = false">Cancelar</v-btn>
                    <v-btn type="submit" :loading="carregando">Salvar</v-btn>
                </v-card-actions>
            </v-form>
        </v-card>
    </v-dialog>
</template>
