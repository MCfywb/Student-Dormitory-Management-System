import request from '@/utils/request'

export function getManagerPage(params) {
  return request.get('/manager/page', { params })
}

export function addManager(data) {
  return request.post('/manager', data)
}

export function updateManager(data) {
  return request.put('/manager', data)
}

export function deleteManager(id) {
  return request.delete(`/manager/${id}`)
}
