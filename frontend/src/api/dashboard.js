import request from '@/utils/request'

export function getOverview() {
  return request.get('/dashboard/overview')
}

export function getBuildingStats() {
  return request.get('/dashboard/buildingStats')
}

export function getRoomStats() {
  return request.get('/dashboard/roomStats')
}

export function getStudentCollegeStats() {
  return request.get('/dashboard/studentCollegeStats')
}

export function getStudentGradeStats() {
  return request.get('/dashboard/studentGradeStats')
}

export function getHygieneStats() {
  return request.get('/dashboard/hygieneStats')
}

export function getFacilityStats() {
  return request.get('/dashboard/facilityStats')
}

export function getFacilityTypeStats() {
  return request.get('/dashboard/facilityTypeStats')
}

export function getRepairStats() {
  return request.get('/dashboard/repairStats')
}

export function getFeeStats() {
  return request.get('/dashboard/feeStats')
}

export function getFeeTrend() {
  return request.get('/dashboard/feeTrend')
}

export function getVisitorStats() {
  return request.get('/dashboard/visitorStats')
}

export function getRecentHygiene() {
  return request.get('/dashboard/recentHygiene')
}

export function getBuildingHygieneStats() {
  return request.get('/dashboard/buildingHygieneStats')
}
