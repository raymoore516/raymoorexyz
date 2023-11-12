create schema if not exists app;

create table if not exists app.Session (
    sessionId uuid primary key default uuid_generate_v4(),
    entryDate timestamptz not null default now()
);