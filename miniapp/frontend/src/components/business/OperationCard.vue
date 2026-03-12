<template>
  <FloatingCard :custom-class="`p-5 relative border-l-4 ${borderColor}`">
    <div class="absolute top-4 right-4 text-4xl font-black opacity-5">{{ procedure.id.toString().padStart(2, '0') }}</div>
    <div class="flex justify-between mb-4">
      <span :class="`text-xs px-2 py-0.5 rounded ${statusClass}`">{{ procedure.statusText }}</span>
      <span class="text-[10px] text-gray-500">{{ procedure.code }}</span>
    </div>
    <h4 class="font-bold mb-2">{{ procedure.name }}</h4>
    <p class="text-[11px] text-gray-400 mb-4 leading-relaxed">{{ procedure.description }}</p>
    <div class="flex items-center justify-between text-[10px] pt-4 border-t border-white/5">
      <div class="flex items-center gap-2">
        <div class="w-5 h-5 rounded-full bg-blue-500 flex items-center justify-center text-[8px]">{{ procedure.operator }}</div>
        <span>负责人：{{ procedure.operatorName }}</span>
      </div>
      <span class="text-gray-500">{{ procedure.time }}</span>
    </div>
  </FloatingCard>
</template>

<script setup>
import { computed } from 'vue'
import FloatingCard from '../common/FloatingCard.vue'

const props = defineProps({
  procedure: { type: Object, required: true }
})

const borderColor = computed(() => {
  const colors = { 1: 'border-emerald-500', 0: 'border-gray-600', 2: 'border-blue-500' }
  return colors[props.procedure.status] || 'border-gray-600'
})

const statusClass = computed(() => {
  const classes = { 1: 'bg-emerald-500/10 text-emerald-400', 0: 'bg-gray-500/10 text-gray-400', 2: 'bg-blue-500/10 text-blue-400' }
  return classes[props.procedure.status] || 'bg-gray-500/10 text-gray-400'
})
</script>
