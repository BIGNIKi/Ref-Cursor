package com.refcursorconnector;

import com.evolveum.midpoint.client.api.Service;
import com.evolveum.midpoint.client.api.exception.ObjectNotFoundException;
import com.evolveum.midpoint.client.api.exception.SchemaException;
import com.evolveum.midpoint.client.impl.prism.RestPrismServiceBuilder;
import com.evolveum.midpoint.xml.ns._public.common.common_3.UserType;
import com.refcursorconnector.config.MidpointConfiguration;

/**
 * Сервис для работы с midpoint client api
 */
public class MidpointClient {
    private Service client;

    public MidpointClient(MidpointConfiguration configuration) throws Exception {
        client = createClient(configuration);
    }

    /**
     * Получить пользователя по id
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
     * @param user
     * @return oid добавленного пользователя, null - в противном случае
     */
    public String addUser(UserType user) {
        try {
            var ref = client.users().add(user).post();
            return ref.getOid();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Удаляет пользователя
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

    private Service createClient(MidpointConfiguration configuration) throws Exception {
        RestPrismServiceBuilder builder = RestPrismServiceBuilder.create();
        return builder.username(configuration.user)
                .password(configuration.password)
                .baseUrl(configuration.host)
                .build();
    }
}
