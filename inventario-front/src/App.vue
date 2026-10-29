<script setup>
import AppSnackbar from './components/AppSnackbar.vue'
</script>

<template>
  <v-app>
    <router-view />
    <AppSnackbar />
  </v-app>
</template>