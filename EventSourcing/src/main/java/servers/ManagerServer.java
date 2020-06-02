package servers;

import com.mongodb.rx.client.Success;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import reactive_mongo_driver.ReactiveMongoDriver;
import reactive_mongo_driver.Ticket;
import rx.Observable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


public class ManagerServer {

    private final ReactiveMongoDriver mongoDriver;

    public ManagerServer(ReactiveMongoDriver mongoDriver) {
        this.mongoDriver = mongoDriver;
    }

    public void run() {
        HttpServer.newServer(8080).start((req, resp) -> {
            Observable<String> response;
            String action = req.getDecodedPath().substring(1);
            Map<String, List<String>> queryParam = req.getQueryParameters();
            switch (action) {
                case "getTicketInfo":
                    response = getTicket(queryParam);
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                case "addTicketInfo":
                    response = handleTicketOperation(queryParam);
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                default:
                    response = Observable.just("Wrong command");
                    resp.setStatus(HttpResponseStatus.BAD_REQUEST);
            }
            return resp.writeString(response);
        }).awaitShutdown();
    }

    public Observable<String> getTicket(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }

        int id = Integer.parseInt(queryParam.get("id").get(0));
        Ticket ticket = mongoDriver.getLatestTicketVersion(id);
        return Observable.just(ticket == null ? "No tickets were found" : ticket.toString());
    }

    public Observable<String> handleTicketOperation(Map<String, List<String>> queryParam) {
        ArrayList<String> required = new ArrayList<>(Arrays.asList("id", "creationDate", "expirationDate"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }
        int id = Integer.parseInt(queryParam.get("id").get(0));
        Date creationDate, expirationDate;
        try {
            creationDate = new SimpleDateFormat("dd-MM-yyyy").parse(queryParam.get("creationDate").get(0));
            expirationDate = new SimpleDateFormat("dd-MM-yyyy").parse(queryParam.get("expirationDate").get(0));
        } catch (ParseException e) {
            return Observable.just("Wrong date format, expected: dd-MM-yyyy");
        }
        return addTicket(id, creationDate, expirationDate);
    }

    public Observable<String> addTicket(int id, Date creationDate, Date expirationDate) {
        if (creationDate.after(expirationDate)) {
            return Observable.just("Creation date is after expiration date");
        }
        if (mongoDriver.addTicket(new Ticket(id, creationDate, expirationDate)) == Success.SUCCESS) {
            return Observable.just("Ticket was created or updated");
        } else {
            return Observable.just("Error: Can't add to database");
        }
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