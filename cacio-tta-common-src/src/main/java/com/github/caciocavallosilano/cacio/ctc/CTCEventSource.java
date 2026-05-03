package com.github.caciocavallosilano.cacio.ctc;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

import com.github.caciocavallosilano.cacio.peer.CacioEventSource;
import com.github.caciocavallosilano.cacio.peer.managed.EventData;

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
