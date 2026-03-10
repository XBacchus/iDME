import axios from 'axios'

const API_BASE = '/api/working-plans'

export default {
  getList(params) {
    return axios.get(API_BASE, { params })
  },
  getById(id) {
    return axios.get(`${API_BASE}/${id}`)
  },
  create(data) {
    return axios.post(API_BASE, data)
  },
  update(id, data) {
    return axios.put(`${API_BASE}/${id}`, data)
  },
  delete(id) {
    return axios.delete(`${API_BASE}/${id}`)
  },
  getProcesses(id) {
    return axios.get(`${API_BASE}/${id}/processes`)
  },
  updateProcesses(id, processes) {
    return axios.put(`${API_BASE}/${id}/processes`, processes)
  }
}
