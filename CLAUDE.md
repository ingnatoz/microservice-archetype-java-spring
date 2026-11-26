# CLAUDE.md

Microservice archetype with Java 25, Spring Boot 4.2, Oracle and hexagonal architecture. It exposes
a task CRUD at `/api/v1/tasks` on port 8082.

## Commands

`make help` lists everything. The usual ones: `make run`, `make format`, `make lint test build` and
`make docker-up`. They need JDK 25 and Maven 3.9. Tests and `make run` also need Docker, because
they start Oracle with Testcontainers.

## Architecture

- `domain`: the `Task` record validates its rules on construction. Plain Java.
- `application`: `TaskService` (use cases) and the outbound port `TaskRepository`. Plain Java;
  Spring creates the service in `infrastructure/config/UseCaseConfig`.
- `infrastructure/rest`: controller, DTOs, CORS and `ApiExceptionHandler`, which turns every error
  into a `ProblemDetail`.
- `infrastructure/persistence`: implements the port with Spring Data JPA. Flyway migrations
  (`src/main/resources/db/migration`) create the schema; Hibernate only validates it.
- `ArchitectureTest` (ArchUnit) fails the tests when a layer depends on one it must not.
- Health check: `GET /actuator/health`, the only Actuator endpoint exposed. The Dockerfile only
  builds and runs the jar.
- Configuration comes from environment variables (listed in the README). In `Docker/compose.yml`,
  Oracle publishes no ports: only the API reaches it.

## Language

- Code, comments, messages, tests, docs and the files in `.claude/` are in English, except
  `README.es.md`, the Spanish translation of `README.md`.
- Keep `README.md` and `README.es.md` in sync.

Before you call a change done, run `/verify`.
