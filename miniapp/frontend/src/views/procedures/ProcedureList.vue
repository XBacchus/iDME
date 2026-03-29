<template>
  <div class="procedure-page space-y-8">
    <section class="flex flex-col gap-6 xl:flex-row xl:items-end xl:justify-between">
      <div>
        <p class="eyebrow">Manufacturing / Procedure Config</p>
        <h1 class="page-heading">工序配置</h1>
        <p class="page-subtitle">
          按控制台的卡片逻辑展示工序状态、责任人和排程时间，保证明亮模式下的状态色和信息层级统一。
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-2 text-xs">
        <span class="data-pill neutral-pill">流程排程</span>
        <span class="data-pill accent-pill">进度跟踪</span>
        <span class="data-pill success-pill">责任到人</span>
      </div>
    </section>

    <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      <article v-for="card in summaryCards" :key="card.label" class="metric-tile">
        <p class="metric-label">{{ card.label }}</p>
        <p class="metric-value">{{ card.value }}</p>
        <p class="metric-note">{{ card.note }}</p>
      </article>
    </section>

    <section class="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
      <OperationCard v-for="proc in procedures" :key="proc.id" :procedure="proc" />
    </section>
  </div>
</template>

<script setup>
import { computed, ref } from 'vue'
import OperationCard from '../../components/business/OperationCard.vue'

const procedures = ref([
  { id: 1, code: 'OP-2023-A1', name: '毛坯制造', description: '对铝合金压铸件进行清理、去毛刺及固溶时效处理，确保内部应力消除。', status: 1, statusText: '进行中', operator: 'WL', operatorName: '王力', time: '2023-10-24 08:00' },
  { id: 2, code: 'OP-2023-A2', name: '粗加工', description: '多轴加工中心对主要配合面进行粗铣，预留 0.5mm 余量用于精修。', status: 0, statusText: '待开始', operator: 'ZS', operatorName: '张顺', time: '预计 10-25' },
  { id: 3, code: 'OP-2023-A3', name: '精加工', description: '内外圆磨床精密加工，配合 CMM 实时反馈补偿，精度等级控制在 IT6。', status: 0, statusText: '待开始', operator: 'LC', operatorName: '李晨', time: '预计 10-27' },
  { id: 4, code: 'OP-2023-A4', name: '检测', description: '使用三坐标测量仪进行全尺寸检测，确保符合图纸要求。', status: 2, statusText: '已完成', operator: 'ZM', operatorName: '赵敏', time: '预计 10-28' },
  { id: 5, code: 'OP-2023-A5', name: '入库', description: '质检合格后完成包装、标识和入库登记，并同步库存系统。', status: 0, statusText: '待开始', operator: 'WF', operatorName: '王芳', time: '预计 10-29' }
])

const summaryCards = computed(() => {
  const rows = procedures.value
  const runningCount = rows.filter((item) => item.status === 1).length
  const waitingCount = rows.filter((item) => item.status === 0).length
  const completedCount = rows.filter((item) => item.status === 2).length
  return [
    {
      label: 'Procedure Count',
      value: rows.length,
      note: '当前路线中的工序总量'
    },
    {
      label: 'Running',
      value: runningCount,
      note: '当前处于执行中的工序节点'
    },
    {
      label: 'Pending',
      value: waitingCount,
      note: '等待排程或尚未启动的工序'
    },
    {
      label: 'Completed',
      value: completedCount,
      note: '已经闭环完成的工序节点'
    }
  ]
})
</script>
