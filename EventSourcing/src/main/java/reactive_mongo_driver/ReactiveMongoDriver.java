package reactive_mongo_driver;

import com.mongodb.rx.client.MongoClient;
import com.mongodb.rx.client.MongoClients;
import com.mongodb.rx.client.Success;
import storage.EventStorage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.mongodb.client.model.Filters.eq;

public class ReactiveMongoDriver {

    public static final MongoClient client = createMongoClient();

    private final String databaseName;
    private final EventStorage eventStorage;

    public ReactiveMongoDriver(String databaseName, EventStorage eventStorage) {
        this.databaseName = databaseName;
        this.eventStorage = eventStorage;
    }

    public Success addTicket(Ticket ticket) {
        return client.getDatabase(databaseName).getCollection("ticket").insertOne(ticket.getDocument())
                .timeout(10, TimeUnit.SECONDS).toBlocking().single();
    }

    public Success addEvent(Event event) {
        Success result = client.getDatabase(databaseName).getCollection("event").insertOne(event.getDocument())
                .timeout(10, TimeUnit.SECONDS).toBlocking().single();
        if (result == Success.SUCCESS) {
            eventStorage.addEvent(event);
        }
        return result;
    }

    public Ticket getLatestTicketVersion(Integer id) {
        List<Ticket> tickets = getAllTicketVersions(id);
        return tickets.stream().max(Comparator.comparing(Ticket::getCreationDate)).orElse(null);
    }

    public List<Ticket> getAllTicketVersions(Integer id) {
        List<Ticket> tickets = new ArrayList<>();
        client.getDatabase(databaseName).getCollection("ticket").find(eq("id", id))
                .maxTime(10, TimeUnit.SECONDS).toObservable().map(Ticket::new).toBlocking().subscribe(tickets::add);
        return tickets;
    }


    public List<Event> getEvents() {
        List<Event> events = new ArrayList<>();
        client.getDatabase(databaseName).getCollection("event").find().maxTime(10, TimeUnit.SECONDS)
                .toObservable().map(Event::new).toBlocking().subscribe(events::add);
        return events;
    }

    private static MongoClient createMongoClient() {
        return MongoClients.create("mongodb://localhost:27017");
    }
}