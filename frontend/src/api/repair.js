import request from '@/utils/request'

export function getRepairPage(params) {
  return request.get('/repair/page', { params })
}

export function getMyRepairs(userId) {
  return request.get(`/repair/my/${userId}`)
}

export function getRepairStats() {
  return request.get('/repair/stats')
}

export function getRepairById(id) {
  return request.get(`/repair/${id}`)
}

export function saveRepair(data) {
  return request.post('/repair', data)
}

export function handleRepair(id, status, handleResult, handlerId, handlerName, repairCost) {
  return request.put('/repair/handle', null, {
    params: { id, status, handleResult, handlerId, handlerName, repairCost }
  })
}

export function deleteRepair(id) {
  return request.delete(`/repair/${id}`)
}
