import com.mongodb.rx.client.Success;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reactive_mongo_driver.Event;
import reactive_mongo_driver.ReactiveMongoDriver;
import reactive_mongo_driver.Ticket;
import rx.observers.TestSubscriber;
import servers.EntranceServer;
import storage.EventStorage;

import java.util.*;

import static reactive_mongo_driver.Event.EventType.ENTER;
import static reactive_mongo_driver.Event.EventType.EXIT;


public class EntranceServerTest {
    private static final String DATABASE_NAME = "entrance-test";

    ReactiveMongoDriver mongoDriver;
    EntranceServer server;

    @Before
    public void clearDB() {
        TestSubscriber<Success> subscriber = new TestSubscriber<>();
        ReactiveMongoDriver.client.getDatabase(DATABASE_NAME).drop().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        mongoDriver = new ReactiveMongoDriver(DATABASE_NAME, new EventStorage());
        server = new EntranceServer(mongoDriver);
    }

    @Test
    public void testMissed() {
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        server.addEnter(new HashMap<>(), new Date()).subscribe(subscriber);
        subscriber.assertValue("Missed attributes: id");
    }

    @Test
    public void testNoTickets() {
        Map<String, List<String>> params = new HashMap<>();
        params.put("id", Collections.singletonList("0"));
        TestSubscriber<String> subscriber = new TestSubscriber<>();
        server.addEnter(params, new Date()).subscribe(subscriber);
        subscriber.assertValues("No tickets were found");
    }

    @Test
    public void testAddEnter() {
        Map<String, List<String>> queryParam = new HashMap<>();
        queryParam.put("id", Collections.singletonList("0"));

        Date creation = new Date();
        Date enter = new Date(creation.getTime() + 1000);
        Date expiration = new Date(creation.getTime() + 5000);

        TestSubscriber<String> subscriber = new TestSubscriber<>();
        mongoDriver.addTicket(new Ticket(0, creation, expiration));
        server.addEnter(queryParam, enter).subscribe(subscriber);

        subscriber.assertValue("New enter registered");

        List<Event> events = mongoDriver.getEvents();
        Assert.assertEquals(1, events.size());
        Event event = events.get(0);
        Assert.assertEquals(enter, event.getTime());
        Assert.assertEquals(ENTER, event.getEventType());
        Assert.assertEquals(0, event.getTicketId());
    }

    @Test
    public void testAddExit() {
        Map<String, List<String>> queryParam = new HashMap<>();
        queryParam.put("id", Collections.singletonList("0"));

        Date creation = new Date();
        Date enter = new Date(creation.getTime() + 1000);
        Date expiration = new Date(creation.getTime() + 5000);

        TestSubscriber<String> subscriber = new TestSubscriber<>();
        mongoDriver.addTicket(new Ticket(0, creation, expiration));
        server.addExit(queryParam, enter).subscribe(subscriber);

        subscriber.assertValue("New exit registered");

        List<Event> events = mongoDriver.getEvents();
        Assert.assertEquals(1, events.size());
        Event event = events.get(0);
        Assert.assertEquals(enter, event.getTime());
        Assert.assertEquals(EXIT, event.getEventType());
        Assert.assertEquals(0, event.getTicketId());
    }
}