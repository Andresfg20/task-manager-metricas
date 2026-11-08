import { useCallback, useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import EmptyState from '../components/EmptyState.jsx';
import ErrorMessage from '../components/ErrorMessage.jsx';
import Pagination from '../components/Pagination.jsx';
import TaskCard from '../components/TaskCard.jsx';
import { taskService } from '../services/taskService.js';

const FILTERS = [
  { key: 'ALL', label: 'All', query: {} },
  { key: 'PENDING', label: 'Pending', query: { status: 'PENDING' } },
  { key: 'COMPLETED', label: 'Completed', query: { status: 'COMPLETED' } },
  { key: 'OVERDUE', label: 'Overdue', query: { overdue: true } },
];

// 12-ux-ui/screens/task-list.md — HU-07 to HU-11.
export default function TaskListPage() {
  const [filter, setFilter] = useState('ALL');
  const [page, setPage] = useState(0);
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      const query = FILTERS.find((f) => f.key === filter).query;
      setData(await taskService.list({ ...query, page }));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [filter, page]);

  useEffect(() => {
    load();
  }, [load]);

  const changeFilter = (key) => {
    setFilter(key);
    setPage(0);
  };

  const handleToggle = async (task) => {
    try {
      await taskService.setStatus(task.id, task.status === 'COMPLETED' ? 'PENDING' : 'COMPLETED');
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  const handleDelete = async (task) => {
    if (!window.confirm('Delete this task permanently?')) return;
    try {
      await taskService.remove(task.id);
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  return (
    <section>
      <div className="page-header">
        <h1>My tasks</h1>
        <Link to="/tasks/new" className="btn btn-primary">New task</Link>
      </div>

      <div className="filter-bar" role="group" aria-label="Filter tasks">
        {FILTERS.map((f) => (
          <button key={f.key} type="button" aria-pressed={filter === f.key}
            className={`btn ${filter === f.key ? 'btn-primary' : 'btn-secondary'}`}
            onClick={() => changeFilter(f.key)}>
            {f.label}
          </button>
        ))}
      </div>

      <ErrorMessage message={error} onRetry={load} />
      {loading && <p className="muted">Loading tasks…</p>}

      {!loading && data && data.content.length === 0 && (
        <EmptyState
          message={filter === 'ALL' ? 'You have no tasks yet.' : 'No tasks match this filter.'}
          action={<Link to="/tasks/new" className="btn btn-primary">New task</Link>}
        />
      )}

      {!loading && data && data.content.length > 0 && (
        <div className="task-list">
          {data.content.map((task) => (
            <TaskCard key={task.id} task={task} onToggle={handleToggle} onDelete={handleDelete} />
          ))}
        </div>
      )}

      {data && <Pagination page={data.page} totalPages={data.totalPages} onChange={setPage} />}
    </section>
  );
}
