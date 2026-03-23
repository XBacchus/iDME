<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="mb-4 flex items-center justify-between">
        <h2 class="text-2xl font-bold text-white">工序明细配置</h2>
        <el-button type="primary" :loading="loading" @click="loadProcedures">刷新</el-button>
      </div>

      <el-table v-loading="loading" :data="procedures" style="width: 100%" class="dark-table">
        <el-table-column prop="procedureCode" label="工序编号" width="150" />
        <el-table-column prop="procedureName" label="工序名称" width="160" />
        <el-table-column prop="productionStep" label="生产步骤" min-width="220" show-overflow-tooltip />
        <el-table-column
          prop="productionAndTestingEquipment"
          label="生产和检测设备"
          min-width="220"
          show-overflow-tooltip
        />
        <el-table-column prop="operatorName" label="操作人员" width="140" />
        <el-table-column prop="startTime" label="开始时间" width="180" show-overflow-tooltip />
        <el-table-column prop="endTime" label="结束时间" width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="100" fixed="right">
          <template #default="{ row }">
            <el-button link size="small" @click="openEditDialog(row)">编辑</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

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
import { onMounted, reactive, ref } from 'vue'
import { ElMessage } from 'element-plus'
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
  if (!value) return ''
  if (typeof value === 'string') return value
  if (typeof value === 'number') return String(value)
  if (typeof value === 'object') {
    return String(value.time ?? value.value ?? value.epochSecond ?? value.seconds ?? '')
  }
  return String(value)
}

const normalizeProcedure = (item = {}) => ({
  id: item.id || '',
  procedureCode: item.procedureCode || item.code || '',
  procedureName: item.procedureName || item.name || '',
  productionStep: item.productionStep || item.description || '',
  productionAndTestingEquipment: item.productionAndTestingEquipment || item.equipment || '',
  operatorName: item.operatorName || item.operator || '',
  startTime: normalizeDateTime(item.startTime),
  endTime: normalizeDateTime(item.endTime)
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
  Object.assign(form, normalizeProcedure(row))
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

onMounted(loadProcedures)
</script>

<style scoped>
.floating-island {
  background: #2C2C2E;
  border: 1px solid rgba(255, 255, 255, 0.08);
  border-radius: 8px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  padding: 24px;
}

:deep(.dark-table) {
  background: transparent;
  color: #ECEFF4;
}

:deep(.el-table th) {
  background: rgba(255, 255, 255, 0.05);
  color: #9CA3AF;
  font-size: 12px;
  font-weight: 500;
}

:deep(.el-table td) {
  border-bottom: 1px solid rgba(255, 255, 255, 0.05);
  color: #D8DEE9;
  font-size: 12px;
}
</style>
