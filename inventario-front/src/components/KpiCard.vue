<script setup>
defineProps({
    rotulo: { type: String, required: true },
    valor: { type: [String, Number], required: true },
    icone: { type: String, default: '' },
    cor: { type: String, default: 'primary' },
    nota: { type: String, default: '' },
})
</script>

<template>
    <v-card class="pa-5 h-100">
        <div class="d-flex align-start">
            <div>
                <div class="rotulo">{{ rotulo }}</div>
                <div class="text-h5 font-weight-bold numero mt-1">{{ valor }}</div>
            </div>
            <v-spacer />
            <v-avatar v-if="icone" :color="cor" variant="tonal" rounded="lg" size="40">
                <v-icon :icon="icone" />
            </v-avatar>
        </div>
        <div v-if="nota" class="text-caption text-warning mt-2">{{ nota }}</div>
    </v-card>
</template>
