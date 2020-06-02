package actors;

import akka.actor.*;
import akka.japi.pf.DeciderBuilder;
import akka.routing.RoundRobinPool;
import responses.CollectedResponse;
import responses.SingleResponse;
import scala.concurrent.duration.Duration;
import search.GoogleSearch;
import search.YandexSearch;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

public class MasterActor extends UntypedAbstractActor {
    private final ActorRef childRouter;
    private String request;
    private PrintWriter writer;

    private static final int SEARCH_ENGINES_NUM = 2;
    private List<CollectedResponse> responses = new ArrayList<>();

    public MasterActor(PrintWriter writer, String request) {
        this.request = request;
        this.writer = writer;
        childRouter = getContext().actorOf(new RoundRobinPool(SEARCH_ENGINES_NUM)
                .props(Props.create(ChildActor.class)), "childRouter");
        getContext().setReceiveTimeout(Duration.create("1 second"));
    }

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message.equals("/start")) {
            childRouter.tell(new GoogleSearch(request), getSelf());
            childRouter.tell(new YandexSearch(request), getSelf());
        } else if (message instanceof CollectedResponse) {
            responses.add((CollectedResponse) message);
            if (responses.size() == SEARCH_ENGINES_NUM) {
                for (CollectedResponse response : responses) {
                    writer.println("Got response from " + response.getName());
                    writer.println(response.getResponses().size());
                    for (SingleResponse result : response.getResponses()) {
                        writer.println(result);
                    }
                }
                throw new StopException();
            }
        }
    }

    @Override
    public void postStop() {
        System.out.println("Master has been stopped");
    }

    @Override
    public SupervisorStrategy supervisorStrategy() {
        return new OneForOneStrategy(false, DeciderBuilder
        .match(StopException.class, e -> (SupervisorStrategy.Directive) OneForOneStrategy.stop())
        .build());
    }

    public static class StopException extends RuntimeException {
        StopException() {
            super();
        }
    }
}
