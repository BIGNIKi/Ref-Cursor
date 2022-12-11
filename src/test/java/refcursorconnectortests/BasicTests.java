package refcursorconnectortests;


import com.evolveum.midpoint.xml.ns._public.common.common_3.SystemObjectsType;
import org.junit.Test;
import refcursorconnector.MidpointClient;

public class BasicTests {

    @Test
    public void testCreate() throws Exception {
        var client = new MidpointClient();
        var existed = client.getUserByOid(SystemObjectsType.USER_ADMINISTRATOR.value());
        client.dispose();

        System.out.println("User : " + existed.getName());
    }
}
