# Configuration reference

All properties live under the `jeap.opensearch.client.connection` prefix.

## Connection properties

| Property                                               | Default | Description                                                                                                                                                                                                  |
|--------------------------------------------------------|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `jeap.opensearch.client.connection.uri`                | — | URI of the OpenSearch cluster (e.g. `https://my-domain.eu-central-2.es.amazonaws.com` or `http://localhost:9200`). A value without a scheme defaults to `https`. Required.                                    |
| `jeap.opensearch.client.connection.aws-signing-region` | — | AWS region for SigV4 request signing (e.g. `eu-central-2`). When set and the AWS SDK is on the classpath, `AwsSdk2Transport` with `DefaultCredentialsProvider` is used and the URI must use `https`. Leave blank for non-AWS deployments. |

## Transport selection

The starter selects the OpenSearch transport automatically:

| Condition                                                                | Transport                                                                                                                                                     |
|--------------------------------------------------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `aws-signing-region` is set **and** AWS SDK classes are on the classpath | `AwsSdk2Transport` — requests are signed using the default AWS credentials provider chain (ECS task role, EC2 instance profile, environment variables, etc.). |
| Otherwise                                                                | `ApacheHttpClient5Transport` — plain HTTP(S) with Apache HttpClient 5.                                                                                        |

## Example configurations

Non-AWS (local or on-premise OpenSearch):

```yaml
jeap:
  opensearch:
    client:
      connection:
        uri: http://localhost:9200
```

AWS OpenSearch Service with IAM/SigV4:

```yaml
jeap:
  opensearch:
    client:
      connection:
        uri: https://my-domain.eu-central-2.es.amazonaws.com
        aws-signing-region: eu-central-2
```

## URI format

The same URI format works for both transports. The starter passes the full normalized URI to the
Apache transport and only the scheme-less `host[:port]` expected by `AwsSdk2Transport` to the AWS
transport.

| Configured value           | Interpreted as              |
|----------------------------|-----------------------------|
| `https://my-host`          | `https://my-host`           |
| `https://my-host:9200`     | `https://my-host:9200`      |
| `my-host`                  | `https://my-host`           |
| `my-host:9200`             | `https://my-host:9200`      |
| `http://localhost:9200`    | `http://localhost:9200`     |

Startup fails when the URI is missing, uses a scheme other than `http` or `https`, contains
credentials, or contains a path, query, or fragment. Plain `http` is rejected when
`aws-signing-region` selects the AWS transport.

## Related

- [Getting started](getting-started.md)
- [Authorization](authorization.md)
- [jeap-opensearch-client-starter](../README.md)
