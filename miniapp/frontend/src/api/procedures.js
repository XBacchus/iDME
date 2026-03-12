import request from './request'

export const getProcedures = () => request.get('/api/procedures')
export const updateProcedure = (id, data) => request.put(`/api/procedures/${id}`, data)
