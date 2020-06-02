import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import rx.Observable;
import model.Stock;
import model.UserAccount;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class UserServer {
    MarketClient marketClient = new MarketClient();
    Map<Integer, UserAccount> users = new HashMap<>();

    public void run() {
        HttpServer.newServer(8081).start((req, resp) -> {
            Observable<String> response;
            String action = req.getDecodedPath().substring(1);
            Map<String, List<String>> queryParam = req.getQueryParameters();
            try {
                switch (action) {
                    case "addUser":
                        response = addUser(queryParam);
                        resp.setStatus(HttpResponseStatus.OK);
                        break;
                    case "depositMoney":
                        response = depositMoney(queryParam);
                        resp.setStatus(HttpResponseStatus.OK);
                        break;
                    case "getStocksInfo":
                        response = getStocksInfo(queryParam);
                        resp.setStatus(HttpResponseStatus.OK);
                        break;
                    case "getBalance":
                        response = getBalance(queryParam);
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
                    default:
                        response = Observable.just("Wrong command");
                        resp.setStatus(HttpResponseStatus.BAD_REQUEST);
                }
            } catch (Exception e) {
                response = Observable.just("Error occurred");
                resp.setStatus(HttpResponseStatus.INTERNAL_SERVER_ERROR);
            }
            return resp.writeString(response);
        }).awaitShutdown();
    }

    public Observable<String> addUser(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "balance"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (users.containsKey(id)) {
            return Observable.just("This user already exists.");
        }
        double balance = Double.parseDouble(queryParam.get("balance").get(0));
        users.put(id, new UserAccount(balance));
        return Observable.just("ok");
    }

    public Observable<String> depositMoney(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "deposit"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (!users.containsKey(id)) {
            return Observable.just("This user doesn't exist.");
        }
        double money = Double.parseDouble(queryParam.get("deposit").get(0));
        UserAccount user = users.get(id);
        user.balance += money;
        return Observable.just(Double.toString(user.balance));
    }

    public Observable<String> getStocksInfo(Map<String, List<String>> queryParam) throws IOException, InterruptedException {
        ArrayList<String> required = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (!users.containsKey(id)) {
            return Observable.just("This user doesn't exist.");
        }
        UserAccount user = users.get(id);
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<Integer, Integer> kv : user.stocks.entrySet()) {
            stringBuilder.append(marketClient.getStocksInfo(kv.getKey())).append("\n");
        }
        return Observable.just(stringBuilder.toString());
    }

    public Observable<String> getBalance(Map<String, List<String>> queryParam) throws IOException, InterruptedException {
        ArrayList<String> required = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        if (!users.containsKey(id)) {
            return Observable.just("This user doesn't exist.");
        }
        UserAccount user = users.get(id);
        double total = user.balance;
        for (Map.Entry<Integer, Integer> kv : user.stocks.entrySet()) {
            total += kv.getValue() * marketClient.getStocksInfo(kv.getKey()).price;
        }
        return Observable.just(Double.toString(total));
    }

    public Observable<String> buyStocks(Map<String, List<String>> queryParam) throws IOException, InterruptedException {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("userId", "companyId", "amount"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int userId = Integer.parseInt(queryParam.get("userId").get(0));
        if (!users.containsKey(userId)) {
            return Observable.just("This user doesn't exist.");
        }
        int companyId = Integer.parseInt(queryParam.get("companyId").get(0));
        int amount = Integer.parseInt(queryParam.get("amount").get(0));
        Stock stockInfo = marketClient.getStocksInfo(companyId);
        if (stockInfo == null) {
            return Observable.just("This company is not in market yet");
        }
        UserAccount user = users.get(userId);
        if (stockInfo.price * amount > user.balance) {
            return Observable.just("User doesn't have enough money for purchase");
        }
        if (amount > stockInfo.amount) {
            return Observable.just("Company doesn't have this amount of stocks");
        }
        marketClient.buyStocks(companyId, amount);
        user.balance -= stockInfo.price * amount;
        user.stocks.put(companyId, user.stocks.getOrDefault(companyId, 0) + amount);
        return Observable.just("ok");
    }

    public Observable<String> sellStocks(Map<String, List<String>> queryParam) throws IOException, InterruptedException {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("userId", "companyId", "amount"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int userId = Integer.parseInt(queryParam.get("userId").get(0));
        if (!users.containsKey(userId)) {
            return Observable.just("This user doesn't exist.");
        }
        int companyId = Integer.parseInt(queryParam.get("companyId").get(0));
        int amount = Integer.parseInt(queryParam.get("amount").get(0));
        UserAccount user = users.get(userId);
        if (amount > user.stocks.getOrDefault(companyId, 0)) {
            return Observable.just("User doesn't have this amount of stocks");
        }
        double price = marketClient.getStocksInfo(companyId).price;
        marketClient.sellStocks(companyId, amount);
        user.balance += price * amount;
        user.stocks.put(companyId, user.stocks.getOrDefault(companyId, 0) - amount);
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