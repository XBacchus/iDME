<template>
  <div class="p-8">
    <div class="floating-island max-w-3xl">
      <h2 class="text-lg font-semibold text-white mb-6">{{ isEdit ? '编辑物料' : '新增物料' }}</h2>

      <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="物料编号" prop="partNo">
          <el-input v-model="form.partNo" placeholder="请输入物料编号" />
        </el-form-item>

        <el-form-item label="物料名称" prop="partName">
          <el-input v-model="form.partName" placeholder="请输入物料名称" />
        </el-form-item>

        <el-form-item label="规格型号" prop="specification">
          <el-input v-model="form.specification" placeholder="请输入规格型号" />
        </el-form-item>

        <el-form-item label="库存数量" prop="stockQty">
          <el-input-number v-model="form.stockQty" :min="0" />
        </el-form-item>

        <el-form-item label="供应商" prop="supplier">
          <el-input v-model="form.supplier" placeholder="请输入供应商" />
        </el-form-item>

        <el-form-item label="分类" prop="categoryId">
          <el-tree-select v-model="form.categoryId" :data="categories" :props="{ label: 'name', value: 'id' }" placeholder="请选择分类" />
        </el-form-item>

        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPartById, createPart, updatePart, getCategoryTree } from '@/api/parts'

const router = useRouter()
const route = useRoute()
const formRef = ref()
const categories = ref([])
const isEdit = ref(false)

const form = reactive({
  partNo: '',
  partName: '',
  specification: '',
  stockQty: 0,
  supplier: '',
  categoryId: null
})

const rules = {
  partNo: [{ required: true, message: '请输入物料编号', trigger: 'blur' }],
  partName: [{ required: true, message: '请输入物料名称', trigger: 'blur' }],
  specification: [{ required: true, message: '请输入规格型号', trigger: 'blur' }],
  stockQty: [{ required: true, message: '请输入库存数量', trigger: 'change' }],
  supplier: [{ required: true, message: '请输入供应商', trigger: 'blur' }],
  categoryId: [{ required: true, message: '请选择分类', trigger: 'change' }]
}

const loadData = async () => {
  const id = route.params.id
  if (id) {
    isEdit.value = true
    const { data } = await getPartById(id)
    Object.assign(form, data)
  }
}

const loadCategories = async () => {
  const { data } = await getCategoryTree()
  categories.value = data
}

const handleSubmit = async () => {
  await formRef.value.validate()
  if (isEdit.value) {
    await updatePart(route.params.id, form)
    ElMessage.success('更新成功')
  } else {
    await createPart(form)
    ElMessage.success('创建成功')
  }
  router.back()
}

onMounted(() => {
  loadCategories()
  loadData()
})
</script>
