import { ref } from 'vue'
import { ElMessage } from 'element-plus'

export function useLoading() {
  const loading = ref(false)

  const withLoading = async (fn, errorMsg = '操作失败') => {
    loading.value = true
    try {
      return await fn()
    } catch (error) {
      ElMessage.error(errorMsg)
      throw error
    } finally {
      loading.value = false
    }
  }

  return { loading, withLoading }
}
