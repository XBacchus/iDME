<template>
  <div class="p-8">
    <div class="floating-island">
      <div class="flex justify-between items-center mb-6">
        <h2 class="text-lg font-semibold text-white">版本历史 - {{ partInfo.partName }}</h2>
        <div class="flex gap-2">
          <el-button size="small" @click="handleCompare" :disabled="selectedVersions.length !== 2">版本对比</el-button>
          <el-button type="primary" size="small" @click="handleCreateVersion">创建新版本</el-button>
        </div>
      </div>

      <el-table :data="versionList" @selection-change="handleSelectionChange">
        <el-table-column type="selection" width="55" />
        <el-table-column prop="version" label="版本号" width="120" />
        <el-table-column prop="description" label="版本说明" />
        <el-table-column prop="status" label="状态" width="100">
          <template #default="{ row }">
            <el-tag :type="row.status === 'published' ? 'success' : 'info'" size="small">
              {{ row.status === 'published' ? '已发布' : '草稿' }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="createdBy" label="创建人" width="120" />
        <el-table-column prop="createdAt" label="创建时间" width="180" />
        <el-table-column label="操作" width="200">
          <template #default="{ row }">
            <el-button link type="primary" size="small" @click="handleView(row)">查看</el-button>
            <el-button link type="warning" size="small" @click="handleRollback(row)">回滚</el-button>
            <el-button v-if="row.status === 'draft'" link type="success" size="small" @click="handlePublish(row)">发布</el-button>
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

    <el-dialog v-model="compareVisible" title="版本对比" width="800px">
      <div v-if="compareData" class="grid grid-cols-2 gap-4">
        <div>
          <h3 class="text-white mb-2">版本 {{ compareData.v1.version }}</h3>
          <div class="text-sm text-gray-400">{{ compareData.v1.description }}</div>
        </div>
        <div>
          <h3 class="text-white mb-2">版本 {{ compareData.v2.version }}</h3>
          <div class="text-sm text-gray-400">{{ compareData.v2.description }}</div>
        </div>
      </div>
      <template #footer>
        <el-button @click="compareVisible = false">关闭</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { useRouter, useRoute } from 'vue-router'
import { ElMessage, ElMessageBox } from 'element-plus'
import { getPartById, getVersionHistory, createVersion, compareVersions, rollbackVersion, updateVersionStatus } from '@/api/parts'

const router = useRouter()
const route = useRoute()
const partInfo = ref({})
const versionList = ref([])
const dialogVisible = ref(false)
const compareVisible = ref(false)
const compareData = ref(null)
const selectedVersions = ref([])
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

const handleSelectionChange = (selection) => {
  selectedVersions.value = selection
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

const handleCompare = async () => {
  const [v1, v2] = selectedVersions.value
  const res = await compareVersions(route.params.id, v1.version, v2.version)
  compareData.value = res.data
  compareVisible.value = true
}

const handleRollback = async (row) => {
  await ElMessageBox.confirm(`确定回滚到版本 ${row.version}？`, '提示', { type: 'warning' })
  await rollbackVersion(route.params.id, row.version)
  ElMessage.success('回滚成功')
  loadData()
}

const handlePublish = async (row) => {
  await updateVersionStatus(route.params.id, row.version, 'published')
  ElMessage.success('发布成功')
  loadData()
}

const handleView = (row) => {
  router.push(`/parts/edit/${route.params.id}?version=${row.version}`)
}

onMounted(loadData)
</script>
