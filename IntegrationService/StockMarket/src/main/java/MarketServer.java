import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;
import model.Company;

import java.util.*;
import java.util.stream.Collectors;

public class MarketServer {

    Map<Integer, Company> companies = new HashMap<>();
    Random random = new Random();

    public void run() {
        HttpServer.newServer(8080).start((req, resp) -> {
            Observable<String> response;
            String action = req.getDecodedPath().substring(1);
            Map<String, List<String>> queryParam = req.getQueryParameters();
            switch (action) {
                case "addCompany":
                    response = addCompany(queryParam);
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                case "getStocksInfo":
                    response = getStocksInfo(queryParam);
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                case "buyStocks":
                    response = buyStocks(queryParam);
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                case "sellStocks":
                    response = sellStocks(queryParam);
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                case "changePrice":
                    response = changePrice(queryParam);
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                default:
                    response = Observable.just("Wrong command");
                    resp.setStatus(HttpResponseStatus.BAD_REQUEST);
            }
            return resp.writeString(response);
        }).awaitShutdown();
    }

    private Observable<String> addCompany(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "price", "amount"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (companies.containsKey(id)) {
            return Observable.just("This company is already in market.");
        }
        double price = Integer.parseInt(queryParam.get("price").get(0));
        int amount = Integer.parseInt(queryParam.get("amount").get(0));
        companies.put(id, new Company(id, price, amount));
        return Observable.just("ok");
    }

    private Observable<String> getStocksInfo(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (!companies.containsKey(id)) {
            return Observable.just("This company is not in market yet.");
        }
        return Observable.just(companies.get(id).stock.toString());
    }

    private Observable<String> buyStocks(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "amount"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (!companies.containsKey(id)) {
            return Observable.just("This company is not in market yet.");
        }
        int amount = Integer.parseInt(queryParam.get("amount").get(0));
        Company company = companies.get(id);
        if (company.stock.amount < amount) {
            return Observable.just("There are only " + company.stock.amount + " stocks on the market");
        }
        company.stock.amount -= amount;
        return Observable.just("ok");
    }

    private Observable<String> sellStocks(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "amount"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (!companies.containsKey(id)) {
            return Observable.just("This company is not in market yet.");
        }
        int amount = Integer.parseInt(queryParam.get("amount").get(0));
        Company company = companies.get(id);
        company.stock.amount += amount;
        return Observable.just("ok");
    }

    private Observable<String> changePrice(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (!companies.containsKey(id)) {
            return Observable.just("This company is not in market yet.");
        }
        Company company = companies.get(id);
        double deltaChange = random.nextGaussian() * company.stock.price;
        company.stock.price += deltaChange;
        return Observable.just("ok");
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
        List<String> missingAttributes = required.stream().filter(val -> !queryParam.containsKey(val))
                .collect(Collectors.toList());
        return "Missing attributes: " + String.join(", ", missingAttributes);
    }
}

