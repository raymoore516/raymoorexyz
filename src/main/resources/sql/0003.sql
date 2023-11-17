create schema if not exists madisonsc;

create table if not exists madisonsc.Contestant (
    contestantId uuid primary key default uuid_generate_v4(),
    entryDate timestamptz not null default now(),
    name text not null unique
);

create index if not exists Contestant_name on madisonsc.Contestant(name);

create table if not exists madisonsc.Pick (
    pickId uuid primary key default uuid_generate_v4(),
    entryDate timestamptz not null default now(),
    contestantId uuid not null references madisonsc.Contestant,
    year int not null,
    week int not null,
    team text not null,
    underdog boolean,
    line numeric(3,1) not null,
    result text
);

create index if not exists Pick_contestantId on madisonsc.Pick(contestantId);
create index if not exists Pick_year on madisonsc.Pick(year);
create index if not exists Pick_week on madisonsc.Pick(week);
create index if not exists Pick_team on madisonsc.Pick(team);
create index if not exists Pick_result on madisonsc.Pick(result);
create unique index if not exists Pick_unique on madisonsc.Pick(contestantId, year, week, team);