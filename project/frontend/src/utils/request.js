import axios from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { getToken, removeToken } from './auth'

const request = axios.create({
  baseURL: import.meta.env.VITE_API_BASE || '/api',
  timeout: 12000
})

request.interceptors.request.use((config) => {
  const token = getToken()
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})

request.interceptors.response.use(
  (response) => {
    const body = response.data
    if (body && Object.prototype.hasOwnProperty.call(body, 'success')) {
      if (body.success) return body.data
      if (body.code === 401) {
        removeToken()
        router.replace('/login')
      }
      ElMessage.error(body.message || '请求处理失败')
      return Promise.reject(new Error(body.message || '请求处理失败'))
    }
    return body
  },
  (error) => {
    const message = error.response?.data?.message || error.message || '网络请求失败'
    if (error.response?.status === 401) {
      removeToken()
      router.replace('/login')
    }
    ElMessage.error(message)
    return Promise.reject(new Error(message))
  }
)

export default request