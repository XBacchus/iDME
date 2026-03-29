<template>
  <header class="header" :class="{ 'is-bright-theme': isBrightTheme }">
    <div class="breadcrumb">
      <span>{{ pageMeta.section }}</span>
      <b>{{ pageMeta.title }}</b>
    </div>

    <div class="header-tools">
      <div class="status">
        <span class="status-dot">●</span>
        <span>系统联机</span>
      </div>

      <input
        v-model="keyword"
        type="text"
        class="search-input"
        :placeholder="pageMeta.searchPlaceholder"
      >
    </div>
  </header>
</template>

<script setup>
import { computed, ref, watch } from 'vue'
import { useRoute } from 'vue-router'

defineProps({
  isBrightTheme: {
    type: Boolean,
    default: false
  }
})

const route = useRoute()
const keyword = ref('')

const pageMeta = computed(() => ({
  section: `${(route.meta.section || 'WORKSPACE').toString().toUpperCase()} /`,
  title: route.meta.title || 'Control Center',
  searchPlaceholder: route.meta.searchPlaceholder || '搜索功能...'
}))

watch(() => route.fullPath, () => {
  keyword.value = ''
})
</script>

<style scoped>
.header {
  height: 64px;
  background: rgba(10, 12, 16, 0.92);
  border-bottom: 1px solid #262c3a;
  display: flex;
  align-items: center;
  justify-content: space-between;
  gap: 24px;
  padding: 0 32px;
}

.header.is-bright-theme {
  background: #ffffff;
  border-bottom-color: #e2e8f0;
}

.breadcrumb {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 14px;
  color: #94a3b8;
}

.header.is-bright-theme .breadcrumb {
  color: #475569;
}

.breadcrumb b {
  color: #f8fafc;
  font-weight: 700;
}

.header.is-bright-theme .breadcrumb b {
  color: #0f172a;
}

.header-tools {
  display: flex;
  align-items: center;
  gap: 20px;
}

.status {
  display: flex;
  align-items: center;
  gap: 6px;
  font-size: 12px;
  font-weight: 700;
  color: #f43f5e;
}

.status-dot {
  line-height: 1;
}

.search-input {
  width: 240px;
  padding: 8px 16px;
  border: 1px solid #262c3a;
  border-radius: 8px;
  background: #11141a;
  color: #f8fafc;
  font-size: 13px;
  outline: none;
  transition: border-color 0.2s ease, box-shadow 0.2s ease;
}

.header.is-bright-theme .search-input {
  border-color: #e2e8f0;
  background: #ffffff;
  color: #0f172a;
}

.search-input:focus {
  border-color: #f43f5e;
  box-shadow: 0 0 0 3px rgba(244, 63, 94, 0.12);
}

.search-input::placeholder {
  color: #9ca3af;
}

@media (max-width: 768px) {
  .header {
    height: auto;
    padding: 16px;
    flex-direction: column;
    align-items: stretch;
  }

  .header-tools {
    flex-direction: column;
    align-items: stretch;
  }

  .search-input {
    width: 100%;
  }
}
</style>
