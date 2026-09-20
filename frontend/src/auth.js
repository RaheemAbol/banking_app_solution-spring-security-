import { request, refreshCsrf } from './api.js';

export async function currentUser() {
  try {
    return await request('/api/auth/me');
  } catch (error) {
    if (error.status === 401) return null;
    throw error;
  }
}

export async function register(displayName, email, password) {
  return request('/api/auth/register', 'POST',
    { displayName, email, password });
}

export async function login(email, password) {
  await request('/api/auth/login', 'POST',
    { email, password });
  await refreshCsrf();
  return currentUser();
}

export async function logout() {
  await request('/api/auth/logout', 'POST');
  await refreshCsrf();
}
