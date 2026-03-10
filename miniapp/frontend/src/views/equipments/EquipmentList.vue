<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-lg font-semibold text-white">设备管理</h2>
        <button @click="handleAdd" class="bg-blue-600 hover:bg-blue-500 text-white px-4 py-1.5 rounded-sm text-xs transition-colors">
          新增设备
        </button>
      </div>

      <el-table :data="tableData" style="width: 100%" class="dark-table" @row-click="handleEdit">
        <el-table-column prop="code" label="设备编码" width="120" />
        <el-table-column prop="name" label="设备名称" width="150" />
        <el-table-column prop="manufacturer" label="生产厂家" width="120" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="model" label="规格型号" width="120" />
        <el-table-column prop="supplier" label="供应商" width="120" />
        <el-table-column prop="productionDate" label="生产日期" width="110" />
        <el-table-column prop="serviceLife" label="使用年限" width="90" />
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column label="操作" width="120" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click.stop="handleEdit(row)">编辑</el-button>
            <el-button link type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <EquipmentForm :data="currentRow" @success="handleSuccess" @cancel="dialogVisible = false" />
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEquipments, deleteEquipment } from '@/api/equipments'
import EquipmentForm from './EquipmentForm.vue'

const tableData = ref([])
const dialogVisible = ref(false)
const currentRow = ref(null)
const dialogTitle = ref('')

const loadData = async () => {
  try {
    const res = await getEquipments()
    tableData.value = res.data || res
  } catch (error) {
    // 使用Mock数据
    tableData.value = [
      { id: 1, code: 'EQ-001', name: 'CNC加工中心', manufacturer: '德马吉', brand: 'DMG', model: 'DMU-50', supplier: '德国机床', productionDate: '2022-05-10', serviceLife: 10, location: '车间A-01' },
      { id: 2, code: 'EQ-002', name: '三坐标测量仪', manufacturer: '海克斯康', brand: 'Hexagon', model: 'Global-S', supplier: '精密仪器', productionDate: '2021-08-15', serviceLife: 8, location: '检测室' }
    ]
  }
}

const handleAdd = () => {
  currentRow.value = null
  dialogTitle.value = '新增设备'
  dialogVisible.value = true
}

const handleEdit = (row) => {
  currentRow.value = { ...row }
  dialogTitle.value = '编辑设备'
  dialogVisible.value = true
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该设备？', '提示', { type: 'warning' })
    await deleteEquipment(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

const handleSuccess = () => {
  dialogVisible.value = false
  loadData()
}

onMounted(loadData)
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

:deep(.el-table__header-wrapper) {
  background: rgba(255, 255, 255, 0.05);
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

:deep(.el-table__row:hover) {
  background: rgba(255, 255, 255, 0.05);
  cursor: pointer;
}
</style>
