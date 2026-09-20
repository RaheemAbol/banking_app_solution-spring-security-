let csrf = null;

export async function refreshCsrf() {
  const response = await fetch('/api/auth/csrf', { credentials: 'same-origin' });
  if (!response.ok) throw new Error('Unable to connect. Check that the backend is running.');
  csrf = await response.json();
}

export async function request(path, method = 'GET', fields = null) {
  const headers = {};
  if (!['GET', 'HEAD', 'OPTIONS'].includes(method)) {
    if (!csrf) await refreshCsrf();
    headers[csrf.headerName] = csrf.token;
  }
  let response;
  try {
    response = await fetch(path, {
      method, headers, credentials: 'same-origin',
      body: fields === null ? null : new URLSearchParams(fields)
    });
  } catch {
    throw new Error('Unable to connect. Check that the backend is running.');
  }
  const text = await response.text();
  const isJson = response.headers.get('Content-Type')?.includes('application/json');
  let data = text;
  if (text && isJson) {
    try { data = JSON.parse(text); } catch { data = null; }
  }
  if (!response.ok) {
    if (response.status === 401 || response.status === 403) csrf = null;
    const messages = {
      400: 'Check the supplied fields.',
      401: 'Sign in, or check your email and password.',
      403: 'Your account does not have permission for this action.',
      404: 'Account unavailable.',
      409: 'This operation cannot be completed.'
    };
    const detail = typeof data === 'object' && data?.message;
    const error = new Error(detail || messages[response.status] || 'Request failed.');
    error.status = response.status;
    throw error;
  }
  return text ? data : null;
}
