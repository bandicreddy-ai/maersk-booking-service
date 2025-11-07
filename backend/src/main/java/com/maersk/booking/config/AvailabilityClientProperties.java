package com.maersk.booking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "availability")
public class AvailabilityClientProperties {
    private String baseUrl = "https://maersk.com";
    private String path = "/api/bookings/checkAvailable";
    private int retries = 3;
    private long backoffMillis = 200;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
    public int getRetries() { return retries; }
    public void setRetries(int retries) { this.retries = retries; }
    public long getBackoffMillis() { return backoffMillis; }
    public void setBackoffMillis(long backoffMillis) { this.backoffMillis = backoffMillis; }
}
