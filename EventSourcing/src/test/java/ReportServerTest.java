import com.mongodb.rx.client.Success;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reactive_mongo_driver.Event;
import reactive_mongo_driver.ReactiveMongoDriver;
import reactive_mongo_driver.Ticket;
import rx.observers.TestSubscriber;
import servers.ReportServer;
import storage.EventStorage;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static reactive_mongo_driver.Event.EventType.ENTER;
import static reactive_mongo_driver.Event.EventType.EXIT;

public class ReportServerTest {

    private static final String DATABASE_NAME = "report-test";

    ReactiveMongoDriver mongoDriver;
    ReportServer server;

    @Before
    public void clearDB() {
        TestSubscriber<Success> subscriber = new TestSubscriber<>();
        ReactiveMongoDriver.client.getDatabase(DATABASE_NAME).drop().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        EventStorage eventStorage = new EventStorage();
        mongoDriver = new ReactiveMongoDriver(DATABASE_NAME, eventStorage);
        server = new ReportServer(mongoDriver, eventStorage);
    }

    @Test
    public void EmptyStat() {
        Assert.assertEquals("No records", server.stats());
        Assert.assertEquals("No records", server.mediumDuration());
    }

    @Test
    public void testDailyStats() {
        fillEnters();
        Assert.assertEquals("24-06-2014: 2\n" +
                "25-06-2014: 1\n" +
                "26-06-2014: 1\n" +
                "27-06-2014: 1\n" +
                "28-06-2014: 1\n", server.stats());
    }

    @Test
    public void testMediumDuration() {
        fillEnters();
        Assert.assertEquals("Medium duration is 120 minutes", server.mediumDuration());
    }

    private void fillEnters() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.JUNE, 23);
        Date creation = calendar.getTime();
        Date expiration = new Date(creation.getTime() + TimeUnit.MILLISECONDS.convert(365, TimeUnit.DAYS));
        mongoDriver.addTicket(new Ticket(0, creation, expiration));

        for (int i = 1; i <= 5; i++) {
            Date entry = new Date(creation.getTime() + TimeUnit.MILLISECONDS.convert(i, TimeUnit.DAYS));
            Date exit = new Date(entry.getTime() + TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS));
            mongoDriver.addEvent(new Event(0, entry, ENTER));
            mongoDriver.addEvent(new Event(0, exit, EXIT));
        }
        Date entry = new Date(creation.getTime() + TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
        Date exit = new Date(entry.getTime() + TimeUnit.MILLISECONDS.convert(2, TimeUnit.HOURS));
        mongoDriver.addEvent(new Event(0, entry, ENTER));
        mongoDriver.addEvent(new Event(0, exit, EXIT));
    }
}