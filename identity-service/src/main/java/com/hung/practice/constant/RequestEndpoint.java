package com.hung.practice.constant;

public class RequestEndpoint {
    private RequestEndpoint() {} // Noncompliant - method is empty

    public static final String USER = "/users";
    public static final String ROLE = "/roles";
    public static final String PERMISSION = "/permissions";
    public static final String AUTH = "/auth";
}
