import request from '@/utils/request'

export function getHygienePage(params) {
  return request.get('/hygiene/page', { params })
}

export function getHygieneByRoom(roomId) {
  return request.get(`/hygiene/room/${roomId}`)
}

export function getHygieneStats() {
  return request.get('/hygiene/stats')
}

export function getRecentChecks(limit = 10) {
  return request.get('/hygiene/recent', { params: { limit } })
}

export function getBuildingHygieneStats() {
  return request.get('/hygiene/buildingStats')
}

export function getHygieneById(id) {
  return request.get(`/hygiene/${id}`)
}

export function saveHygiene(data) {
  return request.post('/hygiene', data)
}

export function updateHygiene(data) {
  return request.put('/hygiene', data)
}

export function deleteHygiene(id) {
  return request.delete(`/hygiene/${id}`)
}
