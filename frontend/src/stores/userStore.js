import { ref, computed } from 'vue'

const userInfo = ref(null)

const initUserInfo = () => {
  const stored = localStorage.getItem('userInfo')
  if (stored) {
    userInfo.value = JSON.parse(stored)
  }
}

const setUserInfo = (info) => {
  userInfo.value = info
  if (info) {
    localStorage.setItem('userInfo', JSON.stringify(info))
  } else {
    localStorage.removeItem('userInfo')
  }
}

const updateAvatar = (avatarUrl) => {
  if (userInfo.value) {
    userInfo.value = { ...userInfo.value, avatar: avatarUrl }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }
}

const updateUserInfo = (updates) => {
  if (userInfo.value) {
    userInfo.value = { ...userInfo.value, ...updates }
    localStorage.setItem('userInfo', JSON.stringify(userInfo.value))
  }
}

const clearUserInfo = () => {
  userInfo.value = null
  localStorage.removeItem('userInfo')
  localStorage.removeItem('studentInfo')
}

const isLoggedIn = computed(() => !!userInfo.value)

initUserInfo()

export function useUserStore() {
  return {
    userInfo: computed(() => userInfo.value),
    isLoggedIn,
    setUserInfo,
    updateAvatar,
    updateUserInfo,
    clearUserInfo
  }
}
