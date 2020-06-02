package actors;

import akka.actor.UntypedAbstractActor;
import responses.CollectedResponse;
import search.AbstractSearch;

public class ChildActor extends UntypedAbstractActor {

    @Override
    public void onReceive(Object message) throws Throwable {
        if (message instanceof AbstractSearch) {
            AbstractSearch engine = (AbstractSearch) message;
            getSender().tell(new CollectedResponse(engine.getEngineName(), engine.search()), self());
        }
    }
}