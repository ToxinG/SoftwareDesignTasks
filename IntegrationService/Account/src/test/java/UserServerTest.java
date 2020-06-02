import org.junit.Test;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserServerTest {
    private final static UserServer USER_SERVER = new UserServer();

    @Test
    public void addUser() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("1"));
        params.put("balance", Collections.singletonList("0"));
        USER_SERVER.addUser(params).test().assertValue("ok");
        USER_SERVER.addUser(params).test().assertValue("This user already exists.");
    }

    @Test
    public void depositMoney() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("2"));
        params.put("deposit", Collections.singletonList("0"));
        USER_SERVER.depositMoney(params).test().assertValue("This user doesn't exist.");
        params.remove("deposit");
        params.put("balance", Collections.singletonList("0"));
        USER_SERVER.addUser(params);
        params.put("deposit", Collections.singletonList("100"));
        params.remove("balance");
        USER_SERVER.depositMoney(params).test().assertValue(Double.toString(100));
    }
}