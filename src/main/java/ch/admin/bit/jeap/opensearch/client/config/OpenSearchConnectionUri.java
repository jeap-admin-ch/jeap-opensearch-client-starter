package ch.admin.bit.jeap.opensearch.client.config;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;

record OpenSearchConnectionUri(String scheme, String host, int port) {

    private static final String PROPERTY = "jeap.opensearch.client.connection.uri";
    private static final String HTTPS = "https";
    private static final String HTTP = "http";
    private static final int NO_PORT = -1;

    static OpenSearchConnectionUri parse(String configuredUri) {
        if (configuredUri == null || configuredUri.isBlank()) {
            throw invalid("must not be empty");
        }
        String trimmed = configuredUri.trim();
        String withScheme = trimmed.contains("://") ? trimmed : HTTPS + "://" + trimmed;

        URI uri;
        try {
            uri = new URI(withScheme);
        } catch (URISyntaxException e) {
            throw invalid(e.getReason());
        }

        String scheme = uri.getScheme() == null ? null : uri.getScheme().toLowerCase(Locale.ROOT);
        if (!HTTPS.equals(scheme) && !HTTP.equals(scheme)) {
            throw invalid(
                    "unsupported scheme '%s', expected 'https' or 'http'".formatted(uri.getScheme()));
        }
        if (uri.getHost() == null) {
            throw invalid("no host name found");
        }
        if (uri.getUserInfo() != null) {
            throw invalid("credentials in the URI are not supported");
        }
        if (uri.getPath() != null && !uri.getPath().isEmpty() && !"/".equals(uri.getPath())) {
            throw invalid("a path is not supported, expected host name and optional port only");
        }
        if (uri.getQuery() != null || uri.getFragment() != null) {
            throw invalid(
                    "a query or fragment is not supported, expected host name and optional port only");
        }
        return new OpenSearchConnectionUri(scheme, uri.getHost(), uri.getPort());
    }

    void requireHttpsForAwsSigning() {
        if (!isHttps()) {
            throw invalid(
                    "'https' is required when 'jeap.opensearch.client.connection.aws-signing-region' is set");
        }
    }

    boolean isHttps() {
        return HTTPS.equals(scheme);
    }

    String hostAndPort() {
        return port == NO_PORT ? host : host + ":" + port;
    }

    String toUri() {
        return scheme + "://" + hostAndPort();
    }

    private static IllegalStateException invalid(String reason) {
        // Never expose the configured URI: rejected values may contain credentials or query secrets.
        return new IllegalStateException(
                "Invalid OpenSearch URI configured in '%s': %s."
                        .formatted(PROPERTY, reason));
    }
}
