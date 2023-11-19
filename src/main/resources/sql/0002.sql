create schema if not exists connections;

create table if not exists connections.Puzzle (
    puzzleId uuid primary key default uuid_generate_v4(),
    puzzleDate date not null
);

create table if not exists connections.Group (
    groupId uuid primary key default uuid_generate_v4(),
    puzzleId uuid not null references connections.Puzzle,
    color text not null,
    description text not null,
    words text[4] not null
);

create table if not exists connections.Game (
    gameId uuid primary key default uuid_generate_v4(),
    sessionId uuid not null references app.Session,
    puzzleId uuid not null references connections.Puzzle,
    data text,
    entryDate timestamptz not null default now()
);