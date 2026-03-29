<template>
  <div class="parts-page space-y-8">
    <section class="flex flex-col gap-6 xl:flex-row xl:items-end xl:justify-between">
      <div>
        <p class="eyebrow">Warehouse / Material List</p>
        <h1 class="page-heading">物料管理</h1>
        <p class="page-subtitle">
          以玻璃化视图统一呈现库存、版本、供应商与 BOM 入口，便于快速盘点生产物料实时状态。
        </p>
      </div>

      <div class="flex flex-wrap gap-3">
        <button class="action-button action-button-secondary" @click="handleExport">
          导出数据
        </button>
        <button class="action-button action-button-secondary" @click="router.push('/parts/categories')">
          分类管理
        </button>
        <button class="action-button action-button-primary" @click="handleAdd">
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          新增物料
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
              v-model="searchForm.keyword"
              type="text"
              placeholder="快速查找物料编号、名称或供应商..."
              @input="handleSearch"
            >
          </label>

          <el-tree-select
            v-model="searchForm.categoryId"
            :data="categories"
            :props="categoryNodeProps"
            node-key="id"
            placeholder="选择分类"
            check-strictly
            clearable
            filterable
            :default-expanded-keys="expandedKeys"
            :loading="categoryLoading"
            class="w-full lg:w-60"
            @change="handleCategoryChange"
            @node-expand="handleCategoryExpand"
            @node-collapse="handleCategoryCollapse"
          />
          <el-button v-if="categoryLoadFailed" link size="small" @click="loadCategories">重试分类加载</el-button>
        </div>

        <div class="flex flex-wrap items-center gap-2 text-xs">
          <span class="data-pill neutral-pill">实时库存</span>
          <span class="data-pill accent-pill">版本追踪</span>
          <span class="data-pill success-pill">BOM 可追溯</span>
        </div>
      </div>

      <div v-if="normalizedRows.length" class="overflow-x-auto">
        <table class="w-full min-w-[1080px] text-left">
          <thead class="table-surface">
            <tr class="section-divider table-header-row border-b text-[11px] uppercase tracking-[0.24em]">
              <th class="px-6 py-4 font-medium">物料编号</th>
              <th class="px-6 py-4 font-medium">物料详情</th>
              <th class="px-6 py-4 font-medium">规格 / 型号</th>
              <th class="px-6 py-4 font-medium">实时库存</th>
              <th class="px-6 py-4 font-medium">供应商</th>
              <th class="px-6 py-4 text-right font-medium">操作</th>
            </tr>
          </thead>

          <tbody class="divide-y divide-white/[0.04]">
            <tr
              v-for="row in normalizedRows"
              :key="row.id"
              class="table-row cursor-pointer"
              @click="handleRowClick(row)"
            >
              <td class="px-6 py-5 align-top">
                <span class="data-pill accent-pill font-mono text-[11px] tracking-[0.14em]">
                  {{ row.partNo }}
                </span>
              </td>

              <td class="px-6 py-5 align-top">
                <div class="space-y-2">
                  <p class="table-primary-text text-sm font-semibold">{{ row.partName }}</p>
                  <div class="table-meta-text flex flex-wrap items-center gap-2 text-[11px]">
                    <span class="data-pill neutral-pill">{{ row.categoryName }}</span>
                    <span>版本 {{ row.version }}</span>
                  </div>
                </div>
              </td>

              <td class="table-secondary-text px-6 py-5 align-top text-sm">{{ row.specification }}</td>

              <td class="px-6 py-5 align-top">
                <div class="space-y-2">
                  <div class="flex items-end gap-2">
                    <span :class="row.stockTextClass" class="text-lg font-bold">{{ row.stockQty }}</span>
                    <span class="table-meta-text text-[10px] uppercase tracking-[0.24em]">PCS</span>
                  </div>
                  <div class="stock-track max-w-[140px]">
                    <div class="stock-fill" :class="row.stockFillClass" :style="{ width: row.stockWidth }"></div>
                  </div>
                </div>
              </td>

              <td class="px-6 py-5 align-top">
                <span class="data-pill neutral-pill">{{ row.supplier }}</span>
              </td>

              <td class="px-6 py-5 align-top">
                <div class="flex justify-end gap-2">
                  <button class="action-button action-button-secondary !px-3 !py-2 text-[11px]" @click.stop="handleEdit(row)">编辑</button>
                  <button class="action-button action-button-secondary !px-3 !py-2 text-[11px]" @click.stop="handleBOM(row)">BOM</button>
                  <button class="action-button action-button-secondary !px-3 !py-2 text-[11px]" @click.stop="handleVersions(row)">版本</button>
                  <button class="action-button action-button-danger !px-3 !py-2 text-[11px]" @click.stop="handleDelete(row)">删除</button>
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <div v-else class="px-6 py-16 text-center">
        <div class="mx-auto max-w-md space-y-3">
          <p class="empty-state-title text-sm font-semibold">当前没有匹配物料</p>
          <p class="empty-state-note text-sm">调整搜索条件或直接创建新物料，列表会在保存后实时刷新。</p>
          <button class="action-button action-button-primary" @click="handleAdd">创建首个物料</button>
        </div>
      </div>

      <div class="section-divider flex flex-col gap-4 border-t px-6 py-5 lg:flex-row lg:items-center lg:justify-between">
        <div class="page-meta-text text-sm">
          共 {{ pagination.total }} 条记录，当前显示 {{ normalizedRows.length }} 条
        </div>

        <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="pagination.total"
            :page-sizes="[10, 20, 50]"
            layout="prev, pager, next"
            @current-change="loadData"
            @size-change="loadData"
          />

          <el-select v-model="pagination.size" class="w-full sm:w-28" @change="handlePageSizeChange">
            <el-option :value="10" label="10 / 页" />
            <el-option :value="20" label="20 / 页" />
            <el-option :value="50" label="50 / 页" />
          </el-select>
        </div>
      </div>
    </section>
  </div>
</template>

<script setup>
import { computed, nextTick, onMounted, onUnmounted, reactive, ref } from 'vue'
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

const normalizePart = (item, index) => ({
  id: item.id ?? item.partId ?? item.partNo ?? `part-${index}`,
  partNo: item.partNo ?? item.partCode ?? item.code ?? 'UNDEFINED',
  partName: item.partName ?? item.name ?? '未命名物料',
  specification: item.specification ?? item.specModel ?? item.model ?? '未设置规格',
  stockQty: Number(item.stockQty ?? item.qty ?? item.quantity ?? 0) || 0,
  supplier: item.supplier ?? '未设置供应商',
  categoryName: item.categoryName ?? item.categoryPath ?? '未分类',
  version: item.version ?? item.versionNo ?? 'V1.0'
})

const normalizeCategoryTree = (nodes) => {
  if (!Array.isArray(nodes)) {
    return []
  }

  return nodes.map((node) => ({
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
    expandedKeys.value = Array.isArray(parsed.expandedKeys) ? parsed.expandedKeys.map((id) => String(id)) : []
  } catch {
    searchForm.categoryId = null
    expandedKeys.value = []
  }
}

const getStockTone = (stockQty) => {
  if (stockQty <= 50) {
    return {
      fillClass: 'bg-gradient-to-r from-amber-400 to-orange-500',
      textClass: 'text-amber-300'
    }
  }

  if (stockQty <= 200) {
    return {
      fillClass: 'bg-gradient-to-r from-cyan-400 to-blue-500',
      textClass: 'text-cyan-300'
    }
  }

  return {
    fillClass: 'bg-gradient-to-r from-emerald-400 to-sky-500',
    textClass: 'text-emerald-300'
  }
}

const normalizedRows = computed(() => {
  return tableData.value.map((item, index) => {
    const row = normalizePart(item, index)
    const tone = getStockTone(row.stockQty)

    return {
      ...row,
      stockWidth: `${Math.min(100, Math.max(8, row.stockQty / 15))}%`,
      stockFillClass: tone.fillClass,
      stockTextClass: tone.textClass
    }
  })
})

const summaryCards = computed(() => {
  const rows = normalizedRows.value
  const supplierCount = new Set(rows.map((row) => row.supplier)).size
  const categoryCount = new Set(rows.map((row) => row.categoryName)).size
  const lowStockCount = rows.filter((row) => row.stockQty <= 50).length

  return [
    {
      label: 'Material Count',
      value: pagination.total,
      note: '系统已纳入管理的物料总数'
    },
    {
      label: 'Low Inventory',
      value: lowStockCount,
      note: '库存低于 50 件，建议优先补货'
    },
    {
      label: 'Suppliers',
      value: supplierCount,
      note: '当前页涉及的供应商覆盖数'
    },
    {
      label: 'Categories',
      value: categoryCount,
      note: '分类视角快速定位物料层级'
    }
  ]
})

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

    const records = data?.records ?? data?.list ?? data?.items ?? data ?? []
    tableData.value = Array.isArray(records) ? records : []
    pagination.total = data?.total ?? tableData.value.length
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

const handleSearch = () => {
  clearTimeout(searchTimer)
  searchTimer = setTimeout(() => {
    pagination.page = 1
    loadData()
  }, 300)
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
    expandedKeys.value = expandedKeys.value.filter((id) => id !== nodeId)
    persistCategoryFilterState()
  }
}

const handlePageSizeChange = () => {
  pagination.page = 1
  loadData()
}

const handleAdd = () => router.push('/parts/add')
const handleEdit = (row) => router.push(`/parts/edit/${row.id}`)
const handleBOM = (row) => router.push(`/parts/bom/${row.id}`)
const handleVersions = (row) => router.push(`/parts/versions/${row.id}`)
const handleRowClick = (row) => router.push(`/parts/edit/${row.id}`)

const handleExport = () => {
  ElMessage.info('导出功能正在接入中，当前先完成页面重构。')
}

const handleDelete = async (row) => {
  try {
    await ElMessageBox.confirm('确认删除该物料？', '提示', { type: 'warning' })
    await deletePart(row.id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败，请稍后重试')
    }
  }
}

const initPage = async () => {
  restoreCategoryFilterState()
  await loadCategories()
  await loadData()
}

onMounted(() => {
  initPage()
})

onUnmounted(() => {
  clearTimeout(searchTimer)
})
</script>
