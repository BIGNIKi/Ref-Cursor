package com.refcursorconnector;

import com.refcursorconnector.config.MidpointConfiguration;
import org.identityconnectors.framework.spi.AbstractConfiguration;
import com.refcursorconnector.config.PostgresConfiguration;

public class RefCursorConnectorConfiguration extends AbstractConfiguration {
    private PostgresConfiguration postgresConfiguration;
    private MidpointConfiguration midpointConfiguration;

    @Override
    public void validate() {
        // TODO implement
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
