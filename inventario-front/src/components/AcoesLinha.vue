<script setup>
defineProps({ semExcluir: Boolean })
defineEmits(['editar', 'excluir'])
</script>

<template>
    <v-btn icon="mdi-pencil" variant="text" title="Editar" @click="$emit('editar')" />
    <v-btn
        v-if="!semExcluir"
        icon="mdi-delete"
        variant="text"
        color="error"
        title="Excluir"
        @click="$emit('excluir')"
    />
</template>