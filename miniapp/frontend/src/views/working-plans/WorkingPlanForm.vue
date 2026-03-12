<template>
  <div class="p-8">
    <div class="mb-6">
      <h1 class="text-lg font-bold text-white">{{ isEdit ? '编辑工艺路线' : '新增工艺路线' }}</h1>
    </div>

    <div class="floating-island p-6 max-w-3xl">
      <el-form :model="form" label-width="100px" label-position="left">
        <el-form-item label="工艺编号">
          <el-input v-model="form.code" placeholder="如: WP-2024-001" />
        </el-form-item>
        <el-form-item label="工艺名称">
          <el-input v-model="form.name" placeholder="如: 中心轮零件加工" />
        </el-form-item>
        <el-form-item label="版本号">
          <el-input v-model="form.version" placeholder="如: 1.0" />
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
        <el-form-item>
          <el-button type="primary" @click="handleSubmit">保存</el-button>
          <el-button @click="$router.back()">取消</el-button>
        </el-form-item>
      </el-form>
    </div>
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
  equipment: ''
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
    form.value = data
  }
})
</script>

<style scoped>
.floating-island {
  background: #2C2C2E;
  border: 1px solid rgba(255, 255, 255, 0.08);
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.3);
  border-radius: 8px;
}
</style>
