import { useCallback, useEffect, useMemo, useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import ErrorMessage from '../components/ErrorMessage.jsx';
import { addDays, groupByDueDate, monthGrid, toIsoDate, weekGrid } from '../components/calendarUtils.js';
import { taskService } from '../services/taskService.js';

const WEEKDAYS = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'];

// 12-ux-ui/screens/calendar.md — HU-12, HU-13, HU-14. A view of Task data, not an entity.
export default function CalendarPage() {
  const navigate = useNavigate();
  const [view, setView] = useState('month');
  const [anchor, setAnchor] = useState(() => new Date());
  const [tasks, setTasks] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [moving, setMoving] = useState(null); // { id, date }

  const days = useMemo(() => (view === 'month' ? monthGrid(anchor) : weekGrid(anchor)), [view, anchor]);
  const from = toIsoDate(days[0]);
  const to = toIsoDate(days[days.length - 1]);
  const todayIso = toIsoDate(new Date());

  const load = useCallback(async () => {
    setLoading(true);
    setError('');
    try {
      setTasks(await taskService.calendar(from, to));
    } catch (e) {
      setError(e.message);
    } finally {
      setLoading(false);
    }
  }, [from, to]);

  useEffect(() => {
    load();
  }, [load]);

  const shift = (direction) => {
    if (view === 'month') setAnchor(new Date(anchor.getFullYear(), anchor.getMonth() + direction, 1));
    else setAnchor(addDays(anchor, 7 * direction));
  };

  const saveMove = async () => {
    if (!moving?.date) {
      setError('Choose a valid date.');
      return;
    }
    try {
      await taskService.reschedule(moving.id, moving.date);
      setMoving(null);
      await load();
    } catch (e) {
      setError(e.message);
    }
  };

  const byDay = groupByDueDate(tasks);
  const title = view === 'month'
    ? anchor.toLocaleDateString(undefined, { month: 'long', year: 'numeric' })
    : `${days[0].toLocaleDateString()} – ${days[6].toLocaleDateString()}`;

  return (
    <section>
      <div className="page-header">
        <h1>Calendar</h1>
        <div className="calendar-toolbar">
          <button type="button" className={`btn ${view === 'month' ? 'btn-primary' : 'btn-secondary'}`}
            aria-pressed={view === 'month'} onClick={() => setView('month')}>Month</button>
          <button type="button" className={`btn ${view === 'week' ? 'btn-primary' : 'btn-secondary'}`}
            aria-pressed={view === 'week'} onClick={() => setView('week')}>Week</button>
          <button type="button" className="btn btn-secondary" onClick={() => shift(-1)}>Previous</button>
          <button type="button" className="btn btn-secondary" onClick={() => setAnchor(new Date())}>Today</button>
          <button type="button" className="btn btn-secondary" onClick={() => shift(1)}>Next</button>
        </div>
      </div>
      <h2 className="calendar-title">{title} {loading && <span className="muted">Loading…</span>}</h2>
      <ErrorMessage message={error} onRetry={load} />
      {!loading && tasks.length === 0 && <p className="muted">Click a day to add a task.</p>}

      <div className={`calendar-grid ${view}`}>
        {WEEKDAYS.map((d) => <div key={d} className="calendar-weekday">{d}</div>)}
        {days.map((day) => {
          const iso = toIsoDate(day);
          const outside = view === 'month' && day.getMonth() !== anchor.getMonth();
          return (
            <div key={iso} className={`calendar-day${outside ? ' outside' : ''}${iso === todayIso ? ' today' : ''}`}>
              <button type="button" className="calendar-day-number" aria-label={`Add a task on ${iso}`}
                onClick={() => navigate(`/tasks/new?dueDate=${iso}`)}>
                {day.getDate()}
              </button>
              {(byDay[iso] || []).map((task) => (
                <div key={task.id} className={`calendar-task${task.status === 'COMPLETED' ? ' task-completed' : ''}${task.overdue ? ' task-overdue' : ''}`}>
                  <Link to={`/tasks/${task.id}`}>{task.title}</Link>
                  {task.overdue && <span className="overdue-label">Overdue</span>}
                  {moving?.id === task.id ? (
                    <span className="move-form">
                      <label htmlFor={`move-${task.id}`} className="visually-hidden">New date</label>
                      <input id={`move-${task.id}`} type="date" value={moving.date}
                        onChange={(e) => setMoving({ id: task.id, date: e.target.value })} />
                      <button type="button" className="btn btn-primary" onClick={saveMove}>Save</button>
                      <button type="button" className="btn btn-secondary" onClick={() => setMoving(null)}>Cancel</button>
                    </span>
                  ) : (
                    <button type="button" className="link-button" onClick={() => setMoving({ id: task.id, date: iso })}>
                      Move
                    </button>
                  )}
                </div>
              ))}
            </div>
          );
        })}
      </div>
    </section>
  );
}
