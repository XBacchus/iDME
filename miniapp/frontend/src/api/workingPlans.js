import request from '@/utils/request'

const API_BASE = '/working-plans'

export default {
  getList(params) {
    return request.get(API_BASE, { params })
  },
  getById(id) {
    return request.get(`${API_BASE}/${id}`)
  },
  create(data) {
    return request.post(API_BASE, data)
  },
  update(id, data) {
    return request.put(`${API_BASE}/${id}`, data)
  },
  delete(id) {
    return request.delete(`${API_BASE}/${id}`)
  },
  getProcesses(id) {
    return request.get(`${API_BASE}/${id}/processes`)
  },
  updateProcesses(id, processes) {
    return request.put(`${API_BASE}/${id}/processes`, processes)
  }
}
