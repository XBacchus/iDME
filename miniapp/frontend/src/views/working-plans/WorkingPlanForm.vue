<template>
  <div class="mx-auto w-full max-w-4xl space-y-6">
    <section>
      <p class="eyebrow">Manufacturing / Route Form</p>
      <h1 class="page-heading">{{ isEdit ? '编辑工艺路线' : '新增工艺路线' }}</h1>
      <p class="page-subtitle">
        维护工艺编号、版本、产品归属与操作信息，保存后会返回工艺路线列表。
      </p>
    </section>

    <section class="floating-island mx-auto w-full max-w-4xl">
      <el-form :model="form" label-width="100px" label-position="left" class="space-y-2">
        <el-form-item label="工艺编号">
          <el-input v-model="form.code" placeholder="例如 WP-2024-001" />
        </el-form-item>

        <el-form-item label="工艺名称">
          <el-input v-model="form.name" placeholder="例如 中心轴零件加工" />
        </el-form-item>

        <el-form-item label="版本号">
          <el-input v-model="form.version" placeholder="例如 1.0" />
        </el-form-item>

        <el-form-item label="所属产品">
          <el-input v-model="form.product" placeholder="产品名称" />
        </el-form-item>

        <el-form-item label="工艺描述">
          <el-input v-model="form.description" type="textarea" :rows="3" />
        </el-form-item>

        <el-form-item label="操作人员">
          <el-input v-model="form.operator" />
        </el-form-item>

        <el-form-item label="设备使用">
          <el-input v-model="form.equipment" placeholder="设备列表" />
        </el-form-item>

        <el-form-item label="操作时间">
          <el-input v-model="form.operationTime" placeholder="例如 2026-03-13 10:00:00" />
        </el-form-item>

        <el-form-item class="!mb-0 pt-4">
          <div class="flex flex-wrap gap-3">
            <button class="action-button action-button-primary" type="button" @click="handleSubmit">
              保存
            </button>
            <button class="action-button action-button-secondary" type="button" @click="$router.back()">
              取消
            </button>
          </div>
        </el-form-item>
      </el-form>
    </section>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import api from '@/api/workingPlans'

const route = useRoute()
const router = useRouter()
const isEdit = ref(false)
const form = ref({
  code: '',
  name: '',
  version: '',
  product: '',
  description: '',
  operator: '',
  equipment: '',
  operationTime: ''
})

const handleSubmit = async () => {
  try {
    if (isEdit.value) {
      await api.update(route.params.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await api.create(form.value)
      ElMessage.success('创建成功')
    }
    router.push('/working-plans')
  } catch (error) {
    ElMessage.error('操作失败')
  }
}

onMounted(async () => {
  if (route.params.id && route.path.includes('edit')) {
    isEdit.value = true
    const { data } = await api.getById(route.params.id)
    form.value = {
      ...form.value,
      ...data,
      operationTime: data.operationTime || ''
    }
  }
})
</script>
