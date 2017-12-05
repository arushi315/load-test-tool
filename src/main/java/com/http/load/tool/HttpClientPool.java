package com.http.load.tool;

import com.http.load.tool.constants.Headers;
import com.http.load.tool.dataobjects.KeyCertOptions;
import com.http.load.tool.dataobjects.TestInput;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.net.PfxOptions;
import io.vertx.ext.web.client.WebClientOptions;
import io.vertx.rxjava.core.Vertx;
import io.vertx.rxjava.core.buffer.Buffer;
import io.vertx.rxjava.ext.web.client.HttpRequest;
import io.vertx.rxjava.ext.web.client.WebClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.apache.commons.lang.StringUtils.equalsIgnoreCase;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

/**
 * Created by manish kumar.
 */
@Component
public class HttpClientPool {

    private int clientsCount;
    private AtomicInteger keyCertIndexCounter = new AtomicInteger();
    private List<WebClient> webClients = new ArrayList<>();
    @Value("${remote.server.timeout.millis:990000}")
    private int remoteServerTimeoutInMillis;
    @Value("${http.connection.pool.size:50000}")
    private int httpConnectionPoolSize;
    @Value("${ignore.ssl.errors:true}")
    private boolean ignoreSslError;
    @Autowired
    private Vertx vertx;
    @Autowired
    private TestInput testInput;

    public HttpRequest<Buffer> request(final String remoteHost, final String remotePath, final HttpMethod method) {
        HttpRequest<Buffer> httpRequest = createClient(remoteHost).request(method, remotePath);
        httpRequest.headers().addAll(Headers.HTTP_HEADERS);
        if (isNotEmpty(testInput.getHttpLoadInput().getBasicAuthHeader())) {
            httpRequest.headers().add("Authorization", testInput.getHttpLoadInput().getBasicAuthHeader());
        }
        return httpRequest;
    }

    private WebClient createClient(final String remoteHost) {
        if (clientsCount < testInput.getHttpLoadInput().getHttpClientInstances()) {
            URI uri = URI.create(remoteHost);
            boolean secure = equalsIgnoreCase(uri.getScheme(), "https");
            WebClientOptions options = new WebClientOptions()
                    .setDefaultHost(uri.getHost())
                    .setSsl(secure)
                    .setConnectTimeout(remoteServerTimeoutInMillis)
                    .setMaxPoolSize(httpConnectionPoolSize)
                    .setTryUseCompression(true)
                    .setIdleTimeout(remoteServerTimeoutInMillis + 5000)
                    .setTrustAll(ignoreSslError)
                    .setVerifyHost(!ignoreSslError)
                    .setDefaultPort(getPort(uri, secure));
            setKeyCertForMutualAuth(options);
            WebClient webClient = WebClient.create(vertx, options);
            webClients.add(webClient);

            // This may cause couple of extra WebClient instances but it's okay.
            clientsCount++;
        }
        return webClients.get((int) (Math.random() * webClients.size()));
    }

    private int getPort(final URI uri, final boolean secure) {
        int port = uri.getPort();
        if (port == -1) {
            port = secure ? 443 : 80;
        }
        return port;
    }

    private void setKeyCertForMutualAuth(final WebClientOptions options) {
        // Pick the certificate to be used in mutual auth. Each HTTP client will pick one of the provided cert.
        if (testInput.getHttpLoadInput().isEnableMutualAuth()) {
            List<KeyCertOptions> keyCertOptions = testInput.getHttpLoadInput().getCertOptionsForMutualAuth();
            int certIndex = keyCertIndexCounter.getAndUpdate(current -> current < keyCertOptions.size() - 1 ? current + 1 : 0);
            KeyCertOptions certOptions = keyCertOptions.get(certIndex);
            options.setPfxKeyCertOptions(
                    new PfxOptions()
                            .setPath(certOptions.getPath())
                            .setPassword(certOptions.getPassword()));
        }
    }
}