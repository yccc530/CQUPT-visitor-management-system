import request from '@/utils/request'
import { setCurrentUser, setToken, removeToken } from '@/utils/auth'

export async function login(data) {
  const result = await request.post('/auth/login', data)
  if (result?.token) setToken(result.token)
  if (result?.user) setCurrentUser(result.user)
  return result
}

export async function logout() {
  try {
    await request.post('/auth/logout')
  } finally {
    removeToken()
  }
}

export function getMe() {
  return request.get('/auth/me')
}

export function getMenus() {
  return request.get('/auth/menus')
}