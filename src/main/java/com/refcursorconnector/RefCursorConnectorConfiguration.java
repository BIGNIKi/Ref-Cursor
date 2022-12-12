package com.refcursorconnector;

import com.refcursorconnector.config.MidpointConfiguration;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import com.refcursorconnector.config.PostgresConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class RefCursorConnectorConfiguration extends AbstractConfiguration {
    private PostgresConfiguration postgresConfiguration;
    private MidpointConfiguration midpointConfiguration;

    private String hostname;

    @Override
    public void validate() {
        // TODO implement
    }

    @ConfigurationProperty(displayMessageKey = "hostname (e.g. http://refcursor:8080)",
            helpMessageKey = "refcursor.config.sampleProperty.help")
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String host) {
        this.hostname = host;
    }

    public void init() {
        postgresConfiguration = new PostgresConfiguration();
        midpointConfiguration = new MidpointConfiguration();
    }

    public PostgresConfiguration getPostgresConfiguration() {
        return postgresConfiguration;
    }

    public MidpointConfiguration getMidpointConfiguration() {
        return midpointConfiguration;
    }

}
