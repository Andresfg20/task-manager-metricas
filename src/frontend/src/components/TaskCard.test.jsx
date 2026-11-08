import { render, screen } from '@testing-library/react';
import { MemoryRouter } from 'react-router-dom';
import { describe, expect, it } from 'vitest';
import TaskCard from './TaskCard.jsx';

const baseTask = { id: '1', title: 'Report', status: 'PENDING', dueDate: '2026-11-01', overdue: false };

// TC-102 — HU-11: the overdue marker depends only on the API's computed field.
describe('TaskCard', () => {
  it('shows "Overdue" when the task is overdue', () => {
    render(<MemoryRouter><TaskCard task={{ ...baseTask, overdue: true }} /></MemoryRouter>);
    expect(screen.getByText('Overdue')).toBeInTheDocument();
  });

  it('does not show "Overdue" otherwise', () => {
    render(<MemoryRouter><TaskCard task={baseTask} /></MemoryRouter>);
    expect(screen.queryByText('Overdue')).not.toBeInTheDocument();
    expect(screen.getByText('Pending')).toBeInTheDocument();
  });
});
