create schema if not exists connections;

create table if not exists connections.Game (
    gameId uuid primary key default uuid_generate_v4(),
    sessionId uuid not null references app.Session,
    entryDate timestamptz not null default now()
);