<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-lg font-semibold text-white">BOM 管理 - {{ partInfo.partName }}</h2>
        <el-button type="primary" size="small" @click="handleAddChild(null)">添加子项</el-button>
      </div>

      <BOMTree :data="bomData" @add="handleAddChild" @delete="handleDeleteNode" />

      <div class="mt-6">
        <el-button type="primary" @click="handleSave">保存</el-button>
        <el-button @click="router.back()">返回</el-button>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="添加子项" width="500px">
      <el-form :model="childForm" label-width="80px">
        <el-form-item label="子物料">
          <el-select v-model="childForm.childPartId" placeholder="选择物料" filterable>
            <el-option v-for="p in partList" :key="p.id" :label="p.partName" :value="p.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="数量">
          <el-input-number v-model="childForm.quantity" :min="1" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleConfirmAdd">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPartById, getPartBOM, updatePartBOM, getPartList } from '@/api/parts'
import BOMTree from '@/components/business/BOMTree.vue'

const router = useRouter()
const route = useRoute()
const partInfo = ref({})
const bomData = ref([])
const partList = ref([])
const dialogVisible = ref(false)
const currentParent = ref(null)
const childForm = reactive({ childPartId: null, quantity: 1 })

const loadData = async () => {
  const id = route.params.id
  try {
    const [part, bom, parts] = await Promise.all([
      getPartById(id),
      getPartBOM(id),
      getPartList({ page: 1, size: 1000 })
    ])
    partInfo.value = part.data
    bomData.value = bom.data
    partList.value = parts.data.records
  } catch (error) {
    // 使用Mock数据
    partInfo.value = { id, partNo: 'MTR-2023-001', partName: '中心轮轴承单元' }
    bomData.value = [
      { id: 2, partNo: 'MTR-2023-042', partName: '精密硬化齿轮', quantity: 2, children: [] }
    ]
    partList.value = [
      { id: 2, partNo: 'MTR-2023-042', partName: '精密硬化齿轮' },
      { id: 3, partNo: 'MTR-2023-050', partName: '铸铝外壳' }
    ]
  }
}

const handleAddChild = (parent) => {
  currentParent.value = parent
  childForm.childPartId = null
  childForm.quantity = 1
  dialogVisible.value = true
}

const handleConfirmAdd = () => {
  const child = partList.value.find(p => p.id === childForm.childPartId)
  const newNode = { ...child, quantity: childForm.quantity, children: [] }

  if (currentParent.value) {
    currentParent.value.children.push(newNode)
  } else {
    bomData.value.push(newNode)
  }
  dialogVisible.value = false
}

const handleDeleteNode = (node, parent) => {
  const list = parent ? parent.children : bomData.value
  const idx = list.indexOf(node)
  list.splice(idx, 1)
}

const handleSave = async () => {
  await updatePartBOM(route.params.id, bomData.value)
  ElMessage.success('保存成功')
}

onMounted(loadData)
</script>
