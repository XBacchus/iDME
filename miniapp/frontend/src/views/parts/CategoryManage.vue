<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-lg font-semibold text-white">分类管理</h2>
        <el-button type="primary" size="small" @click="handleAdd(null)">新增根分类</el-button>
      </div>

      <CategoryTree :data="treeData" @add="handleAdd" @edit="handleEdit" @delete="handleDelete" />
    </div>

    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="400px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="分类名称">
          <el-input v-model="form.name" placeholder="请输入分类名称" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getCategoryTree, createCategory, updateCategory, deleteCategory } from '@/api/parts'
import CategoryTree from '@/components/business/CategoryTree.vue'

const treeData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const currentNode = ref(null)
const form = reactive({ name: '', parentId: null })

const dialogTitle = computed(() => isEdit.value ? '编辑分类' : '新增分类')

const loadData = async () => {
  try {
    const { data } = await getCategoryTree()
    treeData.value = data
  } catch (error) {
    // 使用Mock数据
    treeData.value = [
      { id: 1, name: '轴承', children: [] },
      { id: 2, name: '齿轮', children: [] },
      { id: 3, name: '外壳', children: [] }
    ]
  }
}

const handleAdd = (parent) => {
  isEdit.value = false
  form.name = ''
  form.parentId = parent?.id || null
  dialogVisible.value = true
}

const handleEdit = (node) => {
  isEdit.value = true
  currentNode.value = node
  form.name = node.name
  dialogVisible.value = true
}

const handleDelete = async (node) => {
  await ElMessageBox.confirm('确认删除该分类？', '提示', { type: 'warning' })
  await deleteCategory(node.id)
  ElMessage.success('删除成功')
  loadData()
}

const handleSubmit = async () => {
  if (isEdit.value) {
    await updateCategory(currentNode.value.id, form)
    ElMessage.success('更新成功')
  } else {
    await createCategory(form)
    ElMessage.success('创建成功')
  }
  dialogVisible.value = false
  loadData()
}

loadData()
</script>
