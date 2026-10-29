<script setup>
defineProps({ titulo: { type: String, required: true }})
</script>

<template>
    <div class="d-flex align-center mb-4">
        <h1 class="text-h5">{{ titulo }}</h1>
        <v-spacer />
        <slot />
    </div>
</template>