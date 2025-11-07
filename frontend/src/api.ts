import axios from 'axios'
//const baseURL = (import.meta as any).env.VITE_BACKEND_URL || 'http://localhost:8080'
const baseURL = '/';
export const apiKey = () => localStorage.getItem('X-API-KEY') || 'dev-secret'
export const http = axios.create({ baseURL, headers: { 'Content-Type': 'application/json' } })
http.interceptors.request.use(cfg => {
  cfg.headers = cfg.headers || {}
  ;(cfg.headers as any)['X-API-KEY'] = apiKey()
  console.log(`[API] ${cfg.method?.toUpperCase()} ${cfg.baseURL}${cfg.url}`)
  return cfg
})
http.interceptors.response.use(
  r => { console.log('[API RES]', r.status, r.data); return r },
  e => { console.error('[API ERR]', e?.response?.status, e?.response?.data); return Promise.reject(e) }
)
