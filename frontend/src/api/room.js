import request from '@/utils/request'

export function getRoomPage(params) {
  return request.get('/room/page', { params })
}

export function getAvailableRooms(buildingId) {
  return request.get('/room/available', { params: { buildingId } })
}

export function getRoomsByBuilding(buildingId) {
  return request.get(`/room/building/${buildingId}`)
}

export function getRoomStats() {
  return request.get('/room/stats')
}

export function getFloorStats(buildingId) {
  return request.get(`/room/floorStats/${buildingId}`)
}

export function getRoomById(id) {
  return request.get(`/room/${id}`)
}

export function saveRoom(data) {
  return request.post('/room', data)
}

export function updateRoom(data) {
  return request.put('/room', data)
}

export function deleteRoom(id) {
  return request.delete(`/room/${id}`)
}

export function assignRoom(data) {
  return request.post('/room/assign', null, { params: data })
}

export function leaveRoom(studentId) {
  return request.post(`/room/leave/${studentId}`)
}
