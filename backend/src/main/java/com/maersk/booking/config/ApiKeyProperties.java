package com.maersk.booking.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "security.api-key")
public class ApiKeyProperties {
    private boolean enabled = true;
    private String header = "X-API-KEY";
    private String value = "dev-secret";

    public boolean isEnabled() { return enabled; }
    public void setEnabled(boolean enabled) { this.enabled = enabled; }
    public String getHeader() { return header; }
    public void setHeader(String header) { this.header = header; }
    public String getValue() { return value; }
    public void setValue(String value) { this.value = value; }
}
