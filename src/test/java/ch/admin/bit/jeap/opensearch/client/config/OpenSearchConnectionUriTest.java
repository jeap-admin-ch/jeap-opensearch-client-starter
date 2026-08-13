package ch.admin.bit.jeap.opensearch.client.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OpenSearchConnectionUriTest {

    @Test
    void parseHttpsUriProvidesSchemeLessHostForAwsTransport() {
        OpenSearchConnectionUri uri = OpenSearchConnectionUri.parse(
                "https://search-foo.eu-central-2.es.amazonaws.com");

        assertThat(uri.hostAndPort()).isEqualTo("search-foo.eu-central-2.es.amazonaws.com");
        assertThat(uri.toUri()).isEqualTo("https://search-foo.eu-central-2.es.amazonaws.com");
        assertThat(uri.isHttps()).isTrue();
    }

    @Test
    void parseHttpsUriPreservesPort() {
        OpenSearchConnectionUri uri = OpenSearchConnectionUri.parse("https://search-foo.example:9200");

        assertThat(uri.hostAndPort()).isEqualTo("search-foo.example:9200");
        assertThat(uri.toUri()).isEqualTo("https://search-foo.example:9200");
    }

    @Test
    void parseHostWithoutSchemeDefaultsToHttps() {
        OpenSearchConnectionUri uri = OpenSearchConnectionUri.parse("search-foo.example:9200");

        assertThat(uri.hostAndPort()).isEqualTo("search-foo.example:9200");
        assertThat(uri.toUri()).isEqualTo("https://search-foo.example:9200");
    }

    @Test
    void requireHttpsRejectsHttpForAwsTransport() {
        OpenSearchConnectionUri uri = OpenSearchConnectionUri.parse("http://search-foo.example");

        assertThatThrownBy(uri::requireHttpsForAwsSigning)
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("jeap.opensearch.client.connection.uri")
                .hasMessageContaining("https")
                .hasMessageContaining("aws-signing-region");
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "",
            "   ",
            "ftp://search-foo.example",
            "https://user:secret@search-foo.example",
            "https://search-foo.example/opensearch",
            "https://search-foo.example?foo=bar",
            "https://search-foo.example#fragment"
    })
    void invalidUriFailsWithPropertyName(String configuredUri) {
        assertThatThrownBy(() -> OpenSearchConnectionUri.parse(configuredUri))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("jeap.opensearch.client.connection.uri");
    }

    @Test
    void invalidUriDoesNotExposeConfiguredSecrets() {
        String configuredUri = "https://user:secret@search-foo.example?token=sensitive";

        assertThatThrownBy(() -> OpenSearchConnectionUri.parse(configuredUri))
                .hasMessageContaining("jeap.opensearch.client.connection.uri")
                .hasMessageNotContaining("user")
                .hasMessageNotContaining("secret")
                .hasMessageNotContaining("token")
                .hasMessageNotContaining("sensitive");
    }

    @Test
    void nullUriFailsWithPropertyName() {
        assertThatThrownBy(() -> OpenSearchConnectionUri.parse(null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("jeap.opensearch.client.connection.uri");
    }
}
