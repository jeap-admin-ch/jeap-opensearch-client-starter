# jeap-opensearch-client-starter

Spring Boot starter for type-safe, authorization-aware read access to OpenSearch indices in jEAP applications.

## Overview

This starter auto-configures an `OpenSearchClient` and a `SearchItemClient` that lets application services query OpenSearch indices defined by `IndexType` descriptors. Access is restricted by jEAP roles declared on each `IndexType`, and results are deserialized into the strongly-typed data class of the matching `IndexType`.

## Features

- Auto-configured `OpenSearchClient` for both plain HTTP (Apache) and AWS-signed (`AwsSdk2Transport`) connections
- `SearchItemClient` for multi-version searches across ISM rollover indices
- Authorization filter via `SearchItemAuthorization` — items are filtered by the roles declared on the `IndexType`
- `UserSearchItemAuthorization` integration with jEAP security for user-based role checks
- `SearchItemView` non-generic result type eliminates wildcard return types from public APIs

## Configuration

```yaml
jeap:
  opensearch:
    client:
      connection:
        uri: https://my-opensearch-cluster:9200
        # For AWS OpenSearch Service (optional):
        aws-signing-region: eu-central-2
```

## Apache vs AWS transport

The starter auto-detects which transport to use:

- If `aws-signing-region` is set and the AWS SDK classes are on the classpath, `AwsSdk2Transport` with `DefaultCredentialsProvider` is used.
- Otherwise, `ApacheHttpClient5Transport` is used.

## Authorization

```java
@Autowired SearchItemClient searchItemClient;
@Autowired UserSearchItemAuthorization auth;

List<SearchItemView> results = searchItemClient.searchMultiVersionWithUserAuth(
        request, auth.forCurrentUser());
```

## Build

```bash
mvn verify
```

Integration tests use Testcontainers and require Docker.
