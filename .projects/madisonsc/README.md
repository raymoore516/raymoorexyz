# Madison SC — Project Plan

## Purpose and document status

Build an NFL pick'em league tracker for weekly picks against the spread, results, and cumulative standings as a project within the raymoore.xyz personal website. It will replace the existing Madison SC website.

This is the implementation plan for AI coding agents. The database foundation is implemented with Flyway V1, mapped `Contestant` and `Pick` Java records in `xyz.raymoore.madisonsc.domain` with inner builders, and a startup contestant lookup using Spring Data JDBC `ContestantRepository.findAll()`; API and frontend features remain planned. Proposed API contracts and unresolved business rules are labeled explicitly.

The [root PROJECT.md](../../PROJECT.md) owns shared technologies, application architecture, authentication strategy, database migration conventions, and site navigation. This file owns Madison SC business requirements, its database schema, routes, UI behavior, and acceptance criteria. Read [AGENTS.md](../../AGENTS.md) for collaboration guidance and [README.md](../../README.md) for developer setup and day-to-day workflows. Keep these documents consistent as decisions are made.

## Initial scope and later work

- **Proof of concept:** public Madison SC pages using the shared desktop/mobile hamburger navigation, five-pick administrator submissions using the `api-secret` HTTP header, Postgres persistence, and Flyway SQL migrations.
- **Manual administration:** Ray is the sole administrator. He creates contestants with SQL before submitting their picks, can backfill historical weeks at any time, and records or corrects results with SQL when needed. Do not build contestant-management or result-editing APIs/screens.
- **Later:** a Picks Submission page protected by Spring Security and Google OAuth 2.0 / OpenID Connect. JWT/session design is deferred and must not block the proof of concept.
- Do not add contestant self-service, multi-administrator coordination, submission deadlines, automatic grading, or edit workflows to the initial scope.

## Madison SC domain and database

### Core model

Use lowercase `snake_case` for database identifiers, such as `pick_id`, `contestant_id`, and `entry_date`. Tables remain `madisonsc.contestant` and `madisonsc.pick`. This is the schema contract for both local development and production. Java properties may use normal `camelCase`; map them with Spring Data JDBC conventions or explicit `@Column` annotations as appropriate.

| Table | Column | Type and baseline constraints | Meaning |
| --- | --- | --- | --- |
| `madisonsc.contestant` | `contestant_id` | UUID primary key, database-generated default | Stable contestant identity. |
| | `entry_date` | `timestamptz NOT NULL DEFAULT now()` | Creation time. |
| | `name` | `text NOT NULL UNIQUE` | Display name. |
| `madisonsc.pick` | `pick_id` | UUID primary key, database-generated default | Stable pick identity. |
| | `entry_date` | `timestamptz NOT NULL DEFAULT now()` | Creation time. |
| | `contestant_id` | UUID, required foreign key to contestant | Pick owner. |
| | `year` | `int NOT NULL` | Competition year identifier. |
| | `week` | `int NOT NULL` | Week within that competition year. |
| | `team` | `text NOT NULL` | Team code, e.g. `GB`, `CHI`, `JAC`. |
| | `underdog` | Nullable boolean | Positive-spread indicator; NULL is valid for a zero-line PK pick. |
| | `line` | `numeric(3,1) NOT NULL` | Spread magnitude. |
| | `result` | Nullable text | `win`, `loss`, `tie`; null means pending. |

V1 uses PostgreSQL's built-in `pg_catalog.gen_random_uuid()` for UUIDv4 primary-key defaults; no extension is required. The old scripts used unquoted camelCase columns; V1 creates snake_case columns without converting existing legacy tables. Confirm the live schema before production adoption. Existing tables also need their UUID defaults checked: `CREATE TABLE IF NOT EXISTS` does not update them.

Database invariants and mapping rules:

- A contestant has many picks across years and weeks. Keep the unique key `(contestant_id, year, week, team)`.
- V1 uses the archived migration as reference, with snake_case columns as confirmed during implementation. It retains the original pick index names (`pick_contestantid`, `pick_year`, `pick_week`, `pick_team`, `pick_result`, and unique `pick_unique`). The name uniqueness constraint already creates an index, so V1 omits the redundant `contestant_name` index on fresh databases and preserves it where it already exists. Review indexes against actual queries as those are added.
- Map UUIDs to Java `UUID`, timestamps to `Instant`, and `numeric(3,1)` to `BigDecimal`. Validate nonnegative magnitude, at most one decimal place, and a maximum of `99.9`.
- Store `GB +3.5` as `team = GB`, `underdog = true`, `line = 3.5`; a negative spread uses `underdog = false`. Display zero as `PK`.
- Use explicit team and result values. If Java enums use uppercase names, supply converters for lowercase stored results. Validate unknown values instead of interpreting them as pending.
- A zero line is a PK (Pick Em) game. Its `underdog` value may be NULL or false; direction is irrelevant because the UI always displays `PK` without a sign when `line = 0`. Check the line before the direction. Preserve the nullable SQL column and map it to Java `Boolean`; new PK submissions may consistently write false. A nonzero line requires a known direction; handle a malformed existing row explicitly rather than inventing a sign. No nullability change or data rewrite is needed.
- “Name normalization” means rules such as trimming spaces or treating `Ray` and `ray` as equivalent. Keep the existing case-insensitive contestant-name lookup for submissions and preserve the stored display name. The SQL uniqueness constraint is case-sensitive, so the administrator should avoid names differing only by case. Reject an ambiguous lookup with `409` instead of selecting an arbitrary row; a new normalization system or schema change is not required.
- A contestant must exist before a pick can reference its UUID. Ray creates contestants by manual SQL insert; the API never creates them implicitly. Contestants do not need Google accounts or a user-to-contestant association for this administrator-managed workflow.

There are no required matchup, schedule, or team tables in the initial model. Each pick contains its own spread and manually recorded outcome. Automated NFL score ingestion is outside the initial scope. Add tables only when a concrete feature needs them.

### Competition years and reference page

`year` is the year of the competition, not a calendar year. Year 13 is the **2026–2027 NFL season**, and Year 11 is the **2024–2025 NFL season**. Use these labels consistently in navigation and page headings; keep the competition number in URLs and database rows. With continuous annual numbering, the display mapping is season start year = competition year + 2013. Do not use that mapping to infer the active week or reject historical submissions.

The live [Year 11, Week 18 page](https://raymoore.xyz/madisonsc/picks/11/18) is a content reference: it displays week links 1–18, five picks per contestant, team logos, spreads, results, and weekly/cumulative records. Retain that information in the React design; the new shared navigation and numeric ranks are additional UI requirements.

### Weekly views and ranking

Confirmed rules:

- Each contestant submits **exactly five picks per week**, with distinct teams as required by the unique key. A missing submission remains empty; do not manufacture picks or losses.
- Show picks and a win-loss-tie record for the selected week, plus a cumulative record through that week.
- Rank the weekly view by **cumulative win percentage through the selected week**, highest first. Pending results do not count as completed wins, losses, or ties in the displayed records.
- Later weeks must not contribute to an earlier week's record or ranking.
- The administrator may submit/backfill any competition year and week at any time. Validate positive competition years and weeks 1–18, not calendar deadlines.
- A contestant with any picks in the selected competition year appears in its standings. Such a contestant can have an empty selected week, displayed as “No picks found.” A globally existing contestant with no picks that year need not appear; no season-roster table is needed for the initial application.
- Identical W-L-T records share the same rank. Alphabetical contestant name is a display ordering, not a competitive tiebreaker. Use UUID only as a final stable display key.

Define the exact win-percentage calculation when implementing the source code, including tie treatment, pending results, a zero-game record, and precision. This plan deliberately does not prescribe a formula or point system. One confirmed ordering example is that a cumulative `1-1-3` record ranks above `2-3-0`. Preserve that example as an acceptance case when selecting the calculation.

Recommended rank display is competition ranking (`1, 2, 2, 4`), with tied contestants sharing a number and the following number skipping the tied positions. Keep rank calculation separate from alphabetical display ordering; do not introduce a competitive name-based tiebreaker.

### Administrator workflow

1. Ray inserts a contestant into `madisonsc.contestant` using SQL, before submitting picks for that name.
2. Ray sends a five-pick batch to the secret-protected POST endpoint. It may include initial results for historical backfills or leave results pending.
3. Subsequent result entry/corrections and other data corrections are manual SQL operations. There is no result-update API or UI in the proof of concept.
4. Public readers view the updated data on the next fetch/reload. Live push updates are unnecessary.

Keep contestant data and operational pick/result changes separate from Flyway schema migrations. Manual administration does not imply bypassing foreign keys, uniqueness, or atomic insertion checks in the application.

## HTTP API and browser routes

Retain the existing administrator submission path, header, and shorthand body for the proof of concept. Add a public JSON read API for React. The table describes the target Spring application; these endpoints are not yet implemented in this repository.

| Method | Path | Purpose / intended access |
| --- | --- | --- |
| GET | `/madisonsc` | React landing page. It asks the public API for the latest year/week and displays that week's data, or an explicit empty state. |
| GET | `/api/madisonsc/latest` | Public latest-week lookup, returning the maximum `year`/`week` with pick entries or an empty result. |
| GET | `/madisonsc/picks/{year}/{week}` | React weekly view. |
| GET | `/api/madisonsc/picks/{year}/{week}` | Public weekly view data, including cumulative standings. |
| POST | `/madisonsc/picks/{year}/{week}` | Administrator-only five-pick submission using `api-secret`; JSON response. |

GET and POST intentionally use the same weekly path for different purposes. Restrict the SPA fallback by HTTP method, and ensure the secret check matches the POST route even though it is outside `/api`. There are no contestant CRUD, result PATCH, or `/api/auth/me` endpoints in the initial scope.

### Madison SC landing page

`GET /api/madisonsc/latest` runs this query against the singular table `madisonsc.pick`:

```sql
SELECT year, week
FROM madisonsc.pick
ORDER BY year DESC, week DESC
LIMIT 1;
```

Return JSON containing the selected `year` and `week`, or an explicit empty result when no picks exist. The maximum competition year/week determines the selected week, not the newest insertion timestamp, current calendar date, or whether results are complete. Backfilling an older week must not move the selected week backward. The React `/madisonsc` page uses this response to request `/api/madisonsc/picks/{year}/{week}` and render the weekly view; an empty result renders a clear “No picks available” state with the shared navigation. A dashboard or season-selection landing page may be added later.

### Administrator pick submission

Submission body (five distinct teams; fictional example):

```json
{
  "contestant": "Example Contestant",
  "picks": [
    "GB +3.5 W",
    "CHI -2.5 L",
    "DET PK T",
    "BUF -7",
    "KC +1.5"
  ]
}
```

Each shorthand entry contains a team code, a signed spread or `PK`, and an optional `W`, `L`, or `T`. Missing result means pending. Parse the format deliberately: reject unknown teams/results, missing signs on non-`PK` spreads, invalid decimals, extra tokens, and duplicate teams with a useful validation error. Keep the request contract; use typed values inside the service and persistence layers. A structured request DTO for a future UI is deferred.

Insert all five picks atomically and return HTTP `200` with a JSON object `{ "success": true }`. Validate the contestant exists, require exactly five entries, and reject a contestant/year/week that already contains picks with `409` rather than appending a sixth pick or silently replacing data. One invalid entry must leave the batch unsaved. The unique key alone prevents duplicate teams, not more than five different teams, so check both request size and existing weekly picks. Historical partial data requires manual reconciliation before submitting a full batch; there is no partial-fill endpoint. Missing/invalid secrets should return `401`; document that conventional error status even though the existing service uses `400` for an invalid secret.

### Proposed weekly response

The proposed weekly response includes `year`, `week`, season label, and ordered contestant summaries containing ID, name, rank, cumulative win percentage, cumulative record, weekly record, and that week's picks. Return records as numeric fields (`wins`, `losses`, `ties`) rather than only formatted strings. Calculate standings in Java/SQL; React formats and displays them. Specify the percentage representation and no-results value with the calculation during implementation, then define DTOs and representative response examples.

## Authentication and authorization

Follow the [site authentication strategy in PROJECT.md](../../PROJECT.md). Public Madison SC pages and JSON reads require no login. Ray is the sole administrator.

For the proof of concept, protect `POST /madisonsc/picks/{year}/{week}` with the `api-secret` header backed by `APP_API_SECRET`. Scope the authentication filter and any header-only CSRF exemption to this exact POST route, including the fact that it is outside `/api`. Missing or invalid credentials return `401`; an absent or blank configured secret must prevent writes. The React application never receives the secret. Local API-client submissions go directly to `http://localhost:8080/madisonsc/picks/{year}/{week}`.

A Picks Submission page belongs to the later Google-login phase. It must authorize Ray's configured identity on the backend and must never contain the shared API secret. Browser session/JWT decisions and whether to retain the separate secret-authenticated API are deferred as described in the root plan. Contestants do not need login accounts or a user-to-contestant association.

## Persistence and migration details

The `madisonsc` schema belongs to this project within the shared `raymoorexyz` database. Follow the [root database change workflow in PROJECT.md](../../PROJECT.md), including idempotent initial SQL, immutable versioned migrations, and baseline-at-zero adoption of the existing production schema.

- Keep migration files in `backend/src/main/resources/db/migration/`, starting with `V1__create_madisonsc.sql`; a subsequent agreed change could use `V2__add_pick_validation_constraints.sql`. Versions belong to the shared application migration sequence.
- Initially configure Flyway's managed schemas as `public,madisonsc`, with its history table in `public`, so existing Madison SC objects participate in the empty/nonempty schema check.
- Preserve matching existing production objects and data during initial creation. Keep sample contestants/picks in explicit local fixtures or test resources, separate from production migrations and manual operational data changes.
- Treat `Contestant` and `Pick` as separate Spring Data JDBC aggregate roots. A pick references its contestant by UUID or `AggregateReference`; do not place every historical pick in a persistence-managed contestant collection.
- Insert weekly pick batches in a Spring-managed service transaction. Fetch weekly summaries in bounded queries, avoiding one contestant lookup per row. Keep standings calculation in small, testable Java methods.
- There is one administrator and no application edit workflow: do not add version columns, conflict-resolution screens, or custom concurrency infrastructure. Keep ordinary database constraints and atomic batch writes.
- Apply Bean Validation to nested pick entries and enforce cross-field and league rules in services. Enforce the secret and exactly-five-picks rule on the backend; historical backfill remains allowed.

## Frontend organization and navigation

Use the shared React/TypeScript conventions, `SiteLayout`, and `SiteNavigation` from the root plan.

- Put `MadisonScPage` and `WeeklyPicksPage` in `frontend/src/pages/madisonsc/` and project components in `frontend/src/components/madisonsc/`. Suggested weekly components are `WeekNavigation`, `ContestantCard`, `RecordSummary`, and `PickCard`.
- Put backend code under `backend/src/main/java/xyz/raymoore/madisonsc/`, organized into `controller`, `service`, `domain`, `repository`, and `dto` as needed. Put mapped records in `domain`, repository interfaces in `repository`, and future HTTP DTOs in `dto`, following the shared package and inner `Builder` conventions in `PROJECT.md`. Keep typed API calls and team/result types aligned with the backend contract.
- Define React routes for `/madisonsc` and `/madisonsc/picks/:year/:week`. Use relative API paths such as `/api/madisonsc/picks/13/1` through Vite's `/api` proxy during development.
- The shared Madison SC link points to `/madisonsc`. React calls the latest-week API, then loads the selected weekly data. A normal navigation link also works on a direct browser visit.
- Keep historical year/week navigation on the weekly view, separate from the global menu, with the selected week clearly marked. A separate selection dashboard is deferred.
- Keep historical links such as `/madisonsc/picks/11/18` directly navigable and refreshable. Restrict the production SPA fallback to GET/HEAD UI requests so the administrator POST on the same weekly path reaches Spring Boot.
- Cancel or ignore stale requests when the selected year/week changes. Provide loading, error, empty, and success states, and textual result labels alongside colors. Retain team logos, spreads, results, and weekly/cumulative records from the reference page.
- Use controlled inputs when adding the later Picks Submission page. Result-entry and contestant-management pages remain outside scope.
- Initial UI acceptance includes narrow mobile and desktop viewports, public empty states, and shared menu keyboard operation.

## Verification

Apply the shared test tooling and delivery checks in the root plan, with these Madison SC acceptance cases:

- Use JUnit for cumulative win-percentage ranking once its calculation is specified, including `1-1-3` ranking above `2-3-0`, identical records sharing rank, historical week cutoffs, and pending/no-results cases. Also cover competition-year labels, exactly five picks, distinct teams, and shorthand validation.
- Use real Postgres integration tests for contestant/pick mappings, UUID and timestamp defaults, enum conversion, uniqueness, and transaction rollback. Cover idempotent schema initialization, baseline-at-zero adoption of the existing schema, and `snake_case` column mappings.
- Cover PK rendering with both NULL and false directions.
- Test administrator POST requests with valid, missing, invalid, and unconfigured secrets. Verify one invalid pick cannot leave a partially inserted batch, an existing week cannot receive five more picks, and historical backfill remains allowed. Google-login tests belong to the later phase.
- Use Vitest and React Testing Library for request failures, empty weeks, shared rank display, and integration with shared navigation.
- Verify `/madisonsc` loads through both Vite and the packaged app, requests the latest year/week through the API, renders the selected weekly data, and handles an empty database. Use test fixtures with different insertion and competition-year/week orderings; the selected week must remain data-driven as new picks arrive.
- Verify direct navigation and refresh for historical weekly routes, and confirm administrator POST requests receive JSON instead of the SPA fallback.

## Remaining decisions

- Define the win-percentage calculation during source implementation, including tie treatment, pending/no-results handling, precision, and response representation. The ranking criterion and the `1-1-3` above `2-3-0` example are settled; do not substitute a points-based approximation.
- In the later Google-login phase, apply the shared browser authentication design to the Picks Submission page. This does not block secret-authenticated submissions.

Competition-year meaning, ranking by cumulative win percentage, five picks per week, administrator-managed submissions, unrestricted historical backfill, manual contestant/result maintenance, PK direction handling, use of the existing production database, idempotent SQL, and API-driven latest-week selection are settled requirements. Do not reopen them or add management interfaces as prerequisites to the proof of concept.
