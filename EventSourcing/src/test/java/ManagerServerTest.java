import com.mongodb.rx.client.Success;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import reactive_mongo_driver.ReactiveMongoDriver;
import reactive_mongo_driver.Ticket;
import rx.observers.TestSubscriber;
import servers.ManagerServer;
import storage.EventStorage;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ManagerServerTest {

    private static final String DATABASE_NAME = "manager-test";

    ReactiveMongoDriver mongoDriver;
    ManagerServer server;

    @Before
    public void clearDB() {
        TestSubscriber<Success> subscriber = new TestSubscriber<>();
        ReactiveMongoDriver.client.getDatabase(DATABASE_NAME).drop().subscribe(subscriber);
        subscriber.awaitTerminalEvent();

        mongoDriver = new ReactiveMongoDriver(DATABASE_NAME, new EventStorage());
        server = new ManagerServer(mongoDriver);
    }

    @Test
    public void testNecessaryParams() {
        Map<String, List<String>> queryParam = new HashMap<>();
        Assert.assertEquals("Missed attributes: id, creationDate, expirationDate",
                server.handleTicketOperation(queryParam).toBlocking().first());
        queryParam.put("id", Collections.singletonList("0"));
        Assert.assertEquals("Missed attributes: creationDate, expirationDate",
                server.handleTicketOperation(queryParam).toBlocking().first());
    }

    @Test
    public void testAddTicketVersions() throws Throwable {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.JUNE, 23, 0, 0, 0);

        Date creationDate = calendar.getTime();
        Date expirationDate = new Date(creationDate.getTime()
                + TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));

        fillTickets(creationDate, expirationDate);

        List<Ticket> tickets = mongoDriver.getAllTicketVersions(0);
        Assert.assertEquals(2, tickets.size());
        Ticket ticket = tickets.get(1);
        Assert.assertEquals(0, ticket.getId());
        Assert.assertEquals(creationDate.toString(), ticket.getCreationDate().toString());
        Assert.assertEquals(expirationDate.toString(), ticket.getExpirationDate().toString());
    }

    @Test
    public void testAddTicketLatestVersion() throws Throwable {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2014, Calendar.JUNE, 23, 0, 0, 0);

        Date creationDate = calendar.getTime();
        Date expirationDate = new Date(creationDate.getTime()
                + TimeUnit.MILLISECONDS.convert(30, TimeUnit.DAYS));

        fillTickets(creationDate, expirationDate);

        Ticket ticket = mongoDriver.getLatestTicketVersion(0);
        Assert.assertEquals(0, ticket.getId());
        Assert.assertEquals(creationDate.toString(), ticket.getCreationDate().toString());
        Assert.assertEquals(expirationDate.toString(), ticket.getExpirationDate().toString());
    }

    private void fillTickets(Date creationDate, Date expirationDate) throws Throwable {
        Map<String, List<String>> queryParam = new HashMap<>();
        queryParam.put("id", Collections.singletonList("0"));
        queryParam.put("creationDate",
                Collections.singletonList(new SimpleDateFormat("dd-MM-yyyy")
                        .format(creationDate.getTime() - TimeUnit.MILLISECONDS.convert(10, TimeUnit.DAYS))));
        queryParam.put("expirationDate",
                Collections.singletonList(new SimpleDateFormat("dd-MM-yyyy")
                        .format((creationDate.getTime() - TimeUnit.MILLISECONDS.convert(10, TimeUnit.DAYS)))));

        server.handleTicketOperation(queryParam);
        queryParam.replace("id", Collections.singletonList("0"));
        queryParam.replace("creationDate",
                Collections.singletonList(new SimpleDateFormat("dd-MM-yyyy").format(creationDate)));
        queryParam.replace("expirationDate",
                Collections.singletonList(new SimpleDateFormat("dd-MM-yyyy").format(expirationDate)));
        server.handleTicketOperation(queryParam);
    }
}