<template>
  <div class="floating-island p-8 overflow-x-auto">
    <div class="flex items-center justify-between min-w-[800px]">
      <div v-for="(process, index) in localProcesses" :key="process.id"
           class="process-line relative flex flex-col items-center"
           :class="{ 'last-node': index === localProcesses.length - 1 }">
        <div class="w-14 h-14 circle-icon text-white z-10 mb-3 font-bold"
             :class="getNodeClass(process.status)">
          {{ String(index + 1).padStart(2, '0') }}
        </div>
        <span class="text-sm font-medium text-white">{{ process.name }}</span>
        <span class="text-[10px] text-gray-500 mt-1">{{ process.location }}</span>
        <button v-if="editable" @click="removeProcess(index)"
                class="mt-2 text-[10px] text-red-400 hover:text-red-300 opacity-0 hover:opacity-100 transition-opacity">
          移除
        </button>
      </div>
    </div>

    <div v-if="editable" class="mt-6 pt-6 border-t border-white/5 flex gap-3">
      <input v-model="newProcess.name" placeholder="工序名称"
             class="flex-1 px-3 py-2 text-xs bg-black/20 border border-white/10 text-white rounded outline-none focus:ring-1 focus:ring-blue-500">
      <input v-model="newProcess.location" placeholder="工位"
             class="w-32 px-3 py-2 text-xs bg-black/20 border border-white/10 text-white rounded outline-none focus:ring-1 focus:ring-blue-500">
      <button @click="addProcess"
              class="bg-blue-600 hover:bg-blue-500 px-4 py-2 text-xs text-white rounded-sm transition-colors">
        添加
      </button>
    </div>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'

const props = defineProps({
  processes: { type: Array, default: () => [] },
  editable: { type: Boolean, default: true }
})

const emit = defineEmits(['update'])

const localProcesses = ref([...props.processes])
const newProcess = ref({ name: '', location: '', status: 'pending' })

watch(() => props.processes, (val) => {
  localProcesses.value = [...val]
}, { deep: true })

const getNodeClass = (status) => {
  const classes = {
    active: 'bg-blue-500 shadow-lg shadow-blue-500/20',
    pending: 'bg-gray-700 border border-white/10',
    checking: 'bg-amber-500/20 text-amber-500 border border-amber-500/30',
    completed: 'bg-emerald-500/20 text-emerald-500 border border-emerald-500/30'
  }
  return classes[status] || classes.pending
}

const addProcess = () => {
  if (!newProcess.value.name) return
  localProcesses.value.push({ ...newProcess.value, id: Date.now() })
  emit('update', localProcesses.value)
  newProcess.value = { name: '', location: '', status: 'pending' }
}

const removeProcess = (index) => {
  localProcesses.value.splice(index, 1)
  emit('update', localProcesses.value)
}
</script>

<style scoped>
.floating-island {
  background: #2C2C2E;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border-radius: 8px;
}

.circle-icon {
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
}

.process-line {
  position: relative;
}

.process-line::after {
  content: '';
  position: absolute;
  top: 28px;
  left: 100%;
  width: 40px;
  height: 1px;
  background: rgba(255, 255, 255, 0.08);
  z-index: 0;
}

.process-line.last-node::after {
  display: none;
}
</style>
