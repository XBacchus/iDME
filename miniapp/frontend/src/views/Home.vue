<template>
  <div class="space-y-8">
    <!-- 四个模块卡片 -->
    <section class="grid grid-cols-4 gap-6">
      <div v-for="module in modules" :key="module.name"
           class="floating-island p-6 hover:translate-y-[-4px] transition-all cursor-pointer group"
           @click="$router.push(module.path)">
        <div :class="module.color" class="w-10 h-10 circle-icon mb-4 group-hover:scale-110 transition-transform">
          <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24" v-html="module.icon"/>
        </div>
        <h3 class="font-semibold mb-1">{{ module.name }}</h3>
        <p class="text-xs text-gray-500">{{ module.desc }}</p>
      </div>
    </section>

    <!-- 物料库存列表 -->
    <section class="floating-island overflow-hidden">
      <div class="p-4 border-b border-color flex justify-between items-center bg-white/5">
        <h2 class="text-sm font-bold tracking-wider uppercase text-gray-400">物料库存列表</h2>
      </div>
      <table class="w-full text-left text-sm">
        <thead>
          <tr class="text-gray-500 border-b border-color">
            <th class="px-6 py-4 font-medium">编号</th>
            <th class="px-6 py-4 font-medium">物料名称</th>
            <th class="px-6 py-4 font-medium">规格型号</th>
            <th class="px-6 py-4 font-medium">库存</th>
            <th class="px-6 py-4 font-medium">供应商</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-white/5">
          <tr v-for="item in materials" :key="item.id" class="hover:bg-white/5 transition-colors">
            <td class="px-6 py-4 font-mono text-xs text-blue-400">{{ item.code }}</td>
            <td class="px-6 py-4">{{ item.name }}</td>
            <td class="px-6 py-4 text-gray-400">{{ item.spec }}</td>
            <td class="px-6 py-4">
              <span :class="item.stockClass" class="px-2 py-0.5 text-xs rounded">{{ item.stock }}</span>
            </td>
            <td class="px-6 py-4 text-gray-400">{{ item.supplier }}</td>
          </tr>
        </tbody>
      </table>
    </section>

    <!-- 工艺路线可视化 -->
    <section class="space-y-4">
      <h2 class="text-lg font-bold">工艺路线可视化：中心轮零件加工</h2>
      <div class="floating-island p-8 overflow-x-auto">
        <div class="flex items-center justify-between min-w-[800px]">
          <div v-for="(step, idx) in processSteps" :key="idx" :class="['process-line relative flex flex-col items-center', idx === processSteps.length - 1 ? '' : 'after:content-[\'\'] after:absolute after:top-7 after:left-full after:w-10 after:h-[1px] after:bg-white/10']">
            <div :class="step.circleClass" class="w-14 h-14 circle-icon z-10 mb-3 font-bold">{{ step.num }}</div>
            <span class="text-sm font-medium">{{ step.name }}</span>
            <span class="text-[10px] text-gray-500 mt-1">{{ step.location }}</span>
          </div>
        </div>
      </div>
    </section>

    <!-- 工序明细配置 -->
    <section class="space-y-4 pb-20">
      <h2 class="text-sm font-bold uppercase tracking-widest text-gray-500">工序明细配置</h2>
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
        <div v-for="op in operations" :key="op.id" :class="['floating-island p-5 relative border-l-4', op.borderClass]">
          <div class="absolute top-4 right-4 text-4xl font-black opacity-5">{{ op.num }}</div>
          <div class="flex justify-between mb-4">
            <span :class="op.statusClass" class="text-xs px-2 py-0.5 rounded">{{ op.status }}</span>
            <span class="text-[10px] text-gray-500">{{ op.code }}</span>
          </div>
          <h4 class="font-bold mb-2">{{ op.title }}</h4>
          <p class="text-[11px] text-gray-400 mb-4 leading-relaxed">{{ op.desc }}</p>
          <div class="flex items-center justify-between text-[10px] pt-4 border-t border-white/5">
            <div class="flex items-center gap-2">
              <div :class="op.avatarClass" class="w-5 h-5 rounded-full flex items-center justify-center text-[8px]">{{ op.avatar }}</div>
              <span>负责人：{{ op.owner }}</span>
            </div>
            <span class="text-gray-500">{{ op.time }}</span>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { onMounted, ref } from 'vue'
import { ElMessage } from 'element-plus'
import { getPartList } from '@/api/parts'
import { getProcedures } from '@/api/procedures'

const modules = [
  { name: 'Parts', desc: 'Inventory and BOM traceability', path: '/parts', color: 'bg-blue-500/10 text-blue-400', icon: '<path d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10l-8 4m0-10L4 7m8 4v10M4 7v10l8 4"/>' },
  { name: 'Equipment', desc: 'Equipment ledger and maintenance', path: '/equipments', color: 'bg-emerald-500/10 text-emerald-400', icon: '<path d="M9 3v2m6-2v2M9 19v2m6-2v2M5 9H3m2 6H3m18-6h-2m2 6h-2M7 19h10a2 2 0 002-2V7a2 2 0 00-2-2H7a2 2 0 00-2 2v10a2 2 0 002 2zM9 9h6v6H9V9z"/>' },
  { name: 'Plans', desc: 'Process planning and visualization', path: '/working-plans', color: 'bg-amber-500/10 text-amber-400', icon: '<path d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2"/>' },
  { name: 'Procedures', desc: 'Procedure setup and dispatch', path: '/procedures', color: 'bg-purple-500/10 text-purple-400', icon: '<path d="M11 4a2 2 0 114 0v1a1 1 0 001 1h3a1 1 0 011 1v3a1 1 0 01-1 1h-1a2 2 0 100 4h1a1 1 0 011 1v3a1 1 0 01-1 1h-3a1 1 0 01-1-1v-1a2 2 0 10-4 0v1a1 1 0 01-1 1H7a1 1 0 01-1-1v-3a1 1 0 011-1h1a2 2 0 100-4H7a1 1 0 01-1-1V7a1 1 0 011-1h3a1 1 0 001-1V4z"/>' }
]

const materials = ref([])
const processSteps = ref([])
const operations = ref([])

const getStockClass = (qty) => {
  if (qty > 100) return 'bg-emerald-500/20 text-emerald-400'
  if (qty > 20) return 'bg-amber-500/20 text-amber-400'
  return 'bg-red-500/20 text-red-400'
}

const mapProcedure = (item, idx) => {
  const order = Number.isFinite(Number(item.order)) ? Number(item.order) : idx + 1
  const status = Number.isInteger(item.status) ? item.status : 0
  return {
    id: item.id ?? order,
    num: String(order).padStart(2, '0'),
    code: item.procedureCode || item.code || '',
    title: item.procedureName || item.name || '',
    desc: item.description || '',
    status,
    statusText: item.statusText || (status === 1 ? 'In Progress' : 'Pending'),
    owner: item.operatorName || 'Unassigned',
    avatar: (item.operator || '--').slice(0, 2).toUpperCase(),
    avatarClass: 'bg-blue-500',
    time: item.time || '--',
    location: item.location || '--'
  }
}

const getStatusClass = (status) => {
  if (status === 1) return 'bg-emerald-500/10 text-emerald-400'
  if (status === 2) return 'bg-blue-500/10 text-blue-400'
  return 'bg-gray-500/10 text-gray-400'
}

const getBorderClass = (status) => {
  if (status === 1) return 'border-emerald-500'
  if (status === 2) return 'border-blue-500'
  return 'border-gray-600'
}

const getCircleClass = (status, idx) => {
  if (status === 1 || idx === 0) return 'bg-blue-500 text-white shadow-lg shadow-blue-500/20'
  if (status === 2) return 'bg-emerald-500/20 text-emerald-500 border border-emerald-500/30'
  return 'bg-gray-700 text-white border border-white/10'
}

const loadDashboardData = async () => {
  try {
    const [partsRes, proceduresRes] = await Promise.all([
      getPartList({ page: 1, size: 5 }),
      getProcedures()
    ])

    const partRecords = partsRes?.data?.records || []
    materials.value = partRecords.map((item) => ({
      id: item.id,
      code: item.partNo || '--',
      name: item.partName || '--',
      spec: item.specification || '--',
      stock: String(item.stockQty ?? 0),
      stockClass: getStockClass(Number(item.stockQty ?? 0)),
      supplier: item.supplier || '--'
    }))

    const procedureRows = Array.isArray(proceduresRes?.data) ? proceduresRes.data : []
    const mapped = procedureRows.map(mapProcedure).sort((a, b) => Number(a.num) - Number(b.num))

    processSteps.value = mapped.slice(0, 5).map((item, idx) => ({
      num: item.num,
      name: item.title || '--',
      location: item.location,
      circleClass: getCircleClass(item.status, idx)
    }))

    operations.value = mapped.slice(0, 6).map((item) => ({
      id: item.id,
      num: item.num,
      status: item.statusText,
      statusClass: getStatusClass(item.status),
      code: item.code,
      title: item.title,
      desc: item.desc,
      owner: item.owner,
      avatar: item.avatar,
      avatarClass: item.avatarClass,
      time: item.time,
      borderClass: getBorderClass(item.status)
    }))
  } catch {
    materials.value = []
    processSteps.value = []
    operations.value = []
    ElMessage.error('Failed to load dashboard data')
  }
}

onMounted(loadDashboardData)
</script>
