import request from '@/utils/request'

export function getNoticePage(params) {
  return request.get('/notice/page', { params })
}

export function getNoticeList(limit = 10) {
  return request.get('/notice/list', { params: { limit } })
}

export function getNoticeStats() {
  return request.get('/notice/stats')
}

export function getNoticeById(id) {
  return request.get(`/notice/${id}`)
}

export function saveNotice(data) {
  return request.post('/notice', data)
}

export function updateNotice(data) {
  return request.put('/notice', data)
}

export function deleteNotice(id) {
  return request.delete(`/notice/${id}`)
}
