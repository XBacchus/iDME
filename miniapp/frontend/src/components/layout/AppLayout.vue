<template>
  <div class="h-screen flex overflow-hidden">
    <Sidebar />
    <main class="flex-1 flex flex-col min-w-0 overflow-hidden">
      <Header />
      <div class="flex-1 overflow-y-auto p-8 custom-scrollbar bg-[#1c1c1e]">
        <router-view />
      </div>
    </main>
  </div>
</template>

<script setup>
import Sidebar from './Sidebar.vue'
import Header from './Header.vue'
</script>
