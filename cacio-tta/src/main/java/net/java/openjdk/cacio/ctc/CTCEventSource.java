package net.java.openjdk.cacio.ctc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import sun.awt.peer.cacio.CacioEventSource;
import sun.awt.peer.cacio.managed.EventData;

public class CTCEventSource implements CacioEventSource {

    private static CTCEventSource instance;

    static CTCEventSource getInstance() {
        if (instance == null) {
            instance = new CTCEventSource();
        }
        return instance;
    }

    private BlockingQueue<EventData> queue = new LinkedBlockingQueue<EventData>();

    private CTCEventSource() {
        // Singleton.
    }

    @Override
    public EventData getNextEvent() throws InterruptedException {
        return queue.take();
    }

    void postEvent(EventData ev) {
        queue.offer(ev);
    }
}
