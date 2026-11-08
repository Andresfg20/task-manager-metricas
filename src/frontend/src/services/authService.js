import { apiRequest, session } from './apiClient.js';

async function authenticate(path, email, password) {
  const data = await apiRequest(path, { method: 'POST', body: { email, password }, auth: false });
  session.save(data.token, data.user);
  return data.user;
}

export const authService = {
  login: (email, password) => authenticate('/auth/login', email, password),
  register: (email, password) => authenticate('/auth/register', email, password),
  // There is no logout endpoint: the token is discarded on the client (RF-03).
  logout: () => session.clear(),
  getUser: () => (session.getToken() ? session.getUser() : null),
};
