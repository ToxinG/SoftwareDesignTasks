package servers;

import com.mongodb.rx.client.Success;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import reactive_mongo_driver.Event;
import reactive_mongo_driver.ReactiveMongoDriver;
import reactive_mongo_driver.Ticket;
import rx.Observable;

import java.util.*;
import java.util.stream.Collectors;

public class EntranceServer {
    private final ReactiveMongoDriver mongoDriver;

    public EntranceServer(ReactiveMongoDriver mongoDriver) {
        this.mongoDriver = mongoDriver;
    }

    public void run() {
        HttpServer.newServer(8081).start((req, resp) -> {
            Observable<String> response;
            String action = req.getDecodedPath().substring(1);
            Map<String, List<String>> queryParam = req.getQueryParameters();
            switch (action) {
                case "enter":
                    response = addEnter(queryParam, new Date());
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                case "exit":
                    response = addExit(queryParam, new Date());
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                default:
                    response = Observable.just("Wrong command");
                    resp.setStatus(HttpResponseStatus.BAD_REQUEST);
            }
            return resp.writeString(response);
        }).awaitShutdown();
    }

    public Observable<String> addEnter(Map<String, List<String>> queryParam, Date date) {
        ArrayList<String> required = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }

        int id = Integer.parseInt(queryParam.get("id").get(0));
        Ticket ticket = mongoDriver.getLatestTicketVersion(id);
        if (ticket == null) {
            return Observable.just("No tickets were found");
        }
        Date expirationDate = ticket.getExpirationDate();
        if (date.after(expirationDate)) {
            return Observable.just("Ticket is already expired");
        }

        Event event = new Event(id, date, Event.EventType.ENTER);
        if (mongoDriver.addEvent(event) == Success.SUCCESS) {
            return Observable.just("New enter registered");
        } else {
            return Observable.just("Error: Can't handle the event");
        }
    }

    public Observable<String> addExit(Map<String, List<String>> queryParam, Date date) {
        ArrayList<String> required = new ArrayList<>(Collections.singletonList("id"));
        if (!isCompleteRequest(queryParam, required)) {
            return Observable.just(buildError(queryParam, required));
        }

        int id = Integer.parseInt(queryParam.get("id").get(0));
        Event event = new Event(id, date, Event.EventType.EXIT);
        if (mongoDriver.addEvent(event) == Success.SUCCESS) {
            return Observable.just("New exit registered");
        } else {
            return Observable.just("Error: Can't handle the event");
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
