export type LatestWeek = {
  year: number | null;
  week: number | null;
};

export type PickRecord = {
  wins: number;
  losses: number;
  ties: number;
};

export type WeeklyPick = {
  team: string;
  underdog: boolean | null;
  line: number;
  result: 'win' | 'loss' | 'tie' | null;
};

export type WeeklyContestant = {
  contestantId: string;
  name: string;
  rank: number;
  cumulativeWinPercentage: number;
  cumulativeRecord: PickRecord;
  weeklyRecord: PickRecord;
  picks: WeeklyPick[];
};

export type WeeklyPicks = {
  year: number;
  week: number;
  seasonLabel: string;
  availableYears: number[];
  contestants: WeeklyContestant[];
};

async function getJson<T>(path: string, signal: AbortSignal): Promise<T> {
  const response = await fetch(path, { signal });
  if (!response.ok) {
    throw new Error(`Request failed (${response.status})`);
  }
  return response.json() as Promise<T>;
}

export function getLatestWeek(signal: AbortSignal): Promise<LatestWeek> {
  return getJson('/api/madisonsc/latest', signal);
}

export function getWeeklyPicks(year: number, week: number, signal: AbortSignal): Promise<WeeklyPicks> {
  return getJson(`/api/madisonsc/picks/${year}/${week}`, signal);
}
