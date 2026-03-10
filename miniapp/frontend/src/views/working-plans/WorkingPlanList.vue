<template>
  <div class="p-8 space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-lg font-bold text-white">工艺路线管理</h1>
      <button @click="$router.push('/working-plans/new')" class="bg-blue-600 hover:bg-blue-500 px-4 py-1.5 text-xs text-white rounded-sm transition-colors">
        + 新增工艺路线
      </button>
    </div>

    <div class="floating-island overflow-hidden">
      <div class="p-4 border-b border-white/5 bg-white/5">
        <input v-model="search" placeholder="搜索工艺编号/名称..." class="w-64 px-3 py-1.5 text-xs bg-black/20 border border-white/10 text-white rounded outline-none focus:ring-1 focus:ring-blue-500">
      </div>

      <table class="w-full text-sm text-white">
        <thead>
          <tr class="text-gray-500 border-b border-white/5">
            <th class="px-6 py-4 font-medium text-left">工艺编号</th>
            <th class="px-6 py-4 font-medium text-left">工艺名称</th>
            <th class="px-6 py-4 font-medium text-left">版本号</th>
            <th class="px-6 py-4 font-medium text-left">所属产品</th>
            <th class="px-6 py-4 font-medium text-left">操作人员</th>
            <th class="px-6 py-4 font-medium text-right">操作</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-white/5">
          <tr v-for="item in filteredList" :key="item.id" class="hover:bg-white/5 transition-colors">
            <td class="px-6 py-4 font-mono text-xs text-blue-400">{{ item.code }}</td>
            <td class="px-6 py-4">{{ item.name }}</td>
            <td class="px-6 py-4 text-gray-400">{{ item.version }}</td>
            <td class="px-6 py-4 text-gray-400">{{ item.product }}</td>
            <td class="px-6 py-4 text-gray-400">{{ item.operator }}</td>
            <td class="px-6 py-4 text-right space-x-3">
              <button @click="$router.push(`/working-plans/${item.id}`)" class="text-gray-500 hover:text-white transition-colors">详情</button>
              <button @click="$router.push(`/working-plans/${item.id}/edit`)" class="text-gray-500 hover:text-white transition-colors">编辑</button>
              <button @click="handleDelete(item.id)" class="text-red-400/70 hover:text-red-400 transition-colors">删除</button>
            </td>
          </tr>
        </tbody>
      </table>
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
    item.code.includes(search.value) || item.name.includes(search.value)
  )
})

const loadData = async () => {
  try {
    const { data } = await api.getList()
    list.value = data
  } catch (error) {
    // 使用Mock数据
    list.value = [
      { id: 1, code: 'WP-2023-001', name: '中心轮零件加工', version: 'V1.0', product: '中心轮组件', operator: '张工' }
    ]
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

<style scoped>
.floating-island {
  background: #2C2C2E;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border-radius: 8px;
}
</style>
