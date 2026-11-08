// Thin wrapper around fetch for the Task Manager REST API (07-api/).
const BASE_URL = import.meta.env.VITE_API_URL || '/api/v1';
const TOKEN_KEY = 'tm.token';
const USER_KEY = 'tm.user';

export class ApiError extends Error {
  constructor(status, code, message, details = []) {
    super(message);
    this.status = status;
    this.code = code;
    this.details = details;
  }
}

export const session = {
  getToken: () => localStorage.getItem(TOKEN_KEY),
  getUser: () => {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  },
  save: (token, user) => {
    localStorage.setItem(TOKEN_KEY, token);
    localStorage.setItem(USER_KEY, JSON.stringify(user));
  },
  clear: () => {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
  },
};

export async function apiRequest(path, { method = 'GET', body, query, auth = true } = {}) {
  const url = new URL(BASE_URL + path, window.location.origin);
  Object.entries(query || {}).forEach(([key, value]) => {
    if (value !== undefined && value !== null && value !== '') url.searchParams.set(key, value);
  });

  const headers = {};
  if (body !== undefined) headers['Content-Type'] = 'application/json';
  const token = session.getToken();
  if (auth && token) headers.Authorization = `Bearer ${token}`;

  let response;
  try {
    response = await fetch(url, {
      method,
      headers,
      body: body !== undefined ? JSON.stringify(body) : undefined,
    });
  } catch {
    throw new ApiError(0, 'NETWORK_ERROR', 'The server could not be reached. Check your connection.');
  }

  if (response.status === 204) return null;
  const data = await response.json().catch(() => null);

  if (!response.ok) {
    // An expired or invalid token ends the session (BR-32, 12-ux-ui/screens.md).
    if (response.status === 401 && auth) {
      session.clear();
      if (window.location.pathname !== '/login') window.location.assign('/login');
    }
    throw new ApiError(
      response.status,
      data?.code || 'UNKNOWN_ERROR',
      data?.message || 'An unexpected error occurred.',
      data?.details || [],
    );
  }
  return data;
}
