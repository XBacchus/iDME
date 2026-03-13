<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-4">
        <h2 class="text-2xl font-bold text-white">设备管理</h2>
        <el-button type="primary" size="default" @click="handleAdd">新增设备</el-button>
      </div>

      <div class="mb-6 flex items-center gap-3">
        <el-input
          v-model="query.keyword"
          placeholder="按设备编码/设备名称/厂商搜索"
          clearable
          style="max-width: 360px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" @click="handleSearch">查询</el-button>
        <el-button @click="handleReset">重置</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" class="dark-table" @row-click="handleEdit">
        <el-table-column prop="equipmentCode" label="设备编码" width="140" />
        <el-table-column prop="equipmentName" label="设备名称" width="160" />
        <el-table-column prop="manufacturer" label="生产厂家" width="120" />
        <el-table-column prop="brand" label="品牌" width="100" />
        <el-table-column prop="specModel" label="规格型号" width="130" />
        <el-table-column prop="supplier" label="供应商" width="120" />
        <el-table-column prop="productionDate" label="生产日期" width="130" />
        <el-table-column prop="serviceLifeYears" label="使用年限" width="100" />
        <el-table-column prop="depreciationMethod" label="折旧方式" width="140" show-overflow-tooltip />
        <el-table-column prop="location" label="位置" width="120" />
        <el-table-column prop="technicalParams" label="技术参数信息" min-width="180" show-overflow-tooltip />
        <el-table-column prop="sparePartsInfo" label="备品备件信息" min-width="180" show-overflow-tooltip />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <div class="flex gap-2 overflow-x-auto">
              <el-button link size="small" @click.stop="handleEdit(row)">编辑</el-button>
              <el-button link type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
            </div>
          </template>
        </el-table-column>
      </el-table>

      <div class="flex justify-between items-center mt-6">
        <span class="text-sm text-gray-400">共 {{ pagination.total }} 条</span>
        <el-pagination
          v-model:current-page="pagination.page"
          v-model:page-size="pagination.size"
          :total="pagination.total"
          layout="prev, pager, next"
          @current-change="loadData"
          @size-change="loadData"
        />
        <el-select v-model="pagination.size" size="small" style="width: 100px" @change="loadData">
          <el-option :value="10" label="10 / page" />
          <el-option :value="20" label="20 / page" />
          <el-option :value="50" label="50 / page" />
        </el-select>
      </div>
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
const query = ref({
  keyword: ''
})
const dialogVisible = ref(false)
const currentRow = ref(null)
const dialogTitle = ref('')
const pagination = ref({ page: 1, size: 20, total: 0 })

const normalizeEquipment = (item = {}) => ({
  id: item.id || '',
  equipmentCode: item.equipmentCode || item.code || '',
  equipmentName: item.equipmentName || item.name || '',
  manufacturer: item.manufacturer || '',
  brand: item.brand || '',
  specModel: item.specModel || item.model || '',
  supplier: item.supplier || '',
  productionDate: item.productionDate || '',
  serviceLifeYears: Number(item.serviceLifeYears ?? item.serviceLife ?? 0),
  depreciationMethod: item.depreciationMethod || item.depreciation || '',
  location: item.location || '',
  technicalParams: item.technicalParams || '',
  sparePartsInfo: item.sparePartsInfo || item.spareParts || '',
  status: item.status || 'idle'
})

const loadData = async () => {
  try {
    const keyword = query.value.keyword?.trim()
    const res = await getEquipments(keyword ? { keyword } : undefined)
    tableData.value = (res.data || res || []).map(normalizeEquipment)
    pagination.value.total = tableData.value.length
  } catch {
    tableData.value = []
    pagination.value.total = 0
    ElMessage.error('加载设备列表失败')
  }
}

const handleAdd = () => {
  currentRow.value = null
  dialogTitle.value = '新增设备'
  dialogVisible.value = true
}

const handleSearch = () => {
  loadData()
}

const handleReset = () => {
  query.value.keyword = ''
  loadData()
}

const handleEdit = (row) => {
  currentRow.value = normalizeEquipment(row)
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
