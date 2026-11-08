import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from '../components/AuthContext.jsx';
import CalendarPage from '../pages/CalendarPage.jsx';
import LoginPage from '../pages/LoginPage.jsx';
import NotFoundPage from '../pages/NotFoundPage.jsx';
import RegisterPage from '../pages/RegisterPage.jsx';
import TaskFormPage from '../pages/TaskFormPage.jsx';
import TaskListPage from '../pages/TaskListPage.jsx';
import UsersPage from '../pages/UsersPage.jsx';
import AdminRoute from './AdminRoute.jsx';
import PrivateRoute from './PrivateRoute.jsx';

// Routes from 12-ux-ui/screens.md.
export default function AppRoutes() {
  const { user } = useAuth();
  return (
    <Routes>
      <Route path="/" element={<Navigate to={user ? '/tasks' : '/login'} replace />} />
      <Route path="/login" element={user ? <Navigate to="/tasks" replace /> : <LoginPage />} />
      <Route path="/register" element={user ? <Navigate to="/tasks" replace /> : <RegisterPage />} />
      <Route element={<PrivateRoute />}>
        <Route path="/tasks" element={<TaskListPage />} />
        <Route path="/tasks/new" element={<TaskFormPage />} />
        <Route path="/tasks/:id" element={<TaskFormPage />} />
        <Route path="/calendar" element={<CalendarPage />} />
      </Route>
      <Route element={<AdminRoute />}>
        <Route path="/users" element={<UsersPage />} />
      </Route>
      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
