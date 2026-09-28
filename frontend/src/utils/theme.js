import { ref, computed, watch } from 'vue'

const STORAGE_KEY = 'app-theme'
const mediaQuery = window.matchMedia('(prefers-color-scheme: dark)')

// 用户偏好：auto-跟随系统 / light-浅色 / dark-深色，默认跟随系统
const preference = ref(localStorage.getItem(STORAGE_KEY) || 'auto')
const systemPrefersDark = ref(mediaQuery.matches)

export const isDark = computed(() => {
  if (preference.value === 'dark') return true
  if (preference.value === 'light') return false
  return systemPrefersDark.value
})

function applyTheme() {
  document.documentElement.classList.toggle('dark', isDark.value)
}

watch(isDark, applyTheme)

export function getThemePreference() {
  return preference.value
}

export function setThemePreference(value) {
  preference.value = value
  localStorage.setItem(STORAGE_KEY, value)
}

export function initTheme() {
  applyTheme()
  mediaQuery.addEventListener('change', (e) => {
    systemPrefersDark.value = e.matches
  })
}
