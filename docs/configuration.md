# Configuration reference

All properties live under the `jeap.opensearch.client.connection` prefix.

## Connection properties

| Property                                               | Default | Description                                                                                                                                                                                                  |
|--------------------------------------------------------|---|--------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| `jeap.opensearch.client.connection.uri`                | — | URI of the OpenSearch cluster (e.g. `https://my-domain.eu-central-2.es.amazonaws.com` or `http://localhost:9200`). Required.                                                                                 |
| `jeap.opensearch.client.connection.aws-signing-region` | — | AWS region for SigV4 request signing (e.g. `eu-central-2`). When set and the AWS SDK is on the classpath, `AwsSdk2Transport` with `DefaultCredentialsProvider` is used. Leave blank for non-AWS deployments. |

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

## Related

- [Getting started](getting-started.md)
- [Authorization](authorization.md)
- [jeap-opensearch-client-starter](../README.md)
