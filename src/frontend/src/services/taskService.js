import { apiRequest } from './apiClient.js';

export const taskService = {
  list: ({ status, overdue, page = 0, size = 20 } = {}) =>
    apiRequest('/tasks', { query: { status, overdue: overdue ? 'true' : undefined, page, size } }),
  calendar: (from, to) => apiRequest('/tasks/calendar', { query: { from, to } }),
  get: (id) => apiRequest(`/tasks/${id}`),
  create: (task) => apiRequest('/tasks', { method: 'POST', body: task }),
  update: (id, task) => apiRequest(`/tasks/${id}`, { method: 'PUT', body: task }),
  setStatus: (id, status) => apiRequest(`/tasks/${id}/status`, { method: 'PATCH', body: { status } }),
  reschedule: (id, dueDate) => apiRequest(`/tasks/${id}/due-date`, { method: 'PATCH', body: { dueDate } }),
  remove: (id) => apiRequest(`/tasks/${id}`, { method: 'DELETE' }),
};
