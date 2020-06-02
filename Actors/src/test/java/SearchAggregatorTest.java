import actors.MasterActor;
import akka.actor.ActorSystem;
import akka.actor.Props;
import akka.testkit.TestActorRef;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import responses.CollectedResponse;
import responses.SingleResponse;
import search.GoogleSearch;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;


@RunWith(PowerMockRunner.class)
@PrepareForTest({Jsoup.class, Props.class})
public class SearchAggregatorTest {

    @Test
    public void searchTest() throws IOException {
        final Connection connection = Mockito.mock(Connection.class);
        final Document document = Mockito.mock(Document.class);
        PowerMockito.mockStatic(Jsoup.class);
        when(Jsoup.connect(Mockito.anyString())).thenReturn(connection);
        when(connection.userAgent(Mockito.anyString())).thenReturn(connection);
        when(connection.get()).thenReturn(document);
        when(document.select(Mockito.anyString())).thenReturn(createResponse());

        final List<SingleResponse> results = new GoogleSearch("").search();
        Assert.assertEquals(1, results.size());
        for (SingleResponse response : results) {
            Assert.assertNotNull(response.getUrl());
        }
    }

    @Test (expected = MasterActor.StopException.class)
    public void masterTest() throws Throwable {
        final ActorSystem system = ActorSystem.create("MySystem");
        final PrintWriter pw = new PrintWriter(System.out);
        final CollectedResponse resp = new CollectedResponse("Yandex", new ArrayList<>());
        final TestActorRef<MasterActor> master = TestActorRef.create(system, Props.create(MasterActor.class, pw, "abc"));
        final MasterActor actor = master.underlyingActor();
        actor.onReceive(resp);
        actor.onReceive(resp);
    }

    @Test
    public void masterTimeoutTest() throws Throwable {
        final ActorSystem system = ActorSystem.create("MySystem");
        final PrintWriter pw = new PrintWriter(System.out);
        final TestActorRef<MasterActor> master = TestActorRef.create(system, Props.create(MasterActor.class, pw, "abc"));
        final MasterActor mockedActor = Mockito.spy(master.underlyingActor());

        CollectedResponse resp = new CollectedResponse("Yandex", new ArrayList<>());
        mockedActor.onReceive(resp);
        verify(mockedActor, after(3000)).onReceive(resp);
    }

    private Elements createResponse() {
        List<Element> elements = new ArrayList<>();
        Attributes a1 = new Attributes();
        a1.put("href", "/url?q=https://yandex.com/");
        a1.put("target", "_blank");
        Element e1 = new Element(Tag.valueOf("a"),
                "http://www.google.ru/search?q=yandex&num=5", a1);
        e1.text("Yandex");
        elements.add(e1);
        return new Elements(elements);
    }
}