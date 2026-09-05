# raymoore.xyz

A personal website project, starting with **Madison SC**, an NFL pick’em league tracker for weekly picks, results, and cumulative standings. This project will deprecate the existing Madison SC website.

The project is also a learning exercise in Spring Boot, React, TypeScript, and AI-assisted development.

## Status

The project is in the planning stage. Application scaffolding and local setup are not yet implemented. Setup, build, and test instructions will be added as those workflows become available and are verified.

## Planned architecture

A monorepo with two top-level application directories:

- **`frontend/`** — React and TypeScript, using Vite for development and builds.
- **`backend/`** — Java and Spring Boot with Spring Data JDBC, built with Maven.

PostgreSQL stores application data, Flyway manages SQL schema changes, and Docker supports local infrastructure and deployment.

The hamburger menu links to `/madisonsc`, a React page. That page calls the Spring Boot JSON API for the latest competition year/week, then loads and displays the corresponding weekly data.

See [PROJECT.md](PROJECT.md) for requirements, architecture, and implementation guidance.
