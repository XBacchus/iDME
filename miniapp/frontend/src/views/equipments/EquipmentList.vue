<template>
  <div class="equipment-page space-y-8">
    <section class="flex flex-col gap-6 xl:flex-row xl:items-end xl:justify-between">
      <div>
        <p class="eyebrow">Assets / Equipment Ledger</p>
        <h1 class="page-heading">设备管理</h1>
        <p class="page-subtitle">
          统一查看关键设备台账、供应来源和落位信息，便于在亮色模式下保持和控制台一致的检索与维护体验。
        </p>
      </div>

      <div class="flex flex-wrap gap-3">
        <button class="action-button action-button-secondary" @click="handleReset">
          重置筛选
        </button>
        <button class="action-button action-button-primary" @click="handleAdd">
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          新增设备
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

    <section class="floating-island overflow-hidden p-0">
      <div class="section-divider flex flex-col gap-4 border-b px-6 py-6 lg:flex-row lg:items-center lg:justify-between">
        <div class="flex flex-1 flex-col gap-4 lg:flex-row">
          <label class="glass-input max-w-xl flex-1">
            <svg class="page-search-icon h-4 w-4 shrink-0" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z" />
            </svg>
            <input
              v-model="query.keyword"
              type="text"
              placeholder="按设备编码、名称、品牌或厂商搜索"
              @keyup.enter="handleSearch"
            >
          </label>

          <div class="flex flex-wrap gap-3">
            <button class="action-button action-button-primary" @click="handleSearch">查询</button>
            <button class="action-button action-button-secondary" @click="handleReset">清空</button>
          </div>
        </div>

        <div class="flex flex-wrap items-center gap-2 text-xs">
          <span class="data-pill neutral-pill">设备台账</span>
          <span class="data-pill accent-pill">状态巡检</span>
          <span class="data-pill success-pill">供应链映射</span>
        </div>
      </div>

      <div v-if="paginatedRows.length" class="overflow-x-auto">
        <el-table :data="paginatedRows" style="width: 100%" @row-click="handleEdit">
          <el-table-column prop="equipmentCode" label="设备编码" min-width="150" />
          <el-table-column prop="equipmentName" label="设备名称" min-width="180" />
          <el-table-column prop="manufacturer" label="生产厂家" min-width="150" />
          <el-table-column prop="brand" label="品牌" min-width="120" />
          <el-table-column prop="specModel" label="规格型号" min-width="140" />
          <el-table-column prop="supplier" label="供应商" min-width="150" />
          <el-table-column prop="productionDate" label="生产日期" min-width="130" />
          <el-table-column prop="serviceLifeYears" label="使用年限" min-width="110" />
          <el-table-column prop="depreciationMethod" label="折旧方式" min-width="150" show-overflow-tooltip />
          <el-table-column prop="location" label="位置" min-width="140" />
          <el-table-column prop="technicalParams" label="技术参数" min-width="180" show-overflow-tooltip />
          <el-table-column prop="sparePartsInfo" label="备品备件信息" min-width="180" show-overflow-tooltip />
          <el-table-column label="操作" width="180" fixed="right">
            <template #default="{ row }">
              <div class="flex gap-2 overflow-x-auto">
                <el-button link size="small" @click.stop="handleEdit(row)">编辑</el-button>
                <el-button link type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-else class="px-6 py-16 text-center">
        <div class="mx-auto max-w-md space-y-3">
          <p class="empty-state-title text-sm font-semibold">当前没有匹配设备</p>
          <p class="empty-state-note text-sm">调整搜索条件后重试，或直接新增设备台账。</p>
          <button class="action-button action-button-primary" @click="handleAdd">创建首台设备</button>
        </div>
      </div>

      <div class="section-divider flex flex-col gap-4 border-t px-6 py-5 lg:flex-row lg:items-center lg:justify-between">
        <div class="page-meta-text text-sm">
          共 {{ pagination.total }} 条记录，当前显示 {{ paginatedRows.length }} 条
        </div>

        <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            layout="prev, pager, next"
          />

          <el-select v-model="pagination.size" class="w-full sm:w-28" @change="handleSizeChange">
            <el-option :value="10" label="10 / 页" />
            <el-option :value="20" label="20 / 页" />
            <el-option :value="50" label="50 / 页" />
          </el-select>
        </div>
      </div>
    </section>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="600px">
      <EquipmentForm :data="currentRow" @success="handleSuccess" @cancel="dialogVisible = false" />
    </el-dialog>
  </div>
</template>

<script setup>
import { computed, onMounted, reactive, ref } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getEquipments, deleteEquipment } from '@/api/equipments'
import EquipmentForm from './EquipmentForm.vue'

const tableData = ref([])
const dialogVisible = ref(false)
const currentRow = ref(null)
const dialogTitle = ref('')
const query = reactive({
  keyword: ''
})
const pagination = reactive({
  page: 1,
  size: 10,
  total: 0
})

const normalizeEquipment = (item = {}) => ({
  id: item.id || '',
  equipmentCode: item.equipmentCode || item.code || '',
  equipmentName: item.equipmentName || item.name || '',
  manufacturer: item.manufacturer || '未设置厂家',
  brand: item.brand || '未设置品牌',
  specModel: item.specModel || item.model || '未设置型号',
  supplier: item.supplier || '未设置供应商',
  productionDate: item.productionDate || '-',
  serviceLifeYears: Number(item.serviceLifeYears ?? item.serviceLife ?? 0),
  depreciationMethod: item.depreciationMethod || item.depreciation || '-',
  location: item.location || '未设置位置',
  technicalParams: item.technicalParams || '-',
  sparePartsInfo: item.sparePartsInfo || item.spareParts || '-'
})

const paginatedRows = computed(() => {
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  return tableData.value.slice(start, end)
})

const summaryCards = computed(() => {
  const rows = tableData.value
  const manufacturerCount = new Set(rows.map((row) => row.manufacturer).filter(Boolean)).size
  const supplierCount = new Set(rows.map((row) => row.supplier).filter(Boolean)).size
  const locationCount = new Set(rows.map((row) => row.location).filter(Boolean)).size

  return [
    {
      label: 'Equipment Count',
      value: pagination.total,
      note: '当前纳入统一台账的设备总量'
    },
    {
      label: 'Manufacturers',
      value: manufacturerCount,
      note: '已接入的生产厂家数量'
    },
    {
      label: 'Suppliers',
      value: supplierCount,
      note: '用于备件与采购追踪的供应来源'
    },
    {
      label: 'Locations',
      value: locationCount,
      note: '当前设备分布的落位点位数'
    }
  ]
})

const clampPage = () => {
  const maxPage = Math.max(1, Math.ceil((pagination.total || 0) / pagination.size))
  if (pagination.page > maxPage) {
    pagination.page = maxPage
  }
}

const loadData = async () => {
  try {
    const keyword = query.keyword.trim()
    const res = await getEquipments(keyword ? { keyword } : undefined)
    tableData.value = (res.data || res || []).map(normalizeEquipment)
    pagination.total = tableData.value.length
    clampPage()
  } catch {
    tableData.value = []
    pagination.total = 0
    clampPage()
    ElMessage.error('加载设备列表失败')
  }
}

const handleAdd = () => {
  currentRow.value = null
  dialogTitle.value = '新增设备'
  dialogVisible.value = true
}

const handleSearch = () => {
  pagination.page = 1
  loadData()
}

const handleReset = () => {
  query.keyword = ''
  pagination.page = 1
  loadData()
}

const handleSizeChange = () => {
  pagination.page = 1
  clampPage()
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
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

const handleSuccess = () => {
  dialogVisible.value = false
  loadData()
}

onMounted(loadData)
</script>
