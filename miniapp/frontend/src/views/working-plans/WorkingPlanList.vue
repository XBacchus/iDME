<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-white">工艺路线管理</h2>
        <el-button type="primary" size="default" @click="$router.push('/working-plans/new')">新增工艺路线</el-button>
      </div>

      <div class="mb-6">
        <el-input v-model="search" placeholder="搜索工艺编号/名称" clearable class="w-64" size="small" />
      </div>

      <el-table :data="filteredList" style="width: 100%">
        <el-table-column prop="code" label="工艺编号" width="150" />
        <el-table-column prop="name" label="工艺名称" width="200" />
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="product" label="所属产品" width="150" />
        <el-table-column prop="operator" label="操作人员" width="120" />
        <el-table-column prop="operationTime" label="操作时间" width="180" />
        <el-table-column label="操作" width="280" fixed="right">
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
  </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import api from '@/api/workingPlans'

const search = ref('')
const list = ref([])

const filteredList = computed(() => {
  if (!search.value) return list.value
  return list.value.filter(item =>
    (item.code || '').includes(search.value) || (item.name || '').includes(search.value)
  )
})

const loadData = async () => {
  try {
    const { data } = await api.getList()
    list.value = data
  } catch {
    list.value = []
    ElMessage.error('加载工艺路线列表失败')
  }
}

const handleDelete = async (id) => {
  try {
    await ElMessageBox.confirm('确认删除?', '提示', { type: 'warning' })
    await api.delete(id)
    ElMessage.success('删除成功')
    loadData()
  } catch (error) {
    if (error !== 'cancel') ElMessage.error('删除失败')
  }
}

onMounted(loadData)
</script>
