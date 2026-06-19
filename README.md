# jeap-opensearch-client-starter

Spring Boot starter for type-safe, authorization-aware read access to OpenSearch indices in jEAP
applications.

## Key Features

- **Auto-configured client:** `OpenSearchClient` and `SearchItemClient` configured automatically for plain HTTP or AWS SigV4-signed connections
- **Multi-version search:** Searches across ISM rollover index partitions and dispatches typed deserialization per document by `search_item.major_version`
- **Authorization:** Pre-check, BP pre-filter, and post-filter based on roles declared on each `IndexType`
- **User authorization:** `UserSearchItemAuthorization` integration with jEAP security for current-user role checks
- **Non-generic results:** `SearchItemView` eliminates wildcard return types from public APIs

## Documentation

- [Getting started](docs/getting-started.md)
- [SearchItemClient](docs/search-item-client.md)
- [Authorization](docs/authorization.md)
- [Configuration reference](docs/configuration.md)

## Note

This repository is part of the open source distribution of jEAP. See [github.com/jeap-admin-ch/jeap](https://github.com/jeap-admin-ch/jeap) for more information.

## License

This repository is Open Source Software licensed under the [Apache License 2.0](./LICENSE).
