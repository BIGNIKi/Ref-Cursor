package refcursorconnector;

import org.identityconnectors.framework.spi.AbstractConfiguration;
import org.identityconnectors.framework.spi.ConfigurationProperty;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

public class RefCursorConfiguration extends AbstractConfiguration {

    private String hostname;

    @ConfigurationProperty(displayMessageKey = "hostname (e.g. http://ref-cursor:8080)")
    public String getHostname() {
        return hostname;
    }

    public void setHostname(String host) {
        this.hostname = host;
    }

    // TODO implement
    @Override
    public void validate() {
        throw new NotImplementedException();
    }
}
