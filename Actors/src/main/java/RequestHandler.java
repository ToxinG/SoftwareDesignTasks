import actors.MasterActor;
import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;

import java.io.PrintWriter;

public class RequestHandler {
    private PrintWriter writer;

    public RequestHandler(PrintWriter writer) {
        this.writer = writer;
    }

    public void getResponses(String request) {
        ActorSystem system = ActorSystem.create("SearchAggregatorSystem");
        ActorRef master = system.actorOf(Props.create(MasterActor.class, writer, request));
        master.tell("/start", ActorRef.noSender());
    }
}
