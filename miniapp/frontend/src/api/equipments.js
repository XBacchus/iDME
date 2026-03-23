import request from '@/utils/request'

export const getEquipments = (params) => request.get('/equipments', { params })

export const getEquipment = (id) => request.get(`/equipments/${id}`)

export const createEquipment = (data) => request.post('/equipments', data)

export const updateEquipment = (id, data) => request.put(`/equipments/${id}`, data)

export const deleteEquipment = (id) => request.delete(`/equipments/${id}`)
