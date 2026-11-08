// Calendar View helpers. All dates are local plain dates in "YYYY-MM-DD" form (BR-04).

export function toIsoDate(date) {
  const y = date.getFullYear();
  const m = String(date.getMonth() + 1).padStart(2, '0');
  const d = String(date.getDate()).padStart(2, '0');
  return `${y}-${m}-${d}`;
}

export function addDays(date, days) {
  const next = new Date(date.getFullYear(), date.getMonth(), date.getDate());
  next.setDate(next.getDate() + days);
  return next;
}

/** Monday of the week that contains the date. */
export function startOfWeek(date) {
  const offset = (date.getDay() + 6) % 7; // Monday = 0
  return addDays(date, -offset);
}

/** 42 days (6 weeks) starting on the Monday on or before the 1st of the month. */
export function monthGrid(date) {
  const start = startOfWeek(new Date(date.getFullYear(), date.getMonth(), 1));
  return Array.from({ length: 42 }, (_, i) => addDays(start, i));
}

/** 7 days starting on the Monday of the week. */
export function weekGrid(date) {
  const start = startOfWeek(date);
  return Array.from({ length: 7 }, (_, i) => addDays(start, i));
}

export function groupByDueDate(tasks) {
  return tasks.reduce((groups, task) => {
    (groups[task.dueDate] ||= []).push(task);
    return groups;
  }, {});
}
