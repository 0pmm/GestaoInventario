<script setup>
import { computed } from 'vue';

const props = defineProps({
    valor: { type: String, default: null },
    opcoes: { type: Array, required: true },
})

const opcao = computed(() => props.opcoes.find((o) => o.value === props.valor))
</script>

<template>
    <v-chip v-if="opcao" :color="opcao.cor" size="small" variant="tonal" class="font-weight-medium">
        <span class="ponto" :style="{ background: `rgb(var(--v-theme-${opcao.cor}))` }" />
        {{ opcao.title }}
    </v-chip>
</template>

<style scoped>
.ponto { width: 6px; height: 6px; border-radius: 50%; margin-right: 6px; }
</style>
