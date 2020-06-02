package servers;

import io.netty.handler.codec.http.HttpResponseStatus;
import io.reactivex.netty.protocol.http.server.HttpServer;
import reactive_mongo_driver.Event;
import reactive_mongo_driver.ReactiveMongoDriver;
import rx.Observable;
import storage.EventStorage;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class ReportServer {

    private final EventStorage eventStorage;
    private List<Event> events;

    public ReportServer(ReactiveMongoDriver mongoDriver, EventStorage eventStorage) {
        this.eventStorage = eventStorage;
        events = mongoDriver.getEvents();
    }

    public void run() {
        HttpServer.newServer(8082).start((req, resp) -> {
            Observable<String> response;
            String statType = req.getDecodedPath().substring(1);
            switch (statType) {
                case "dailyStats":
                    response = Observable.just(stats());
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                case "mediumDuration":
                    response = Observable.just(mediumDuration());
                    resp.setStatus(HttpResponseStatus.OK);
                    break;
                default:
                    response = Observable.just("Wrong command");
                    resp.setStatus(HttpResponseStatus.BAD_REQUEST);
            }
            return resp.writeString(response);
        }).awaitShutdown();
    }

    private void updateEvents() {
        while (eventStorage.getSize() > 0) {
            Event event = eventStorage.peek();
            events.add(event);
            eventStorage.pop();
        }
    }

    public String stats() {
        updateEvents();
        Map<String, Integer> eventsByDay = events.stream().filter(event -> event.getEventType() == Event.EventType.ENTER)
                .collect(Collectors.groupingBy(event -> {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(event.getTime());
                    return new SimpleDateFormat("dd-MM-yyyy").format(calendar.getTime());
                })).entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().size(),
                        (v1, v2) -> {
                            throw new RuntimeException(String.format("Duplicate key for values %s and %s", v1, v2));
                        }, TreeMap::new));

        if (eventsByDay.isEmpty()) {
            return "No records";
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (Map.Entry<String, Integer> e : eventsByDay.entrySet()) {
            stringBuilder.append(e.getKey()).append(": ").append(e.getValue()).append("\n");
        }
        return stringBuilder.toString();
    }

    public String mediumDuration() {
        updateEvents();
        Map<Integer, List<Event>> eventsByTicketId = events.stream().collect(Collectors.groupingBy(Event::getTicketId));
        if (eventsByTicketId.isEmpty()) {
            return "No records";
        }
        long sumTime = 0;
        int numSessions = 0;

        for (List<Event> eventList : eventsByTicketId.values()) {
            for (Event event : eventList) {
                if (event.getEventType() == Event.EventType.ENTER) {
                    sumTime -= event.getTime().getTime();
                    numSessions++;
                } else {
                    sumTime += event.getTime().getTime();
                }
            }
        }
        long medMinutes = TimeUnit.MINUTES.convert(sumTime / numSessions, TimeUnit.MILLISECONDS);
        return "Medium duration is " + medMinutes + " minutes";
    }
}