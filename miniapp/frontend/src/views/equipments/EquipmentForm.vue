<template>
  <el-form ref="formRef" :model="form" :rules="rules" label-width="100px">
    <el-form-item label="设备编码" prop="equipmentCode">
      <el-input v-model="form.equipmentCode" placeholder="请输入设备编码" />
    </el-form-item>
    <el-form-item label="设备名称" prop="equipmentName">
      <el-input v-model="form.equipmentName" placeholder="请输入设备名称" />
    </el-form-item>
    <el-form-item label="生产厂家" prop="manufacturer">
      <el-input v-model="form.manufacturer" placeholder="请输入生产厂家" />
    </el-form-item>
    <el-form-item label="品牌" prop="brand">
      <el-input v-model="form.brand" placeholder="请输入品牌" />
    </el-form-item>
    <el-form-item label="规格型号" prop="specModel">
      <el-input v-model="form.specModel" placeholder="请输入规格型号" />
    </el-form-item>
    <el-form-item label="供应商" prop="supplier">
      <el-input v-model="form.supplier" placeholder="请输入供应商" />
    </el-form-item>
    <el-form-item label="生产日期" prop="productionDate">
      <el-date-picker v-model="form.productionDate" type="date" placeholder="选择日期" value-format="YYYY-MM-DD" style="width: 100%" />
    </el-form-item>
    <el-form-item label="使用年限" prop="serviceLifeYears">
      <el-input-number v-model="form.serviceLifeYears" :min="0" placeholder="年" style="width: 100%" />
    </el-form-item>
    <el-form-item label="折旧方式" prop="depreciationMethod">
      <el-select v-model="form.depreciationMethod" placeholder="请选择" style="width: 100%">
        <el-option label="直线法" value="直线法" />
        <el-option label="双倍余额递减法" value="双倍余额递减法" />
        <el-option label="年数总和法" value="年数总和法" />
      </el-select>
    </el-form-item>
    <el-form-item label="位置" prop="location">
      <el-input v-model="form.location" placeholder="请输入位置" />
    </el-form-item>
    <el-form-item label="技术参数">
      <el-input v-model="form.technicalParams" type="textarea" :rows="3" placeholder="请输入技术参数信息" />
    </el-form-item>
    <el-form-item label="备品备件">
      <el-input v-model="form.sparePartsInfo" type="textarea" :rows="3" placeholder="请输入备品备件信息" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click="handleSubmit">保存</el-button>
      <el-button @click="$emit('cancel')">取消</el-button>
    </el-form-item>
  </el-form>
</template>

<script setup>
import { ref, watch } from 'vue'
import { ElMessage } from 'element-plus'
import { createEquipment, updateEquipment } from '@/api/equipments'

const props = defineProps({
  data: Object
})

const emit = defineEmits(['success', 'cancel'])

const formRef = ref()
const createEmptyForm = () => ({
  equipmentCode: '',
  equipmentName: '',
  manufacturer: '',
  brand: '',
  specModel: '',
  supplier: '',
  productionDate: '',
  serviceLifeYears: 0,
  depreciationMethod: '',
  location: '',
  technicalParams: '',
  sparePartsInfo: ''
})

const normalizeFormData = (raw = {}) => ({
  id: raw.id || '',
  equipmentCode: raw.equipmentCode || raw.code || '',
  equipmentName: raw.equipmentName || raw.name || '',
  manufacturer: raw.manufacturer || '',
  brand: raw.brand || '',
  specModel: raw.specModel || raw.model || '',
  supplier: raw.supplier || '',
  productionDate: raw.productionDate || '',
  serviceLifeYears: Number(raw.serviceLifeYears ?? raw.serviceLife ?? 0),
  depreciationMethod: raw.depreciationMethod || raw.depreciation || '',
  location: raw.location || '',
  technicalParams: raw.technicalParams || '',
  sparePartsInfo: raw.sparePartsInfo || raw.spareParts || ''
})
const form = ref(createEmptyForm())

const rules = {
  equipmentCode: [{ required: true, message: '请输入设备编码', trigger: 'blur' }],
  equipmentName: [{ required: true, message: '请输入设备名称', trigger: 'blur' }],
  manufacturer: [{ required: true, message: '请输入生产厂家', trigger: 'blur' }]
}

watch(() => props.data, (val) => {
  if (val) {
    form.value = normalizeFormData(val)
  } else {
    form.value = createEmptyForm()
    formRef.value?.resetFields()
  }
}, { immediate: true })

const handleSubmit = async () => {
  try {
    await formRef.value.validate()
    if (form.value.id) {
      await updateEquipment(form.value.id, form.value)
      ElMessage.success('更新成功')
    } else {
      await createEquipment(form.value)
      ElMessage.success('创建成功')
    }
    emit('success')
  } catch (error) {
    if (error !== false) ElMessage.error('操作失败')
  }
}
</script>
