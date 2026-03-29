<template>
  <div class="working-plan-page space-y-8">
    <section class="flex flex-col gap-6 xl:flex-row xl:items-end xl:justify-between">
      <div>
        <p class="eyebrow">Manufacturing / Process Routes</p>
        <h1 class="page-heading">工艺路线管理</h1>
        <p class="page-subtitle">
          聚合路线版本、归属产品和维护人信息，让工艺总览在明亮模式下延续控制台的工作台层级和操作反馈。
        </p>
      </div>

      <div class="flex flex-wrap gap-3">
        <button class="action-button action-button-secondary" @click="handleReset">
          清空筛选
        </button>
        <button class="action-button action-button-primary" @click="$router.push('/working-plans/new')">
          <svg class="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 4v16m8-8H4" />
          </svg>
          新增工艺路线
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
              v-model="search"
              type="text"
              placeholder="搜索工艺编号、名称、产品或维护人"
            >
          </label>
        </div>

        <div class="flex flex-wrap items-center gap-2 text-xs">
          <span class="data-pill neutral-pill">路线总览</span>
          <span class="data-pill accent-pill">版本维护</span>
          <span class="data-pill success-pill">产品映射</span>
        </div>
      </div>

      <div v-if="paginatedList.length" class="overflow-x-auto">
        <el-table :data="paginatedList" style="width: 100%">
          <el-table-column prop="code" label="工艺编号" min-width="160" />
          <el-table-column prop="name" label="工艺名称" min-width="200" />
          <el-table-column prop="version" label="版本号" min-width="120" />
          <el-table-column prop="product" label="所属产品" min-width="160" />
          <el-table-column prop="operator" label="维护人员" min-width="130" />
          <el-table-column prop="operationTime" label="操作时间" min-width="180" />
          <el-table-column label="操作" width="260" fixed="right">
            <template #default="{ row }">
              <div class="flex gap-2 overflow-x-auto">
                <el-button link size="small" @click="$router.push(`/working-plans/${row.id}`)">详情</el-button>
                <el-button link size="small" @click="$router.push(`/working-plans/${row.id}/edit`)">编辑</el-button>
                <el-button link type="danger" size="small" @click="handleDelete(row.id)">删除</el-button>
              </div>
            </template>
          </el-table-column>
        </el-table>
      </div>

      <div v-else class="px-6 py-16 text-center">
        <div class="mx-auto max-w-md space-y-3">
          <p class="empty-state-title text-sm font-semibold">当前没有匹配工艺路线</p>
          <p class="empty-state-note text-sm">调整筛选条件后重试，或直接创建新的工艺路线模板。</p>
          <button class="action-button action-button-primary" @click="$router.push('/working-plans/new')">
            创建首条路线
          </button>
        </div>
      </div>

      <div class="section-divider flex flex-col gap-4 border-t px-6 py-5 lg:flex-row lg:items-center lg:justify-between">
        <div class="page-meta-text text-sm">
          共 {{ filteredList.length }} 条记录，当前显示 {{ paginatedList.length }} 条
        </div>

        <div class="flex flex-col gap-3 sm:flex-row sm:items-center">
          <el-pagination
            v-model:current-page="pagination.page"
            v-model:page-size="pagination.size"
            :total="filteredList.length"
            layout="prev, pager, next"
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
import { computed, onMounted, reactive, ref, watch } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/workingPlans'

const search = ref('')
const list = ref([])
const pagination = reactive({
  page: 1,
  size: 10
})

const normalizePlan = (item = {}) => ({
  id: item.id ?? '',
  code: item.code ?? '未设置编号',
  name: item.name ?? '未命名路线',
  version: item.version ?? 'V1.0',
  product: item.product ?? '未绑定产品',
  operator: item.operator ?? '未分配',
  operationTime: item.operationTime ?? '-'
})

const filteredList = computed(() => {
  const keyword = search.value.trim()
  if (!keyword) {
    return list.value
  }

  return list.value.filter((item) => {
    return [item.code, item.name, item.product, item.operator]
      .filter(Boolean)
      .some((field) => field.includes(keyword))
  })
})

const paginatedList = computed(() => {
  const start = (pagination.page - 1) * pagination.size
  const end = start + pagination.size
  return filteredList.value.slice(start, end)
})

const summaryCards = computed(() => {
  const products = new Set(list.value.map((item) => item.product).filter(Boolean)).size
  const operators = new Set(list.value.map((item) => item.operator).filter(Boolean)).size
  const versions = new Set(list.value.map((item) => item.version).filter(Boolean)).size

  return [
    {
      label: 'Route Count',
      value: list.value.length,
      note: '当前纳入管理的工艺路线总量'
    },
    {
      label: 'Products',
      value: products,
      note: '已关联路线的产品数量'
    },
    {
      label: 'Maintainers',
      value: operators,
      note: '当前参与维护的人员数量'
    },
    {
      label: 'Versions',
      value: versions,
      note: '路线版本在库中的覆盖情况'
    }
  ]
})

const clampPage = () => {
  const maxPage = Math.max(1, Math.ceil(filteredList.value.length / pagination.size))
  if (pagination.page > maxPage) {
    pagination.page = maxPage
  }
}

const loadData = async () => {
  try {
    const { data } = await api.getList()
    list.value = Array.isArray(data) ? data.map(normalizePlan) : []
    clampPage()
  } catch {
    list.value = []
    clampPage()
    ElMessage.error('加载工艺路线列表失败')
  }
}

const handleReset = () => {
  search.value = ''
  pagination.page = 1
}

const handlePageSizeChange = () => {
  pagination.page = 1
  clampPage()
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除该工艺路线？', '提示', { type: 'warning' })
    await api.delete(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') {
      ElMessage.error('删除失败')
    }
  }
}

watch(search, () => {
  pagination.page = 1
  clampPage()
})

onMounted(loadData)
</script>
