import { apiRequest } from './apiClient.js';

// ADMIN only (RF-20). The backend answers 403 to any other role.
export const userService = {
  list: ({ page = 0, size = 20 } = {}) => apiRequest('/users', { query: { page, size } }),
};
