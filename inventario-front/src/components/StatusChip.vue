<script setup>
import { computed } from 'vue';

const props = defineProps({
    valor: { type: String, default: null},
    opcoes: { type: Array, required: true },
})

const opcao = computed(() => props.opcoes.find((o) => o.value === props.valor))
</script>

<template>
    <v-chip v-if="opcao" :color="opcao.cor" size="small" variant="tonal"> {{ opcao.title }}</v-chip>
</template>