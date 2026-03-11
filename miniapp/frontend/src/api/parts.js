import request from '@/utils/request'

export const getPartList = (params) => request.get('/api/parts', { params })

export const getPartById = (id) => request.get(`/api/parts/${id}`)

export const createPart = (data) => request.post('/api/parts', data)

export const updatePart = (id, data) => request.put(`/api/parts/${id}`, data)

export const deletePart = (id) => request.delete(`/api/parts/${id}`)

export const getPartBOM = (id) => request.get(`/api/parts/${id}/bom`)

export const updatePartBOM = (id, data) => request.put(`/api/parts/${id}/bom`, data)

export const getCategoryTree = () => request.get('/api/parts/categories')

export const createCategory = (data) => request.post('/api/parts/categories', data)

export const updateCategory = (id, data) => request.put(`/api/parts/categories/${id}`, data)

export const deleteCategory = (id) => request.delete(`/api/parts/categories/${id}`)

export const getVersionHistory = (id) => request.get(`/api/parts/${id}/versions`)

export const createVersion = (id, data) => request.post(`/api/parts/${id}/versions`, data)

export const compareVersions = (id, v1, v2) => request.get(`/api/parts/${id}/versions/compare`, { params: { v1, v2 } })

export const rollbackVersion = (id, version) => request.post(`/api/parts/${id}/versions/${version}/rollback`)

export const updateVersionStatus = (id, version, status) => request.put(`/api/parts/${id}/versions/${version}/status`, { status })
