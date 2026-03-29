<template>
  <div class="procedure-page space-y-8">
    <section class="flex flex-col gap-6 xl:flex-row xl:items-end xl:justify-between">
      <div>
        <p class="eyebrow">Manufacturing / Procedure Config</p>
        <h1 class="page-heading">工序配置</h1>
        <p class="page-subtitle">
          按控制台的卡片逻辑展示工序状态、责任人和排程时间，并保留运行态加载与在线编辑能力。
        </p>
      </div>

      <div class="flex flex-wrap items-center gap-3">
        <span class="data-pill neutral-pill">流程排程</span>
        <span class="data-pill accent-pill">进度跟踪</span>
        <span class="data-pill success-pill">责任到人</span>
        <button class="action-button action-button-secondary !px-4 !py-2 text-xs" :disabled="loading" @click="loadProcedures">
          {{ loading ? '刷新中...' : '刷新列表' }}
        </button>
      </div>
    </section>

    <section class="grid gap-4 md:grid-cols-2 xl:grid-cols-4">
      <article v-for="card in summaryCards" :key="card.label" class="metric-tile">
        <p class="metric-label">{{ card.label }}</p>
        <p class="metric-value">{{ card.value }}</p>
        <p class="metric-note">{{ card.note }}</p>
      </article>
    </section>

    <section v-if="procedures.length" class="grid grid-cols-1 gap-6 md:grid-cols-2 xl:grid-cols-3">
      <div v-for="proc in procedures" :key="proc.id" class="space-y-3">
        <OperationCard :procedure="proc" />
        <div class="flex justify-end">
          <button class="action-button action-button-secondary !px-3 !py-2 text-[11px]" @click="openEditDialog(proc)">
            编辑工序
          </button>
        </div>
      </div>
    </section>

    <section v-else class="floating-island px-6 py-16 text-center">
      <div class="mx-auto max-w-md space-y-3">
        <p class="empty-state-title text-sm font-semibold">当前没有可展示的工序</p>
        <p class="empty-state-note text-sm">请确认后端接口返回正常，或稍后点击“刷新列表”重新加载。</p>
      </div>
    </section>

    <el-dialog v-model="dialogVisible" title="编辑工序" width="720px">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="120px">
        <el-form-item label="工序编号" prop="procedureCode">
          <el-input v-model="form.procedureCode" placeholder="请输入工序编号" />
        </el-form-item>
        <el-form-item label="工序名称" prop="procedureName">
          <el-input v-model="form.procedureName" placeholder="请输入工序名称" />
        </el-form-item>
        <el-form-item label="生产步骤" prop="productionStep">
          <el-input v-model="form.productionStep" type="textarea" :rows="3" placeholder="请输入生产步骤" />
        </el-form-item>
        <el-form-item label="生产和检测设备">
          <el-input v-model="form.productionAndTestingEquipment" disabled placeholder="由工序-设备关系自动汇总" />
        </el-form-item>
        <el-form-item label="操作人员" prop="operatorName">
          <el-input v-model="form.operatorName" placeholder="请输入操作人员" />
        </el-form-item>
        <el-form-item label="开始时间" prop="startTime">
          <el-input v-model="form.startTime" placeholder="例如：2026-03-13 09:00:00" />
        </el-form-item>
        <el-form-item label="结束时间" prop="endTime">
          <el-input v-model="form.endTime" placeholder="例如：2026-03-13 10:00:00" />
        </el-form-item>
      </el-form>

      <template #footer>
        <div class="flex justify-end gap-2">
          <el-button @click="dialogVisible = false">取消</el-button>
          <el-button type="primary" :loading="saving" @click="saveProcedure">保存</el-button>
        </div>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
import OperationCard from '../../components/business/OperationCard.vue'
import { getProcedures, updateProcedure } from '@/api/procedures'

const loading = ref(false)
const saving = ref(false)
const procedures = ref([])
const dialogVisible = ref(false)
const formRef = ref()

const form = reactive({
  id: '',
  procedureCode: '',
  procedureName: '',
  productionStep: '',
  productionAndTestingEquipment: '',
  operatorName: '',
  startTime: '',
  endTime: ''
})

const rules = {
  procedureCode: [{ required: true, message: '请输入工序编号', trigger: 'blur' }],
  procedureName: [{ required: true, message: '请输入工序名称', trigger: 'blur' }],
  productionStep: [{ required: true, message: '请输入生产步骤', trigger: 'blur' }],
  operatorName: [{ required: true, message: '请输入操作人员', trigger: 'blur' }],
  startTime: [{ required: true, message: '请输入开始时间', trigger: 'blur' }],
  endTime: [{ required: true, message: '请输入结束时间', trigger: 'blur' }]
}

const normalizeDateTime = (value) => {
  if (!value) return '--'
  if (typeof value === 'string') return value
  if (typeof value === 'number') return String(value)
  if (typeof value === 'object') {
    return String(value.time ?? value.value ?? value.epochSecond ?? value.seconds ?? '--')
  }
  return String(value)
}

const normalizeProcedure = (item = {}) => {
  const status = Number(item.status ?? 0)
  const statusText = item.statusText || (status === 1 ? '进行中' : status === 2 ? '已完成' : '待开始')

  return {
    id: item.id || '',
    code: item.procedureCode || item.code || '',
    name: item.procedureName || item.name || '未命名工序',
    description: item.productionStep || item.description || '暂无工序说明',
    status,
    statusText,
    operator: (item.operatorName || item.operator || 'NA').slice(0, 2).toUpperCase(),
    operatorName: item.operatorName || item.operator || '未分配',
    time: normalizeDateTime(item.startTime || item.time),
    productionAndTestingEquipment: item.productionAndTestingEquipment || item.equipment || '',
    startTime: normalizeDateTime(item.startTime),
    endTime: normalizeDateTime(item.endTime)
  }
}

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

const loadProcedures = async () => {
  loading.value = true
  try {
    const res = await getProcedures()
    const rows = Array.isArray(res?.data) ? res.data : Array.isArray(res) ? res : []
    procedures.value = rows.map(normalizeProcedure)
  } catch {
    procedures.value = []
    ElMessage.error('加载工序列表失败')
  } finally {
    loading.value = false
  }
}

const openEditDialog = (row) => {
  Object.assign(form, {
    id: row.id || '',
    procedureCode: row.code || '',
    procedureName: row.name || '',
    productionStep: row.description || '',
    productionAndTestingEquipment: row.productionAndTestingEquipment || '',
    operatorName: row.operatorName || '',
    startTime: row.startTime === '--' ? '' : row.startTime,
    endTime: row.endTime === '--' ? '' : row.endTime
  })
  dialogVisible.value = true
}

const saveProcedure = async () => {
  if (!formRef.value) return

  try {
    await formRef.value.validate()
    saving.value = true
    await updateProcedure(form.id, {
      procedureCode: form.procedureCode.trim(),
      procedureName: form.procedureName.trim(),
      productionStep: form.productionStep.trim(),
      operatorName: form.operatorName.trim(),
      startTime: form.startTime.trim(),
      endTime: form.endTime.trim()
    })
    ElMessage.success('工序保存成功')
    dialogVisible.value = false
    await loadProcedures()
  } catch {
    ElMessage.error('工序保存失败，请检查输入后重试')
  } finally {
    saving.value = false
  }
}

onMounted(() => {
  loadProcedures()
})
</script>
