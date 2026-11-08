import { describe, expect, it } from 'vitest';
import { groupByDueDate, monthGrid, toIsoDate, weekGrid } from './calendarUtils.js';

// TC-104 — HU-12: month grid starts on Monday and covers 42 days.
describe('calendarUtils', () => {
  it('builds a 42-day month grid starting on Monday', () => {
    const grid = monthGrid(new Date(2026, 10, 15)); // November 2026
    expect(grid).toHaveLength(42);
    expect(grid[0].getDay()).toBe(1);
    expect(toIsoDate(grid[0])).toBe('2026-10-26');
    expect(grid.map(toIsoDate)).toContain('2026-11-30');
  });

  it('builds a 7-day week grid starting on Monday', () => {
    const grid = weekGrid(new Date(2026, 10, 4)); // Wednesday
    expect(grid.map(toIsoDate)).toEqual([
      '2026-11-02', '2026-11-03', '2026-11-04', '2026-11-05', '2026-11-06', '2026-11-07', '2026-11-08',
    ]);
  });

  it('groups tasks by due date', () => {
    const groups = groupByDueDate([
      { id: 'a', dueDate: '2026-11-02' },
      { id: 'b', dueDate: '2026-11-02' },
      { id: 'c', dueDate: '2026-11-03' },
    ]);
    expect(groups['2026-11-02']).toHaveLength(2);
    expect(groups['2026-11-03']).toHaveLength(1);
  });
});
