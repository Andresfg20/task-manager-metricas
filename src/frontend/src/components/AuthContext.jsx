import { createContext, useContext, useMemo, useState } from 'react';
import { authService } from '../services/authService.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => authService.getUser());

  const value = useMemo(
    () => ({
      user,
      isAdmin: user?.role === 'ADMIN',
      login: async (email, password) => setUser(await authService.login(email, password)),
      register: async (email, password) => setUser(await authService.register(email, password)),
      logout: () => {
        authService.logout();
        setUser(null);
      },
    }),
    [user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
