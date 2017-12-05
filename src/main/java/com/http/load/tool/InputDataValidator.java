package com.http.load.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.http.load.tool.dataobjects.HttpLoadInput;
import com.http.load.tool.dataobjects.KeyCertOptions;
import com.http.load.tool.dataobjects.TestInput;
import com.http.load.tool.dataobjects.TestStatus;
import io.vertx.core.Handler;
import io.vertx.rxjava.ext.web.RoutingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Base64;
import java.util.List;

import static com.http.load.tool.constants.LoadTestType.CONCURRENT_OPEN_CONNECTIONS;
import static com.http.load.tool.constants.LoadTestType.REQUEST_PER_SECOND;
import static io.vertx.core.impl.Arguments.require;
import static org.apache.commons.lang.StringUtils.isEmpty;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Component
public class InputDataValidator implements Handler<RoutingContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(InputDataValidator.class);
    private final ObjectMapper mapper = new ObjectMapper();
    @Autowired
    private TestStatus testStatus;
    @Autowired
    private TestInput input;

    @Override
    public void handle(final RoutingContext context) {
        HttpLoadInput loadInput = validateInput(context);
        LOGGER.info("Successfully validated the load test input \n{}", loadInput);
        input.setHttpLoadInput(loadInput);
        context.next();
    }

    private HttpLoadInput validateInput(final RoutingContext context) {
        try {
            HttpLoadInput httpLoadInput = mapper.readValue(context.getBodyAsJson().toString(), HttpLoadInput.class);
            require(httpLoadInput.getDurationInSeconds() != null && httpLoadInput.getDurationInSeconds() > 0,
                    "Duration to run the tests should be defined in minutes using variable 'durationInSeconds'");
            require(httpLoadInput.getRampUpTimeInSeconds() != null &&
                    httpLoadInput.getRampUpTimeInSeconds() > 0, "Provide 'rampUpTimeInSeconds'");
            require(!CollectionUtils.isEmpty(httpLoadInput.getRemoteOperations()),
                    "Define the remote hosts as array with name 'remoteHostsWithPortAndProtocol'");
            require(httpLoadInput.getTestType() == REQUEST_PER_SECOND || httpLoadInput.getTestType() == CONCURRENT_OPEN_CONNECTIONS,
                    "Define 'testType' parameter value as 'REQUEST_PER_SECOND' or 'CONCURRENT_OPEN_CONNECTIONS'");

            if (httpLoadInput.getTestType() == CONCURRENT_OPEN_CONNECTIONS) {
                require(httpLoadInput.getMaxOpenConnections() > 0, "Define 'maxOpenConnections'");
                httpLoadInput.getRemoteOperations().forEach(remoteOperation -> {
                    require(isNotEmpty(remoteOperation.getOperationType()),
                            "Please define operation type. It can be any string and just used for grouping and reporting purpose.");
                    require(remoteOperation.getLoadPercentage() > 0,
                            "Please Define 'loadPercentage' for " + remoteOperation.getOperationType());
                });
            }
            if (httpLoadInput.isUseBasicAuth()) {
                // For optimization purpose, create Basic Auth header now itself.
                require(httpLoadInput.getBasicAuthUser() != null, "Please provide user for basic auth with key \"basicAuthUser\"");
                require(httpLoadInput.getBasicAuthPassword() != null, "Please provide user for basic auth with key \"basicAuthPassword\"");
                httpLoadInput.setBasicAuthHeader("Basic " + Base64.getEncoder().encodeToString((
                        httpLoadInput.getBasicAuthUser() + ":" + httpLoadInput.getBasicAuthPassword()).getBytes()));
            }

            if (httpLoadInput.isEnableMutualAuth()) {
                List<KeyCertOptions> certs = httpLoadInput.getCertOptionsForMutualAuth();
                require(certs != null && certs.size() > 0, "Provide certs for mutual auth.");
                boolean validCerts = !certs.stream().anyMatch(options -> isEmpty(options.getPassword()) || isEmpty(options.getPassword()));
                require(validCerts, "Please provide path and password for each key cert.");
            }

            return httpLoadInput;
        } catch (final Exception e) {
            LOGGER.error("Error parsing test input data.", e);
            testStatus.shutdown();
            throw new RuntimeException(e);
        }
    }
}