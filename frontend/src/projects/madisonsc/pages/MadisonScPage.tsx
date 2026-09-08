import { useEffect, useState } from 'react';

type Contestant = {
  contestantId: string;
  entryDate: string;
  name: string;
};

export default function MadisonScPage() {
  const [contestants, setContestants] = useState<Contestant[]>([]);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    fetch('/api/madisonsc/contestants')
      .then((response) => {
        if (!response.ok) throw new Error(`Request failed (${response.status})`);
        return response.json() as Promise<Contestant[]>;
      })
      .then(setContestants)
      .catch((requestError: unknown) => {
        setError(requestError instanceof Error ? requestError.message : 'Unable to load contestants.');
      });
  }, []);

  return (
    <main>
      <h1>Contestants</h1>
      {error ? <p role="alert">{error}</p> : (
        <ul>{contestants.map((contestant) => <li key={contestant.contestantId}>{contestant.name}</li>)}</ul>
      )}
    </main>
  );
}
