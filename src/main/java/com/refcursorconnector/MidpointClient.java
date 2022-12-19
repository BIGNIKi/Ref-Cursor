package com.refcursorconnector;

import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.api.exception.CommonException;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.client.api.exception.SchemaException;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import com.evolveum.prism.xml.ns._public.types_3.PolyStringType;
import com.refcursorconnector.config.MidpointConfiguration;
import org.identityconnectors.common.logging.Log;
import com.evolveum.midpoint.client.impl.restjaxb.*;

import java.io.IOException;

/**
 * Сервис для работы с midpoint client api
 */
public class MidpointClient {
    public static final Log LOG = Log.getLog(MidpointClient.class);

    private Service client;

    public MidpointClient() throws IOException {
        client = createClient();
    }

    public PolyStringType createPoly(String source) {
        return client.util().createPoly(source);
    }

    /**
     * Получить пользователя по id
     *
     * @param oid
     * @return существующего пользователя или null, если его не удалось найти
     */
    public UserType getUserByOid(String oid) {
        try {
            return client.users().oid(oid).get();
        } catch (SchemaException | ObjectNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Добавляет пользователя в midpoint
     *
     * @param user
     * @return oid добавленного пользователя, null - в противном случае
     */
    public String addUser(UserType user) {
        try {
            LOG.info("[Connector] client is {0}", client);
            var collection = client.users().add(user);
            var ref = collection.post();
            LOG.info("[Connector] ref is {0}", ref);
            return ref.getOid();
        } catch (CommonException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Удаляет пользователя
     *
     * @param oid
     * @return true, если удаление прошло успешно, false - в противном случае
     */
    public boolean deleteUserByOid(String oid) {
        try {
            client.users().oid(oid).delete();
            return true;
        } catch (ObjectNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Очищает используемые сервисом ресурсы.
     * Вызывать после работы с сервисом
     */
    public void dispose() {
        if (client != null) {
            this.client.close();
        }
    }

    private Service createClient() throws IOException {
        return new RestJaxbServiceBuilder()
                .password(MidpointConfiguration.password)
                .url(MidpointConfiguration.host)
                .username(MidpointConfiguration.user)
                .authentication(AuthenticationType.BASIC)
                .build();
    }
}
