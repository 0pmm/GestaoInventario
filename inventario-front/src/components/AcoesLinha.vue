<script setup>
defineProps({ semExcluir: Boolean })
defineEmits(['editar', 'excluir'])
</script>

<template>
    <v-btn icon="mdi-pencil-outline" variant="text" size="small" color="secondary" title="Editar" @click="$emit('editar')" />
    <slot />
    <v-btn
        v-if="!semExcluir"
        icon="mdi-trash-can-outline"
        variant="text"
        size="small"
        color="error"
        title="Excluir"
        @click="$emit('excluir')"
    />
</template>
