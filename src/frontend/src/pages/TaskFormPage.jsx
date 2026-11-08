import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams, useSearchParams } from 'react-router-dom';
import ErrorMessage from '../components/ErrorMessage.jsx';
import FormField from '../components/FormField.jsx';
import StatusBadge from '../components/StatusBadge.jsx';
import { formatTimestamp } from '../components/dateFormat.js';
import { taskService } from '../services/taskService.js';

const ISO_DATE = /^\d{4}-\d{2}-\d{2}$/;

// 12-ux-ui/screens/task-form.md — HU-04, HU-05, HU-06, HU-13.
export default function TaskFormPage() {
  const { id } = useParams();
  const [searchParams] = useSearchParams();
  const navigate = useNavigate();
  const isEdit = Boolean(id);

  const [form, setForm] = useState({
    title: '',
    description: '',
    dueDate: ISO_DATE.test(searchParams.get('dueDate') || '') ? searchParams.get('dueDate') : '',
  });
  const [task, setTask] = useState(null);
  const [errors, setErrors] = useState({});
  const [apiError, setApiError] = useState('');
  const [notFound, setNotFound] = useState(false);
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);

  useEffect(() => {
    if (!isEdit) return;
    taskService
      .get(id)
      .then((loaded) => {
        setTask(loaded);
        setForm({ title: loaded.title, description: loaded.description || '', dueDate: loaded.dueDate || '' });
      })
      .catch((e) => (e.status === 404 ? setNotFound(true) : setApiError(e.message)))
      .finally(() => setLoading(false));
  }, [id, isEdit]);

  const validate = () => {
    const next = {};
    const title = form.title.trim();
    if (title.length < 1 || title.length > 120) next.title = 'Enter a title of 1 to 120 characters.';
    if (form.description.length > 1000) next.description = 'Use at most 1000 characters.';
    if (form.dueDate && !ISO_DATE.test(form.dueDate)) next.dueDate = 'Enter a valid date.';
    setErrors(next);
    return Object.keys(next).length === 0;
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    setApiError('');
    if (!validate()) return;
    const payload = {
      title: form.title.trim(),
      description: form.description.trim() || null,
      dueDate: form.dueDate || null,
    };
    setSaving(true);
    try {
      if (isEdit) await taskService.update(id, payload);
      else await taskService.create(payload);
      navigate('/tasks');
    } catch (e) {
      if (e.details?.length) setErrors(Object.fromEntries(e.details.map((d) => [d.field, d.message])));
      else setApiError(e.message);
    } finally {
      setSaving(false);
    }
  };

  const handleDelete = async () => {
    if (!window.confirm('Delete this task permanently?')) return;
    try {
      await taskService.remove(id);
      navigate('/tasks');
    } catch (e) {
      setApiError(e.message);
    }
  };

  const update = (field) => (event) => setForm({ ...form, [field]: event.target.value });

  if (loading) return <p className="muted">Loading task…</p>;
  if (notFound) {
    return (
      <section className="card">
        <h1>This task does not exist.</h1>
        <Link to="/tasks">Back to my tasks</Link>
      </section>
    );
  }

  return (
    <section className="card">
      <h1>{isEdit ? 'Edit task' : 'New task'}</h1>
      <ErrorMessage message={apiError} />
      {task && (
        <dl className="task-details">
          <dt>Status</dt>
          <dd><StatusBadge status={task.status} /> {task.overdue && <span className="overdue-label">Overdue</span>}</dd>
          <dt>Created</dt>
          <dd>{formatTimestamp(task.createdAt)}</dd>
          {task.completedAt && (
            <>
              <dt>Completed</dt>
              <dd>{formatTimestamp(task.completedAt)}</dd>
            </>
          )}
        </dl>
      )}
      <form onSubmit={handleSubmit} noValidate>
        <FormField id="title" label="Title" maxLength={120} value={form.title}
          onChange={update('title')} error={errors.title} />
        <FormField id="description" label="Description (optional)" as="textarea" rows={4} maxLength={1000}
          value={form.description} onChange={update('description')} error={errors.description} />
        <FormField id="dueDate" label="Due date (optional)" type="date" value={form.dueDate}
          onChange={update('dueDate')} error={errors.dueDate} />
        <div className="form-actions">
          <button type="submit" className="btn btn-primary" disabled={saving}>
            {saving ? 'Saving…' : 'Save'}
          </button>
          <Link to="/tasks" className="btn btn-secondary">Cancel</Link>
          {isEdit && (
            <button type="button" className="btn btn-danger" onClick={handleDelete}>Delete</button>
          )}
        </div>
      </form>
    </section>
  );
}
