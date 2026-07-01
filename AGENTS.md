# Repository Guidelines

## Project Structure & Module Organization
This is a Java 21, Spring Boot 3.5 multi-module Maven repository. Top-level modules are `zyn-kit` (shared utilities), `zyn-conf` (shared config), `zyn-infra/*` (Redis, MyBatis, storage, web, geo, etc.), `zyn-api/*` (API contracts), and `zyn-app/*` (runtime services). Active app services include `zyn-app-system`, `zyn-app-map`, `zyn-app-demo`, plus `zyn-app-gateway`. Standard layout applies in each module: `src/main/java`, `src/main/resources`, and `src/test/java`.

## Build, Test, and Development Commands
- `mvn clean install`: build the full reactor and install all modules locally.
- `mvn test`: run unit tests across the repository.
- `mvn -pl zyn-app/zyn-app-map -am -DskipTests compile`: compile one service with required upstream modules.
- `mvn -pl zyn-app/zyn-app-system spring-boot:run -Dspring-boot.run.profiles=dev`: start a single service in `dev`.

Use `-pl <module> -am` for targeted iteration instead of rebuilding everything.

## Coding Style & Naming Conventions
Use 4-space indentation, UTF-8, and the existing `com.zynboot.*` package layout. Follow the current layering already used in app modules: controller for HTTP protocol, service for orchestration, aggregate/repository for domain writes, mapper for persistence only. Keep controllers thin. Prefer immutable response DTOs (`*Res`) and command objects (`*Cmd`). Test classes use `*Test`; Spring Boot smoke tests use `*ApplicationTests`.

## Testing Guidelines
Tests live beside each module under `src/test/java`. Existing coverage is strongest around aggregates and module smoke tests, for example `LayerAggregateTest` and `ZynDemoApplicationTests`. Add or update tests for any domain rule, mapper SQL branch, or service workflow you change. No repository-wide coverage gate is enforced, so contributors should state exactly what they ran.

## Commit & Pull Request Guidelines
Recent history follows Conventional Commit style with optional scopes, such as `feat:`, `fix:`, and `refactor(map):`. Keep scopes module-focused (`map`, `system`, `infra-web`). Pull requests should include the changed modules, schema/config impacts, API examples or screenshots for externally visible changes, and the Maven commands used for verification.

## Configuration & Operations Notes
Profiles are managed with Maven/Spring profiles (`dev` default, `prod` optional). Shared infra dependencies are centralized in the root `pom.xml`; update versions there first. For map-related work, verify compile success with targeted module builds before touching other services.
