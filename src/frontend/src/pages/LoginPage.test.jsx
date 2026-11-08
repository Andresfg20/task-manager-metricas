import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it, vi } from 'vitest';
import { AuthProvider } from '../components/AuthContext.jsx';
import LoginPage from './LoginPage.jsx';

// TC-101 — HU-02: client validation shows field errors and does not call the API.
describe('LoginPage', () => {
  it('shows field errors and does not call the API when the form is empty', async () => {
    const fetchSpy = vi.spyOn(globalThis, 'fetch');
    render(
      <MemoryRouter>
        <AuthProvider>
          <LoginPage />
        </AuthProvider>
      </MemoryRouter>,
    );

    await userEvent.click(screen.getByRole('button', { name: 'Log in' }));

    expect(screen.getByText('Enter a valid email address.')).toBeInTheDocument();
    expect(screen.getByText('Enter your password.')).toBeInTheDocument();
    expect(fetchSpy).not.toHaveBeenCalled();
    fetchSpy.mockRestore();
  });
});
