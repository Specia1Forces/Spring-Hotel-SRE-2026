import { computed, reactive } from 'vue'
import { api, clearCsrf, refreshCsrf } from './api.js'

const state = reactive({
  user: null,
  loaded: false,
})

export const currentUser = computed(() => state.user)
export const isAuthenticated = computed(() => Boolean(state.user))

export function hasAnyRole(...roles) {
  return roles.some((role) => state.user?.roles?.includes(role))
}

export async function loadCurrentUser() {
  try {
    state.user = await api('/auth/me')
  } catch (error) {
    if (![0, 401].includes(error.status)) throw error
    state.user = null
  } finally {
    state.loaded = true
  }
  return state.user
}

export async function login(username, password) {
  await refreshCsrf()
  state.user = await api('/auth/login', {
    method: 'POST',
    body: { username, password },
  })
  clearCsrf()
  await refreshCsrf()
  state.loaded = true
  return state.user
}

export async function logout() {
  try {
    await api('/auth/logout', { method: 'POST' })
  } finally {
    clearAuthentication()
  }
}

export function clearAuthentication() {
  state.user = null
  state.loaded = true
  clearCsrf()
}

export function authIsLoaded() {
  return state.loaded
}
