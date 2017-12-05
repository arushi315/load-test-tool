package com.http.load.tool.executor;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.http.load.tool.condition.NamedPipeModeCondition;
import com.http.load.tool.dataobjects.KerberosLoadTestConfig;
import com.http.load.tool.dataobjects.KerberosRequest;
import com.http.load.tool.dataobjects.TestStatus;
import com.http.load.tool.pipe.NamedPipeReader;
import io.vertx.rxjava.core.Vertx;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Conditional;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static io.vertx.core.impl.Arguments.require;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

@Component
@Conditional({NamedPipeModeCondition.class})
public class NamedPipeLoadExecutor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NamedPipeLoadExecutor.class);
    private final List<NamedPipeReader> namedPipeReaders = new ArrayList<>();
    private final ObjectMapper mapper = new ObjectMapper();
    private KerberosLoadTestConfig config;

    @Value("${kerberos.config.path}")
    private String kerberosConfigPath;
    @Autowired
    private Vertx vertx;
    @Autowired
    private TestStatus testStatus;
    @Autowired(required = false)
    private List<Map<String, String>> testData;

    @PostConstruct
    public void initExecutor() throws IOException {

        require(isNotEmpty(kerberosConfigPath), "Define the kerberos config path using \"-Dkerberos.config.path\"");
        Path path = Paths.get(kerberosConfigPath);
        require(Files.exists(path), "Kerberos config not found at path " + path);
        config = mapper.readValue(new String(Files.readAllBytes(path)), KerberosLoadTestConfig.class);
        for (String pipeName : config.getPipeNames()) {
            namedPipeReaders.add(new NamedPipeReader(pipeName));
        }
        testStatus.setNamedPipeReaders(namedPipeReaders);
        startTest();
    }

    void startTest() {
        vertx.setPeriodic(250, doNothing -> {
            for (int index = 0; index < config.getRequestsPerSecond() / 4; index++) {
                NamedPipeReader pipe = findAvailablePipe();
                testStatus.incrementTotalRequests();
                if (pipe == null) {
                    testStatus.incrementMissedRequestsOnPipe();
                } else {
                    readFromPipe(pipe);
                }
            }
        });
    }

    private void readFromPipe(final NamedPipeReader pipe) {
        require(testData != null, "CSV file containing \"User\" variables not provided.");
        int random = (int) (Math.random() * testData.size());
        Map<String, String> data = testData.get(random);
        KerberosRequest kerberosRequest = new KerberosRequest()
                .setRequestId(UUID.randomUUID().toString())
                .setPrincipal(config.getPrinciple())
                .setPassword(config.getPassword())
                .setUpn(data.get("User"))
                .setSpn(config.getSpn());

        String requestJson;
        try {
            requestJson = mapper.writeValueAsString(kerberosRequest);
        } catch (final JsonProcessingException ex) {
            throw new RuntimeException(ex);
        }
        LOGGER.debug("JSON request = {}", requestJson);

        vertx.rxExecuteBlocking(future -> {
            byte[] kcdResponse = pipe.read(requestJson);
            LOGGER.debug("Got response for user {}, length = {}", kerberosRequest.getUpn(), kcdResponse.length);

            future.complete(kcdResponse);
        }).subscribe(kcdResponse -> {
            pipe.markAvailable();
            testStatus.incrementSuccessCount();
            LOGGER.debug("Successfully generated the token for upn {}", kerberosRequest.getUpn());
        }, ex -> {
            pipe.markAvailable();
            testStatus.incrementErrorCount();
            LOGGER.error("Error while reading from pipe {}", pipe.getPipeName(), ex);
        });
    }

    private NamedPipeReader findAvailablePipe() {
        NamedPipeReader pipeReader = null;
        for (NamedPipeReader reader : namedPipeReaders) {
            if (reader.isKerberosReady() && reader.acquire()) {
                pipeReader = reader;
                break;
            }
        }
        return pipeReader;
    }
}