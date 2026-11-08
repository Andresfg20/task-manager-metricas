import { Link } from 'react-router-dom';
import StatusBadge from './StatusBadge.jsx';
import { formatDueDate } from './dateFormat.js';

export default function TaskCard({ task, onToggle, onDelete, compact = false }) {
  const completed = task.status === 'COMPLETED';
  const classes = ['task-card', completed && 'task-completed', task.overdue && 'task-overdue', compact && 'task-compact']
    .filter(Boolean)
    .join(' ');

  return (
    <article className={classes}>
      {onToggle && (
        <input
          type="checkbox"
          checked={completed}
          onChange={() => onToggle(task)}
          aria-label={`Mark "${task.title}" as ${completed ? 'pending' : 'completed'}`}
        />
      )}
      <div className="task-card-body">
        <Link to={`/tasks/${task.id}`} className="task-title">
          {task.title}
        </Link>
        {!compact && (
          <div className="task-meta">
            <span className="muted">{formatDueDate(task.dueDate)}</span>
            <StatusBadge status={task.status} />
            {task.overdue && <span className="overdue-label">Overdue</span>}
          </div>
        )}
        {compact && task.overdue && <span className="overdue-label">Overdue</span>}
      </div>
      {onDelete && (
        <button type="button" className="btn btn-danger" onClick={() => onDelete(task)}>
          Delete
        </button>
      )}
    </article>
  );
}
