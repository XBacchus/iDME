<template>
  <div class="space-y-6">
    <div class="flex items-center justify-between">
      <h2 class="text-sm font-bold uppercase tracking-widest text-gray-500">工序明细配置</h2>
    </div>
    <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
      <OperationCard v-for="proc in procedures" :key="proc.id" :procedure="proc" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import OperationCard from '../../components/business/OperationCard.vue'

const procedures = ref([
  { id: 1, code: 'OP-2023-A1', name: '毛坯制造', description: '对铝合金压铸件进行清理、去毛刺及固溶时效处理，确保内应力消除。', status: 1, statusText: '进行中', operator: 'WL', operatorName: '王力', time: '2023-10-24 08:00' },
  { id: 2, code: 'OP-2023-A2', name: '粗加工', description: '多轴加工中心对主要配合面进行粗铣，预留 0.5mm 余量用于精修。', status: 0, statusText: '待开始', operator: 'ZS', operatorName: '张顺', time: '预计 10-25' },
  { id: 3, code: 'OP-2023-A3', name: '精加工', description: '内外圆磨床精密加工，配合 CMM 实时反馈补偿，精度等级 IT6。', status: 0, statusText: '待开始', operator: 'LC', operatorName: '李晨', time: '预计 10-27' },
  { id: 4, code: 'OP-2023-A4', name: '检测', description: '使用三坐标测量仪进行全尺寸检测，确保符合图纸要求。', status: 0, statusText: '待开始', operator: 'ZM', operatorName: '赵敏', time: '预计 10-28' },
  { id: 5, code: 'OP-2023-A5', name: '入库', description: '质检合格后进行包装、标识，入库登记并更新库存系统。', status: 0, statusText: '待开始', operator: 'WF', operatorName: '王芳', time: '预计 10-29' }
])
</script>
