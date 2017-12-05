package com.http.load.tool.dataobjects;

public class KeyCertOptions {

    private String path;
    private String password;

    public String getPath() {
        return path;
    }

    public KeyCertOptions setPath(String path) {
        this.path = path;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public KeyCertOptions setPassword(String password) {
        this.password = password;
        return this;
    }

    @Override
    public String toString() {
        return "KeyCertOptions{" +
                "path='" + path + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}