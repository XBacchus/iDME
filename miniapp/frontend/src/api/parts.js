import request from '@/utils/request'

export const getPartList = (params) => request.get('/parts', { params })

export const getPartById = (id) => request.get(`/parts/${id}`)

export const createPart = (data) => request.post('/parts', data)

export const updatePart = (id, data) => request.put(`/parts/${id}`, data)

export const deletePart = (id) => request.delete(`/parts/${id}`)

export const getPartBOM = (id) => request.get(`/parts/${id}/bom`)

export const updatePartBOM = (id, data) => request.put(`/parts/${id}/bom`, data)

export const getCategoryTree = () => request.get('/parts/categories')

export const createCategory = (data) => request.post('/parts/categories', data)

export const updateCategory = (id, data) => request.put(`/parts/categories/${id}`, data)

export const deleteCategory = (id) => request.delete(`/parts/categories/${id}`)

export const getVersionHistory = (id) => request.get(`/parts/${id}/versions`)

export const createVersion = (id, data) => request.post(`/parts/${id}/versions`, data)

export const compareVersions = (id, v1, v2) => request.get(`/parts/${id}/versions/compare`, { params: { v1, v2 } })

export const rollbackVersion = (id, version) => request.post(`/parts/${id}/versions/${version}/rollback`)

export const updateVersionStatus = (id, version, status) => request.put(`/parts/${id}/versions/${version}/status`, { status })
