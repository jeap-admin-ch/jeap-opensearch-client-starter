# AGENTS.md — jeap-opensearch-client-starter

## Project purpose
Spring Boot auto-configuration for reading from jEAP OpenSearch indices with type safety and jEAP role-based authorization.

## Key packages
- `config` — `OpenSearchClientConfiguration` (Apache) and `OpenSearchClientAwsConfiguration` (AWS); both disable `FAIL_ON_TRAILING_TOKENS` on the `JsonMapper` passed to `JacksonJsonpMapper`
- `search` — `SearchItemClient`: executes multi-version searches, deserializes hits using `IndexType.dataClass()`
- `auth` — `SearchItemAuthorization`, `SearchItemAccessDeniedException`, `UserSearchItemAuthorization`
- `domain` — `SearchItemView` (non-generic result interface), `SearchItemTyped<T>` (typed result record)
- `filter` — query filter builders

## Critical convention
Every `JacksonJsonpMapper` instantiation must use a `JsonMapper` rebuilt with `.disable(DeserializationFeature.FAIL_ON_TRAILING_TOKENS)`. Jackson 3 enforces trailing-token checks even for `readValue(JsonParser, ...)`, which breaks deserialization when reading `JsonNode` fields from within a streaming JSONP response.

## External dependency
Depends on `jeap-opensearch-index-type`. Version controlled via the `jeap-opensearch-index-type.version` property.

## Build & test
```bash
mvn verify
```
Integration tests use Testcontainers (OpenSearch container). Requires Docker.
