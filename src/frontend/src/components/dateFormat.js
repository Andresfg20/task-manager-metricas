// Due dates are plain dates (BR-04): shown as written, with no time-zone conversion.
export function formatDueDate(isoDate) {
  if (!isoDate) return 'No due date';
  const [year, month, day] = isoDate.split('-').map(Number);
  return new Date(year, month - 1, day).toLocaleDateString(undefined, {
    year: 'numeric',
    month: 'short',
    day: 'numeric',
  });
}

// Timestamps are stored in UTC and shown in the user's zone (BR-25).
export function formatTimestamp(isoInstant) {
  return isoInstant ? new Date(isoInstant).toLocaleString() : '—';
}
