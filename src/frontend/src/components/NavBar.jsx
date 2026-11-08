import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from './AuthContext.jsx';

export default function NavBar() {
  const { user, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  if (!user) return null;

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <header className="navbar">
      <span className="navbar-brand">Task Manager</span>
      <nav aria-label="Main">
        <NavLink to="/tasks">Tasks</NavLink>
        <NavLink to="/calendar">Calendar</NavLink>
        {isAdmin && <NavLink to="/users">Users</NavLink>}
      </nav>
      <div className="navbar-user">
        <span className="muted">{user.email}</span>
        <button type="button" className="btn btn-secondary" onClick={handleLogout}>
          Log out
        </button>
      </div>
    </header>
  );
}
