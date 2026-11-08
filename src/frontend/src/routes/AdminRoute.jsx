import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../components/AuthContext.jsx';

// Convenience guard only: the backend enforces BR-33 with a 403.
export default function AdminRoute() {
  const { user, isAdmin } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  return isAdmin ? <Outlet /> : <Navigate to="/tasks" replace />;
}
