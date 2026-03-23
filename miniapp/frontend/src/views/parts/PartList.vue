<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-white">物料管理</h2>
        <div class="flex gap-2">
          <el-button size="default" @click="router.push('/parts/categories')">分类管理</el-button>
          <el-button type="primary" size="default" @click="handleAdd">新增物料</el-button>
        </div>
      </div>

      <div class="flex gap-4 mb-6">
        <el-input
          v-model="searchForm.keyword"
          placeholder="搜索物料编号/名称"
          clearable
          class="w-64"
          size="small"
          @input="handleSearch"
        />
        <el-tree-select
          v-model="searchForm.categoryId"
          :data="categories"
          :props="categoryNodeProps"
          node-key="id"
          check-strictly
          clearable
          filterable
          :default-expanded-keys="expandedKeys"
          :loading="categoryLoading"
          placeholder="选择分类"
          class="w-56"
          size="small"
          @change="handleCategoryChange"
          @node-expand="handleCategoryExpand"
          @node-collapse="handleCategoryCollapse"
        />
        <el-button v-if="categoryLoadFailed" link size="small" @click="loadCategories">重试分类加载</el-button>
      </div>

      <el-table :data="tableData" style="width: 100%" @row-click="handleRowClick">
        <el-table-column prop="partNo" label="物料编号" width="150" />
        <el-table-column prop="partName" label="物料名称" width="200" />
        <el-table-column prop="specification" label="规格型号" width="150" />
        <el-table-column prop="stockQty" label="库存数量" width="120" />
        <el-table-column prop="supplier" label="供应商" width="150" />
        <el-table-column prop="categoryName" label="分类" width="120" />
        <el-table-column prop="version" label="版本" width="100" />
        <el-table-column label="操作" width="320" fixed="right">
          <template #default="{ row }">
            <div class="flex gap-2 overflow-x-auto">
              <el-button link size="small" @click.stop="handleEdit(row)">编辑</el-button>
              <el-button link size="small" @click.stop="handleBOM(row)">BOM</el-button>
              <el-button link size="small" @click.stop="handleVersions(row)">版本</el-button>
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
          :page-sizes="[10, 20, 50]"
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted, nextTick } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPartList, deletePart, getCategoryTree } from '@/api/parts'

const CATEGORY_FILTER_CACHE_KEY = 'parts:list:category-filter:v1'

const router = useRouter()
const tableData = ref([])
const categories = ref([])
const expandedKeys = ref([])
const categoryDescendantMap = ref(new Map())
const categoryLoading = ref(false)
const categoryLoadFailed = ref(false)
const searchForm = reactive({ keyword: '', categoryId: null })
const pagination = reactive({ page: 1, size: 20, total: 0 })
const categoryNodeProps = { label: 'name', value: 'id', children: 'children' }
let searchTimer = null

const normalizeCategoryTree = (nodes) => {
  if (!Array.isArray(nodes)) {
    return []
  }
  return nodes.map(node => ({
    ...node,
    id: String(node.id),
    children: normalizeCategoryTree(node.children || [])
  }))
}

const collectDescendantIds = (node) => {
  const ids = [String(node.id)]
  for (const child of node.children || []) {
    ids.push(...collectDescendantIds(child))
  }
  return ids
}

const buildCategoryDescendantMap = (treeNodes) => {
  const map = new Map()
  const walk = (nodes) => {
    for (const node of nodes) {
      map.set(String(node.id), collectDescendantIds(node))
      walk(node.children || [])
    }
  }
  walk(treeNodes)
  return map
}

const persistCategoryFilterState = () => {
  const payload = {
    categoryId: searchForm.categoryId ? String(searchForm.categoryId) : null,
    expandedKeys: expandedKeys.value
  }
  sessionStorage.setItem(CATEGORY_FILTER_CACHE_KEY, JSON.stringify(payload))
}

const restoreCategoryFilterState = () => {
  const raw = sessionStorage.getItem(CATEGORY_FILTER_CACHE_KEY)
  if (!raw) {
    return
  }
  try {
    const parsed = JSON.parse(raw)
    searchForm.categoryId = parsed.categoryId ? String(parsed.categoryId) : null
    expandedKeys.value = Array.isArray(parsed.expandedKeys) ? parsed.expandedKeys.map(id => String(id)) : []
  } catch {
    searchForm.categoryId = null
    expandedKeys.value = []
  }
}

const loadData = async () => {
  try {
    const selectedCategoryId = searchForm.categoryId ? String(searchForm.categoryId) : ''
    const selectedCategoryIds = selectedCategoryId
      ? (categoryDescendantMap.value.get(selectedCategoryId) || [selectedCategoryId])
      : []

    const { data } = await getPartList({
      keyword: searchForm.keyword,
      categoryId: selectedCategoryId || undefined,
      categoryIds: selectedCategoryIds.length ? selectedCategoryIds.join(',') : undefined,
      page: pagination.page,
      size: pagination.size
    })
    tableData.value = data.records
    pagination.total = data.total
  } catch {
    tableData.value = []
    pagination.total = 0
    ElMessage.error('加载物料列表失败')
  }
}

const loadCategories = async () => {
  try {
    categoryLoading.value = true
    categoryLoadFailed.value = false
    const { data } = await getCategoryTree()
    const normalized = normalizeCategoryTree(data)
    categories.value = normalized
    categoryDescendantMap.value = buildCategoryDescendantMap(normalized)

    if (searchForm.categoryId && !categoryDescendantMap.value.has(String(searchForm.categoryId))) {
      searchForm.categoryId = null
      persistCategoryFilterState()
    }
    await nextTick()
  } catch {
    categories.value = []
    categoryDescendantMap.value = new Map()
    categoryLoadFailed.value = true
    ElMessage.error('加载分类失败')
  } finally {
    categoryLoading.value = false
  }
}

const handleCategoryChange = () => {
  pagination.page = 1
  persistCategoryFilterState()
  loadData()
}

const handleCategoryExpand = (nodeData) => {
  const nodeId = String(nodeData.id)
  if (!expandedKeys.value.includes(nodeId)) {
    expandedKeys.value = [...expandedKeys.value, nodeId]
    persistCategoryFilterState()
  }
}

const handleCategoryCollapse = (nodeData) => {
  const nodeId = String(nodeData.id)
  if (expandedKeys.value.includes(nodeId)) {
    expandedKeys.value = expandedKeys.value.filter(id => id !== nodeId)
    persistCategoryFilterState()
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
const handleVersions = (row) => router.push(`/parts/versions/${row.id}`)
const handleRowClick = (row) => router.push(`/parts/edit/${row.id}`)

const handleDelete = async (row) => {
  await ElMessageBox.confirm('确认删除该物料？', '提示', { type: 'warning' })
  await deletePart(row.id)
  ElMessage.success('删除成功')
  loadData()
}

const initPage = async () => {
  restoreCategoryFilterState()
  await loadCategories()
  await loadData()
}

onMounted(() => {
  initPage()
})
</script>
