package com.http.load.tool.dataobjects;

import static org.apache.commons.lang.StringUtils.length;

public class KerberosRequest {

    private String requestId;
    private String upn;
    private String spn;
    private String principal;
    private String password;
    private String keyTabPath;

    public String getUpn() {
        return upn;
    }

    public KerberosRequest setUpn(String upn) {
        this.upn = upn;
        return this;
    }

    public String getSpn() {
        return spn;
    }

    public KerberosRequest setSpn(String spn) {
        this.spn = spn;
        return this;
    }

    public String getPrincipal() {
        return principal;
    }

    public KerberosRequest setPrincipal(String principal) {
        this.principal = principal;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public KerberosRequest setPassword(String password) {
        this.password = password;
        return this;
    }

    public String getKeyTabPath() {
        return keyTabPath;
    }

    public KerberosRequest setKeyTabPath(String keyTabPath) {
        this.keyTabPath = keyTabPath;
        return this;
    }

    public String getRequestId() {
        return requestId;
    }

    public KerberosRequest setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    @Override
    public String toString() {
        return "KerberosRequest{" +
                "requestId='" + requestId + '\'' +
                ", upn='" + upn + '\'' +
                ", spn='" + spn + '\'' +
                ", principal='" + principal + '\'' +
                ", pass='" + length(password) + '\'' +
                '}';
    }

    @Override
    @SuppressWarnings("PMD.IfStmtsMustUseBraces")
    public boolean equals(Object that) {
        if (that == null) return false;
        return requestId.equals(((KerberosRequest) that).requestId);
    }

    @Override
    public int hashCode() {
        return requestId.hashCode();
    }
}