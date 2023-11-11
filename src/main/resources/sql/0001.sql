create schema if not exists connections;

-- Puzzle --

create table if not exists connections.Puzzle (
    puzzleId uuid primary key default uuid_generate_v4(),
    puzzleDate date not null,
    properties jsonb,
    entryDate timestamptz not null default now()
);

create index if not exists Puzzle_puzzleDate on connections.Puzzle(puzzleDate);

-- Group --

create table if not exists connections.Group (
	groupId uuid primary key default uuid_generate_v4(),
	puzzleId uuid not null references connections.Puzzle,
	color text not null,
	description text not null,
	words text[] not null,
	properties jsonb,
	entryDate timestamptz not null default now()
);

create index if not exists Group_puzzleId on connections.Group(puzzleId);