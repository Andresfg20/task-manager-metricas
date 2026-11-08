import { render, screen } from '@testing-library/react';
import { MemoryRouter, Route, Routes } from 'react-router-dom';
import { afterEach, describe, expect, it } from 'vitest';
import { AuthProvider } from '../components/AuthContext.jsx';
import { session } from '../services/apiClient.js';
import AdminRoute from './AdminRoute.jsx';

function renderAt(path) {
  render(
    <MemoryRouter initialEntries={[path]}>
      <AuthProvider>
        <Routes>
          <Route element={<AdminRoute />}>
            <Route path="/users" element={<p>Users page</p>} />
          </Route>
          <Route path="/tasks" element={<p>Tasks page</p>} />
          <Route path="/login" element={<p>Login page</p>} />
        </Routes>
      </AuthProvider>
    </MemoryRouter>,
  );
}

// TC-103 — HU-15: only ADMIN reaches /users in the SPA (the backend still enforces BR-33).
describe('AdminRoute', () => {
  afterEach(() => session.clear());

  it('redirects a USER to /tasks', () => {
    session.save('token', { id: '1', email: 'u@test.local', role: 'USER' });
    renderAt('/users');
    expect(screen.getByText('Tasks page')).toBeInTheDocument();
  });

  it('lets an ADMIN through', () => {
    session.save('token', { id: '2', email: 'a@test.local', role: 'ADMIN' });
    renderAt('/users');
    expect(screen.getByText('Users page')).toBeInTheDocument();
  });

  it('sends a visitor to /login', () => {
    renderAt('/users');
    expect(screen.getByText('Login page')).toBeInTheDocument();
  });
});
