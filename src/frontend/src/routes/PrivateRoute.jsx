import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../components/AuthContext.jsx';

// Convenience guard only: the backend enforces BR-32 on every request.
export default function PrivateRoute() {
  const { user } = useAuth();
  return user ? <Outlet /> : <Navigate to="/login" replace />;
}
