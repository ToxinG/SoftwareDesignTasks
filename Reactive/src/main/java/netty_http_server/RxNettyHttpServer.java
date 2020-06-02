package netty_http_server;

import com.mongodb.rx.client.Success;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import reactive_mongo_driver.Product;
import reactive_mongo_driver.ReactiveMongoDriver;
import reactive_mongo_driver.User;
import rx.Observable;

import java.util.*;
import java.util.stream.Collectors;

public class RxNettyHttpServer {

    public static void main(final String[] args) {
        HttpServer
                .newServer(8080)
                .start((req, resp) -> {
                    Observable<String> response;
                    String name = req.getDecodedPath().substring(1);
                    Map<String, List<String>> queryParam = req.getQueryParameters();
                    switch (name) {
                        case "createUser":
                            response = createUser(queryParam);
                            resp.setStatus(HttpResponseStatus.OK);
                            break;
                        case "createProduct":
                            response = createProduct(queryParam);
                            resp.setStatus(HttpResponseStatus.OK);
                            break;
                        case "getProducts":
                            response = getProducts(queryParam);
                            resp.setStatus(HttpResponseStatus.OK);
                            break;
                        default:
                            response = Observable.just("Wrong command");
                            resp.setStatus(HttpResponseStatus.BAD_REQUEST);
                    }
                    return resp.writeString(response);
                })
                .awaitShutdown();
    }

    public static Observable<String> createProduct(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "name", "eur", "rub", "usd"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }

        Integer id = new Integer(queryParam.get("id").get(0));
        String name = queryParam.get("name").get(0);

        String eur = queryParam.get("eur").get(0);
        String rub = queryParam.get("rub").get(0);
        String usd = queryParam.get("usd").get(0);

        if (ReactiveMongoDriver.createProduct(new Product(id, name, rub, usd, eur)) == Success.SUCCESS) {
            return Observable.just("SUCCESS");
        } else {
            return Observable.just("Error: Can't add to database");
        }
    }

    public static Observable<String> createUser(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "currency", "name"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }

        Integer id = new Integer(queryParam.get("id").get(0));
        String name = queryParam.get("name").get(0);
        String currency = queryParam.get("currency").get(0);
        if (ReactiveMongoDriver.createUser(new User(id, name, currency)) == Success.SUCCESS) {
            return Observable.just("SUCCESS");
        } else {
            return Observable.just("Error: Can't add to database");
        }
    }

    public static Observable<String> getProducts(Map<String, List<String>> queryParam) {
        ArrayList<String> needValues = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, needValues)) {
            return Observable.just(buildError(queryParam, needValues));
        }
        Integer id = new Integer(queryParam.get("id").get(0));
        Observable<String> products = ReactiveMongoDriver.getAllProducts(id);
        return Observable.just("{ user_id = " + id + ", products = [").concatWith(products).concatWith(Observable.just("]}"));
    }

    private static boolean isCompleteRequest(Map<String, List<String>> queryParam, List<String> required) {
        for (String value : required) {
            if (!queryParam.containsKey(value)) {
                return false;
            }
        }
        return true;
    }

    private static String buildError(Map<String, List<String>> queryParam, List<String> required) {
        List<String> missedAttributes = required.stream().filter(val -> !queryParam.containsKey(val))
                .collect(Collectors.toList());
        return "Missed attributes: " + String.join(", ", missedAttributes);
    }
}