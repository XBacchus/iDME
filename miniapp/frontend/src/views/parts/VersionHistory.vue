<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-lg font-semibold text-white">版本历史 - {{ partInfo.partName }}</h2>
        <el-button type="primary" size="small" @click="handleCreateVersion">创建新版本</el-button>
      </div>

      <el-table :data="versionList">
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="description" label="版本说明" />
        <el-table-column prop="createdBy" label="创建人" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="100">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
          </template>
        </el-table-column>
      </el-table>

      <div class="mt-6">
        <el-button @click="router.back()">返回</el-button>
      </div>
    </div>

    <el-dialog v-model="dialogVisible" title="创建新版本" width="500px">
      <el-form :model="form" label-width="80px">
        <el-form-item label="版本说明">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入版本说明" />
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
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage } from 'element-plus'
import { getPartById, getVersionHistory, createVersion } from '@/api/parts'

const router = useRouter()
const route = useRoute()
const partInfo = ref({})
const versionList = ref([])
const dialogVisible = ref(false)
const form = reactive({ description: '' })

const loadData = async () => {
  const id = route.params.id
  const [part, versions] = await Promise.all([
    getPartById(id),
    getVersionHistory(id)
  ])
  partInfo.value = part.data
  versionList.value = versions.data
}

const handleCreateVersion = () => {
  form.description = ''
  dialogVisible.value = true
}

const handleSubmit = async () => {
  await createVersion(route.params.id, form)
  ElMessage.success('创建成功')
  dialogVisible.value = false
  loadData()
}

const handleView = (row) => {
  ElMessage.info('查看版本详情功能待实现')
}

onMounted(loadData)
</script>
