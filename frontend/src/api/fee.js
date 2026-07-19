import request from '@/utils/request'

export function getFeeTypeList() {
  return request.get('/feeType/list')
}

export function getFeeTypePage() {
  return request.get('/feeType/page')
}

export function getFeeTypeById(id) {
  return request.get(`/feeType/${id}`)
}

export function saveFeeType(data) {
  return request.post('/feeType', data)
}

export function updateFeeType(data) {
  return request.put('/feeType', data)
}

export function deleteFeeType(id) {
  return request.delete(`/feeType/${id}`)
}

export function getFeePage(params) {
  return request.get('/fee/page', { params })
}

export function getFeeByStudent(studentId) {
  return request.get(`/fee/student/${studentId}`)
}

export function getMyFees(studentId) {
  return request.get(`/fee/my/${studentId}`)
}

export function getFeeStats() {
  return request.get('/fee/stats')
}

export function getFeeTrend() {
  return request.get('/fee/trend')
}

export function getFeeById(id) {
  return request.get(`/fee/${id}`)
}

export function saveFee(data) {
  return request.post('/fee', data)
}

export function payFee(id, payMethod) {
  return request.post('/fee/pay', null, { params: { id, payMethod } })
}

export function updateFee(data) {
  return request.put('/fee', data)
}

export function deleteFee(id) {
  return request.delete(`/fee/${id}`)
}
