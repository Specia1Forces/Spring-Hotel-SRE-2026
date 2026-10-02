const API_URL = '/api/v1'

let csrf = null

async function request(url, options) {
  try {
    return await fetch(url, options)
  } catch {
    throw new ApiError('Backend недоступен. Проверьте, что Spring Boot запущен', 0)
  }
}

function errorMessage(data, status) {
  const message = data?.message || data?.error || `Ошибка HTTP ${status}`
  const fieldErrors = Object.entries(data?.fieldErrors || {})

  if (!fieldErrors.length) return message
  return `${message}: ${fieldErrors.map(([field, value]) => `${field} — ${value}`).join('; ')}`
}

export class ApiError extends Error {
  constructor(message, status, fieldErrors = {}) {
    super(message)
    this.name = 'ApiError'
    this.status = status
    this.fieldErrors = fieldErrors
  }
}

export async function refreshCsrf() {
  const response = await request(`${API_URL}/auth/csrf`, {
    credentials: 'include',
  })

  if (!response.ok) {
    throw new ApiError('Не удалось получить CSRF-токен', response.status)
  }

  csrf = await response.json()
  return csrf
}

export function clearCsrf() {
  csrf = null
}

export async function api(path, options = {}) {
  const method = options.method || 'GET'
  const headers = {
    Accept: 'application/json',
    ...options.headers,
  }

  if (options.body !== undefined) {
    headers['Content-Type'] = 'application/json'
  }

  if (!['GET', 'HEAD', 'OPTIONS'].includes(method.toUpperCase())) {
    if (!csrf) await refreshCsrf()
    headers[csrf.headerName] = csrf.token
  }

  const response = await request(`${API_URL}${path}`, {
    ...options,
    method,
    headers,
    credentials: 'include',
    body: options.body === undefined ? undefined : JSON.stringify(options.body),
  })

  const contentType = response.headers.get('content-type') || ''
  const data = contentType.includes('application/json') ? await response.json() : null

  if (!response.ok) {
    if (response.status === 401 && !path.startsWith('/auth/')) {
      window.dispatchEvent(new Event('auth:unauthorized'))
    }

    throw new ApiError(
      errorMessage(data, response.status),
      response.status,
      data?.fieldErrors,
    )
  }

  return response.status === 204 ? null : data
}
