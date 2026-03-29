<template>
  <div class="app-shell" :class="{ 'theme-bright': isBrightTheme }">
    <Sidebar :is-bright-theme="isBrightTheme" @toggle-bright-theme="toggleBrightTheme" />
    <main class="app-main">
      <Header :is-bright-theme="isBrightTheme" />
      <div class="app-content custom-scrollbar">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue'
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'

const THEME_STORAGE_KEY = 'idme-shell-theme'
const isBrightTheme = ref(false)

onMounted(() => {
  const storedTheme = window.localStorage.getItem(THEME_STORAGE_KEY)
  isBrightTheme.value = storedTheme ? storedTheme === 'bright' : true
})

watch(isBrightTheme, (value) => {
  window.localStorage.setItem(THEME_STORAGE_KEY, value ? 'bright' : 'dark')
})

const toggleBrightTheme = () => {
  isBrightTheme.value = !isBrightTheme.value
}
</script>
