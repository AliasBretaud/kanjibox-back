# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

FloNoKanji is a Spring Boot REST API for managing Japanese kanji characters and words. Resources are user-scoped via OAuth2 JWT authentication (Auth0/Okta). The base context path is `/kanjibox` and the Swagger UI is at `/kanjibox/swagger-ui/index.html`.

## Commands

### Build
```bash
./mvnw clean install
```

### Run (local dev)
```bash
./mvnw spring-boot:run
```
Requires PostgreSQL running locally (`localhost:5432/kanjidb`, user: `postgres`, password: `root`) and the env vars below.

### Run with Docker Compose (full stack)
```bash
docker-compose up
```
Starts PostgreSQL, the API on port 8080, and the frontend on port 3000. Set `DEEPL_API_KEY`, `OKTA_OAUTH2_AUDIENCE`, and `OKTA_OAUTH2_ISSUER` in the environment before running.

### Run tests
```bash
./mvnw test
```
Tests run under the `test` Spring profile automatically (configured in `maven-surefire-plugin`). They use an H2 in-memory database seeded from `db/insert_data.sql`.

### Run a single test class
```bash
./mvnw test -Dtest=KanjiControllerTest
```

### Run a single test method
```bash
./mvnw test -Dtest=KanjiControllerTest#testPostKanjiOk1
```

## Required Environment Variables

| Variable | Description |
|---|---|
| `DEEPL_API_KEY` | DeepL API key for auto-translation |
| `OKTA_OAUTH2_AUDIENCE` | Auth0 audience (e.g. `https://kanjibox/api`) |
| `OKTA_OAUTH2_ISSUER` | Auth0 issuer URL |
| `AUTO_TRANSLATION_ENABLED` | `true`/`false`, defaults to `false` |

## Architecture

The codebase follows a strict three-layer architecture:

```
web/controller     → REST layer (Spring MVC, request/response handling)
business/service   → Business logic, orchestration
integration/       → JPA entities and Spring Data repositories
```

Cross-cutting concerns:
- `business/model` — Business objects (DTOs passed between web and service layers)
- `business/mapper` — Manual bidirectional mappers (`KanjiMapper`, `WordMapper`) between business models and JPA entities
- `business/validator` — Custom JSR-380 validators (e.g., `@JapaneseCharacterFormat`)
- `util/` — Shared utilities (`CharacterUtils`, `PatchHelper`, `ListUtils`, `TranslationUtils`)
- `conf/` — Spring configuration beans (security, CORS, REST template, converters)

### User Isolation

All resources (kanjis, words) are owned by a user identified by the JWT `sub` claim. Every service method takes a `userSub` string parameter extracted from `JwtAuthenticationToken`. `UserServiceImpl` creates users on first access via `createOrGetBySub()`.

### Key Domain Concepts

- **Kanji**: A single Japanese character with on-yomi (Chinese-style reading), kun-yomi (Japanese-style reading), multilingual translations, and a reference to words it appears in.
- **Word**: A Japanese word composed of kanji characters, with a furigana reading and translations. Words link back to their component kanjis via a `@ManyToMany` relationship.
- **Translations**: Stored as `TranslationEntity` records keyed by `Language` enum (`EN`, `FR`, `ES`, `JA`). In business models they are `Map<Language, List<String>>`.

### PATCH Operations

Both `KanjiController` and `WordController` support JSON merge-patch via `PATCH /{id}`. The `PatchHelper` utility applies a `JsonNode` patch onto the existing business object using Jackson's `ObjectMapper.readerForUpdating()`.

### Auto-detection Features

- **Kanji readings**: When `autoDetect=true` is passed to `POST /kanjis`, `KanjiServiceImpl` looks up readings from the `KanjiDictionary` (mojibox library).
- **Word translations**: When `AUTO_TRANSLATION_ENABLED=true`, translations are fetched asynchronously via DeepL (`DeeplTranslationServiceImpl`) using `CompletableFuture`.
- **Furigana**: Automatically generated for words containing kanji using Kuromoji morphological analysis (`CharacterUtils.getWordFurigana`).
- **Preview mode**: Both `POST /kanjis` and `POST /words` accept `?preview=true` to return the enriched object without persisting it.

### Security

Spring Security is configured in `WebSecurityConfig`. Public routes: `/`, `/error`, `/actuator/health`, `/swagger-ui/**`, `/v3/api-docs/**`. All other routes require a valid JWT. CORS allowed origins are driven by `kanji.cors.allowed-origins` per profile.

### Test Structure

- `unit/business/service/` — Unit tests with mocked dependencies
- `integration/repository/` — `@DataJpaTest` repository tests against H2
- `web/controller/` — `@SpringBootTest` + `MockMvc` integration tests using H2 seeded data, with `JwtDecoder` mocked out and JWT simulated via `SecurityMockMvcRequestPostProcessors.jwt()`

JaCoCo enforces a minimum of 70% complexity coverage on every build.
