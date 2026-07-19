import request from '@/utils/request'

export function getVisitorPage(params) {
  return request.get('/visitor/page', { params })
}

export function getVisitorStats() {
  return request.get('/visitor/stats')
}

export function getVisitorById(id) {
  return request.get(`/visitor/${id}`)
}

export function saveVisitor(data) {
  return request.post('/visitor', data)
}

export function leaveVisitor(id) {
  return request.post(`/visitor/leave/${id}`)
}

export function deleteVisitor(id) {
  return request.delete(`/visitor/${id}`)
}
