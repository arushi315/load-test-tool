package com.http.load.tool.dataobjects;

import io.vertx.core.http.HttpMethod;
import io.vertx.rxjava.core.buffer.Buffer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Created by manish kumar.
 */
public class RemoteOperation {

    private List<Parameter> parameters;

    // Uniquely identify the operation type. It's a unique key to group the various HTTP
    // request types, and control the HTTP request load parameters, and respective reporting.
    private String operationType;
    private int loadPercentage;
    private int loadRequestsPerSecond;

    // You can override the HTTP method to remote server.
    private HttpMethod httpMethod = HttpMethod.POST;

    // If we want to send some payload in HTTP request to remote server.
    private String requestFilePath;
    private Buffer requestBuffer;

    public List<Parameter> getParameters() {
        return parameters;
    }

    public RemoteOperation setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
        return this;
    }

    public String getOperationType() {
        return operationType;
    }

    public RemoteOperation setOperationType(String operationType) {
        this.operationType = operationType;
        return this;
    }

    public int getLoadPercentage() {
        return loadPercentage;
    }

    public RemoteOperation setLoadPercentage(int loadPercentage) {
        this.loadPercentage = loadPercentage;
        return this;
    }

    public int getLoadRequestsPerSecond() {
        return loadRequestsPerSecond;
    }

    public RemoteOperation setLoadRequestsPerSecond(int loadRequestsPerSecond) {
        this.loadRequestsPerSecond = loadRequestsPerSecond;
        return this;
    }

    public HttpMethod getHttpMethod() {
        return httpMethod;
    }

    public RemoteOperation setHttpMethod(HttpMethod httpMethod) {
        this.httpMethod = httpMethod;
        return this;
    }

    public Buffer getRequestBuffer() {
        return requestBuffer;
    }

    public RemoteOperation setRequestFilePath(final String requestFilePath) throws IOException {
        if (requestFilePath != null) {
            requestBuffer = Buffer.newInstance(io.vertx.core.buffer.Buffer.buffer(Files.readAllBytes(Paths.get(requestFilePath))));
        }
        return this;
    }

    @Override
    public String toString() {
        return "RemoteOperation{" +
                "parameters=" + parameters +
                ", operationType='" + operationType + '\'' +
                ", loadPercentage=" + loadPercentage +
                ", loadRequestsPerSecond=" + loadRequestsPerSecond +
                ", httpMethod=" + httpMethod +
                ", requestFilePath='" + requestFilePath + '\'' +
                ", requestBuffer=" + requestBuffer +
                '}';
    }
}