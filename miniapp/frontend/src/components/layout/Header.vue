<template>
  <header class="h-14 flex items-center justify-between px-8 border-b border-color">
    <nav class="flex text-sm text-gray-400 gap-2 items-center">
      <span class="text-white">{{ title }}</span>
    </nav>
    <div class="flex items-center gap-4">
      <div class="relative">
        <input type="text" placeholder="搜索..." class="w-64 pl-9 pr-4 py-1.5 text-xs outline-none transition-all focus:ring-1 focus:ring-blue-500 rounded">
        <svg class="w-4 h-4 absolute left-3 top-2 text-gray-500" fill="none" stroke="currentColor" viewBox="0 0 24 24"><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"/></svg>
      </div>
    </div>
  </header>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()
const title = computed(() => {
  const titles = {
    'Home': '首页控制台',
    'Parts': '物料管理',
    'PartAdd': '新增物料',
    'PartEdit': '编辑物料',
    'PartBOM': 'BOM管理',
    'PartVersions': '版本管理',
    'PartCategories': '分类管理',
    'Equipments': '设备管理',
    'EquipmentAdd': '新增设备',
    'EquipmentEdit': '编辑设备',
    'WorkingPlans': '工艺路线管理',
    'WorkingPlanAdd': '新增工艺路线',
    'WorkingPlanEdit': '编辑工艺路线',
    'WorkingPlanDetail': '工艺路线详情',
    'Procedures': '工序配置'
  }
  return titles[route.name] || '首页'
})
</script>
