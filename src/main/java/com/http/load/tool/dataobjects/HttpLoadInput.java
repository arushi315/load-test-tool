package com.http.load.tool.dataobjects;

import com.http.load.tool.constants.LoadTestType;
import java.util.List;

/**
 * Created by manish kumar.
 */
public class HttpLoadInput {

    private List<RemoteOperation> remoteOperations;
    private List<Parameter> commonParameters;
    private List<String> userAgentHeaders;

    private Long durationInSeconds;
    private int maxOpenConnections;

    private String path;
    private List<String> remoteHosts;

    private boolean useBasicAuth;
    private String basicAuthUser;
    private String basicAuthPassword;
    private Double rampUpTimeInSeconds;
    private LoadTestType testType;

    // If we want to HTTP Client to use mutual auth with remote server.
    private boolean enableMutualAuth;
    private List<KeyCertOptions> certOptionsForMutualAuth;


    // We will allow simulating the device by doing handshake from each client to remote server.
    private int httpClientInstances = 2;

    // This doesn't come at input parameter, used for optimization.
    private String basicAuthHeader;

    public Double getRampUpTimeInSeconds() {
        return rampUpTimeInSeconds;
    }

    public HttpLoadInput setRampUpTimeInSeconds(Double rampUpTimeInSeconds) {
        this.rampUpTimeInSeconds = rampUpTimeInSeconds;
        return this;
    }

    public List<RemoteOperation> getRemoteOperations() {
        return remoteOperations;
    }

    public HttpLoadInput setRemoteOperations(List<RemoteOperation> remoteOperations) {
        this.remoteOperations = remoteOperations;
        return this;
    }

    public boolean isUseBasicAuth() {
        return useBasicAuth;
    }

    public HttpLoadInput setUseBasicAuth(boolean useBasicAuth) {
        this.useBasicAuth = useBasicAuth;
        return this;
    }

    public Long getDurationInSeconds() {
        return durationInSeconds;
    }

    public HttpLoadInput setDurationInSeconds(Long durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
        return this;
    }

    public String getBasicAuthHeader() {
        return basicAuthHeader;
    }

    public HttpLoadInput setBasicAuthHeader(String basicAuthHeader) {
        this.basicAuthHeader = basicAuthHeader;
        return this;
    }

    public int getMaxOpenConnections() {
        return maxOpenConnections;
    }

    public HttpLoadInput setMaxOpenConnections(int maxOpenConnections) {
        this.maxOpenConnections = maxOpenConnections;
        return this;
    }

    public String getBasicAuthUser() {
        return basicAuthUser;
    }

    public HttpLoadInput setBasicAuthUser(String basicAuthUser) {
        this.basicAuthUser = basicAuthUser;
        return this;
    }

    public String getBasicAuthPassword() {
        return basicAuthPassword;
    }

    public HttpLoadInput setBasicAuthPassword(String basicAuthPassword) {
        this.basicAuthPassword = basicAuthPassword;
        return this;
    }

    public LoadTestType getTestType() {
        return testType;
    }

    public HttpLoadInput setTestType(LoadTestType testType) {
        this.testType = testType;
        return this;
    }

    public List<String> getRemoteHosts() {
        return remoteHosts;
    }

    public HttpLoadInput setRemoteHosts(List<String> remoteHosts) {
        this.remoteHosts = remoteHosts;
        return this;
    }

    public String getPath() {
        return path;
    }

    public HttpLoadInput setPath(String path) {
        this.path = path;
        return this;
    }

    public List<Parameter> getCommonParameters() {
        return commonParameters;
    }

    public HttpLoadInput setCommonParameters(List<Parameter> commonParameters) {
        this.commonParameters = commonParameters;
        return this;
    }

    public int getHttpClientInstances() {
        return httpClientInstances;
    }

    public HttpLoadInput setHttpClientInstances(int httpClientInstances) {
        this.httpClientInstances = httpClientInstances;
        return this;
    }

    public boolean isEnableMutualAuth() {
        return enableMutualAuth;
    }

    public HttpLoadInput setEnableMutualAuth(boolean enableMutualAuth) {
        this.enableMutualAuth = enableMutualAuth;
        return this;
    }

    public List<KeyCertOptions> getCertOptionsForMutualAuth() {
        return certOptionsForMutualAuth;
    }

    public HttpLoadInput setCertOptionsForMutualAuth(List<KeyCertOptions> certOptionsForMutualAuth) {
        this.certOptionsForMutualAuth = certOptionsForMutualAuth;
        return this;
    }

    public List<String> getUserAgentHeaders() {
        return userAgentHeaders;
    }

    public HttpLoadInput setUserAgentHeaders(final List<String> userAgentHeaders) {
        this.userAgentHeaders = userAgentHeaders;
        return this;
    }

    @Override
    public String toString() {
        return "HttpLoadInput{" +
                "remoteOperations=" + remoteOperations +
                ", commonParameters=" + commonParameters +
                ", userAgentHeaders=" + userAgentHeaders +
                ", durationInSeconds=" + durationInSeconds +
                ", maxOpenConnections=" + maxOpenConnections +
                ", path='" + path + '\'' +
                ", remoteHosts=" + remoteHosts +
                ", useBasicAuth=" + useBasicAuth +
                ", basicAuthUser='" + basicAuthUser + '\'' +
                ", basicAuthPassword='" + basicAuthPassword + '\'' +
                ", rampUpTimeInSeconds=" + rampUpTimeInSeconds +
                ", testType=" + testType +
                ", enableMutualAuth=" + enableMutualAuth +
                ", certOptionsForMutualAuth=" + certOptionsForMutualAuth +
                ", httpClientInstances=" + httpClientInstances +
                ", basicAuthHeader='" + basicAuthHeader + '\'' +
                '}';
    }
}