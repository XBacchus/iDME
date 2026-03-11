<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-2xl font-bold text-white">BOM 管理 - {{ partInfo.partName }}</h2>
        <div class="flex gap-2">
          <el-input v-model="searchKeyword" placeholder="搜索物料..." clearable class="w-64" size="small" />
          <el-button type="primary" size="default" @click="handleAddChild(null)">添加子项</el-button>
        </div>
      </div>

      <div class="bom-tree-container">
        <vue3-tree-org
          :data="treeData"
          :horizontal="true"
          :tool-bar="false"
          :scalable="true"
          :draggable="true"
        >
          <template #default="{ node }">
            <div class="bom-node" :class="[node.type, { highlight: searchKeyword && node.label?.includes(searchKeyword) }]">
              <div v-if="node.nodeLabel" class="node-label">{{ node.nodeLabel }}</div>
              <div v-if="node.quantity" class="node-qty">x{{ node.quantity }}</div>
              <div class="node-name">{{ node.label }}</div>
              <div v-if="node.version" class="node-version">{{ node.version }}</div>
            </div>
          </template>
        </vue3-tree-org>
      </div>

      <div class="mt-6 flex gap-2">
        <el-button type="primary" @click="handleSave">保存</el-button>
        <el-button @click="router.back()">返回</el-button>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑子项' : '添加子项'" width="500px">
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
import { ref, reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPartById, getPartBOM, updatePartBOM, getPartList } from '@/api/parts'

const router = useRouter()
const route = useRoute()
const partInfo = ref({})
const bomData = ref([])
const partList = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentParent = ref(null)
const currentNode = ref(null)
const childForm = reactive({ childPartId: null, quantity: 1 })
const searchKeyword = ref('')

const treeData = computed(() => {
  if (!partInfo.value.partName) return {}

  return {
    label: partInfo.value.partName,
    nodeLabel: '总成',
    version: partInfo.value.version,
    type: 'root',
    children: bomData.value.map(child => ({
      label: child.partName,
      nodeLabel: '子组件',
      quantity: child.quantity,
      version: child.version,
      type: 'child',
      children: child.children?.map(leaf => ({
        label: leaf.partName,
        quantity: leaf.quantity,
        version: leaf.version,
        type: 'leaf'
      })) || []
    }))
  }
})

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
    partInfo.value = { id, partNo: 'MTR-2023-001', partName: '中心轮组件', version: 'V1.0' }
    bomData.value = [
      { id: 2, partNo: 'MTR-2023-042', partName: '轴承单元', quantity: 2, version: 'V1.0', children: [
        { id: 4, partNo: 'MTR-2023-044', partName: '轴承', quantity: 1, version: 'V1.0' },
        { id: 5, partNo: 'MTR-2023-045', partName: '密封圈', quantity: 2, version: 'V1.0' }
      ]},
      { id: 3, partNo: 'MTR-2023-043', partName: '齿轮', quantity: 1, version: 'V1.0', children: [] }
    ]
    partList.value = [
      { id: 2, partNo: 'MTR-2023-042', partName: '轴承单元', version: 'V1.0' },
      { id: 3, partNo: 'MTR-2023-043', partName: '齿轮', version: 'V1.0' },
      { id: 4, partNo: 'MTR-2023-044', partName: '轴承', version: 'V1.0' }
    ]
  }
}

const handleAddChild = (parent) => {
  isEdit.value = false
  currentParent.value = parent
  childForm.childPartId = null
  childForm.quantity = 1
  dialogVisible.value = true
}

const handleEdit = (node) => {
  isEdit.value = true
  currentNode.value = node
  childForm.childPartId = node.id
  childForm.quantity = node.quantity
  dialogVisible.value = true
}

const handleConfirmAdd = () => {
  if (!childForm.childPartId || childForm.quantity < 1) {
    ElMessage.warning('请填写完整信息')
    return
  }

  if (isEdit.value) {
    currentNode.value.quantity = childForm.quantity
  } else {
    const child = partList.value.find(p => p.id === childForm.childPartId)
    const newNode = { ...child, quantity: childForm.quantity, children: [] }

    if (currentParent.value) {
      if (!currentParent.value.children) currentParent.value.children = []
      currentParent.value.children.push(newNode)
    } else {
      bomData.value.push(newNode)
    }
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

<style scoped>
.bom-tree-container {
  background: #1C1C1E;
  border-radius: 8px;
  padding: 2rem;
  overflow: hidden;
  min-height: 600px;
  height: calc(100vh - 300px);
}

/* 覆盖组件默认样式 */
.bom-tree-container :deep(.org-tree-container) {
  background: transparent !important;
}

.bom-tree-container :deep(.org-tree) {
  background: transparent !important;
}

.bom-tree-container :deep(.org-tree-node-label-inner) {
  background: transparent !important;
  border: none !important;
  padding: 0 !important;
}

.bom-tree-container :deep(.org-tree-node-btn) {
  background: #5E81AC !important;
  border-color: #5E81AC !important;
  color: white !important;
}

.bom-tree-container :deep(.org-tree-node::before),
.bom-tree-container :deep(.org-tree-node::after),
.bom-tree-container :deep(.org-tree-node-children::before) {
  border-color: rgba(94, 129, 172, 0.3) !important;
}

.bom-tree-container :deep(.horizontal .org-tree-node::before),
.bom-tree-container :deep(.horizontal .org-tree-node::after),
.bom-tree-container :deep(.horizontal .org-tree-node-children::before) {
  border-color: rgba(94, 129, 172, 0.3) !important;
}

.bom-node {
  position: relative;
  min-width: 160px;
  padding: 1rem;
  border-radius: 4px;
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
  transition: all 0.3s;
  background: #2C2C2E;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.bom-node.root {
  background: #2C2C2E;
  border: 2px solid #5E81AC;
}

.bom-node.child {
  background: #2C2C2E;
  border: 1px solid rgba(255, 255, 255, 0.2);
}

.bom-node.leaf {
  background: #2C2C2E;
  border: 1px solid rgba(255, 255, 255, 0.15);
  padding: 0.75rem;
  min-width: 140px;
}

.bom-node.highlight {
  border-color: #FFD700;
  box-shadow: 0 0 10px rgba(255, 215, 0, 0.3);
}

.node-label {
  font-size: 10px;
  color: rgba(236, 239, 244, 0.5);
  text-transform: uppercase;
}

.node-name {
  font-size: 14px;
  font-weight: 600;
  color: #ECEFF4;
}

.node-version {
  font-size: 11px;
  color: rgba(94, 129, 172, 0.8);
}

.node-qty {
  position: absolute;
  top: -12px;
  right: 8px;
  background: #5E81AC;
  color: white;
  font-size: 11px;
  font-weight: bold;
  padding: 2px 8px;
  border-radius: 9999px;
}
</style>

