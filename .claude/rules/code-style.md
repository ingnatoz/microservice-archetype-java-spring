# Code style

- Hexagonal architecture: `domain` depends on nothing, `application` only on `domain`, and the
  `infrastructure` adapters do not use each other. `ArchitectureTest` checks it.
- No Spring or JPA in `domain` or `application`. JPA entities live in `infrastructure/persistence`
  and convert with `from` and `toDomain`.
- Use cases are plain Java classes, registered as beans in `UseCaseConfig`.
- Business rules go in the domain. Request validation (Bean Validation) goes in the controller DTOs.
- Errors leave as `ProblemDetail` from `ApiExceptionHandler`. Never return the message or the stack
  trace of an unexpected exception.
- The schema changes only through a new `V<n>__description.sql` migration. Never edit an applied
  migration.
- Formatting with Spotless (palantir-java-format): `make format`.
- Everything in the code is in English: identifiers, comments, error messages and test data. The only
  Spanish file is `README.es.md`.
- Minimal comments: only when the code cannot show the why. Mark a shortcut with a known limit as
  `// shortcut: <limit>, <when to improve it>`.
- No `.env` files or secrets in the code: configuration comes from environment variables.
