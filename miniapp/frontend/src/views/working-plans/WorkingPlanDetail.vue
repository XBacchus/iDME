<template>
  <div class="p-8 space-y-6">
    <div class="flex justify-between items-center">
      <h1 class="text-lg font-bold text-white">工艺路线详情</h1>
      <button @click="$router.back()" class="bg-gray-700 hover:bg-gray-600 px-4 py-1.5 text-xs text-white rounded-sm transition-colors">返回</button>
    </div>

    <div class="grid grid-cols-3 gap-6">
      <div class="floating-island p-6">
        <p class="text-[10px] uppercase font-bold text-gray-500 mb-4 tracking-widest">基本信息</p>
        <div class="space-y-3">
          <div>
            <label class="text-[10px] text-gray-500 block mb-1">工艺编号</label>
            <p class="text-blue-400 font-mono text-sm">{{ detail.code }}</p>
          </div>
          <div>
            <label class="text-[10px] text-gray-500 block mb-1">工艺名称</label>
            <p class="text-white text-sm">{{ detail.name }}</p>
          </div>
          <div>
            <label class="text-[10px] text-gray-500 block mb-1">版本号</label>
            <p class="text-white text-sm">{{ detail.version }}</p>
          </div>
          <div>
            <label class="text-[10px] text-gray-500 block mb-1">所属产品</label>
            <p class="text-gray-400 text-sm">{{ detail.product }}</p>
          </div>
          <div>
            <label class="text-[10px] text-gray-500 block mb-1">操作人员</label>
            <p class="text-gray-400 text-sm">{{ detail.operator }}</p>
          </div>
        </div>
      </div>

      <div class="col-span-2 floating-island p-6">
        <p class="text-[10px] uppercase font-bold text-gray-500 mb-4 tracking-widest">工艺描述</p>
        <p class="text-gray-300 text-sm leading-relaxed">{{ detail.description }}</p>
        <div class="mt-4 pt-4 border-t border-white/5">
          <label class="text-[10px] text-gray-500 block mb-2">设备使用情况</label>
          <p class="text-gray-400 text-sm">{{ detail.equipment }}</p>
        </div>
      </div>
    </div>

    <div class="space-y-4">
      <div class="flex items-center justify-between">
        <h2 class="text-lg font-bold text-white">工艺流程：{{ detail.name }}</h2>
        <button @click="showAddDialog = true" class="bg-gray-700 hover:bg-gray-600 px-4 py-1.5 text-xs text-white rounded-sm transition-colors">+ 添加工序</button>
      </div>
      <ProcessFlow :processes="processes" @update="handleUpdateProcesses" />
    </div>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api/workingPlans'
import ProcessFlow from '@/components/business/ProcessFlow.vue'

const route = useRoute()
const detail = ref({})
const processes = ref([])
const showAddDialog = ref(false)

const loadData = async () => {
  try {
    const { data } = await api.getById(route.params.id)
    detail.value = data
    const { data: procs } = await api.getProcesses(route.params.id)
    processes.value = procs
  } catch (error) {
    // 使用Mock数据
    detail.value = {
      code: 'WP-2023-001',
      name: '中心轮零件加工',
      version: 'V1.0',
      product: '中心轮组件',
      operator: '张工',
      description: '中心轮零件的完整加工工艺流程，包含毛坯制造、粗加工、精加工、检测和入库五个关键工序。',
      equipment: 'CNC加工中心、三坐标测量仪、磨床'
    }
    processes.value = [
      { id: 1, num: '01', name: '毛坯制造', location: '铸造车间', status: 'active' },
      { id: 2, num: '02', name: '粗加工', location: 'CNC-01 工位', status: 'pending' },
      { id: 3, num: '03', name: '精加工', location: 'CNC-05 工位', status: 'pending' },
      { id: 4, num: '04', name: '检测', location: 'CMM 三坐标', status: 'pending' },
      { id: 5, num: '05', name: '入库', location: '成品仓', status: 'pending' }
    ]
  }
}

const handleUpdateProcesses = async (newProcesses) => {
  try {
    await api.updateProcesses(route.params.id, newProcesses)
    processes.value = newProcesses
    ElMessage.success('更新成功')
  } catch (error) {
    ElMessage.error('更新失败')
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
