package com.refcursorconnector;

import com.refcursorconnector.config.MidpointConfiguration;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import com.refcursorconnector.config.PostgresConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;

public class RefCursorConnectorConfiguration extends AbstractConfiguration {
    private PostgresConfiguration postgresConfiguration;
    private MidpointConfiguration midpointConfiguration;

    private String hostname;
    private String midpointHostname;

    @Override
    public void validate() {
        throw new UnsupportedOperationException();
    }

    @ConfigurationProperty(displayMessageKey = "hostname (e.g. http://refcursor:8080)",
            helpMessageKey = "refcursor.config.sampleProperty.help")
    public String getHostname() {
        return hostname;
    }

    @ConfigurationProperty(displayMessageKey = "midpointHostname (e.g. http://midpoint:8080)",
            helpMessageKey = "refcursor.config.sampleProperty.help")
    public void setHostname(String host) {
        this.hostname = host;
    }

    public String getMidpointHostname() {
        return midpointHostname;
    }

    public void setMidpointHostname(String value) {
        this.midpointHostname = value;
    }

    public PostgresConfiguration getPostgresConfiguration() {
        return postgresConfiguration;
    }

    public MidpointConfiguration getMidpointConfiguration() {
        return midpointConfiguration;
    }

}
