# SearchItemClient

`SearchItemClient` is the central API for type-safe, authorization-aware search access to OpenSearch
indices. It is auto-configured by the starter and can be injected directly.

## Method families

Every search method comes in three authorization stages:

| Stage     | Method suffix      | Authorization                                                                                                                        |
|-----------|--------------------|--------------------------------------------------------------------------------------------------------------------------------------|
| Unchecked | `…Unchecked(…)`    | No authorization checks. Use only for internal or trusted callers.                                                                   |
| Explicit  | `…(…, auth)`       | Accepts an explicit `Authorization` object. Throws `IndexTypeAccessDeniedException` if `auth` is `null` or lacks the required roles. |
| User      | `…WithUserAuth(…)` | Reads the current user's `Authorization` from `UserSearchItemAuthorization` and delegates to the explicit stage.                     |

## Multi-version search

All methods search across multiple index type versions simultaneously, dispatching deserialization
to the correct `IndexType` based on `search_item.major_version` stored in each document.

All provided `IndexType` instances must share the same `system` and `originType`. Authorization uses
the roles of the **latest** version (highest `majorVersion/minorVersion`).

### searchMultiVersionUnchecked

```java
List<SearchItemView> searchMultiVersionUnchecked(
    List<IndexType<?>> indexTypes,
    Query query)

List<SearchItemView> searchMultiVersionUnchecked(
    List<IndexType<?>> indexTypes,
    Query query,
    Consumer<SearchRequest.Builder> searchRequestCustomizer)
```

Executes the query with no authorization checks. Use for background jobs or trusted internal APIs.

### searchMultiVersion

```java
List<SearchItemView> searchMultiVersion(
    List<IndexType<?>> indexTypes,
    Query query,
    Authorization auth)

List<SearchItemView> searchMultiVersion(
    List<IndexType<?>> indexTypes,
    Query query,
    Consumer<SearchRequest.Builder> searchRequestCustomizer,
    Authorization auth)
```

Authorization-checked search against an explicit `Authorization`:

1. Validates `indexTypes` and resolves the latest version.
2. Calls `IndexTypeAuthorization.checkAccess` — throws `IndexTypeAccessDeniedException` if `auth` is `null` or lacks the required roles.
3. Wraps the query in a BP pre-filter when the caller has only BP-scoped roles (no global userrole), so OpenSearch returns only items for the caller's authorised business partners.
4. Post-filters results via `SearchItemAuthorization.filterByAuthorization`.

### searchMultiVersionWithUserAuth

```java
List<SearchItemView> searchMultiVersionWithUserAuth(
    List<IndexType<?>> indexTypes,
    Query query)

List<SearchItemView> searchMultiVersionWithUserAuth(
    List<IndexType<?>> indexTypes,
    Query query,
    Consumer<SearchRequest.Builder> searchRequestCustomizer)
```

Reads the current user's `Authorization` from `UserSearchItemAuthorization` and delegates to
`searchMultiVersion`.

## SearchItemView

`SearchItemView` is the non-generic result type returned by all search methods. It provides
type-safe access to the deserialized data:

```java
for (SearchItemView view : results) {
    Origin origin = view.origin();
    MyDocumentData data = view.dataAs(MyDocumentData.class);
    SearchItemMetadata meta = view.metadata();
}
```

`dataAs(Class<T>)` casts the already-deserialized data object to `T`. It throws
`ClassCastException` if `T` does not match the actual data class of the document's `IndexType`.

## Request customization

The `searchRequestCustomizer` callback provides access to the `SearchRequest.Builder` for setting
pagination, sorting, highlighting, or other OpenSearch search options:

```java
searchItemClient.searchMultiVersionWithUserAuth(
    indexTypes,
    query,
    builder -> builder
        .from(0)
        .size(20)
        .sort(s -> s.field(f -> f.field("data.created_at").order(SortOrder.Desc)))
);
```

## Exceptions

| Exception                        | Thrown when                                                                                                                                                                                                                                   |
|----------------------------------|-----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `IndexTypeAccessDeniedException` | `auth` is `null` or lacks the roles required by the index type.                                                                                                                                                                               |
| `SearchItemClientException`      | `indexTypes` is null/empty, two versions share the same `majorVersion`, a document is missing `search_item.major_version`, the major version of a document does not match any provided `IndexType`, or an OpenSearch I/O or API error occurs. |

## Related

- [Getting started](getting-started.md)
- [Authorization](authorization.md)
- [Configuration reference](configuration.md)
- [jeap-opensearch-client-starter](../README.md)
