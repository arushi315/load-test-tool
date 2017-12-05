package com.http.load.tool.dataobjects;

import java.util.List;

public class KerberosLoadTestConfig {

    private List<String> pipeNames;
    private int durationInSeconds;
    private int requestsPerSecond;
    private String principle;
    private String password;
    private String spn;

    public List<String> getPipeNames() {
        return pipeNames;
    }

    public KerberosLoadTestConfig setPipeNames(List<String> pipeNames) {
        this.pipeNames = pipeNames;
        return this;
    }

    public int getDurationInSeconds() {
        return durationInSeconds;
    }

    public KerberosLoadTestConfig setDurationInSeconds(int durationInSeconds) {
        this.durationInSeconds = durationInSeconds;
        return this;
    }

    public int getRequestsPerSecond() {
        return requestsPerSecond;
    }

    public KerberosLoadTestConfig setRequestsPerSecond(int requestsPerSecond) {
        this.requestsPerSecond = requestsPerSecond;
        return this;
    }

    public String getPrinciple() {
        return principle;
    }

    public KerberosLoadTestConfig setPrinciple(String principle) {
        this.principle = principle;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public KerberosLoadTestConfig setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getSpn() {
        return spn;
    }

    public KerberosLoadTestConfig setSpn(String spn) {
        this.spn = spn;
        return this;
    }
}