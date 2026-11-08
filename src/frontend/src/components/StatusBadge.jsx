// Status is always written as text, never shown by color alone (12-ux-ui/design-system.md).
export default function StatusBadge({ status }) {
  const completed = status === 'COMPLETED';
  return (
    <span className={`badge ${completed ? 'badge-success' : 'badge-neutral'}`}>
      {completed ? 'Completed' : 'Pending'}
    </span>
  );
}
