package storage;

import reactive_mongo_driver.Event;

import java.util.LinkedList;
import java.util.Queue;


public class EventStorage {

    private Queue<Event> newEventsQueue;

    public EventStorage() {
        newEventsQueue = new LinkedList<>();
    }

    public void addEvent(Event event) {
        newEventsQueue.add(event);
    }

    public int getSize() {
        return newEventsQueue.size();
    }

    public Event pop() {
        return newEventsQueue.poll();
    }

    public Event peek() {
        return newEventsQueue.peek();
    }
}