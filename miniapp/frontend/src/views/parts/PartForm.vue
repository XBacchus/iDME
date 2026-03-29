<template>
  <div class="mx-auto w-full max-w-4xl space-y-6">
    <section>
      <p class="eyebrow">Warehouse / Material Form</p>
      <h1 class="page-heading">{{ isEdit ? '编辑物料' : '新增物料' }}</h1>
      <p class="page-subtitle">
        维护物料主数据、分类归属与库存信息，保存后会同步回当前物料管理视图。
      </p>
    </section>

    <section class="floating-island mx-auto w-full max-w-4xl">
      <el-form
        ref="formRef"
        :model="form"
        :rules="rules"
        label-width="100px"
        class="part-form-inline-errors space-y-2"
      >
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
          <el-tree-select
            v-model="form.categoryId"
            :data="categories"
            :props="{ label: 'name', value: 'id' }"
            placeholder="请选择分类"
          />
        </el-form-item>

        <el-form-item class="!mb-0 pt-4">
          <div class="flex flex-wrap gap-3">
            <button class="action-button action-button-primary" type="button" @click="handleSubmit">
              保存
            </button>
            <button class="action-button action-button-secondary" type="button" @click="router.back()">
              取消
            </button>
          </div>
        </el-form-item>
      </el-form>
    </section>
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

<style scoped>
.part-form-inline-errors :deep(.el-form-item) {
  margin-bottom: 1.35rem;
}

.part-form-inline-errors :deep(.el-form-item__content) {
  display: grid;
  grid-template-columns: minmax(0, 1fr) 13rem;
  align-items: center;
  column-gap: 0.9rem;
}

.part-form-inline-errors :deep(.el-form-item__content > :not(.el-form-item__error)) {
  grid-column: 1;
  min-width: 0;
  width: 100%;
}

.part-form-inline-errors :deep(.el-form-item__error) {
  position: static;
  grid-column: 2;
  padding-top: 0;
  line-height: 1.35;
  white-space: normal;
}

.part-form-inline-errors :deep(.el-input-number) {
  width: 100%;
}

@media (max-width: 900px) {
  .part-form-inline-errors :deep(.el-form-item__content) {
    grid-template-columns: minmax(0, 1fr);
  }

  .part-form-inline-errors :deep(.el-form-item__error) {
    grid-column: 1;
    justify-self: start;
    margin-top: 0.35rem;
  }
}
</style>
