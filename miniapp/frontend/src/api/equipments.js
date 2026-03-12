import request from '@/utils/request'

export const getEquipments = (params) => request.get('/api/equipments', { params })

export const getEquipment = (id) => request.get(`/api/equipments/${id}`)

export const createEquipment = (data) => request.post('/api/equipments', data)

export const updateEquipment = (id, data) => request.put(`/api/equipments/${id}`, data)

export const deleteEquipment = (id) => request.delete(`/api/equipments/${id}`)
