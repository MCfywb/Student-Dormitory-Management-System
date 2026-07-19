import request from '@/utils/request'

export function getStudentPage(params) {
  return request.get('/student/page', { params })
}

export function getStudentByUserId(userId) {
  return request.get(`/student/user/${userId}`)
}

export function getStudentById(id) {
  return request.get(`/student/${id}`)
}

export function addStudent(data) {
  return request.post('/student', data)
}

export function getStudentDetail(id) {
  return request.get(`/student/detail/${id}`)
}

export function getCollegeStats() {
  return request.get('/student/collegeStats')
}

export function getGradeStats() {
  return request.get('/student/gradeStats')
}

export function getRoommates(roomId) {
  return request.get(`/student/roommates/${roomId}`)
}

export function updateStudent(data) {
  return request.put('/student', data)
}

export function deleteStudent(id) {
  return request.delete(`/student/${id}`)
}
