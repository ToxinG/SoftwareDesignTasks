import org.junit.Assert;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.testcontainers.containers.FixedHostPortGenericContainer;
import org.testcontainers.containers.GenericContainer;
import model.Stock;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationTest {

    @ClassRule
    public static GenericContainer simpleWebServer
            = new FixedHostPortGenericContainer("stock_market:1.0-SNAPSHOT")
            .withFixedExposedPort(8080, 8080)
            .withExposedPorts(8080);

    private final static String MARKET_SOCKET = "http://localhost:8080/";

    private final static HttpClient HTTP_CLIENT = HttpClient.newBuilder().build();

    private final static UserServer USER_SERVER = new UserServer();


    private String sendRequest(String path) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create(path)).build();
        HttpResponse<String> response = HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    @Before
    public void initiate() throws InterruptedException, IOException {
        sendRequest(MARKET_SOCKET + "addCompany?id=1&price=10&amount=10");
        sendRequest(MARKET_SOCKET + "addCompany?id=2&price=100&amount=100");
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("1"));
        params.put("balance", Collections.singletonList("50"));
        USER_SERVER.addUser(params);
        params.replace("id", Collections.singletonList("2"));
        params.replace("balance", Collections.singletonList("1000000"));
        USER_SERVER.addUser(params);
        params.remove("balance");
        params.remove("id");
        params.put("userId", Collections.singletonList("2"));
        params.put("companyId", Collections.singletonList("2"));
        params.put("amount", Collections.singletonList("100"));
        USER_SERVER.buyStocks(params);
    }

    @Test
    public void testAddingCompany() throws InterruptedException, IOException {
        Assert.assertEquals("ok",
                sendRequest(MARKET_SOCKET + "addCompany?id=3&price=10&amount=0"));
        Assert.assertEquals("This company is already in market.",
                sendRequest(MARKET_SOCKET + "addCompany?id=3&price=10&amount=0"));
    }

    @Test
    public void testGettingStocksInfo() throws InterruptedException, IOException {
        Stock stock = new Stock(10, 10);
        Assert.assertEquals(stock.toString(),
                sendRequest(MARKET_SOCKET + "getStocksInfo?id=1"));
        Assert.assertEquals("This company is not in market yet.",
                sendRequest(MARKET_SOCKET + "getStocksInfo?id=10"));
    }

    @Test
    public void testBuyingStocks() throws InterruptedException, IOException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("userId", Collections.singletonList("1"));
        params.put("companyId", Collections.singletonList("1"));
        params.put("amount", Collections.singletonList("5"));
        USER_SERVER.buyStocks(params).test().assertValue("ok");
        USER_SERVER.buyStocks(params).test().assertValue("User doesn't have enough money for purchase");
        params.replace("userId", Collections.singletonList("2"));
        params.replace("amount", Collections.singletonList("100"));
        USER_SERVER.buyStocks(params).test().assertValue("Company doesn't have this amount of stocks");
    }

    @Test
    public void testSellingStocks() throws InterruptedException, IOException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("userId", Collections.singletonList("2"));
        params.put("companyId", Collections.singletonList("2"));
        params.put("amount", Collections.singletonList("100"));
        USER_SERVER.sellStocks(params).test().assertValue("ok");
    }

    @Test
    public void testBalance() throws InterruptedException, IOException {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("1"));
        USER_SERVER.getBalance(params).test().assertValue(Double.toString(50));
        params.put("id", Collections.singletonList("2"));
        USER_SERVER.getBalance(params).test().assertValue(Double.toString(1000000));
    }

}