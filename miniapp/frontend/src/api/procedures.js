import request from './request'

export const getProcedures = () => request.get('/procedures')
export const updateProcedure = (id, data) => request.put(`/procedures/${id}`, data)
