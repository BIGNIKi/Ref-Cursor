package com.refcursorconnector;

import com.refcursorconnector.config.MidpointConfiguration;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import com.refcursorconnector.config.PostgresConfiguration;

public class RefCursorConfiguration extends AbstractConfiguration {
    private PostgresConfiguration postgresConfiguration;
    private MidpointConfiguration midpointConfiguration;

    private String hostname;

    public RefCursorConfiguration () {
        super();

        postgresConfiguration = new PostgresConfiguration();
        midpointConfiguration = new MidpointConfiguration();
    }

    @ConfigurationProperty(displayMessageKey = "hostname (e.g. http://ref-cursor:8080)")
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String host) {
        this.hostname = host;
    }


    @Override
    public void validate() {
        // TODO implement
    }


    public PostgresConfiguration getPostgresConfiguration() {
        return postgresConfiguration;
    }

    public MidpointConfiguration getMidpointConfiguration() {
        return midpointConfiguration;
    }

}
