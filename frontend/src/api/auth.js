import request from '@/utils/request'

const BASE_URL = 'http://localhost:8080/api'

export function login(data) {
  return request.post('/auth/login', data)
}

export function register(data) {
  return request.post('/auth/register', data)
}

export function getUserInfo(userId) {
  return request.get(`/auth/info/${userId}`)
}

export function updatePassword(data) {
  return request.post('/auth/updatePassword', data)
}

export function resetPassword(data) {
  return request.post('/auth/resetPassword', data)
}

export function uploadAvatar(file, userId) {
  const formData = new FormData()
  formData.append('file', file)
  formData.append('userId', userId)
  return request.post('/upload/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export function getAvatarUrl(avatar) {
  if (!avatar) return ''
  if (avatar.startsWith('http')) return avatar
  return BASE_URL + avatar
}
