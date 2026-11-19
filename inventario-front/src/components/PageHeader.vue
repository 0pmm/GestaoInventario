<script setup>
defineProps({
    titulo: { type: String, required: true },
    subtitulo: { type: String, default: '' },
})
</script>

<template>
    <div class="d-flex align-center flex-wrap ga-3 mb-6">
        <div>
            <h1 class="text-h5 font-weight-bold">{{ titulo }}</h1>
            <p v-if="subtitulo" class="texto-suave text-body-2 mt-1">{{ subtitulo }}</p>
        </div>
        <v-spacer />
        <slot />
    </div>
</template>
