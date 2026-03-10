<template>
  <div class="p-8">
    <div class="floating-island mb-6">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-lg font-semibold text-white">物料管理</h2>
        <el-button type="primary" size="small" @click="handleAdd">新增物料</el-button>
      </div>

      <div class="flex gap-4 mb-6">
        <el-input v-model="searchForm.keyword" placeholder="搜索物料编号/名称" clearable class="w-64" size="small" @input="handleSearch" />
        <el-select v-model="searchForm.categoryId" placeholder="选择分类" clearable class="w-48" size="small" @change="loadData">
          <el-option v-for="cat in categories" :key="cat.id" :label="cat.name" :value="cat.id" />
        </el-select>
      </div>

      <el-table :data="tableData" style="width: 100%" @row-click="handleRowClick">
        <el-table-column prop="partNo" label="物料编号" width="150" />
        <el-table-column prop="partName" label="物料名称" width="200" />
        <el-table-column prop="specification" label="规格型号" width="150" />
        <el-table-column prop="stockQty" label="库存数量" width="120" />
        <el-table-column prop="supplier" label="供应商" width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column label="操作" width="200" fixed="right">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click.stop="handleEdit(row)">编辑</el-button>
            <el-button link type="primary" size="small" @click.stop="handleBOM(row)">BOM</el-button>
            <el-button link type="danger" size="small" @click.stop="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <el-pagination
        v-model:current-page="pagination.page"
        v-model:page-size="pagination.size"
        :total="pagination.total"
        :page-sizes="[10, 20, 50]"
        layout="total, sizes, prev, pager, next"
        class="mt-6"
        @current-change="loadData"
        @size-change="loadData"
      />
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPartList, deletePart, getCategoryTree } from '@/api/parts'

const router = useRouter()
const tableData = ref([])
const categories = ref([])
const searchForm = reactive({ keyword: '', categoryId: null })
const pagination = reactive({ page: 1, size: 20, total: 0 })
let searchTimer = null

const loadData = async () => {
  try {
    const { data } = await getPartList({ ...searchForm, page: pagination.page, size: pagination.size })
    tableData.value = data.records
    pagination.total = data.total
  } catch (error) {
    // 使用Mock数据
    tableData.value = [
      { id: 1, partNo: 'MTR-2023-001', partName: '中心轮轴承单元', specification: 'SKF-6205-2RS', stockQty: 1240, supplier: 'SKF Group', categoryName: '轴承', version: 'V1.0' },
      { id: 2, partNo: 'MTR-2023-042', partName: '精密硬化齿轮', specification: 'MOD-2.5-42T', stockQty: 15, supplier: '德国精工部件', categoryName: '齿轮', version: 'V1.0' }
    ]
    pagination.total = 2
  }
}

const loadCategories = async () => {
  try {
    const { data } = await getCategoryTree()
    categories.value = data
  } catch (error) {
    // 使用Mock数据
    categories.value = [
      { id: 1, name: '轴承' },
      { id: 2, name: '齿轮' },
      { id: 3, name: '外壳' }
    ]
  }
}

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    pagination.page = 1
    loadData()
  }, 300)
}

const handleAdd = () => router.push('/parts/add')
const handleEdit = (row) => router.push(`/parts/edit/${row.id}`)
const handleBOM = (row) => router.push(`/parts/bom/${row.id}`)
const handleRowClick = (row) => router.push(`/parts/edit/${row.id}`)

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该物料？', '提示', { type: 'warning' })
  await deletePart(row.id)
  ElMessage.success('删除成功')
  loadData()
}

onMounted(() => {
  loadData()
  loadCategories()
})
</script>
