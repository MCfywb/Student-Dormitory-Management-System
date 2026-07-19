import request from '@/utils/request'

export function getFacilityPage(params) {
  return request.get('/facility/page', { params })
}

export function getFacilityByRoom(roomId) {
  return request.get(`/facility/room/${roomId}`)
}

export function getFacilityStats() {
  return request.get('/facility/stats')
}

export function getFacilityTypeStats() {
  return request.get('/facility/typeStats')
}

export function getFacilityById(id) {
  return request.get(`/facility/${id}`)
}

export function saveFacility(data) {
  return request.post('/facility', data)
}

export function updateFacility(data) {
  return request.put('/facility', data)
}

export function deleteFacility(id) {
  return request.delete(`/facility/${id}`)
}
