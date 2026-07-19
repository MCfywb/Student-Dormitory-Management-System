import request from '@/utils/request'

export function getBuildingPage(params) {
  return request.get('/building/page', { params })
}

export function getBuildingList() {
  return request.get('/building/list')
}

export function getBuildingStats() {
  return request.get('/building/stats')
}

export function getBuildingById(id) {
  return request.get(`/building/${id}`)
}

export function getBuildingDetail(id) {
  return request.get(`/building/detail/${id}`)
}

export function saveBuilding(data) {
  return request.post('/building', data)
}

export function updateBuilding(data) {
  return request.put('/building', data)
}

export function deleteBuilding(id) {
  return request.delete(`/building/${id}`)
}
