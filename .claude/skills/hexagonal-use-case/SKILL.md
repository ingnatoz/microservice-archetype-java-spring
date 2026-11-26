---
name: hexagonal-use-case
description: Adds or changes a use case with this repo's hexagonal architecture, from the domain to the REST endpoint. Use it when creating endpoints, business rules, fields or tables.
---

# Use case with hexagonal architecture

1. Domain (`domain`): rules live in the record and are validated on construction. A broken rule
   throws a domain exception. Plain Java, no Spring or JPA.
2. Use case (`application`): a service method that orchestrates the domain and the ports. If it
   needs the time, it uses the `Clock` it receives in the constructor.
3. Outbound port: if the use case needs something external, add the method to the port interface
   (`TaskRepository`) with domain types, never JPA types.
4. Persistence (`infrastructure/persistence`): implement the method in the adapter. If the schema
   changes, create `src/main/resources/db/migration/V<n>__description.sql` and adjust the entity.
5. REST (`infrastructure/rest`): request DTO with Bean Validation and response DTO with `from`. A
   new exception maps to a `ProblemDetail` in `ApiExceptionHandler`.
6. A new service is registered as a bean in `UseCaseConfig`, with no annotations in `application`.
7. Tests:
   - Unit tests for the domain and the service.
   - `@WebMvcTest` for the HTTP contract: status, body and errors.
   - `TasksApplicationTests` when the database changes.
8. Finish with `make format lint test`.
