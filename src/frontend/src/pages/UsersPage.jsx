import { useCallback, useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import EmptyState from '../components/EmptyState.jsx';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Pagination from '../components/Pagination.jsx';
import { formatTimestamp } from '../components/dateFormat.js';
import { userService } from '../services/userService.js';

// 12-ux-ui/screens/users.md — HU-15. Read-only; it exists only to make the ADMIN role
// required by the course guide observable (ADR-005).
export default function UsersPage() {
  const navigate = useNavigate();
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setData(await userService.list({ page }));
    } catch (e) {
      if (e.status === 403) navigate('/tasks', { replace: true });
      else setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [page, navigate]);

  useEffect(() => {
    load();
  }, [load]);

  return (
    <section>
      <div className="page-header">
        <h1>Users</h1>
      </div>
      <ErrorMessage message={error} onRetry={load} />
      {loading && <p className="muted">Loading users…</p>}
      {!loading && data && data.content.length === 0 && <EmptyState message="No users found." />}
      {!loading && data && data.content.length > 0 && (
        <div className="table-wrapper">
          <table>
            <thead>
              <tr>
                <th scope="col">Email</th>
                <th scope="col">Role</th>
                <th scope="col">Registered on</th>
              </tr>
            </thead>
            <tbody>
              {data.content.map((u) => (
                <tr key={u.id}>
                  <td>{u.email}</td>
                  <td>{u.role}</td>
                  <td>{formatTimestamp(u.createdAt)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      )}
      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </section>
  );
}
