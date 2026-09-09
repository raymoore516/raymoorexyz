import type { LatestWeek, WeeklyPicksResponse } from '../types/picks';

async function getJson<T>(path: string, signal: AbortSignal): Promise<T> {
  const response = await fetch(path, { signal });
  if (!response.ok) {
    throw new Error(`Request failed (${response.status})`);
  }
  return response.json() as Promise<T>;
}

export function getLatestWeek(signal: AbortSignal): Promise<LatestWeek> {
  return getJson('/api/madisonsc/picks/latest', signal);
}

export function getWeeklyPicks(year: number, week: number, signal: AbortSignal): Promise<WeeklyPicksResponse> {
  return getJson(`/api/madisonsc/picks/${year}/${week}`, signal);
}
