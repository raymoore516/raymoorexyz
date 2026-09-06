CREATE SCHEMA IF NOT EXISTS madisonsc;

CREATE TABLE IF NOT EXISTS madisonsc.contestant (
    contestant_id uuid PRIMARY KEY DEFAULT pg_catalog.gen_random_uuid(),
    entry_date timestamptz NOT NULL DEFAULT now(),
    name text NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS madisonsc.pick (
    pick_id uuid PRIMARY KEY DEFAULT pg_catalog.gen_random_uuid(),
    entry_date timestamptz NOT NULL DEFAULT now(),
    contestant_id uuid NOT NULL REFERENCES madisonsc.contestant (contestant_id),
    year int NOT NULL,
    week int NOT NULL,
    team text NOT NULL,
    underdog boolean,
    line numeric(3,1) NOT NULL,
    result text
);

CREATE INDEX IF NOT EXISTS pick_contestant_id ON madisonsc.pick (contestant_id);
CREATE INDEX IF NOT EXISTS pick_year ON madisonsc.pick (year);
CREATE INDEX IF NOT EXISTS pick_week ON madisonsc.pick (week);
CREATE INDEX IF NOT EXISTS pick_team ON madisonsc.pick (team);
CREATE INDEX IF NOT EXISTS pick_result ON madisonsc.pick (result);
CREATE UNIQUE INDEX IF NOT EXISTS pick_unique ON madisonsc.pick (contestant_id, year, week, team);
